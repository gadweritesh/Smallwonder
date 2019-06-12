package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceDraftListData;

import java.util.List;

public class AttendanceDraftAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<AttendanceDraftListData> draftList;

    public AttendanceDraftAdapter(Context context, List<AttendanceDraftListData> draftList) {
        this.context = context;
        this.draftList = draftList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return draftList.size();
    }

    @Override
    public AttendanceDraftListData getItem(int position) {
        return draftList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {

            holder = new ViewHolder();
            view = this.inflater.inflate(R.layout.attendance_draft_list_item, viewGroup, false);

            holder.dateTextView = (TextView) view.findViewById(R.id.draftListDateTextView);
            holder.classTextView = (TextView) view.findViewById(R.id.draftListClassTextView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AttendanceDraftListData attendanceDraftListData = draftList.get(i);

        holder.dateTextView.setText("Dt : " + attendanceDraftListData.getAttendanceDate());
        holder.classTextView.setText("Class  " + attendanceDraftListData.getAttendanceClass() + " " + attendanceDraftListData.getAttendanceDivision());

        return view;
    }

    private class ViewHolder {

        private TextView dateTextView;
        private TextView classTextView;

    }
}
