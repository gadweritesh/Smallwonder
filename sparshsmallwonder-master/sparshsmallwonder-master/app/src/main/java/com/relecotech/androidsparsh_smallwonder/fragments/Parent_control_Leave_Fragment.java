package com.relecotech.androidsparsh_smallwonder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.activities.ApproveLeave;
import com.relecotech.androidsparsh_smallwonder.adapters.LeavesAdapter;
import com.relecotech.androidsparsh_smallwonder.models.LeavesListData;

import java.util.List;

/**
 * Created by amey on 8/6/2016.
 */
public class Parent_control_Leave_Fragment extends Fragment {
    ListView parentControlListView;
    private List<LeavesListData> getLeaveListData;
    private LeavesAdapter parentControlLeaveAdapter;
    private LeavesListData selectedLeaveListData;
    private TextView parentcontrolNoDataAvailable_Leave_TextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parent_control_fragment, container, false);
        parentControlListView = (ListView) rootView.findViewById(R.id.parent_control_listView);
        parentcontrolNoDataAvailable_Leave_TextView = (TextView) rootView.findViewById(R.id.no_data_available_prent_control_textView);

        Bundle getBundleData = this.getArguments();
        getLeaveListData = (List<LeavesListData>) getBundleData.getSerializable("LeaveList");
        System.out.println("getLeaveListData " + getLeaveListData);

        try {
            if (!getLeaveListData.isEmpty() || getLeaveListData != null) {
                parentControlLeaveAdapter = new LeavesAdapter(getActivity(), getLeaveListData);
                parentControlListView.setAdapter(parentControlLeaveAdapter);

            } else {
                System.out.println("Parents Zone Leave List  is NULLL");
                parentcontrolNoDataAvailable_Leave_TextView.setVisibility(View.VISIBLE);
                parentcontrolNoDataAvailable_Leave_TextView.setText(R.string.noDataAvailable);

            }
        } catch (Exception e) {
            System.out.println("getMassage---Leave Fragments----" + e.getMessage());
            parentcontrolNoDataAvailable_Leave_TextView.setVisibility(View.VISIBLE);
            parentcontrolNoDataAvailable_Leave_TextView.setText(R.string.noDataAvailable);

        }

        parentControlListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLeaveListData = getLeaveListData.get(position);
                Intent leaveApproveIntent = new Intent(getActivity(), ApproveLeave.class);
                Bundle leaveBundle = new Bundle();
                leaveBundle.putString("StudentName", selectedLeaveListData.getStudentName());
                leaveBundle.putString("StudentRollNo", selectedLeaveListData.getStudentRollNo());
                leaveBundle.putString("StudentId", selectedLeaveListData.getStudentId());
                leaveBundle.putString("Status", selectedLeaveListData.getLeaveStatus());
                leaveBundle.putString("StartDay", selectedLeaveListData.getLeaveStartDay());
                leaveBundle.putString("EndDay", selectedLeaveListData.getLeaveEndDay());
                leaveBundle.putString("Cause", selectedLeaveListData.getLeaveCause());
                leaveBundle.putString("PostDate", selectedLeaveListData.getLeavePostDate());
                leaveBundle.putString("Reply", selectedLeaveListData.getLeaveReply());
                leaveBundle.putInt("DaysCount", selectedLeaveListData.getLeaveDayCount());
                leaveBundle.putString("LeaveId", selectedLeaveListData.getLeaveId());
                leaveApproveIntent.putExtras(leaveBundle);
                startActivity(leaveApproveIntent);

            }
        });


        return rootView;
    }
}
