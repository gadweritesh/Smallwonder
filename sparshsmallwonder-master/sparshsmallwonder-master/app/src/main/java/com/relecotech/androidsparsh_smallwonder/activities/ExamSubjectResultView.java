package com.relecotech.androidsparsh_smallwonder.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.ExamSubjectResultViewAdapter;
import com.relecotech.androidsparsh_smallwonder.models.ExamSubjectResultListData;

import java.util.ArrayList;

public class ExamSubjectResultView extends AppCompatActivity {

    private ListView subjectListView;
    private ArrayList<ExamSubjectResultListData> getSubjectListFromIntent;
    private ExamSubjectResultViewAdapter examSubjectresult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_subject_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subjectListView = (ListView) findViewById(R.id.subjectListView);

        Bundle bundle = getIntent().getExtras();
        ArrayList<ExamSubjectResultListData> arraylist = bundle.getParcelableArrayList("examList");
        System.out.println("arraylist--------------  " + arraylist);
        System.out.println("arraylist---size-----------  " + arraylist.size());

        examSubjectresult = new ExamSubjectResultViewAdapter(arraylist, ExamSubjectResultView.this);
        subjectListView.setAdapter(examSubjectresult);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

