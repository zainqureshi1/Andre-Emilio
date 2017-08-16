package com.e2esp.andreemilio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.e2esp.andreemilio.R;
import com.e2esp.andreemilio.fragments.HomeFragment;
import com.e2esp.andreemilio.models.Home;

import java.util.ArrayList;

/**
 * Created by Ali on 8/10/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeItem> {


    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Home> adsList;


    public HomeAdapter(Context context, ArrayList<Home> adsList) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.adsList = adsList;

    }


    @Override
    public HomeAdapter.HomeItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.home_list_layout, parent, false);
        return new HomeItem(view);
    }

    @Override
    public void onBindViewHolder(HomeItem holder, int position) {

        holder.bindView(adsList.get(position));
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }


    public class HomeItem extends RecyclerView.ViewHolder {

        private ImageView imageViewCover;

        public HomeItem(View itemView) {
            super(itemView);

            imageViewCover = (ImageView) itemView.findViewById(R.id.imageView);

        }

        public void bindView(Home home) {
            imageViewCover.setImageResource(home.getAds());

        }
    }

}
