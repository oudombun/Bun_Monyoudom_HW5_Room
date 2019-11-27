package com.example.homework5.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "cateName")
    private String cateName;

    public Category(String cateName) {
        this.cateName = cateName;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", cateName='" + cateName + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }
}
