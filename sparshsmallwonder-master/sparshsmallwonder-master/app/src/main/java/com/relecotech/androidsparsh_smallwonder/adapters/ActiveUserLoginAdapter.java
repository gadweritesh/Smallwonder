package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.ActiveUserLoginListData;

import java.util.List;

public class ActiveUserLoginAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    List<ActiveUserLoginListData> activeUserLoginLists;

    public ActiveUserLoginAdapter(Context context, List<ActiveUserLoginListData> activeUserLoginLists) {
        this.context = context;
        this.activeUserLoginLists = activeUserLoginLists;
    }


    @Override
    public int getCount() {
        return activeUserLoginLists.size();
    }

    @Override
    public ActiveUserLoginListData getItem(int position) {
        return activeUserLoginLists.get(position);
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
            convertView = inflater.inflate(R.layout.active_user_login_detail, parent, false);
            //   holder.userSerialNoTextView = (TextView) convertView.findViewById(R.id.textView3);
            holder.userFullNameTextView = (TextView) convertView.findViewById(R.id.textView4);
            holder.userRoleTextView = (TextView) convertView.findViewById(R.id.textView11);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final ActiveUserLoginListData activeUserLoginListData = activeUserLoginLists.get(position);
        try {
            // holder.userSerialNoTextView.setText(String.valueOf(activeUserLoginListData.getSerialNo()));
            holder.userFullNameTextView.setText(activeUserLoginListData.getUserName());
            holder.userRoleTextView.setText(activeUserLoginListData.getUserRole());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }

    private class ViewHolder {
        TextView userSerialNoTextView;
        TextView userFullNameTextView;
        TextView userRoleTextView;
    }

}
