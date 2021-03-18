package com.trendingstocks.Service.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trendingstocks.Entity.Company;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public  interface CompanyDao {
    @Query("SELECT * FROM Company")
    List<Company> getAll();

    @Query("SELECT * FROM Company")
    Flowable<List<Company>> getAllFlowable();

    @Query("SELECT COUNT (*) FROM Company")
    int getCountCompany();

    @Query("SELECT * FROM Company WHERE ticker =:ticker")
    Company getByTicker(String ticker);

    @Query("SELECT * FROM Company WHERE favorite =:isFavorite")
    List<Company> getAll(boolean isFavorite);

    @Query("SELECT * FROM Company WHERE favorite =:isFavorite")
    Flowable<List<Company>>  getAllFlowable(boolean isFavorite);

    @Insert
    void insert(Company company);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Iterable<Company> companies);

    @Update
    void update(Company company);

    @Delete
    void delete(Company company);
}
