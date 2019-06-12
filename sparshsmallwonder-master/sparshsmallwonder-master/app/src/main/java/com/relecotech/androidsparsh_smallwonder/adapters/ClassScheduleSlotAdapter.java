package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.ClassScheduleListData;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class ClassScheduleSlotAdapter extends BaseAdapter {

    Context context;
    List<ClassScheduleListData> timeTableList;
    LayoutInflater inflater;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;


    public ClassScheduleSlotAdapter(Context context, List<ClassScheduleListData> timeTableList) {
        this.context = context;
        this.timeTableList = timeTableList;
        this.inflater = LayoutInflater.from(context);
        sessionManager = new SessionManager(context ,sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
    }

    @Override
    public int getCount() {
        return timeTableList.size();
    }

    @Override
    public ClassScheduleListData getItem(int position) {
        return timeTableList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            view = this.inflater.inflate(R.layout.class_schedule_day_timetable, viewGroup, false);

            holder.startTimeTextView = (TextView) view.findViewById(R.id.start_time_textView);
//            holder.endTimeTextView = (TextView) view.findViewById(R.id.endtime_textView);
            holder.subjectTextView = (TextView) view.findViewById(R.id.subject_textView);
            holder.subjectTeacherTextView = (TextView) view.findViewById(R.id.subject_teacher_name_textView);
            holder.classSchedule_RelativeLayout = (RelativeLayout) view.findViewById(R.id.class_schedule_layout);
            holder.freetimeTextView = (TextView) view.findViewById(R.id.free_time_textView);
            holder.endTimeTextView = (TextView) view.findViewById(R.id.endtime_textView);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        ClassScheduleListData studentTimeTableListData = timeTableList.get(i);
        SimpleDateFormat targetFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());


        if (userRole.equals("Teacher")) {


            if (studentTimeTableListData.getSlotType().equals("Break")) {
                holder.endTimeTextView.setVisibility(View.GONE);

                holder.classSchedule_RelativeLayout.setBackgroundColor(Color.parseColor("#F0E5DB"));
                String start_Time = targetFormat.format(studentTimeTableListData.getSlotStartTime());
                String end_Time = targetFormat.format(studentTimeTableListData.getSlotEndTime());

                holder.startTimeTextView.setText(start_Time);
                holder.subjectTeacherTextView.setText("");
                holder.subjectTextView.setText(studentTimeTableListData.getSlotSubject());
                holder.freetimeTextView.setVisibility(View.INVISIBLE);

                System.out.println("Break +++++++++++++++++++++++++++++++++++ ");
            }
            if (studentTimeTableListData.getSlotType().equals("class")) {

                if (studentTimeTableListData.getExpandable_list_status() == false) {
                    holder.endTimeTextView.setVisibility(View.GONE);
                } else {
                    holder.endTimeTextView.setVisibility(View.VISIBLE);
                }
                holder.startTimeTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.subjectTeacherTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.subjectTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.classSchedule_RelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                holder.subjectTeacherTextView.setGravity(Gravity.CENTER);

                System.out.println("Class +++++++++++++++++++++++++++++++++++ ");

                String start_Time = targetFormat.format(studentTimeTableListData.getSlotStartTime());
                String end_Time = targetFormat.format(studentTimeTableListData.getSlotEndTime());
                holder.startTimeTextView.setText(start_Time);

                holder.endTimeTextView.setText(end_Time);

                holder.subjectTeacherTextView.setText(studentTimeTableListData.getTeacherClassDivision());
                holder.subjectTextView.setText(studentTimeTableListData.getSlotSubject());

                holder.freetimeTextView.setVisibility(View.INVISIBLE);
            }

            if (studentTimeTableListData.getSlotType().equals("Free")) {

                holder.endTimeTextView.setVisibility(View.GONE);
                holder.startTimeTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.subjectTeacherTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.subjectTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.classSchedule_RelativeLayout.setBackgroundColor(Color.TRANSPARENT);

                holder.subjectTextView.setText("");
                holder.startTimeTextView.setText("");
                holder.subjectTeacherTextView.setText("");
                holder.freetimeTextView.setVisibility(View.VISIBLE);

                System.out.println("Free +++++++++++++++++++++++++++++++++++ ");
            }
        }
        if (userRole.equals("Student")) {
            if (studentTimeTableListData.getSlotType().equals("Break")) {

                holder.endTimeTextView.setVisibility(View.GONE);

                holder.classSchedule_RelativeLayout.setBackgroundColor(Color.parseColor("#F0E5DB"));

                holder.subjectTeacherTextView.setGravity(Gravity.END);
                String start_Time = targetFormat.format(studentTimeTableListData.getSlotStartTime());
                holder.startTimeTextView.setText(start_Time);
                holder.subjectTeacherTextView.setText("");
                holder.subjectTextView.setText(studentTimeTableListData.getSlotSubject());
                holder.freetimeTextView.setVisibility(View.INVISIBLE);

            }
            if (studentTimeTableListData.getSlotType().equals("class")) {
                if (studentTimeTableListData.getExpandable_list_status() == false) {
                    holder.endTimeTextView.setVisibility(View.GONE);
                } else {
                    holder.endTimeTextView.setVisibility(View.VISIBLE);
                }

                holder.startTimeTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.subjectTeacherTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.subjectTextView.setBackgroundColor(Color.TRANSPARENT);
                holder.classSchedule_RelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                String start_Time = targetFormat.format(studentTimeTableListData.getSlotStartTime());
                String end_Time = targetFormat.format(studentTimeTableListData.getSlotEndTime());
                holder.startTimeTextView.setText(start_Time);

                holder.endTimeTextView.setText(end_Time);

                holder.subjectTeacherTextView.setGravity(Gravity.END);
                holder.subjectTeacherTextView.setText(studentTimeTableListData.getSubjectTeacherName());
                holder.subjectTextView.setText(studentTimeTableListData.getSlotSubject());
                holder.freetimeTextView.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private class ViewHolder {
        private TextView startTimeTextView;
        private TextView endTimeTextView;
        private TextView subjectTeacherTextView;
        private TextView subjectTextView;
        private TextView freetimeTextView;
        private RelativeLayout classSchedule_RelativeLayout;

    }
}
