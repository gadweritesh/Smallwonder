package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.SchoolGalleryListData;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Amey on 06-04-2018.
 */

public class GalleryItemListAdapter extends BaseAdapter {

    protected List<SchoolGalleryListData> galleryList;
    Context context;
    LayoutInflater inflater;

    public GalleryItemListAdapter(Context context, List<SchoolGalleryListData> galleryList) {
        this.galleryList = galleryList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return galleryList.size();
    }

    public SchoolGalleryListData getItem(int position) {
        return galleryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.gallery_detail_list_item, parent, false);
            holder.galleryImg = (ImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SchoolGalleryListData galleryListData = galleryList.get(position);

        try {
            System.out.println("Before Picasso Set ");
            Picasso.with(context)

                    .load(galleryListData.getImageUrlToDownloadImage())
                    .resize(350, 350)
                    .into(holder.galleryImg);

            System.out.println("After Picasso Set ");
        } catch (Exception e) {
            System.out.println("Picasso error");
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView galleryImg;
    }

}
