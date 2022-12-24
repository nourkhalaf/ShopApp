package com.example.graduationonlineshop.Model;

public class StoreOwner extends Customer {
    public String id, name, password, storeName, phone, email, image;

    public StoreOwner() {
    }

    public StoreOwner(String id, String name, String password, String storeName, String phone, String email, String image) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.storeName = storeName;
        this.phone = phone;
        this.email = email;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
