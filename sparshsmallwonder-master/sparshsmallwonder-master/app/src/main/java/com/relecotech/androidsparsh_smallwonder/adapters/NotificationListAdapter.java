package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.NotificationListData;

import java.util.List;

public class NotificationListAdapter extends BaseAdapter {

    protected List<NotificationListData> noticeList;
    Context context;
    LayoutInflater inflater;

    public NotificationListAdapter(Context context, List<NotificationListData> noticeList) {
        this.noticeList = noticeList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return noticeList.size();
    }

    public NotificationListData getItem(int position) {
        return noticeList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.notification_list_item, parent, false);

            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_Date);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_Title);
            holder.txtBody = (TextView) convertView.findViewById(R.id.txt_Body);
            holder.txtSubmitted_by = (TextView) convertView.findViewById(R.id.txt_Submitted_By);
            holder.imageView_tag = (ImageView) convertView.findViewById(R.id.notification_tag_imageView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final NotificationListData noticeListData = noticeList.get(position);
        holder.txtDate.setText(noticeListData.getNotifaction_Post_Date());
        holder.txtTitle.setText(noticeListData.getNotifaction_Tag());
        holder.txtBody.setText(noticeListData.getNotification_Message_Body());
        System.out.println("noticeListData.getSubmittedBy() Adapter Adapter  " + noticeListData.getNotifaction_SubmittedBy());
        holder.txtSubmitted_by.setText(noticeListData.getNotifaction_SubmittedBy());

        System.out.println(" noticeListData.getNotifaction_Tag() " + noticeListData.getNotifaction_Tag());
        switch (noticeListData.getNotifaction_Tag()) {
            case "Assignment":
                holder.imageView_tag.setImageResource(R.drawable.ic_assignment);
                break;
            case "Alert":
                holder.imageView_tag.setImageResource(R.drawable.ic_assignment);
                break;
            case "Leave":
                holder.imageView_tag.setImageResource(R.drawable.ic_leave);
                break;
            case "Notes":
                holder.imageView_tag.setImageResource(R.drawable.ic_notes);
                break;
            case "Gallery":
                holder.imageView_tag.setImageResource(R.drawable.ic_gallery);
                break;
            case "Achievement":
                holder.imageView_tag.setImageResource(R.drawable.ic_achievement_trophy_cup);
                break;
            case "Attendance":
                holder.imageView_tag.setImageResource(R.drawable.ic_attedance_notificaion);
                break;

            default:
                holder.imageView_tag.setImageResource(R.drawable.ic_assignment);
                break;

        }
//        if (noticeListData.getNotifaction_Tag().equals("Assignment")) {
//            System.out.println("**************************************Assignment");
//            holder.imageView_tag.setImageResource(R.drawable.ic_assignment);
//        } else if (noticeListData.getNotifaction_Tag().equals("Leave")) {
//            System.out.println("**************************************Leave");
////            holder.imageView_tag.setImageResource(R.drawable.leave_notification_96);
//        } else if (noticeListData.getNotifaction_Tag().equals("Notes")) {
//            System.out.println("**************************************Notes");
////            holder.imageView_tag.setImageResource(R.drawable.notes_notification_96);
//        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtDate;
        TextView txtTitle;
        TextView txtBody;
        TextView txtSubmitted_by;
        ImageView imageView_tag;
    }

}
