package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AttachmentListData;
import com.relecotech.androidsparsh_smallwonder.utils.ItemClickListener;

import java.io.File;
import java.util.List;


/**
 * Created by amey on 1/4/2017.
 */
public class AlertAttachmentAdapter extends RecyclerView.Adapter<AlertAttachmentAdapter.AttachViewHolder> {

    private Context context;
    List<AttachmentListData> assignmentAttachmentListData;
    Integer[] imageId;
    private static ItemClickListener clickListener;
    private static File attachmentDir;

//    public AssignmentAttachmentAdapter(Context context, Integer[] imageId) {
//        this.context = context;
//        this.imageId = imageId;
//    }

    public AlertAttachmentAdapter(Context context, List<AttachmentListData> attachmentListData) {
        this.context = context;
        this.assignmentAttachmentListData = attachmentListData;
        System.out.println("abc abc 11");
    }

    @Override
    public AttachViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_list_item, parent, false);
        return new AttachViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AttachViewHolder holder, int position) {
        AttachmentListData data = assignmentAttachmentListData.get(position);
        System.out.println("getAlertListObject.getTitle() " + data.getAttachmentName());
//        holder.assignmentImageView.setImageResource(imageId[position]);
        holder.attachmentName.setText(data.getAttachmentName());

        try {
            attachmentDir = new File(Environment.getExternalStorageDirectory(), "/" + context.getString(R.string.folderName) + "/Alert_Download/" + data.getAttachmentName());
            System.out.println(" dir " + attachmentDir);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (holder.valueProgressbar == 1) {
            holder.downloadProgressBar.setVisibility(View.INVISIBLE);
            holder.valueProgressbar = 0;
        }

        if (attachmentDir.exists()) {
            holder.downLoadRelLay.setVisibility(View.INVISIBLE);
            Bitmap myBitmap = BitmapFactory.decodeFile(attachmentDir.getAbsolutePath());
            holder.assignmentImageView.setImageBitmap(myBitmap);
        }

        if (data.getAttachmentName().contains(".jpg") || data.getAttachmentName().contains(".png") || data.getAttachmentName().contains(".jpeg")) {
            holder.attachmentIcon.setImageResource(R.drawable.ic_attachment_imgtype);
        } else if (data.getAttachmentName().contains(".pdf")) {
            holder.attachmentIcon.setImageResource(R.drawable.ic_picture_as_pdf_red_600_24dp);
        } else if (data.getAttachmentName().contains(".docx")) {
            holder.attachmentIcon.setImageResource(R.drawable.ic_attachment_doctype);
        }
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
        System.out.println("setClickListener setClickListener");
    }


    @Override
    public int getItemCount() {
        return assignmentAttachmentListData.size();
    }

    public static class AttachViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView assignmentImageView;
        private RelativeLayout downLoadRelLay;
        private ProgressBar downloadProgressBar;
        private ImageView attachmentIcon;
        private TextView attachmentName;
        private int valueProgressbar = 0;


        public AttachViewHolder(View convertView) {
            super(convertView);

            System.out.println("AttachViewHolder AttachViewHolder");
            assignmentImageView = (ImageView) convertView.findViewById(R.id.assignmentImageView);
            downLoadRelLay = (RelativeLayout) convertView.findViewById(R.id.assignmentDownLoadRelativeLayout);
            downloadProgressBar = (ProgressBar) convertView.findViewById(R.id.attachmentDownloadProgressBar);
            attachmentIcon = (ImageView) convertView.findViewById(R.id.attachment_imageView_icon);
            attachmentName = (TextView) convertView.findViewById(R.id.attachment_textView);

            convertView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(view, getAdapterPosition());

                if (attachmentDir.exists()) {
                    System.out.println(" INSIDE  checkIfPresentValue");
                } else {
                    System.out.println("onClick onClick");
                    downLoadRelLay.setVisibility(View.INVISIBLE);
                    valueProgressbar = 1;
                    downloadProgressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}

