package com.relecotech.androidsparsh_smallwonder.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Amey on 06-04-2018.
 */

public class GalleryPhotoPickerAdapter extends RecyclerView.Adapter<GalleryPhotoPickerAdapter.FileViewHolder> {

    private final ArrayList<String> paths;
    private final ArrayList<String> selectedFileNameList;
    private final Context context;
    private int imageSize;
    private ItemClickListener clickListener;

    public GalleryPhotoPickerAdapter(Context context, ArrayList<String> paths, ArrayList<String> selectedFileNameList) {
        this.context = context;
        this.paths = paths;
        this.selectedFileNameList = selectedFileNameList;
    }


    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_picker_layout, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        String path = paths.get(position);
        holder.titleTextView.setText(selectedFileNameList.get(position));
        //  Picasso.with(context).load(new File(path)).fit().into(holder.imageView);
        Picasso.with(context)

                .load(new File(path))
                .resize(500, 500)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView titleTextView;


        public FileViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            titleTextView = (TextView) itemView.findViewById(R.id.imageFileNameTextView);
            titleTextView.setTag(itemView);
            titleTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }
}


