package com.example.graduationonlineshop.Model;

public class Product {
    public String name, image, description, price, productId, storeId;
    public Product() {
    }

    public Product(String name, String image, String description, String price, String productId, String storeId) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.productId = productId;
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
