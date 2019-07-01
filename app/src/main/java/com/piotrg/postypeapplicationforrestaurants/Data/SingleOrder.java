package com.piotrg.postypeapplicationforrestaurants.Data;

import java.util.ArrayList;
import java.util.List;

public class SingleOrder {

    private ArrayList<OrderPosition> productsList;

    public SingleOrder(){
        this.productsList = new ArrayList<>();
    }

    public void addOrder(FoodProduct foodDish, String note){
        productsList.add(new OrderPosition(foodDish, note));
    }
    public void addOrder(FoodProduct foodDish){
        productsList.add(new OrderPosition(foodDish));
    }
    public void removeOrder(int pos){
        productsList.remove(pos);
    }
    public void editNote(int pos, String newNote){
        productsList.get(pos).setNote(newNote);
    }
    public double getTotalPrice(){
        double sum = 0;
        for(OrderPosition o : productsList){
            sum += o.getFoodProduct().getPrice();
        }
        return sum;
    }
    public OrderPosition getPosition(int index){
        return productsList.get(index);
    }
    public int size(){
            return productsList.size();
    }
    public ArrayList<OrderPosition> getProductsList() {
        return productsList;
    }



    public class OrderPosition {
        private FoodProduct foodProduct;
        private String note;

        public OrderPosition(FoodProduct foodProduct, String note){
            this.foodProduct = foodProduct;
            this.note = note;
        }

        public OrderPosition(FoodProduct foodDish){
            this.foodProduct = foodDish;
        }

        public FoodProduct getFoodProduct() {
            return foodProduct;
        }
        public String getNote() {
            return note;
        }
        public void setNote(String note){
            this.note=note;
        }
        public int showNote(){
            if(note != null) {
                return 1;
            }
            return 0;
        }
    }
}
