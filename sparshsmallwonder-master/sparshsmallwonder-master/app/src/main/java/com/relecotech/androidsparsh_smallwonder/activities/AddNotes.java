package com.relecotech.androidsparsh_smallwonder.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Teacher;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AddNotes extends AppCompatActivity {

    private Button notesSubmitButton;
    private static TextView timePickerTextView;
    private EditText descriptionEditText;
    private List<String> spinnerTagList;
    private Spinner tagSpinner, teacherSpinner;
    private ProgressDialog progressDialog;
    private Date meetingDate;
    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private JsonObject jsonObjectAddNote;
    private HashMap<String, String> teacherNameMap;
    private String teacherId;
    private MobileServiceTable<Teacher> mTeacherTable;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);

        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timePickerTextView = (TextView) findViewById(R.id.timepicker);
        notesSubmitButton = (Button) findViewById(R.id.notesSubmitButton);
        descriptionEditText = (EditText) findViewById(R.id.descriptioneditText);
        tagSpinner = (Spinner) findViewById(R.id.tagspinner);
        teacherSpinner = (Spinner) findViewById(R.id.tospinner);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        mTeacherTable = mClient.getTable(Teacher.class);

        teacherNameMap = new HashMap<>();
        spinnerTagList = new ArrayList<>();

        if (connectionDetector.isConnectingToInternet()) {
            new GetTeacherName().execute();
        } else {
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String teacherName = teacherSpinner.getSelectedItem().toString();
                System.out.println(" teacherName " + teacherName);
                teacherId = teacherNameMap.get(teacherName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerTagList.add("Meeting");
        spinnerTagList.add("Feedback");
        spinnerTagList.add("General Query");
        spinnerTagList.add("[ Category ]");


        ArrayAdapter<String> tagArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerTagList);
        tagSpinner.setAdapter(tagArrayAdapter);
        tagArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        tagSpinner.setSelection(tagArrayAdapter.getCount() - 1);


        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tagSelector = (String) tagSpinner.getItemAtPosition(position);
                switch (tagSelector) {
                    case "Select Tag":
                        break;
                    case "Meeting":
                        showTimePickerTextView();
                        break;
                    case "Feedback":
                        hideTimePickerTextView();
                    case "General Query":
                        hideTimePickerTextView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        notesSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (connectionDetector.isConnectingToInternet()) {
                    if (!tagSpinner.getSelectedItem().toString().equals("[ Category ]")) {
                        if (!teacherSpinner.getSelectedItem().toString().equals("[ Select Teacher ]")) {

                            if (descriptionEditText.length() > 0) {

                                String s = timePickerTextView.getText().toString();

                                if (tagSpinner.getSelectedItem().toString().equals("Meeting")) {
                                    if (s.contains("Select Time")) {

                                        FancyToast.makeText(AddNotes.this, "Select Time", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                                    } else {
                                        AddNotesApiCall();
                                    }
                                } else {
                                    AddNotesApiCall();
                                }
                            } else {
                                FancyToast.makeText(AddNotes.this, "Add Description", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                            }
                        } else {
                            FancyToast.makeText(AddNotes.this, "Select Teacher", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        }
                    } else {
                        FancyToast.makeText(AddNotes.this, "Select Category", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    }
                } else {
                    FancyToast.makeText(AddNotes.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });

        timePickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
    }


    private void hideTimePickerTextView() {

        // get the center for the clipping circle
        int cx = timePickerTextView.getWidth() / 2;
        int cy = timePickerTextView.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(timePickerTextView, cx, cy, initialRadius, 0);


            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    timePickerTextView.setVisibility(View.INVISIBLE);
                }
            });


            // start the animation
            anim.start();
        } else {
            timePickerTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void showTimePickerTextView() {
        //method to show transition to reveal

        // get the center for the clipping circle
        int cx = timePickerTextView.getWidth() / 2;
        int cy = timePickerTextView.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(timePickerTextView, cx, cy, 0, finalRadius);
        }

        // make the view visible and start the animation
        timePickerTextView.setVisibility(View.VISIBLE);
        if (anim != null) {
            anim.start();
        }
    }

    private void JsonObject() {

        Calendar calendar = Calendar.getInstance();

        jsonObjectAddNote = new JsonObject();

        if (tagSpinner.getSelectedItem().toString().equals("Meeting")) {

            String getMeetingSchedule = timePickerTextView.getText().toString();
            System.out.println("getMeetingSchedule " + getMeetingSchedule);
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.getDefault());
            try {
                meetingDate = dateFormat.parse(getMeetingSchedule);
                jsonObjectAddNote.addProperty("MeetingSchedule", meetingDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        jsonObjectAddNote.addProperty("Description_cause", descriptionEditText.getText().toString());
        jsonObjectAddNote.addProperty("Category", tagSpinner.getSelectedItem().toString());
        jsonObjectAddNote.addProperty("Student_id", userDetails.get(SessionManager.KEY_STUDENT_ID));
        jsonObjectAddNote.addProperty("Teacher_id", teacherId);
        jsonObjectAddNote.addProperty("School_class_id", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        jsonObjectAddNote.addProperty("Branch_id", userDetails.get(SessionManager.KEY_BRANCH_ID));
        jsonObjectAddNote.addProperty("PostDate", calendar.getTimeInMillis());
        jsonObjectAddNote.addProperty("Status", "Pending");
        jsonObjectAddNote.addProperty("Reply", "");
        jsonObjectAddNote.addProperty("Active", 1);
    }

    private void AddNotesApiCall() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject();

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("parentZoneInsertApi", jsonObjectAddNote);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Add Notes API exception    " + exception);
                failureDialog();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Add Notes API   response    " + response);
                if (response.toString().equals("true")) {
                    progressDialog.dismiss();

                    new AlertDialog.Builder(AddNotes.this)
                            .setTitle("Note Successfully Submitted.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                } else {
                    failureDialog();
                }
            }
        });
    }

    private void failureDialog() {
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
                new AlertDialog.Builder(AddNotes.this)
                        .setMessage(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AddNotesApiCall();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                }).show();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 5000);
    }


    private class GetTeacherName extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                List<Teacher> result = mTeacherTable.where().field("Branch_id").eq(userDetails.get(SessionManager.KEY_BRANCH_ID))
                        .select("id", "firstName", "lastName").execute().get();
                System.out.println("result " + result);

                for (int i = 0; i < result.size(); i++) {
                    String teacherFullName = result.get(i).getFirstName() + " " + result.get(i).getLastName();
//                    teacherNameList.add(teacherFullName);
//                    teacherNameMap.put(teacherFullName, result.get(i).getId());
                    if (!teacherNameMap.containsKey(teacherFullName)) {
                        teacherNameMap.put(teacherFullName, result.get(i).getId());
                    }
                }
            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            List<String> teacherNameList = new ArrayList<>(teacherNameMap.keySet());
            teacherNameList.add("[ Select Teacher ]");
            ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, teacherNameList);
            stringArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            teacherSpinner.setAdapter(stringArrayAdapter);
        }
    }

