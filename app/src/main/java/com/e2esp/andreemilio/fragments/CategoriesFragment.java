package com.e2esp.andreemilio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e2esp.andreemilio.R;

/**
 * Created by Ali on 8/9/2017.
 */

import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e2esp.andreemilio.R;
import com.e2esp.andreemilio.adapters.CategoriesAdapter;
import com.e2esp.andreemilio.adapters.HomeAdapter;
import com.e2esp.andreemilio.data.AndreEmilioContract;
import com.e2esp.andreemilio.interfaces.DrawerCallbacks;
import com.e2esp.andreemilio.interfaces.NavigationDrawerCallbacks;
import com.e2esp.andreemilio.models.Categories;
import com.e2esp.andreemilio.models.orders.DrawerItem;
import com.e2esp.andreemilio.models.orders.DrawerSubItem;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Zain on 2/17/2017.
 */

public class CategoriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private View rootView;
    private RecyclerView categoriesRecyclerView;
    private ArrayList<Categories> categoriesList;
    private CategoriesAdapter categoriesAdapter;

    private static final int CATEGORY_LOADER = 3;
    private static final String[] CATEGORY_PROJECTION = {
            AndreEmilioContract.CategoryEntry.COLUMN_NAME,
            AndreEmilioContract.CategoryEntry.COLUMN_IMAGE
    };
    private int CATEGORY_COLUMN_NAME = 0;
    private int CATEGORY_COLUMN_IMAGE = 1;
    private static final String[] COUNT_PROJECTION = {
            "COUNT(*)"
    };
    private int COLUMN_COUNT = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        setUpView();

        return rootView;
    }

    public void setUpView(){

        categoriesRecyclerView = (RecyclerView) rootView.findViewById(R.id.categoriesRecyclerView);
        categoriesList = new ArrayList<>();

        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        categoriesAdapter = new CategoriesAdapter(getContext(), categoriesList);

        /*LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoriesRecyclerView.setLayoutManager(layoutManager);*/

        getActivity().getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        categoriesRecyclerView.setAdapter(categoriesAdapter);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;
        switch (id) {

            case CATEGORY_LOADER: {
                Uri categoryUri = AndreEmilioContract.CategoryEntry.CONTENT_URI;
                String sortOrder = AndreEmilioContract.CategoryEntry._ID + " ASC";
                cursorLoader = new CursorLoader(
                        getActivity().getApplicationContext(),
                        categoryUri,
                        CATEGORY_PROJECTION,
                        null,
                        null,
                        sortOrder);
            }
            break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        switch (cursorLoader.getId()) {
            case CATEGORY_LOADER: {
                if (cursor.moveToFirst()) {
                    //categoriesList.add(new Categories("ALL", 0));
                    do {
                        String name = cursor.getString(CATEGORY_COLUMN_NAME);
                        String image = cursor.getString(CATEGORY_COLUMN_IMAGE);

                        categoriesList.add(new Categories(name.toUpperCase(Locale.getDefault()), 0,image));
                    } while (cursor.moveToNext());
                    categoriesAdapter.notifyDataSetChanged();
                }
                cursor.close();
            }
            break;
            default:
                break;
        }
        categoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}