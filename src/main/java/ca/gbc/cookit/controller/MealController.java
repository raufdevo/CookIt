package ca.gbc.cookit.controller;


import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.dto.MealDto;
import ca.gbc.cookit.exception.BadRequestRuntimeException;
import ca.gbc.cookit.model.Meal;
import ca.gbc.cookit.service.MealService;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MealController {

    private final MealService mealService;
    private final Environment environment;

    public MealController(MealService mealService, Environment environment) {
        this.mealService = mealService;
        this.environment = environment;
    }

    @GetMapping("/meal/plan")
    public String planMeal() {
        return "meal/plan_meal";
    }

    @PostMapping("/meal/plan")
    public String planMeal(Model model, HttpServletResponse httpServletResponse, @RequestParam(value = "recipe_code", required = false) String recipeCode, @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date date) {

        List<String> errors = new ArrayList<>();

        if (recipeCode == null || recipeCode.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_PLAN_MEAL_EMPTY_RECIPE_CODE);
            errors.add(message);
        }

        if (date == null) {
            String message = this.environment.getProperty(Constants.FORM_PLAN_MEAL_EMPTY_DATE);
            errors.add(message);
        }

        if (errors.isEmpty()) {
            try {
                this.mealService.addMealForCurrentUser(recipeCode, date);
                httpServletResponse.setHeader("Location", "/profile/meals");
                httpServletResponse.setStatus(302);
            } catch (BadRequestRuntimeException badRequestRuntimeException) {
                String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
                errors.add(message);
            }
        }

        model.addAttribute("errors", errors);

        return "meal/plan_meal";
    }

    @GetMapping("/meal/{mealId}/edit")
    public String editMeal(Model model, @PathVariable("mealId") Long mealId) {
        Meal meal = this.mealService.getMealByIdForCurrentUser(mealId);

        MealDto mealDto = MealService.mapToDto(meal);

        model.addAttribute("meal", mealDto);

        return "meal/edit_meal";
    }

    @PostMapping("/meal/{mealId}/edit")
    public String editMeal(Model model, HttpServletResponse httpServletResponse, @PathVariable("mealId") Long mealId, @RequestParam(value = "recipe_code", required = false) String recipeCode, @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date date) {
        List<String> errors = new ArrayList<>();

        if (recipeCode == null || recipeCode.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_PLAN_MEAL_EMPTY_RECIPE_CODE);
            errors.add(message);
        }
        if (date == null) {
            String message = this.environment.getProperty(Constants.FORM_PLAN_MEAL_EMPTY_DATE);
            errors.add(message);
        }
        if (errors.isEmpty()) {
            try {
                this.mealService.editMealForCurrentUser(mealId, recipeCode, date);
                httpServletResponse.setHeader("Location", "/profile/meals");
                httpServletResponse.setStatus(302);
            } catch (BadRequestRuntimeException badRequestRuntimeException) {
                String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
                errors.add(message);
            }
        }

        try {
            Meal meal = this.mealService.getMealByIdForCurrentUser(mealId);
            MealDto mealDto = MealService.mapToDto(meal);
            model.addAttribute("meal", mealDto);
        } catch (BadRequestRuntimeException badRequestRuntimeException) {
            String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
            errors.add(message);
        }
        model.addAttribute("errors", errors);

        return "meal/edit_meal";
    }

    @GetMapping("/meal/{mealId}/remove")
    public void removeMeal(HttpServletResponse httpServletResponse, @PathVariable("mealId") Long mealId) {
        this.mealService.deleteMealForCurrentUser(mealId);

        httpServletResponse.setHeader("Location", "/profile/meals");
        httpServletResponse.setStatus(302);
    }
}
