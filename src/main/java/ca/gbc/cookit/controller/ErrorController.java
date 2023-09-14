package ca.gbc.cookit.controller;


import ca.gbc.cookit.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    private final Environment environment;

    public ErrorController(Environment environment) {


        this.environment = environment;
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleThrowable(Throwable throwable, Model model) {
        logger.error("Exception while execution of application", throwable);
        return this.error(model);
    }

    @GetMapping("/error")
    public String error(Model model) {
        String errorMessage = this.environment.getProperty(Constants.UNKNOWN_ERROR);
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

}
