package ca.gbc.cookit.controller;


import ca.gbc.cookit.authentication.CustomUserDetails;
import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exception.BadRequestRuntimeException;
import ca.gbc.cookit.model.User;
import ca.gbc.cookit.service.UserService;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;




@Controller
public class AuthController {

    private final Environment environment;
    private final UserService userService;

    public AuthController(Environment environment, UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register() {
        return "authentication/register";
    }

    @PostMapping("/register")
    public String register(Model model, HttpServletResponse httpServletResponse, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "username", required = false) String username, @RequestParam(value = "password", required = false) String password, @RequestParam(value = "question", required = false) String question, @RequestParam(value = "answer", required = false) String answer) {

        List<String> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_REGISTER_EMPTY_NAME);
            errors.add(message);
        }
        if (username == null || username.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_REGISTER_EMPTY_USERNAME);
            errors.add(message);
        }
        if (password == null || password.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_REGISTER_EMPTY_PASSWORD);
            errors.add(message);
        } else {
            if (password.length() < 8) {
                String message = this.environment.getProperty(Constants.FORM_REGISTER_WEAK_PASSWORD);
                errors.add(message);
            }
        }
        if (question == null || question.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_REGISTER_EMPTY_QUESTION);
            errors.add(message);
        }
        if (answer == null || answer.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_REGISTER_EMPTY_ANSWER);
            errors.add(message);
        }
        if (errors.isEmpty()) {
            try {
                this.userService.register(name, username, password, question, answer);
                httpServletResponse.setHeader("Location", "/login");
                httpServletResponse.setStatus(302);
            } catch (BadRequestRuntimeException badRequestRuntimeException) {
                String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
                errors.add(message);
            }
        }

        model.addAttribute("errors", errors);

        return "authentication/register";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);

            String message = this.environment.getProperty(Constants.FORM_LOGIN_INVALID_USERNAME_PASSWORD);
            model.addAttribute("loginErrorMessage", message);
        }
        return "authentication/login";
    }

    @GetMapping("/fp/forgot")
    public String forgotPassword(Model model, @RequestParam(value = "error", defaultValue = "") String error) {
        if (error.equals("1")) {
            String msg = environment.getProperty(Constants.USER_NOT_FOUND);
            model.addAttribute("error", msg);
        }

        return "authentication/forgot_password_get_username";
    }

    @GetMapping("/fp/question")
    public String askUserQuestion(Model model, HttpServletResponse httpServletResponse, @RequestParam(value = "username") String username, @RequestParam(value = "error", defaultValue = "") String error) {
        if (error.equals("1")) {
            String msg = environment.getProperty(Constants.PERMISSION_DENIED);
            model.addAttribute("error", msg);
        }

        try {
            User user = this.userService.findByUsername(username);

            model.addAttribute("question", user.getQuestion());
            model.addAttribute("username", username);
        } catch (BadRequestRuntimeException badRequestRuntimeException) {
            if (badRequestRuntimeException.getMessageCode().equals(Constants.USER_NOT_FOUND)) {
                httpServletResponse.setHeader("Location", "/fp/forgot?username=" + username + "&error=1");
                httpServletResponse.setStatus(302);
            }
        }

        return "authentication/forgot_password_question";
    }

    @PostMapping("/fp/answer")
    public void answerUserQuestion(HttpServletResponse httpServletResponse, @RequestParam(value = "answer") String answer, @RequestParam(value = "username") String username) {

        User user = this.userService.findByUsername(username);

        if (!user.getAnswer().equals(answer)) {
            httpServletResponse.setHeader("Location", "/fp/question?username=" + username + "&error=1");
            httpServletResponse.setStatus(302);
        } else {
            CustomUserDetails userDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            httpServletResponse.setHeader("Location", "/reset-password");
            httpServletResponse.setStatus(302);
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword() {

        return "authentication/forgot_password_reset_password";

    }

    @PostMapping("/reset-password")
    public void resetPassword(HttpServletResponse httpServletResponse, @RequestParam(value = "password") String password) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        this.userService.resetPasswordForCurrentUser(password);

        User user = this.userService.findByUsername(userDetails.getUsername());
        CustomUserDetails newUserDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(newUserDetails, password);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        httpServletResponse.setHeader("Location", "/");
        httpServletResponse.setStatus(302);
    }

}
