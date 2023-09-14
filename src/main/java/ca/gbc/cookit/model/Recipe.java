package ca.gbc.cookit.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;



@Entity
@Table(name = "recipe_table")
public class Recipe {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String code;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createdDate;


    //    private String description = "";
    @Column(nullable = false)
    private String description = "";
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public Date getCreatedDate() {

        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {

        this.createdDate = createdDate;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public List<Ingredient> getIngredients() {

        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {

        this.ingredients = ingredients;
    }
}
