package com.example.homework5.recycler;

import android.graphics.Bitmap;
import android.net.Uri;

public class Book {
    private String title;
    private int page;
    private String category;
    private double price;
    private Uri image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public Book(String title, int page, String category, double price, Uri image) {
        this.title = title;
        this.page = page;
        this.category = category;
        this.price = price;
        this.image = image;
    }
}
