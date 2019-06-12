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
import com.relecotech.androidsparsh_smallwonder.activities.AssignmentDetail;
import com.relecotech.androidsparsh_smallwonder.adapters.AssignmentRecyclerAdapter;
import com.relecotech.androidsparsh_smallwonder.models.AssignmentListData;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Relecotech on 07-03-2018.
 */

public class Assignment_Tabs_Fragment extends Fragment {
    ListView assignmentTabRecyclerView;
    private AssignmentRecyclerAdapter assignment_adapter;
    private List<AssignmentListData> assignmentlist;
    private AssignmentListData assignmentListData;
    private TextView assignmentNoDataAvailable_TextView;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private SimpleDateFormat dateFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getActivity(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);

        dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        Bundle bundle = this.getArguments();
        assignmentlist = new ArrayList<>();
        assignmentlist = (List<AssignmentListData>) bundle.getSerializable("assignmentList");
        try {
            Collections.sort(assignmentlist, new Comparator<AssignmentListData>() {
                @Override
                public int compare(AssignmentListData obj1, AssignmentListData obj2) {
                    System.out.println("Sorted Place");
                    return obj2.getIssueDate().compareTo(obj1.getIssueDate());
                }
            });
        }catch (NullPointerException e){
            System.out.println(" Collections.sort assignment list " + e.getMessage());
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.assignment_tabs_fragment, container, false);
        assignmentTabRecyclerView = (ListView) rootView.findViewById(R.id.assignmentTabRecyclerView);

        assignmentNoDataAvailable_TextView = (TextView) rootView.findViewById(R.id.assignmentTabNoDataAvailable_textView);
        System.out.println("*************************Assignment_Tabs_Fragment*******************************");

        if (assignmentlist == null) {
            assignmentNoDataAvailable_TextView.setText(R.string.noDataAvailable);
        } else {
            assignment_adapter = new AssignmentRecyclerAdapter(getActivity(), assignmentlist);
            assignmentTabRecyclerView.setAdapter(assignment_adapter);
        }


        assignmentTabRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                assignmentListData = assignmentlist.get(position);

                Intent assDetailIntent = new Intent(getActivity(), AssignmentDetail.class);
                Bundle intentBundle = new Bundle();
                intentBundle.putString("AssignId", assignmentListData.getAssId());

                intentBundle.putString("AssignStatus", assignmentListData.getAssStatus());
                intentBundle.putString("AssignAttachCount", assignmentListData.getAttachmentCount());
                intentBundle.putString("AssignMaxCredits", assignmentListData.getMaxCredits());
                intentBundle.putString("AssignDescription", assignmentListData.getDescription());
                intentBundle.putString("AssignSubject", assignmentListData.getSubject());
                intentBundle.putString("AssignIssueDate", dateFormat.format(assignmentListData.getIssueDate()));
                intentBundle.putString("AssignSubmittedBy", assignmentListData.getSubmittedBy());
                intentBundle.putString("AssignClassStd", assignmentListData.getClassStd());
                intentBundle.putString("AssignDivision", assignmentListData.getDivision());
                intentBundle.putString("AssignGradeEarned", assignmentListData.getGradeEarned());
                intentBundle.putString("AssignScoreType", assignmentListData.getScoreType());
                intentBundle.putString("AssignDueDate", dateFormat.format(assignmentListData.getDueDate()));
                intentBundle.putString("AssignStatusComment", assignmentListData.getNote());
                intentBundle.putString("AssignAttachmentIdentifier", assignmentListData.getAssignmentAttachmentIdentifier());
                intentBundle.putBoolean("AssignLikeCheck", assignmentListData.isLikeCheck());
                assDetailIntent.putExtras(intentBundle);
                startActivity(assDetailIntent);
            }
        });
        return rootView;
    }
}
