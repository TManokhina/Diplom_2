package site.nomoreparties.stellarburger.ingredient;

import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("_id")
    private String id;

    public String getId() {
        return id;
    }
}
