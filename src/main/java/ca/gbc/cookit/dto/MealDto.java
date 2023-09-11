package ca.gbc.cookit.dto;

import ca.gbc.cookit.model.Recipe;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * DTO for {@link ca.gbc.cookit.model.Meal}
 */
public class MealDto implements Serializable {
    private final Long id;
    private final Recipe recipe;
    private final Date date;

    public MealDto(Long id, Recipe recipe, Date date) {
        this.id = id;
        this.recipe = recipe;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealDto entity = (MealDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.recipe, entity.recipe) &&
                Objects.equals(this.date, entity.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recipe, date);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "recipe = " + recipe + ", " +
                "date = " + date + ")";
    }
}