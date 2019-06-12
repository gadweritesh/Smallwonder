package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AlertStudentListData;

import java.util.List;

/**
 * Created by Relecotech on 21-03-2018.
 */

public class AlertStudentListAdapter extends BaseAdapter {

    Context context;
    public List<AlertStudentListData> spinnerStudentLists;
    ListView listView;

    public AlertStudentListAdapter(Context context, List<AlertStudentListData> spinnerStudentLists, ListView studentListView) {
        this.spinnerStudentLists = spinnerStudentLists;
        this.context = context;
        this.listView = studentListView;
    }

    @Override
    public int getCount() {
        return spinnerStudentLists.size();
    }

    @Override
    public AlertStudentListData getItem(int position) {
        return spinnerStudentLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alert_student_listitem, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.text_drop_down);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.alertCheckBox);
            convertView.setTag(holder);

//            holder.checkBox.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    System.out.println(" click on Check Box " + v);
//                    CheckBox cb = (CheckBox) v;
//                    AlertStudentListData data = (AlertStudentListData) cb.getTag();
//
//                    data.setSelected(cb.isChecked());
////                    if (data.getStudentId().equals("All")) {
////                        Toast.makeText(context, "Clicked on ALL ", Toast.LENGTH_LONG).show();
////                    } else {
////                        Toast.makeText(context, "Clicked on Other: ", Toast.LENGTH_LONG).show();
////                    }
//                }
//            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        AlertStudentListData spinnerStudentListObj = spinnerStudentLists.get(position);

        holder.checkBox.setTag(position);
        holder.textView.setText(" " + spinnerStudentListObj.getStudentRollNo() + "  " + spinnerStudentListObj.getFullName());
        holder.checkBox.setChecked(spinnerStudentListObj.isSelected());
        holder.checkBox.setTag(spinnerStudentListObj);

        return convertView;
    }


    private class ViewHolder {
        private TextView textView;
        private CheckBox checkBox;
    }

}
