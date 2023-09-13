package ca.gbc.cookit.service.maps;

import ca.gbc.cookit.authentication.CustomUserDetails;
import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exceptions.BadRequestRuntimeException;
import ca.gbc.cookit.model.Ingredient;
import ca.gbc.cookit.model.Recipe;
import ca.gbc.cookit.model.User;
import ca.gbc.cookit.repository.IngredientRepository;
import ca.gbc.cookit.repository.UserRepository;
import ca.gbc.cookit.service.IngredientService;
import ca.gbc.cookit.service.RecipeService;
import ca.gbc.cookit.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class UserServiceMap implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    public UserServiceMap(PasswordEncoder passwordEncoder, UserRepository userRepository, RecipeService recipeService, IngredientService ingredientService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }


    @Override
    public User addUser(String name, String username, String password, String question, String answer) {
        Optional<User> userByUsernameOpt = this.userRepository.findByUsername(username);

        if (userByUsernameOpt.isPresent()) {
            throw new BadRequestRuntimeException(Constants.USER_USERNAME_DUPLICATE);
        }
        String encodedPassword = this.passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setQuestion(question);
        newUser.setAnswer(answer);

        return this.userRepository.save(newUser);
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> userByUsernameOpt = this.userRepository.findByUsername(username);

        return userByUsernameOpt.orElseThrow(() -> new BadRequestRuntimeException(Constants.USER_NOT_FOUND));
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return this.findByUsername(userDetails.getUser().getUsername());
    }

    @Override
    public void addRecipeForCurrentUser(String name, String recipeCode, String description) {
        try {
            this.recipeService.findRecipeByCode(recipeCode);
            throw new BadRequestRuntimeException(Constants.RECIPE_CODE_DUPLICATE);
        } catch (BadRequestRuntimeException badRequestRuntimeException) {
            if (!badRequestRuntimeException.getMessageCode().equals(Constants.RECIPE_NOT_FOUND)) {
                throw badRequestRuntimeException;
            }
        }

        Recipe recipe = new Recipe();
        recipe.setCreatedDate(new Date());
        recipe.setName(name);
        recipe.setCode(recipeCode);
        recipe.setDescription(description);

        User currentUser = this.getCurrentUser();

        currentUser.getRecipes().add(recipe);

        this.userRepository.save(currentUser);
    }

    @Override
    public void updateCurrentUser(String newUsername, String newName) {
        User currentUser = this.getCurrentUser();

        if (!newUsername.equals(currentUser.getUsername())) {
            Optional<User> userByNewUsernameTemp = this.userRepository.findByUsername(newUsername);
            userByNewUsernameTemp.ifPresent((userByNewUsername) -> {
                throw new BadRequestRuntimeException(Constants.USER_USERNAME_DUPLICATE);
            });
        }

        currentUser.setName(newName);
        currentUser.setUsername(newUsername);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        userDetails.getUser().setName(newName);
        userDetails.getUser().setUsername(newUsername);

        this.userRepository.save(currentUser);
    }

    @Override
    public void addRecipeAsFavoriteForCurrentUser(String recipeCode) {
        User currentUser = this.getCurrentUser();

        if (currentUser.getFavoriteRecipes().stream().map(Recipe::getCode).anyMatch(Predicate.isEqual(recipeCode))) {
            return;
        }

        Recipe recipe = this.recipeService.findRecipeByCode(recipeCode);
        currentUser.getFavoriteRecipes().add(recipe);

        this.userRepository.save(currentUser);
    }

    @Override
    public void addIngredientToBasketForCurrentUser(Long ingredientId) {
        Ingredient ingredient = this.ingredientService.findById(ingredientId);
        User currentUser = this.getCurrentUser();

        currentUser.getBasket().add(ingredient);

        this.userRepository.save(currentUser);
    }

    @Override
    public void removeIngredientFromBasketForCurrentUser(Long ingredientId) {
        User currentUser = this.getCurrentUser();
        List<Ingredient> basket = currentUser.getBasket();
        for (Ingredient item : basket) {
            if (item.getId().equals(ingredientId)) {
                basket.remove(item);
                break;
            }
        }

        this.userRepository.save(currentUser);
    }

    @Override
    public void removeIngredientFromAllIngredientLists(Long ingredientId) {
        this.userRepository.findAll().forEach(
                user -> user.getBasket().removeIf(
                        ingredient -> ingredient.getId().equals(ingredientId)
                )
        );
    }

    @Override
    public void resetPasswordForCurrentUser(String newPassword) {
        User user = this.getCurrentUser();
        String encodedPassword = this.passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
    }
}
