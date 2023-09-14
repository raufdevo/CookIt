package ca.gbc.cookit.service.map;


import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exception.BadRequestRuntimeException;
import ca.gbc.cookit.model.Recipe;
import ca.gbc.cookit.model.User;
import ca.gbc.cookit.repository.RecipeRepository;
import ca.gbc.cookit.service.RecipeService;
import ca.gbc.cookit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceMap implements RecipeService {
    private final RecipeRepository recipeRepository;
    private UserService userService;


    public RecipeServiceMap(RecipeRepository recipeRepository) {

        this.recipeRepository = recipeRepository;
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<Recipe> findAll() {
        Sort.TypedSort<Recipe> recipeTypedSort = Sort.sort(Recipe.class);
        Sort sort = recipeTypedSort.by(Recipe::getName).ascending();
        return this.recipeRepository.findAll(sort);
    }

    @Override
    public Recipe findByCode(String code) {
        Optional<Recipe> recipeByCodeOpt = this.recipeRepository.findByCode(code);

        return recipeByCodeOpt.orElseThrow(() -> new BadRequestRuntimeException(Constants.RECIPE_NOT_FOUND));
    }

    @Override
    public Recipe findRecipeByCodeForCurrentUser(String recipeCode) {
        User currentUser = this.userService.getCurrentUser();

        if (currentUser.getRecipes().stream().noneMatch(recipe -> recipe.getCode().equals(recipeCode))) {

            throw new BadRequestRuntimeException(Constants.RECIPE_CODE_DUPLICATE);
        }

        return this.findByCode(recipeCode);
    }

    @Override
    public List<Recipe> findByName(String name) {

        return this.recipeRepository.findByNameContainingIgnoreCase(name);
    }

}
