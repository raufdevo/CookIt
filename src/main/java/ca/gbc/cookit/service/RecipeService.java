package ca.gbc.cookit.service;


import ca.gbc.cookit.model.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe findByCode(String code);

    Recipe findRecipeByCodeForCurrentUser(String recipeCode);


    List<Recipe> findByName(String name);

    List<Recipe> findAll();
}
