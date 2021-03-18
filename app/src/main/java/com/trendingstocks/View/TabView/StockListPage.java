package com.trendingstocks.View.TabView;

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
import com.trendingstocks.View.StockListView.StockCompanyListAdapter;


public class StockListPage extends Fragment {

    RecyclerView companyRecyclerView;
    StockCompanyListAdapter stockCompanyListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stocklist_view, container,false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyRecyclerView = getActivity().findViewById(R.id.company_recycler_view);
        stockCompanyListAdapter = new StockCompanyListAdapter();
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyRecyclerView.setAdapter(stockCompanyListAdapter);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
