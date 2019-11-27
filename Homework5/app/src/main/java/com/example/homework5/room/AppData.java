package com.example.homework5.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MyBook.class,Category.class},version = 1)
public abstract class AppData extends RoomDatabase {
    public abstract DaoAPI daoAPI();
}
