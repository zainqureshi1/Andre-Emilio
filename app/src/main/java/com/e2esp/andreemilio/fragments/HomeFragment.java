package com.e2esp.andreemilio.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e2esp.andreemilio.R;
import com.e2esp.andreemilio.adapters.HomeAdapter;
import com.e2esp.andreemilio.models.Home;

import java.util.ArrayList;

import static android.support.v7.widget.GridLayoutManager.*;

/**
 * Created by Ali on 8/9/2017.
 */

public class HomeFragment extends Fragment {

    private RecyclerView homeAdsRecyclerView;
    private ArrayList<Home> homeAds;
    private View rootView;
    private HomeAdapter homeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        setUpView();

        return rootView;
    }

    public void setUpView(){

        homeAdsRecyclerView = (RecyclerView) rootView.findViewById(R.id.homeAdsRecyclerView);
        homeAds = new ArrayList<>();

        homeAdapter = new HomeAdapter(getContext(), homeAds);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeAdsRecyclerView.setLayoutManager(layoutManager);
        homeAdsRecyclerView.setAdapter(homeAdapter);

        homeAds.add(new Home(R.drawable.ties));
        homeAds.add(new Home(R.drawable.cufflinks));
        homeAds.add(new Home(R.drawable.pocket));

        homeAdsRecyclerView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();

    }

}
