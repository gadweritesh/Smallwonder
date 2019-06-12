package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.ExamSummaryListData;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Amey on 14-12-2017.
 */

public class ExamResultHistoryAdapter extends BaseAdapter {

    protected List<ExamSummaryListData> dataList;
    Context context;
    LayoutInflater inflater;
    private SimpleDateFormat targetDateFormat;

    public ExamResultHistoryAdapter(List<ExamSummaryListData> dataList, Context context) {
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
            convertView = this.inflater.inflate(R.layout.exam_result_summary_item_list, parent, false);
            holder.examdate_comment = (TextView) convertView.findViewById(R.id.examdate_comment);
            holder.examtype_textview = (TextView) convertView.findViewById(R.id.examtype_textview);
            holder.examdate_textview = (TextView) convertView.findViewById(R.id.examdate_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ExamSummaryListData examSummaryListData = dataList.get(position);
        holder.examtype_textview.setText(examSummaryListData.getExamType());
        holder.examdate_comment.setText(examSummaryListData.getResult_summary_comment());

        try {
            targetDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
            targetDateFormat.setTimeZone(TimeZone.getDefault());
            holder.examdate_textview.setText(targetDateFormat.format(examSummaryListData.getExamDate()));
        }catch (Exception e){
            System.out.println("Format exam date Exception ");
            e.getMessage();
        }

        return convertView;
    }

    private class ViewHolder {

        TextView examdate_comment;
        TextView examtype_textview;
        TextView examdate_textview;
        TextView more_textView;

    }
}
