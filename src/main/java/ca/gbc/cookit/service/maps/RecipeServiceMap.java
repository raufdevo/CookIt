package ca.gbc.cookit.service.maps;

import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exceptions.BadRequestRuntimeException;
import ca.gbc.cookit.model.Recipe;
import ca.gbc.cookit.model.User;
import ca.gbc.cookit.repository.RecipeRepository;
import ca.gbc.cookit.service.RecipeService;
import ca.gbc.cookit.service.UserService;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceMap implements RecipeService {
    private  UserService userService;
    private final RecipeRepository recipeRepository;

    public RecipeServiceMap(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public Recipe findRecipeByCode(String code) {
      Optional <Recipe> recipeByCodeOpt = this.recipeRepository.findRecipeByCode(code);
      return recipeByCodeOpt.orElseThrow(()-> new BadRequestRuntimeException(Constants.RECIPE_NOT_FOUND));
    }

    @Override
    public Recipe findRecipeByCodeForCurrentUser(String recipeCode) {
        User currentUser = this.userService.getCurrentUser();

        if (currentUser.getRecipes().stream().noneMatch(recipe -> recipe.getCode().equals(recipeCode))) {

            throw new BadRequestRuntimeException(Constants.RECIPE_CODE_DUPLICATE);
        }

        return this.findRecipeByCode(recipeCode);
    }

    @Override
    public List<Recipe> findRecipeByName(String name) {
        return this.recipeRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Recipe> findAllRecipes(){
        Sort.TypedSort<Recipe> recipeTypedSortOpt = Sort.sort(Recipe.class);
        Sort sortedRecipes = recipeTypedSortOpt.by(Recipe::getName).ascending();
        return this.recipeRepository.findAll(sortedRecipes);
    }

}
