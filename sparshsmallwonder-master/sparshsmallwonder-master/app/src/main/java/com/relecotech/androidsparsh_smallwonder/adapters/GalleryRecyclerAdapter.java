package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.SchoolGalleryListData;

import java.util.List;

/**
 * Created by Amey on 05-04-2018.
 */

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder> {

    private List<SchoolGalleryListData> galleryListData;
    Context context;
    private SchoolGalleryListData fdggdgd;

    public GalleryRecyclerAdapter(List<SchoolGalleryListData> galleryListData, Context context) {
        this.galleryListData = galleryListData;
        this.context = context;
    }

    @Override
    public GalleryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_recycler_adapter_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GalleryRecyclerAdapter.ViewHolder holder, int position) {
        fdggdgd = galleryListData.get(position);
        holder.galleryDate.setText(fdggdgd.getGalleryPostDate());
        holder.galleryTitle.setText(fdggdgd.getGalleryTitle());
        holder.galleryCount.setText(String.valueOf(fdggdgd.getGalleryImageCount()));
        //  Picasso.with(context).load(new File(fdggdgd.getGalleryURL())).fit().into(holder.galleryImg);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView galleryDate;
        TextView galleryTitle;
        TextView galleryTag;
        TextView galleryCount;
        TextView galleryDesc;
        ImageView galleryImg;

        public ViewHolder(View itemView) {
            super(itemView);

            galleryImg = (ImageView) itemView.findViewById(R.id.gallery_ImageView);
            galleryDate = (TextView) itemView.findViewById(R.id.gallery_txt_Date);
            galleryTitle = (TextView) itemView.findViewById(R.id.gallery_txt_Title);
            galleryTag = (TextView) itemView.findViewById(R.id.gallery_tag_field);
            galleryCount = (TextView) itemView.findViewById(R.id.gallery_img_count);
        }
    }
}

