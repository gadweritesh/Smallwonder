package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Syllabus;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.util.List;

public class SyllabusSubjectAdapter extends BaseAdapter {

    List<Syllabus> dataList;
    Context context;
    LayoutInflater inflater;
    private SessionManager sessionManager;

    public SyllabusSubjectAdapter(Context context, List<Syllabus> dataList) {
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
            convertView = this.inflater.inflate(R.layout.syllabus_list_item, parent, false);
            holder.syllabusSubject = (TextView) convertView.findViewById(R.id.syllabus_subject_textView);
            holder.syllabusDesc = (TextView) convertView.findViewById(R.id.syllabus_desc_textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Syllabus listData = dataList.get(position);
        holder.syllabusSubject.setText(listData.getSyllabusSubject());
        holder.syllabusDesc.setText(listData.getSyllabusDescription());

        return convertView;
    }

    private class ViewHolder {
        TextView syllabusSubject;
        TextView syllabusDesc;
    }
}