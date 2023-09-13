package ca.gbc.cookit.service;

import ca.gbc.cookit.dto.MealDto;
import ca.gbc.cookit.model.Meal;

import java.text.SimpleDateFormat;
import java.util.Date;


public interface MealService {

    static MealDto mapToDto(Meal meal){
      MealDto mealDto = new MealDto();
      mealDto.setId(meal.getId());
      mealDto.setRecipeCode(meal.getRecipe().getCode());

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
      String formattedDate = simpleDateFormat.format(meal.getDate());
      mealDto.setFormattedDate(formattedDate);

      return mealDto;
    };

    Meal getMealByIdForCurrentUser(Long mealId);
    void addMealForCurrentUser(String recipeCode, Date date);
    void deleteMealForCurrentUser(Long mealId);
    void editMealForCurrentUser(Long mealId, String recipeCode, Date date);

}
