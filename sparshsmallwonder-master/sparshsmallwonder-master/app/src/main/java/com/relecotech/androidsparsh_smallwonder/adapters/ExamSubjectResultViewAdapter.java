package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.ExamSubjectResultListData;

import java.util.List;

public class ExamSubjectResultViewAdapter extends BaseAdapter {


    protected List<ExamSubjectResultListData> dataList;
    Context context;
    LayoutInflater inflater;

    public ExamSubjectResultViewAdapter(List<ExamSubjectResultListData> dataList, Context context) {
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
            convertView = this.inflater.inflate(R.layout.exam_subject_result_view, parent, false);
            holder.subject_textview = (TextView) convertView.findViewById(R.id.subject_textview);
            holder.obatinedmark_textview = (TextView) convertView.findViewById(R.id.obatinedmark_textview);
            holder.totalmark_textview = (TextView) convertView.findViewById(R.id.totalmark_textview);
            holder.grade_textview = (TextView) convertView.findViewById(R.id.obatinedgrade_textview);
            holder.comment_textview = (TextView) convertView.findViewById(R.id.comment_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ExamSubjectResultListData examSubjectResultListData = dataList.get(position);
        holder.subject_textview.setText(examSubjectResultListData.getExamSubject());
        holder.obatinedmark_textview.setText(examSubjectResultListData.getExamSubjectMarkOtained());
        holder.totalmark_textview.setText(examSubjectResultListData.getExamSubjectTotalMark());
        holder.grade_textview.setText(examSubjectResultListData.getExamSubjectGrade());
        System.out.println("examSubjectResultListData.getExamSubjectTotalMark()----------  " + examSubjectResultListData.getExamSubjectComment());
        if (examSubjectResultListData.getExamSubjectComment() != "null") {
            holder.comment_textview.setText(examSubjectResultListData.getExamSubjectComment());
        } else {
            holder.comment_textview.setText("Not Available");

        }
        return convertView;
    }

    private class ViewHolder {

        TextView subject_textview;
        TextView obatinedmark_textview;
        TextView totalmark_textview;
        TextView comment_textview;
        TextView grade_textview;

    }
}
