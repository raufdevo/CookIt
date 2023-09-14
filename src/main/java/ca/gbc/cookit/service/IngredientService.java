package ca.gbc.cookit.service;



import ca.gbc.cookit.model.Ingredient;

import java.math.BigDecimal;


public interface IngredientService {

    Ingredient findById(Long id);

    void addIngredientForCurrentUser(String name, BigDecimal price, String recipeCode);

    void removeIngredientForCurrentUser(Long ingredientId, String recipeCode);

    Ingredient updateIngredientForCurrentUser(Long ingredientId, String recipeCode, String name, BigDecimal price);

    Ingredient findIngredientByIdForCurrentUser(String recipeCode, Long ingredientId);

}
