package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AbsentStudentListData;

import java.util.List;

/**
 * Created by Amey on 29-05-2018.
 */

public class AbsentStudentAdapter extends BaseAdapter {


    LayoutInflater inflater;
    Context context;
    List<AbsentStudentListData> absentStudentLists;

    public AbsentStudentAdapter(Context context, List<AbsentStudentListData> absentStudentLists) {
        this.context = context;
        this.absentStudentLists = absentStudentLists;
    }


    @Override
    public int getCount() {
        return absentStudentLists.size();
    }

    @Override
    public AbsentStudentListData getItem(int position) {
        return absentStudentLists.get(position);
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.absent_student_list_item, parent, false);
            holder.serialNoTextView = (TextView) convertView.findViewById(R.id.serialNoTextView);
            holder.studentFullNameTextView = (TextView) convertView.findViewById(R.id.studentFullNameTextView);
            holder.studentSchoolClassDivisionTextView = (TextView) convertView.findViewById(R.id.studentSchoolClassDivisionTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        AbsentStudentListData absentStudentListData = absentStudentLists.get(position);

        holder.serialNoTextView.setText(absentStudentListData.getAbsentStudentUniqueNo());
        holder.studentFullNameTextView.setText(absentStudentListData.getAbsentStudentName());
        holder.studentSchoolClassDivisionTextView.setText(absentStudentListData.getAbsentStudentClassDivision());

        return convertView;
    }

    private class ViewHolder {
        TextView serialNoTextView;
        TextView studentFullNameTextView;
        TextView studentSchoolClassDivisionTextView;
    }

}


