package ca.gbc.cookit.service.maps;

import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exceptions.BadRequestRuntimeException;
import ca.gbc.cookit.model.Ingredient;
import ca.gbc.cookit.model.Recipe;
import ca.gbc.cookit.repository.IngredientRepository;
import ca.gbc.cookit.service.IngredientService;
import ca.gbc.cookit.service.RecipeService;
import ca.gbc.cookit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class IngredientServiceMap implements IngredientService {
    private final IngredientRepository ingredientRepository;
    private final RecipeService recipeService;
    private UserService userService;

    public IngredientServiceMap(IngredientRepository ingredientRepository, RecipeService recipeService) {
        this.ingredientRepository = ingredientRepository;
        this.recipeService = recipeService;
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public Ingredient findById(Long id) {
        Optional<Ingredient> ingredientOptional = this.ingredientRepository.findById(id);
        return ingredientOptional.orElseThrow(() -> new BadRequestRuntimeException(Constants.INGREDIENT_NOT_FOUND));
    }

    @Override
    @Transactional
    public void addIngredientForCurrentUser(String name, BigDecimal price, String recipeCode) {
        Recipe recipe = this.recipeService.findRecipeByCodeForCurrentUser(recipeCode);

        if (recipe.getIngredients().stream().map(Ingredient::getName).anyMatch(Predicate.isEqual(name))){
            throw new BadRequestRuntimeException(Constants.INGREDIENT_NAME_DUPLICATE);
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setPrice(price);
        recipe.getIngredients().add(ingredient);

    }

    @Override
    public void removeIngredientForCurrentUser(Long ingredientId, String recipeCode) {

        Recipe recipe = this.recipeService.findRecipeByCodeForCurrentUser(recipeCode);
        boolean result = recipe.getIngredients().removeIf(ingredient -> ingredient.getId().equals(ingredientId));

        if(!result){
            throw new BadRequestRuntimeException(Constants.INGREDIENT_NOT_FOUND);
        }
        this.userService.removeIngredientFromBasketForCurrentUser(ingredientId);
    }

    @Override
    public Ingredient updateIngredientForCurrentUser(Long ingredientId, String recipeCode, String name, BigDecimal price) {
        Ingredient ingredient = this.findIngredientByIdForCurrentUser(recipeCode, ingredientId);
        ingredient.setName(name);
        ingredient.setPrice(price);

        return ingredient;
    }

    @Override
    public Ingredient findIngredientByIdForCurrentUser(String recipeCode, Long ingredientId) {
        Recipe recipe = this.recipeService.findRecipeByCodeForCurrentUser(recipeCode);

        return recipe.getIngredients().stream().filter(ingredient -> ingredient.getId().equals(ingredientId)).findAny().orElseThrow(() -> new BadRequestRuntimeException(Constants.INGREDIENT_NOT_FOUND));
    }
}
