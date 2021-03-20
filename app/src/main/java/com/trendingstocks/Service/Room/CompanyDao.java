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
    @Query("SELECT * FROM Company ORDER BY currentPrice DESC ")
    List<Company> getAll();

    @Query("SELECT * FROM Company ORDER BY currentPrice DESC")
    Flowable<List<Company>> getAllFlowable();

    @Query("SELECT COUNT (*) FROM Company ORDER BY currentPrice DESC")
    int getCountCompany();

    @Query("SELECT * FROM Company WHERE ticker =:ticker")
    Company getByTicker(String ticker);

    @Query("SELECT * FROM Company WHERE isFavorite =:isFavorite ORDER BY currentPrice DESC")
    List<Company> getAll(boolean isFavorite);

    @Query("SELECT * FROM Company WHERE isFavorite =:isFavorite ORDER BY currentPrice DESC")
    Flowable<List<Company>>  getAllFlowable(boolean isFavorite);


    @Query("SELECT * FROM Company WHERE isSearchResult = 1 ORDER BY currentPrice DESC")
    List<Company> getAllSearch();

    @Query("SELECT * FROM Company WHERE isSearchResult = 1 ORDER BY currentPrice DESC")
    Flowable<List<Company>> getAllSearchFlowable();

    @Query("UPDATE Company SET isSearchResult = 0 WHERE isSearchResult = 1 AND isFavorite = 1")
    void saveFavoriteSearch();

    @Query("DELETE FROM Company WHERE isSearchResult = 1")
    void deleteNonFavoriteSearch();



    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Company company);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Iterable<Company> companies);

    @Update
    void update(Company company);

    @Query("UPDATE Company SET prevClosePrice = :prevClosePrice, currentPrice =:currPrice WHERE ticker= :ticker")
    void setStock(String ticker, double prevClosePrice, double currPrice);

    @Delete
    void delete(Company company);


}
