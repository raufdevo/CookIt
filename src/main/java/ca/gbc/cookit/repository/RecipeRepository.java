package ca.gbc.cookit.repository;


import ca.gbc.cookit.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByCode(String code);

    List<Recipe> findByNameContainingIgnoreCase(String name);
}
