package com.e2esp.andreemilio.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e2esp.andreemilio.R;
import com.e2esp.andreemilio.expandablerecyclerview.ChildViewHolder;
import com.e2esp.andreemilio.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.e2esp.andreemilio.expandablerecyclerview.ParentViewHolder;
import com.e2esp.andreemilio.interfaces.CategoriesCallbacks;
import com.e2esp.andreemilio.interfaces.DrawerCallbacks;
import com.e2esp.andreemilio.models.Categories;
import com.e2esp.andreemilio.models.Home;
import com.e2esp.andreemilio.models.orders.DrawerItem;
import com.e2esp.andreemilio.models.orders.DrawerSubItem;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ali on 8/10/2017.
 */



public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesItem>{

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Categories> categoryList;
    private CategoriesCallbacks categoriesCallbacks;


    public CategoriesAdapter(Context context, ArrayList<Categories> categoryList,CategoriesCallbacks categoriesCallbacks) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.categoryList = categoryList;
        this.categoriesCallbacks = categoriesCallbacks;

    }

    @Override
    public CategoriesItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.categories_list_layout, parent, false);
        return new CategoriesAdapter.CategoriesItem(view);
    }

    @Override
    public void onBindViewHolder(CategoriesItem holder, int position) {

        try {
            holder.bindView(categoryList.get(position),holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }



    public class CategoriesItem extends RecyclerView.ViewHolder {

        private TextView textView;
        private TextView countView;
        private ImageView imageView;

        public CategoriesItem(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.label);
            countView = (TextView) itemView.findViewById(R.id.count);
            imageView = (ImageView) itemView.findViewById(R.id.icon);

        }

        //set all the views here
        public void bindView(final Categories category, CategoriesItem holder) throws IOException {

            textView.setText(category.getSection());
            countView.setText(category.getCount()+"");

            if(category.getIcon() != null && category.getIcon().length() > 0) {
                Log.d("Category Image", "Image: " + category.getIcon());
                Picasso.with(context)
                        .load(category.getIcon())
                        .resize(300, 300)
                        .centerCrop()
                        .placeholder(android.R.color.transparent)
                        .error(R.drawable.ic_action_cancel)
                        .into(holder.imageView);
            }
            else {

                imageView.setImageResource(R.drawable.ic_action_cancel);

            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoriesCallbacks.onCategorySelected(getAdapterPosition(),category.getSection());
                }
            });


        }
    }

}