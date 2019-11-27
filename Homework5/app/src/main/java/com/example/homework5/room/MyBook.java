package com.example.homework5.room;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyBook {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String category;
    private int page;
    private double price;
    private String image;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public MyBook(String title, String category, int page, double price, String image) {
        this.title = title;
        this.category = category;
        this.page = page;
        this.price = price;
        this.image = image;
    }
}
