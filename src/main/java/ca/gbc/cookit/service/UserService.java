package ca.gbc.cookit.service;

import ca.gbc.cookit.model.User;
import org.springframework.stereotype.Service;


public interface UserService {
    User addUser(String name, String username, String password, String question, String answer);
    User findByUsername(String username);
    User getCurrentUser();
    void addRecipeForCurrentUser(String name, String recipeCode, String description);
    void updateCurrentUser(String newUsername, String newName);
    void addRecipeAsFavoriteForCurrentUser(String recipeCode);
    void addIngredientToBasketForCurrentUser(Long ingredientId);
    void  removeIngredientFromBasketForCurrentUser(Long ingredientId);
    void removeIngredientFromAllIngredientLists(Long ingredientId);
    void resetPasswordForCurrentUser(String newPassword);
}
