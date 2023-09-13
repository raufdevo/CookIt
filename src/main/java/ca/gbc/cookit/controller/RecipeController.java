package ca.gbc.cookit.controller;

import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exceptions.BadRequestRuntimeException;
import ca.gbc.cookit.model.Recipe;
import ca.gbc.cookit.model.Ingredient;
import ca.gbc.cookit.service.IngredientService;
import ca.gbc.cookit.service.RecipeService;
import ca.gbc.cookit.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RecipeController {
    private final UserService userService;
    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    private final Environment environment;

    public RecipeController(UserService userService, RecipeService recipeService, IngredientService ingredientService, Environment environment) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.environment = environment;
    }

    @GetMapping("add-recipe")
    public String addRecipe() {

        return "recipe/add_recipe";
    }

    @PostMapping("add-recipe")
    public String addRecipe(Model model, HttpServletResponse httpServletResponse, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "code", required = false) String code, @RequestParam(value = "description", defaultValue = "") String description) {

        List<String> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_ADD_RECIPE_EMPTY_NAME);
            errors.add(message);
        }
        if (code == null || code.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_ADD_RECIPE_EMPTY_CODE);
            errors.add(message);
        }
        if (errors.isEmpty()) {
            try {
                this.userService.addRecipeForCurrentUser(name, code, description);
                httpServletResponse.setHeader("Location", "/recipes");
                httpServletResponse.setStatus(302);
            } catch (BadRequestRuntimeException badRequestRuntimeException) {
                String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
                errors.add(message);
            }
        }

        model.addAttribute("errors", errors);

        return "recipe/add_recipe";
    }

    @GetMapping("/recipes")
    public String getAllRecipes(Model model) {

        List<Recipe> recipes = this.recipeService.findAllRecipes();

        model.addAttribute("recipes", recipes);

        return "recipe/recipes";
    }


    @GetMapping("/search-recipe")
    public String searchRecipe(Model model, @RequestParam(value = "query", required = false) String query) {
        if (query == null || query.isEmpty()) {
            model.addAttribute("result", false);
        } else {
            List<Recipe> recipes = this.recipeService.findRecipeByName(query);
            model.addAttribute("recipes", recipes);
            model.addAttribute("result", true);
        }

        return "recipe/search";
    }


    @GetMapping("/add-ingredient")
    public String addIngredient() {


        return "recipe/add_ingredient";
    }

    @PostMapping("/add-ingredient")
    public String planMeal(Model model, HttpServletResponse httpServletResponse, @RequestParam(value = "recipe_code", required = false) String recipeCode, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "price", required = false) BigDecimal price) {
        List<String> errors = new ArrayList<>();

        if (recipeCode == null || recipeCode.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_ADD_INGREDIENT_EMPTY_RECIPE_CODE);
            errors.add(message);
        }
        if (name == null || name.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_ADD_INGREDIENT_EMPTY_NAME);
            errors.add(message);
        }
        if (price == null) {
            String message = this.environment.getProperty(Constants.FORM_ADD_INGREDIENT_EMPTY_PRICE);
            errors.add(message);
        }
        if (errors.isEmpty()) {
            try {
                this.ingredientService.addIngredientForCurrentUser(name, price, recipeCode);
                httpServletResponse.setHeader("Location", "/recipe/" + recipeCode + "/ingredients");
                httpServletResponse.setStatus(302);
            } catch (BadRequestRuntimeException badRequestRuntimeException) {
                String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
                errors.add(message);
            }
        }

        model.addAttribute("errors", errors);

        return "recipe/add_ingredient";
    }

    @GetMapping("/recipe/{recipeCode}/ingredients")
    public String getIngredients(Model model, @PathVariable("recipeCode") String recipeCode) {

        Recipe recipe = this.recipeService.findRecipeByCode(recipeCode);
        model.addAttribute("ingredients", recipe.getIngredients());
        model.addAttribute("recipeCode", recipeCode);
        return "recipe/ingredients";
    }

    @GetMapping("/recipe/{recipeCode}/ingredients/{ingredientId}/remove")
    public void removeIngredient(HttpServletResponse httpServletResponse, @PathVariable("recipeCode") String recipeCode, @PathVariable("ingredientId") Long ingredientId) {
        this.ingredientService.removeIngredientForCurrentUser(ingredientId, recipeCode);
        httpServletResponse.setHeader("Location", "/recipe/" + recipeCode + "/ingredients");
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/recipe/{recipeCode}/ingredients/{ingredientId}/edit")
    public String editIngredient(Model model, @PathVariable("recipeCode") String recipeCode, @PathVariable("ingredientId") Long ingredientId) {
        Ingredient ingredient = this.ingredientService.findIngredientByIdForCurrentUser(recipeCode, ingredientId);

        model.addAttribute("ingredient", ingredient);
        model.addAttribute("recipeCode", recipeCode);

        return "recipe/edit_ingredient";
    }

    @PostMapping("/recipe/{recipeCode}/ingredients/{ingredientId}/edit")
    public String editIngredient(Model model, HttpServletResponse httpServletResponse, @PathVariable("recipeCode") String recipeCode, @PathVariable("ingredientId") Long ingredientId, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "price", required = false) BigDecimal price) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            String message = this.environment.getProperty(Constants.FORM_ADD_INGREDIENT_EMPTY_NAME);
            errors.add(message);
        }

        if (price == null) {
            String message = this.environment.getProperty(Constants.FORM_ADD_INGREDIENT_EMPTY_PRICE);
            errors.add(message);
        }

        if (errors.isEmpty()) {
            try {
                this.ingredientService.updateIngredientForCurrentUser(ingredientId, recipeCode, name, price);
                httpServletResponse.setHeader("Location", "/recipe/" + recipeCode + "/ingredients");
                httpServletResponse.setStatus(302);
            } catch (BadRequestRuntimeException badRequestRuntimeException) {
                String message = this.environment.getProperty(badRequestRuntimeException.getMessageCode());
                errors.add(message);
            }
        }

        model.addAttribute("errors", errors);

        Ingredient ingredient = this.ingredientService.findIngredientByIdForCurrentUser(recipeCode, ingredientId);
        model.addAttribute("ingredient", ingredient);
        model.addAttribute("recipeCode", recipeCode);

        return "recipe/edit_ingredient";
    }

}

