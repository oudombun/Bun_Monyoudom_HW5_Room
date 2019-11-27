package com.example.homework5.room;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface DaoAPI {

    @Insert
    void insertBook(MyBook book);

    @Query("select * from mybook")
    List<MyBook> getAllBook();

    @Delete
    void deleteBook(MyBook book);

    @Update
    void updateBook(MyBook book);

    @Query("select * from category")
    List<Category> getAllCategory();

    @Insert
    void insertCate(Category category);
}
