package com.relecotech.androidsparsh_smallwonder.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.SchoolGalleryListData;
import com.relecotech.androidsparsh_smallwonder.utils.Listview_communicator;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Amey on 06-04-2018.
 */

public class GalleryAdapter extends BaseAdapter {

    protected List<SchoolGalleryListData> galleryList;
    Context context;
    LayoutInflater inflater;
    ViewHolder holder;

    public GalleryAdapter(Context context, List<SchoolGalleryListData> galleryList) {
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


    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = this.inflater.inflate(R.layout.gallery_list_item, parent, false);

            holder.galleryImg = (ImageView) convertView.findViewById(R.id.gallery_ImageView);
            holder.galleryDate = (TextView) convertView.findViewById(R.id.gallery_txt_Date);
            holder.galleryTitle = (TextView) convertView.findViewById(R.id.gallery_txt_Title);
            holder.galleryCount = (TextView) convertView.findViewById(R.id.gallery_img_count);
            holder.galleryCategory = (TextView) convertView.findViewById(R.id.gallery_tag_field);
            holder.list_item_spam_tv = (TextView) convertView.findViewById(R.id.list_item_spam_tv);
            holder.galleryDesc = (TextView) convertView.findViewById(R.id.gallery_txt_Body);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final SchoolGalleryListData galleryListData = galleryList.get(position);
        holder.galleryDate.setText("Post On : " + galleryListData.getGalleryPostDate());
        holder.galleryTitle.setText(galleryListData.getGalleryTitle());
        holder.galleryCount.setText("Image Count : " + String.valueOf(galleryListData.getGalleryImageCount()));
        holder.galleryCategory.setText("Posted By : " + galleryListData.getGalleryCategory());
        holder.galleryDesc.setText(galleryListData.getGalleryDescription());

        try {
            System.out.println("Before Picasso Set ");
            Picasso.with(context)

                    .load(galleryListData.getImageUrlToDownloadImage())
                    .resize(500, 500)
                    .centerCrop()
                    .into(holder.galleryImg);

            System.out.println("After Picasso Set ");
        } catch (Exception e) {
            System.out.println("Picasso error");
        }
      /*  if (galleryListData.getUserRole().equals("Student")) {
            holder.list_item_spam_tv.setVisibility(View.INVISIBLE);
        } else {
            holder.list_item_spam_tv.setVisibility(View.VISIBLE);
        }*/

        holder.list_item_spam_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("position------------   " + position);
                ((Listview_communicator) context).listViewOnclick(position, 3);

            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView galleryDate;
        TextView galleryTitle;
        TextView galleryCount;
        TextView galleryDesc;
        ImageView galleryImg;
        ImageView moreIcon;
        TextView galleryCategory;
        TextView list_item_spam_tv;
    }

}

