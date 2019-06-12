package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AchievementListData;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AchievementAdapter extends BaseAdapter {

    private final HashMap<String, String> userDetails;
    List<AchievementListData> dataList;
    Context context;
    LayoutInflater inflater;
    private SessionManager sessionManager;
    private String userRole;

    public AchievementAdapter(Context context, List<AchievementListData> dataList) {
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;


        sessionManager = new SessionManager(context, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.achievement_list_item, parent, false);
            holder.achievementPostDate = (TextView) convertView.findViewById(R.id.achievement_txt_Date);
            holder.achievementCard = (RelativeLayout) convertView.findViewById(R.id.achievement_relative);
            holder.achievementStudentName = (TextView) convertView.findViewById(R.id.achievement_name_textView);
            holder.achievementTitle = (TextView) convertView.findViewById(R.id.achievement_title_textView);
            holder.achievementDesc = (TextView) convertView.findViewById(R.id.achievement_desc_textView);
            holder.achievementCategory = (TextView) convertView.findViewById(R.id.achievement_category_textView);
            holder.achievementImageView = (ImageView) convertView.findViewById(R.id.achievement_imageView);
            holder.achievementSubmittedBy = (TextView) convertView.findViewById(R.id.achievement_submitted_by);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AchievementListData listData = dataList.get(position);
        holder.achievementPostDate.setText(listData.getAchievementPostDate());
        holder.achievementTitle.setText(listData.getAchievementTitle() + " (" + listData.getAchievementYear() + ")");
        if (!listData.getAchievementStudentName().equals("null")) {
            holder.achievementStudentName.setText(listData.getAchievementStudentName());
        }
        if (!listData.getAchievementDescription().equals("null")) {
            holder.achievementDesc.setText(listData.getAchievementDescription());
        }
        if (!listData.getAchievementCategory().equals("null")) {
            holder.achievementCategory.setText(listData.getAchievementCategory());
        }

        holder.achievementSubmittedBy.setText("Submitted By: " +listData.getUploaderName());
//        String teacherId = userDetails.get(SessionManager.KEY_TEACHER_ID);
//        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
//        if (listData.getUploaderId().equals(teacherId)){
//            holder.achievementSubmittedBy.setTextColor(Color.BLUE);
//        }else {
//            holder.achievementSubmittedBy.setVisibility(View.INVISIBLE);
//        }



//        sessionManager = new SessionManager(context, sharedPrefValue);
//        if (!listData.getStudent_id().equals(sessionManager.getUserDetails().get(SessionManager.KEY_STUDENT_ID))) {
//            holder.achievementCard.setCardBackgroundColor(Color.LTGRAY);
//        }

        try {
            System.out.println("Before Picasso Set ");
            Picasso.with(context)

                    .load(listData.getAchievementImageUrl())
                    .resize(200, 200)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.achievementImageView);
            System.out.println("After Picasso Set ");
        } catch (Exception e) {
            System.out.println("Picasso error");
        }

        return convertView;
    }

    private class ViewHolder {

        TextView achievementPostDate;
        ImageView achievementImageView;
        TextView achievementStudentName;
        TextView achievementTitle;
        TextView achievementDesc;
        TextView achievementCategory;
        RelativeLayout achievementCard;
        TextView achievementSubmittedBy;
    }
}

