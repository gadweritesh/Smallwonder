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
import com.relecotech.androidsparsh_smallwonder.activities.ApproveNotes;
import com.relecotech.androidsparsh_smallwonder.adapters.NotesAdapter;
import com.relecotech.androidsparsh_smallwonder.models.NotesListData;

import java.util.List;

/**
 * Created by amey on 8/6/2016.
 */
public class Parent_control_Notes_Fragment extends Fragment {
    ListView parentControlListView;
    private NotesAdapter notesAdapter;
    private List<NotesListData> getNotesListData;
    private NotesListData selectedNotesListData;
    private TextView parentcontrolNoDataAvailable_Notes_TextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parent_control_fragment, container, false);
        parentControlListView = (ListView) rootView.findViewById(R.id.parent_control_listView);
        parentcontrolNoDataAvailable_Notes_TextView = (TextView) rootView.findViewById(R.id.no_data_available_prent_control_textView);
        Bundle getBundleData = this.getArguments();
        getNotesListData = (List<NotesListData>) getBundleData.getSerializable("NotesList");

        System.out.println("getNotesListData " + getNotesListData);
        try {

            if (!getNotesListData.isEmpty() || getNotesListData != null) {
                notesAdapter = new NotesAdapter(getActivity(), getNotesListData);
                parentControlListView.setAdapter(notesAdapter);

            } else {
                System.out.println("Parents Zone Notes List is null");
                parentcontrolNoDataAvailable_Notes_TextView.setVisibility(View.VISIBLE);
                parentcontrolNoDataAvailable_Notes_TextView.setText(R.string.noDataAvailable);
            }
        } catch (Exception e) {
            parentcontrolNoDataAvailable_Notes_TextView.setVisibility(View.VISIBLE);
            parentcontrolNoDataAvailable_Notes_TextView.setText(R.string.noDataAvailable);
            System.out.println("getMessage Notes Fragments----" + e.getMessage());


        }

        parentControlListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedNotesListData = getNotesListData.get(position);
                Intent notesApproveIntent = new Intent(getActivity(), ApproveNotes.class);
                Bundle notesBundle = new Bundle();
                notesBundle.putString("Description", selectedNotesListData.getDescription());
                notesBundle.putString("Category", selectedNotesListData.getNoteCategory());
                notesBundle.putString("PostTime", selectedNotesListData.getPostDatetime());
                notesBundle.putString("MeetingSchedule", selectedNotesListData.getMeetingScheduleDateTime());
                notesBundle.putString("Status", selectedNotesListData.getNotesStatus());
                notesBundle.putString("Reply", selectedNotesListData.getReply());
                notesBundle.putString("ConcernedTeacher", selectedNotesListData.getConcernedTeacher());
                notesBundle.putString("ConcernedStudent", selectedNotesListData.getConcernedStudent());
                notesBundle.putString("StudentRollNo", selectedNotesListData.getStudentRollNo());
                notesBundle.putString("StudentId", selectedNotesListData.getStudentId());
                notesBundle.putString("NotesId", selectedNotesListData.getNoteId());
                notesApproveIntent.putExtras(notesBundle);
                startActivity(notesApproveIntent);
            }
        });
        return rootView;
    }
}
