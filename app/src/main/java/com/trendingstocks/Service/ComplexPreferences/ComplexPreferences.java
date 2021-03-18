package com.trendingstocks.Service.ComplexPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendingstocks.Entity.Company;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ComplexPreferences {
    private static ComplexPreferences       complexPreferences;
    private final Context context;
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor  editor;
    private static Gson GSON = new Gson();
    Type typeOfObject    = new TypeToken<Object>(){}
            .getType();


    private ComplexPreferences(Context context, String namePreferences, int mode) {
        this.context = context;
        if (namePreferences == null || namePreferences.equals("")) {
            namePreferences = "ObjectPreferenceStorage";
        }
        preferences = context.getSharedPreferences(namePreferences, mode);
        editor = preferences.edit();
    }

    public static ComplexPreferences getComplexPreferences(Context context,
                                                           String namePreferences, int mode) {
        if (complexPreferences == null) {
            complexPreferences = new ComplexPreferences(context,
                    namePreferences, mode);
        }
        return complexPreferences;
    }

    public void putCompanies(String key, Object[] companies) {
        if (companies == null) {
            throw new IllegalArgumentException("Object is null");
        }
        if (key.equals("") || (key == null)) {
            throw new IllegalArgumentException("Key is empty or null");
        }
        editor.putString(key, GSON.toJson(companies));
        commit();
    }

    public void commit() {
        editor.commit();
    }



    public Company[] getCompanies(String key) {
        String gson = preferences.getString(key, null);
        if (gson == null) {
            return null;
        }
        else {
            try {

                return GSON.fromJson(gson,Company[].class);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key "
                        + key + " is instance of other class");
            }
        }
    } }