//    private void teacherSpinnerData() {
//
//        JsonObject jsonObjectFetchTeacher = new JsonObject();
//        jsonObjectFetchTeacher.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
//        jsonObjectFetchTeacher.addProperty("TAG", "Add_notes");
//
//        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
//        ListenableFuture<JsonElement> serviceFilterFuture = LauncherActivity.mobileServiceClient.invokeApi("parentZoneFetchApi", jsonObjectFetchTeacher);
//
//        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
//            @Override
//            public void onFailure(Throwable exception) {
//                resultFuture.setException(exception);
//                System.out.println("exception    " + exception);
//            }
//
//            @Override
//            public void onSuccess(JsonElement response) {
//                resultFuture.set(response);
//                System.out.println(" PARENT ZONE  ADD API   response    " + response);
//
//                JsonArray getJsonListResponse = response.getAsJsonArray();
//                try {
//                    if (getJsonListResponse.size() == 0) {
//                        System.out.println(" Json not received");
//                    }
//                    for (int g = 0; g < getJsonListResponse.size(); g++) {
//                        JsonObject jsonObjectforIterarion = getJsonListResponse.get(g).getAsJsonObject();
//                        String teacherID = jsonObjectforIterarion.get("id").toString().replace("\"", "");
//                        String teacherFirstName = jsonObjectforIterarion.get("firstName").toString().replace("\"", "");
//                        String teacherLastName = jsonObjectforIterarion.get("lastName").toString().replace("\"", "");
//                        String teacherFullName = teacherFirstName + " " + teacherLastName;
//
//                        if (!teacherNameMap.containsKey(teacherFullName)) {
//                            teacherNameMap.put(teacherFullName, teacherID);
//                        }
//                    }
//
//
//                    List<String> values = new ArrayList<>(teacherNameMap.keySet());
//                    values.add("[ Select Teacher ]");
//                    ArrayAdapter<String> teacherArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, values);
//                    teacherSpinner.setAdapter(teacherArrayAdapter);
//                    teacherSpinner.setSelection(teacherSpinner.getCount() - 1);
//                    teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            String teacherName = teacherSpinner.getSelectedItem().toString();
////                            String tagSelector = (String) teacherSpinner.getItemAtPosition(position);
//                            System.out.println(" teacherName " + teacherName);
//                            teacherId = teacherNameMap.get(teacherName);
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//
//                } catch (Exception e) {
//
//                }
//            }
//        });
//    }

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

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private static String date;
        String AM_PM;
        private int yy, mm, dd;
        private int mHour, mMinute;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            yy = calendar.get(java.util.Calendar.YEAR);
            mm = calendar.get(java.util.Calendar.MONTH);
            dd = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR);
            mMinute = calendar.get(Calendar.MINUTE);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd) {
                @Override
                public void onClick(DialogInterface dialog, int doneBtn) {
                    if (doneBtn == BUTTON_POSITIVE) {
                        int year = getDatePicker().getYear();
                        int month = getDatePicker().getMonth() + 1;
                        int day = getDatePicker().getDayOfMonth();
                        SelectDateFragment.date = day + "-" + month + "-" + year;
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12 && hourOfDay >= 0) {
                                    AM_PM = "AM";
                                } else {
                                    hourOfDay -= 12;
                                    if (hourOfDay == 0) {
                                        hourOfDay = 12;
                                    }
                                    AM_PM = "PM";
                                }
                                timePickerTextView.setText("" + date + " " + hourOfDay + ":" + minute + " " + AM_PM);
                            }
                        },
                                mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                    super.onClick(dialog, doneBtn);
                }

                @Override
                public void onDateChanged(DatePicker view, int Dyear, int DmonthOfYear, int DdayOfMonth) {
                    if (Dyear < yy)
                        view.updateDate(yy, mm, dd);

                    if (DmonthOfYear < mm && Dyear == yy)
                        view.updateDate(yy, mm, dd);

                    if (DdayOfMonth < dd && Dyear == yy && DmonthOfYear == mm)
                        view.updateDate(yy, mm, dd);

                }
            };
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        }

    }

}