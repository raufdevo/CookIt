package ca.gbc.cookit.service;


import ca.gbc.cookit.model.User;

public interface UserService {

    User register(String name, String username, String plainPassword, String question, String answer);

    User findByUsername(String username);

    User getCurrentUser();

    void addRecipeForCurrentUser(String name, String code, String description);

    void updateCurrentUser(String newUsername, String newName);

    void addRecipeAsFavoriteForCurrentUser(String recipeCode);

    void addToBasketForCurrentUser(Long ingredientId);

    void removeFromBasketForCurrentUser(Long ingredientId);


    void removeFromAllIngredientLists(Long ingredientId);


    void resetPasswordForCurrentUser(String newPassword);

}
