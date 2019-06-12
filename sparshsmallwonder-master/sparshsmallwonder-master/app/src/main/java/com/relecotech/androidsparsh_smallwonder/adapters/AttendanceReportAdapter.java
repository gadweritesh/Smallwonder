package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceListData;

import java.util.List;

/**
 * Created by Amey on 12-04-2018.
 */

public class AttendanceReportAdapter  extends BaseAdapter {

    protected List<AttendanceListData> attendanceReportList;
    Context context;
    LayoutInflater inflater;
    private AttendanceListData attendanceListData;

    public AttendanceReportAdapter(Context context, List<AttendanceListData> attendancListData) {
        this.attendanceReportList = attendancListData;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }


    public int getCount() {
        return attendanceReportList.size();
    }

    public AttendanceListData getItem(int position) {
        return attendanceReportList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.attendance_report_item, parent, false);

            holder.attendanceDateTextView = (TextView) convertView.findViewById(R.id.attendanceDateTextView);
            holder.attendanceStatusTextView = (TextView) convertView.findViewById(R.id.attendanceStatusTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        attendanceListData = attendanceReportList.get(position);

        holder.attendanceDateTextView.setText(attendanceListData.getAttendanceDate());
        holder.attendanceStatusTextView.setText(attendanceListData.getAttendanceStatus());

        return convertView;
    }

    private class ViewHolder {
        TextView attendanceDateTextView;
        TextView attendanceStatusTextView;

    }
}

