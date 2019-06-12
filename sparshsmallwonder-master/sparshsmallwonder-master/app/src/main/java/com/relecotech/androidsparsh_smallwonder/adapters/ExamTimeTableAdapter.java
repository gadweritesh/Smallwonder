package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.ExamTimeTableListData;

import java.util.List;

public class ExamTimeTableAdapter extends BaseAdapter {

    protected List<ExamTimeTableListData> dataList;
    Context context;
    LayoutInflater inflater;

    public ExamTimeTableAdapter(Context context, List<ExamTimeTableListData> dataList) {
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
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
            convertView = this.inflater.inflate(R.layout.exam_time_table_item, parent, false);
            holder.examDate = (TextView) convertView.findViewById(R.id.exam_time_table_date);
            holder.examTime = (TextView) convertView.findViewById(R.id.exam_time_table_time);
            holder.examSubject = (TextView) convertView.findViewById(R.id.exam_time_table_subject);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ExamTimeTableListData timeTableListData = dataList.get(position);
        holder.examDate.setText(timeTableListData.getExamDate());
        holder.examTime.setText(timeTableListData.getExamTime());
        holder.examSubject.setText(timeTableListData.getExamSubject());

        return convertView;
    }

    private class ViewHolder {

        TextView examDate;
        TextView examTime;
        TextView examSubject;
    }
}
