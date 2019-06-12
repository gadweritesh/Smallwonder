package com.relecotech.androidsparsh_smallwonder.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.LeavesListData;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.util.HashMap;
import java.util.List;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by amey on 10/16/2015.
 */
public class LeavesAdapter extends BaseAdapter {

    private final SessionManager sessionManager;
    private final HashMap<String, String> userDetails;
    private final String userRole;
    Context context;
    LayoutInflater inflater;

    List<LeavesListData> leavesList;

    public LeavesAdapter(Context context, List<LeavesListData> leavesList) {
        this.context = context;
        this.leavesList = leavesList;
        this.inflater = LayoutInflater.from(context);
        sessionManager = new SessionManager(context, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
    }

    @Override
    public int getCount() {
        return leavesList.size();
    }

    @Override
    public LeavesListData getItem(int position) {
        return leavesList.get(position);
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
            view = this.inflater.inflate(R.layout.leaves_list_item, viewGroup, false);

            holder.dateTextView = (TextView) view.findViewById(R.id.leavesDateTextView);
            holder.statusTextView = (TextView) view.findViewById(R.id.leavesStatusTextView);
            holder.titleTextView = (TextView) view.findViewById(R.id.leavesTitleTextView);
            holder.fromTextView = (TextView) view.findViewById(R.id.leavesperiodTextView);
            holder.toTextView = (TextView) view.findViewById(R.id.leavesToTextView);
            holder.studentNameTextView = (TextView) view.findViewById(R.id.studentNameTextView);
            holder.partition = view.findViewById(R.id.namePartition);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        LeavesListData leavesListData = leavesList.get(i);

        holder.dateTextView.setText(leavesListData.getLeavePostDate());
        holder.titleTextView.setText(leavesListData.getLeaveCause().replace("\\n", "\n"));

        if (userRole.equals("Teacher")) {
            holder.studentNameTextView.setText(leavesListData.getStudentName());
        } else {
            holder.studentNameTextView.setText("");
            holder.partition.setVisibility(View.INVISIBLE);
        }

        holder.statusTextView.setText(leavesListData.getLeaveStatus());

        if (leavesListData.getLeaveStatus().equals("Approved")) {
            holder.statusTextView.setTextColor(Color.parseColor("#009688"));
        }
        if (leavesListData.getLeaveStatus().equals("Pending")) {
            holder.statusTextView.setTextColor(Color.parseColor("#8E8E8E"));
        }
        if (leavesListData.getLeaveStatus().equals("Denied")) {
            holder.statusTextView.setTextColor(Color.RED);
        }
        holder.fromTextView.setText(leavesListData.getLeaveStartDay() + "  To  " + leavesListData.getLeaveEndDay());

        return view;
    }

    private class ViewHolder {

        private TextView dateTextView;
        private TextView titleTextView;
        private TextView statusTextView;
        private TextView fromTextView;
        private TextView toTextView;
        private View partition;
        private TextView studentNameTextView;

    }
}

