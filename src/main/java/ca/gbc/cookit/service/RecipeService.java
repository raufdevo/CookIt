package ca.gbc.cookit.service;

import ca.gbc.cookit.model.Recipe;
import org.springframework.stereotype.Service;

import java.util.List;


public interface RecipeService {
    Recipe findRecipeByCode(String code);
    Recipe findRecipeByCodeForCurrentUser(String recipeCode);
    List<Recipe> findRecipeByName(String name);
    List<Recipe> findAllRecipes();
}
