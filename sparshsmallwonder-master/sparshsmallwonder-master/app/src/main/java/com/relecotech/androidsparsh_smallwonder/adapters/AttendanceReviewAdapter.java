package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceReviewListData;

import java.util.List;

public class AttendanceReviewAdapter extends BaseAdapter {
    List<AttendanceReviewListData> attendanceReviewList;
    Context context;
    LayoutInflater inflater;

    public AttendanceReviewAdapter(Context context, List<AttendanceReviewListData> attendanceReviewList) {
        this.attendanceReviewList = attendanceReviewList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return attendanceReviewList.size();
    }

    public AttendanceReviewListData getItem(int position) {
        return attendanceReviewList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.attendance_review_list_item, parent, false);

            holder.attendanceRollNoTextView = (TextView) convertView.findViewById(R.id.attendancereviewRollNoTextView);
            holder.studentNameTextView = (TextView) convertView.findViewById(R.id.attendanceReviewStdNameTextView);
            holder.presentStatusTextView = (TextView) convertView.findViewById(R.id.attendanceReviewStatusTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AttendanceReviewListData attendanceReviewListData = attendanceReviewList.get(position);
        holder.studentNameTextView.setText(attendanceReviewListData.getfName() + " " + attendanceReviewListData.getlName());
        holder.attendanceRollNoTextView.setText(attendanceReviewListData.getRollNo());

        switch (attendanceReviewListData.getPresentStatus()) {

            case "P":
                holder.presentStatusTextView.setText(attendanceReviewListData.getPresentStatus());
                holder.presentStatusTextView.setBackgroundResource(R.drawable.green_circle_bg);
                break;
            case "A":
                holder.presentStatusTextView.setText(attendanceReviewListData.getPresentStatus());
                holder.presentStatusTextView.setBackgroundResource(R.drawable.red_circe_bg);
                break;
            case "U":
                holder.presentStatusTextView.setText("");
                holder.presentStatusTextView.setBackgroundResource(R.drawable.grey_empty_attendance_circle_bg);
                break;
        }

        return convertView;
    }

    private class ViewHolder {
        TextView attendanceRollNoTextView;
        TextView studentNameTextView;
        TextView presentStatusTextView;
    }
}
