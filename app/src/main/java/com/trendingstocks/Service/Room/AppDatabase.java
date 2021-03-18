package com.trendingstocks.Service.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.trendingstocks.Entity.Company;

@Database(entities = {Company.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CompanyDao companyDao();
}