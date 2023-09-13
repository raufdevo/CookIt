package ca.gbc.cookit.controller;

import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.dto.MealDto;
import ca.gbc.cookit.exceptions.BadRequestRuntimeException;
import ca.gbc.cookit.model.Meal;
import ca.gbc.cookit.model.User;
import ca.gbc.cookit.service.MealService;
import ca.gbc.cookit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    private final Environment environment;
    private final UserService userService;

    public ProfileController(Environment environment, UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = this.userService.getCurrentUser();

        model.addAttribute("name", currentUser.getName());
        model.addAttribute("username", currentUser.getUsername());

        return "profile/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        User currentUser = this.userService.getCurrentUser();

        model.addAttribute("name", currentUser.getName());
        model.addAttribute("username", currentUser.getUsername());

        return "profile/edit";
    }

    @PostMapping("/profile/edit")
    public String editProfile(Model model, HttpServletResponse httpServletResponse, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "username", required = false) String username) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_EDIT_PROFILE_EMPTY_NAME);
            errors.add(message);
        }
        if (username == null || username.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_EDIT_PROFILE_EMPTY_USERNAME);
            errors.add(message);
        }
        if (errors.isEmpty()) {
            try {
                this.userService.updateCurrentUser(username, name);
                httpServletResponse.setHeader("Location", "/profile");
                httpServletResponse.setStatus(302);
            } catch (BadRequestRuntimeException badRequestRuntimeException) {
                String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
                errors.add(message);
            }
        }

        model.addAttribute("errors", errors);

        return "profile/edit";
    }

    @GetMapping("/profile/recipes")
    public String profileRecipes(Model model) {
        User currentUser = this.userService.getCurrentUser();
        model.addAttribute("recipes", currentUser.getRecipes());
        return "profile/recipes";
    }


    @GetMapping("/profile/recipe/{recipeCode}/mark-as-favorite")
    public void markAsFavorite(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @PathVariable("recipeCode") String recipeCode) {
        try {
            this.userService.addRecipeAsFavoriteForCurrentUser(recipeCode);
            httpServletResponse.setHeader("Location", httpServletRequest.getHeader(HttpHeaders.REFERER));
            httpServletResponse.setStatus(302);
        } catch (BadRequestRuntimeException ignored) {
        }
    }

    @GetMapping("/profile/meals")
    public String profileMeals(Model model) {
        User currentUser = this.userService.getCurrentUser();
        List<Meal> meals = currentUser.getMeals();

        List<MealDto> mealDtoList = meals.stream().map(MealService::mapToDto).collect(Collectors.toList());

        model.addAttribute("meals", mealDtoList);
        return "meal/meals";
    }


    @GetMapping("/profile/fav-recipes")
    public String profileFavoriteRecipes(Model model) {
        User currentUser = this.userService.getCurrentUser();
        model.addAttribute("recipes", currentUser.getFavoriteRecipes());
        return "profile/favorite_recipes";
    }

    @GetMapping("/profile/basket")
    public String profileBasket(Model model) {
        User currentUser = this.userService.getCurrentUser();
        model.addAttribute("basket", currentUser.getBasket());
        return "profile/basket";
    }


    @GetMapping("/profile/add-to-basket/{ingredientId}")
    public void addToBasket(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @PathVariable("ingredientId") Long ingredientId) {
        try {
            this.userService.addIngredientToBasketForCurrentUser(ingredientId);
            httpServletResponse.setHeader("Location", httpServletRequest.getHeader(HttpHeaders.REFERER));
            httpServletResponse.setStatus(302);
        } catch (BadRequestRuntimeException ignored) {
        }
    }

    @GetMapping("/profile/remove-from-basket/{ingredientId}")
    public void removeFromBasket(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @PathVariable("ingredientId") Long ingredientId) {
        try {
            this.userService.removeIngredientFromBasketForCurrentUser(ingredientId);
            httpServletResponse.setHeader("Location", httpServletRequest.getHeader(HttpHeaders.REFERER));
            httpServletResponse.setStatus(302);
        } catch (BadRequestRuntimeException ignored) {
        }
    }


}
