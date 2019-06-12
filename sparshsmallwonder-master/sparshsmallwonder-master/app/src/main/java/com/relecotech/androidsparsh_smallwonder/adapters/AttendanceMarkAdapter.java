package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceMarkListData;

import java.util.List;

public class AttendanceMarkAdapter extends BaseAdapter {
    List<AttendanceMarkListData> attendanceMarkList;
    Context context;
    LayoutInflater inflater;

    public AttendanceMarkAdapter(Context context, List<AttendanceMarkListData> attendancemarkList) {
        this.attendanceMarkList = attendancemarkList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return attendanceMarkList.size();
    }

    public AttendanceMarkListData getItem(int position) {
        return attendanceMarkList.get(position);
    }

    public long getItemId(int i) {
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

        AttendanceMarkListData attendanceMarkListData = attendanceMarkList.get(position);

        holder.studentNameTextView.setText(attendanceMarkListData.getFullName());
        holder.attendanceRollNoTextView.setText("" + attendanceMarkListData.getRollNo());

        switch (attendanceMarkListData.getPresentStatus()) {

            case "P":
                holder.presentStatusTextView.setText(attendanceMarkListData.getPresentStatus());
                holder.presentStatusTextView.setBackgroundResource(R.drawable.green_circle_bg);
                break;
            case "A":
                holder.presentStatusTextView.setText(attendanceMarkListData.getPresentStatus());
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
