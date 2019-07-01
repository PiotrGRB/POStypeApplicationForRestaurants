package com.piotrg.postypeapplicationforrestaurants.Data;

public class FoodProduct {
    private String name;
    private double price;

    public FoodProduct(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public FoodProduct() {
        this.name = "Dish";
        this.price = 0.0;
    }
    public FoodProduct(String Name) {
        this.name = Name;
        this.price = 0.0;
    }
    public FoodProduct(String Name, double price) {
        this.name = Name;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
