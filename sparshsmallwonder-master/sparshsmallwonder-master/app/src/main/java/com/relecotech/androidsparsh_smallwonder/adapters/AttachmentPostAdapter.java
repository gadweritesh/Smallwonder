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
 * Created by Amey on 28-03-2018.
 */

public class AttachmentPostAdapter extends RecyclerView.Adapter<AttachmentPostAdapter.FileViewHolder> {

    private final ArrayList<String> paths;
    private final ArrayList<String> selectedFileNameList;
    private final Context context;
    private ItemClickListener clickListener;

    public AttachmentPostAdapter(Context context, ArrayList<String> paths, ArrayList<String> selectedFileNameList) {
        this.context = context;
        this.paths = paths;
        this.selectedFileNameList = selectedFileNameList;
    }


    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_attachment_list_item, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        String path = paths.get(position);
        holder.titleTextView.setText(selectedFileNameList.get(position));
        System.out.println("path------------------ Attachment Post Adapter--- " + path);
        if (path.contains(".pdf")) {
            System.out.println("Check Pass for Pdf");
            holder.attachmentImageView.setVisibility(View.GONE);
            holder.attachmentIconImageView.setImageResource(R.drawable.ic_picture_as_pdf_red_600_24dp);

        } else if (path.contains(".doc") || path.contains(".docx") || path.contains(".txt")) {
            System.out.println("Check Pass for Doc,Docx,Txt");
            holder.attachmentImageView.setVisibility(View.GONE);
            holder.attachmentIconImageView.setImageResource(R.drawable.ic_document_svg);

        } else {
            System.out.println("Check Pass for Image-------  ");
            System.out.println("Path Of Attachment-------  " + path);
            Picasso.with(context)
                    .load(new File(path))
                    .resize(500, 500)
                    .centerCrop()
                    .into(holder.attachmentImageView);
        }

    }

    @Override
    public int getItemCount() {
        return paths.size();
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView attachmentImageView;
        ImageView attachmentIconImageView;
        TextView titleTextView;


        public FileViewHolder(View itemView) {
            super(itemView);
            attachmentImageView = (ImageView) itemView.findViewById(R.id.attachmentImageView);
            attachmentIconImageView = (ImageView) itemView.findViewById(R.id.attachment_imageView_icon);
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
