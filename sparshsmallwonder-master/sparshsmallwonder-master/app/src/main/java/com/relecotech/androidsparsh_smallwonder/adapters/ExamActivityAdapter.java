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

public class ExamActivityAdapter extends BaseAdapter {

    protected List<ExamTimeTableListData> dataList;
    Context context;
    LayoutInflater inflater;

    public ExamActivityAdapter(Context context, List<ExamTimeTableListData> dataList) {
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
            convertView = this.inflater.inflate(R.layout.exam_activity_list_item, parent, false);
            holder.examTitle = (TextView) convertView.findViewById(R.id.exam_title_textView);
            holder.examComment = (TextView) convertView.findViewById(R.id.exam_tentative_textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ExamTimeTableListData timeTableListData = dataList.get(position);
        holder.examTitle.setText(timeTableListData.getExamTitle());
        if (!timeTableListData.getTentativeDateComment().equals("null")) {
            holder.examComment.setText(timeTableListData.getTentativeDateComment());
        }

        return convertView;
    }

    private class ViewHolder {

        TextView examTitle;
        TextView examComment;
    }
}
