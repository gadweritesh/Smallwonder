package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.CalendarHolidaysAndEventsListData;

import java.util.List;

/**
 * Created by Amey on 11-04-2018.
 */

public class CalendarEntryListAdapter extends BaseAdapter {


    protected List<CalendarHolidaysAndEventsListData> calendarDataList;
    Context context;
    LayoutInflater inflater;

    public CalendarEntryListAdapter(Context context, List<CalendarHolidaysAndEventsListData> calendarEventList) {
        this.calendarDataList = calendarEventList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return calendarDataList.size();
    }

    @Override
    public CalendarHolidaysAndEventsListData getItem(int position) {
        return calendarDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {

            holder = new ViewHolder();
            view = this.inflater.inflate(R.layout.calendar_list_item, viewGroup, false);

            holder.dateTextView = (TextView) view.findViewById(R.id.calendarDateTextView);
            holder.titleTextView = (TextView) view.findViewById(R.id.calendarTitleTextView);
            holder.calendarTypeTextView = (TextView) view.findViewById(R.id.calendarTypeTitleTextView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CalendarHolidaysAndEventsListData calendarFragmentListData = calendarDataList.get(i);

        try {


            holder.dateTextView.setText(calendarFragmentListData.getCalendarListViewDate());
            holder.titleTextView.setText(calendarFragmentListData.getCalendarTitle());
            holder.calendarTypeTextView.setText(calendarFragmentListData.getCalendarType());

        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
        }

        holder.calendarTypeTextView.setText(calendarFragmentListData.getCalendarType());
//        if (calendarFragmentListData.getCalendarTag().equals("Event")) {
//            holder.calendarTypeTextView.setTextColor(Color.parseColor("#0d5787"));
//        } else if (calendarFragmentListData.getCalendarTag().equals("Holiday")) {
//            holder.calendarTypeTextView.setTextColor(Color.parseColor("#21be31"));
//        }


        return view;
    }

    private class ViewHolder {
        private TextView dateTextView;
        private TextView titleTextView;
        private TextView calendarTypeTextView;
    }
}
