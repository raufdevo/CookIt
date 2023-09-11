package ca.gbc.cookit.repository;

import ca.gbc.cookit.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {
}
