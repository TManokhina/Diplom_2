package site.nomoreparties.stellarburger.ingredient;

import java.util.List;

public class IngredientRequest {

    private List<String> ingredients;

    public IngredientRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public IngredientRequest() {
    }



    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

}
