package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.BusRouteListData;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.util.List;

public class BusScheduleAdapter extends BaseAdapter {

    List<BusRouteListData> dataList;
    Context context;
    LayoutInflater inflater;
    private SessionManager sessionManager;

    public BusScheduleAdapter(Context context, List<BusRouteListData> dataList) {
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
            convertView = this.inflater.inflate(R.layout.bus_list_item, parent, false);
            holder.busNo = (TextView) convertView.findViewById(R.id.bus_no_textView);
            holder.route = (TextView) convertView.findViewById(R.id.route_textView);
            holder.vehicleNo = (TextView) convertView.findViewById(R.id.vehicle_no_textView);
            holder.driverName = (TextView) convertView.findViewById(R.id.driver_name_textView);
            holder.mobileNo = (TextView) convertView.findViewById(R.id.mobile_no_textView);
            holder.startTime = (TextView) convertView.findViewById(R.id.start_time_textView);
            holder.startTime = (TextView) convertView.findViewById(R.id.start_time_textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BusRouteListData busRouteListData = dataList.get(position);
        holder.busNo.setText(busRouteListData.getRouteNo());
        holder.route.setText(busRouteListData.getBusRoute());
        holder.vehicleNo.setText(busRouteListData.getVehicleNo());
        System.out.println(" busRouteListData.getName() " + busRouteListData.getName());
        holder.mobileNo.setText(busRouteListData.getMobileNo());
        holder.driverName.setText(busRouteListData.getName());
        holder.startTime.setText(busRouteListData.getStartTime());


        return convertView;
    }

    private class ViewHolder {
        TextView busNo;
        TextView route;
        TextView vehicleNo;
        TextView driverName;
        TextView mobileNo;
        TextView startTime;
    }
}