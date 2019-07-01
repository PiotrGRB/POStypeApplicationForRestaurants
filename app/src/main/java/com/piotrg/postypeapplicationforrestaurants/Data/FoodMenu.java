package com.piotrg.postypeapplicationforrestaurants.Data;

import java.util.ArrayList;
import java.util.List;

public class FoodMenu {
    private String name;
    private List<FoodCategory> foodCategoriesList = new ArrayList<>();

    public FoodMenu(String name){
        this.name=name;
    }
    public FoodMenu(){
        this.name="DefaultName";
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public int size(){
        return foodCategoriesList.size();
    }
    public FoodCategory getCategory(int index){
        return foodCategoriesList.get(index);
    }
    public void removeCategory(int index){
        foodCategoriesList.remove(index);
    }
    public void addCategory(FoodCategory foodCategory){
        foodCategoriesList.add(foodCategory);
    }


    public void createDefaultFoodMenu() {
        foodCategoriesList.clear();

        this.name = "ExampleRestaurantName";
        FoodCategory cat1 = new FoodCategory("Przystawki");
        cat1.addToFoodProductsList(new FoodProduct("Grzanki czosnkowe", 8.00));
        cat1.addToFoodProductsList(new FoodProduct("Orzeszki ziemne", 6.00));
        cat1.addToFoodProductsList(new FoodProduct("Paluchy serowe", 7.00));
        cat1.addToFoodProductsList(new FoodProduct("Deska serów", 12.00));

        FoodCategory cat2 = new FoodCategory("Zupy");
        cat2.addToFoodProductsList(new FoodProduct("Ogórkowa", 10.00));
        cat2.addToFoodProductsList(new FoodProduct("Pomidorowa", 10.00));
        cat2.addToFoodProductsList(new FoodProduct("Rybna", 10.00));
        cat2.addToFoodProductsList(new FoodProduct("Rosół", 10.00));
        cat2.addToFoodProductsList(new FoodProduct("Grzybowa", 10.00));

        FoodCategory cat3 = new FoodCategory("Desery");
        cat3.addToFoodProductsList(new FoodProduct("Lodowe szaleństwo", 11.00));
        cat3.addToFoodProductsList(new FoodProduct("Gorące maliny", 13.00));
        cat3.addToFoodProductsList(new FoodProduct("Bananowy split", 10.00));
        cat3.addToFoodProductsList(new FoodProduct("Truskawki w gorącej czekoladzie", 10.00));
        foodCategoriesList.add(cat1);
        foodCategoriesList.add(cat2);
        foodCategoriesList.add(cat3);
    }
}
