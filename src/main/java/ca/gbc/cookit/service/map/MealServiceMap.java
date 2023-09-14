package ca.gbc.cookit.service.map;


import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exception.BadRequestRuntimeException;
import ca.gbc.cookit.model.Meal;
import ca.gbc.cookit.model.Recipe;
import ca.gbc.cookit.model.User;
import ca.gbc.cookit.service.MealService;
import ca.gbc.cookit.service.RecipeService;
import ca.gbc.cookit.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MealServiceMap implements MealService {

    private final UserService userService;
    private final RecipeService recipeService;

    public MealServiceMap(UserService userService, RecipeService recipeService) {
        this.userService = userService;
        this.recipeService = recipeService;
    }

    @Override
    @Transactional
    public void addMealForCurrentUser(String recipeCode, Date date) {
        Recipe recipeByCode = this.recipeService.findByCode(recipeCode);
        User currentUser = this.userService.getCurrentUser();

        Meal meal = new Meal();
        meal.setRecipe(recipeByCode);
        meal.setDate(date);

        if (currentUser.getMeals().stream().anyMatch(m -> m.getDate().equals(date))) {
            throw new BadRequestRuntimeException(Constants.FORM_PLAN_MEAL_DATE_NOT_AVAILABLE);
        }
        currentUser.getMeals().add(meal);
    }

    @Override
    @Transactional
    public void deleteMealForCurrentUser(Long mealId) {

        User currentUser = this.userService.getCurrentUser();
        boolean result = currentUser.getMeals().removeIf(meal -> meal.getId().equals(mealId));

        if (!result) {
            throw new BadRequestRuntimeException(Constants.MEAL_NOT_FOUND);
        }
    }

    @Override
    public Meal getMealByIdForCurrentUser(Long mealId) {
        User currentUser = this.userService.getCurrentUser();

        return currentUser.getMeals().stream().filter(meal -> meal.getId().equals(mealId)).findAny().orElseThrow(() -> new BadRequestRuntimeException(Constants.MEAL_NOT_FOUND));
    }

    @Override
    @Transactional
    public void editMealForCurrentUser(Long mealId, String recipeCode, Date date) {
        User currentUser = this.userService.getCurrentUser();
        List<Meal> meals = currentUser.getMeals();

        if (meals.stream().anyMatch(m -> !m.getId().equals(mealId) && m.getDate().equals(date))) {
            throw new BadRequestRuntimeException(Constants.FORM_PLAN_MEAL_DATE_NOT_AVAILABLE);
        }

        Meal meal = meals.stream().filter(m -> m.getId().equals(mealId)).findAny().orElseThrow(() -> new BadRequestRuntimeException(Constants.MEAL_NOT_FOUND));
        Recipe recipeByCode = this.recipeService.findByCode(recipeCode);

        meal.setRecipe(recipeByCode);
        meal.setDate(date);
    }

}
