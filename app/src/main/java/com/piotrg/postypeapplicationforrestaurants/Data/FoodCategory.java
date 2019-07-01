package com.piotrg.postypeapplicationforrestaurants.Data;

import java.util.ArrayList;
import java.util.List;

public class FoodCategory {
    private List<FoodProduct> foodProductsList;
    private String name;

    public FoodCategory(String name) {
        foodProductsList = new ArrayList<>();
        this.name = name;
    }
    public FoodProduct getFoodProduct(int index){
        return foodProductsList.get(index);
    }
    public void addToFoodProductsList(FoodProduct foodProduct) {
        this.foodProductsList.add(foodProduct);
    }
    public void removeFromFoodProductsList(FoodProduct foodProduct){
        foodProductsList.remove(foodProduct);
    }
    public void removeFromFoodProductsList(int index){
        foodProductsList.remove(index);
    }

    public int size(){
        return foodProductsList.size();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
