package com.trendingstocks.View.TabView;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.R;
import com.trendingstocks.Service.App;
import com.trendingstocks.View.StockListView.FavorCompanyListAdapter;

import java.util.ArrayList;

public class FavoriteListPage extends Fragment {


    RecyclerView companyRecyclerView;
    FavorCompanyListAdapter favorCompanyListAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favor_list_view, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        companyRecyclerView = getActivity().findViewById(R.id.favorite_recycler_view);
        favorCompanyListAdapter = new FavorCompanyListAdapter();
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyRecyclerView.setAdapter(favorCompanyListAdapter);

         new AsyncTask() {
            @Override
            protected Void doInBackground(Object[] objects) {
                favorCompanyListAdapter.companyList = new ArrayList<>(App.getInstance().getDatabase().companyDao().getAll(true));
                return  null;
            }

             @Override
             protected void onPostExecute(Object o) {
                 favorCompanyListAdapter.notifyDataSetChanged();
             }
         }.execute();
        //добавление данных в recyclerView


    }

}
