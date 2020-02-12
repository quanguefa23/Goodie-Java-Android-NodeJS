package com.nhq.goodie.Class;

public class ShortProduct {
    String id;
    String title;
    String price;
    String img;

    public ShortProduct(String id, String title, String price, String img) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    public ShortProduct getDuplicate() {
        return new ShortProduct(id, title, price, img);
    }
}
