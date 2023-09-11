package ca.gbc.cookit.repository;

import ca.gbc.cookit.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
