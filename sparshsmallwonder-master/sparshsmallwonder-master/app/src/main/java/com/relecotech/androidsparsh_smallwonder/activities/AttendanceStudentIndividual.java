package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.models.AttendanceListData;
import com.relecotech.androidsparsh_smallwonder.models.StudentAttendanceListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class AttendanceStudentIndividual extends AppCompatActivity {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole, studentId, branchId;
    private ProgressDialog progressDialog;
    private StudentAttendanceListData studentAttendanceListData;
    private HashMap<Integer, StudentAttendanceListData> attendanceStudentHashMap;
    private HashMap<Integer, String> monthNameHashMap;
    private int presentStatusCount;
    private List<String> monthList;
    private Spinner monthSelector;
    private TreeSet<Integer> keys;
    private CombinedChart combinedChart;
    private float percentageOfAttendance;
    private int percentageInInteger;
    private TextView monthPercent;
    private List<AttendanceListData> attendanceReportList;
    private Map<Integer, List<AttendanceListData>> studentAttendanceReportMap;
    private MobileServiceClient mClient;
    private ArrayAdapter<String> monthAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_student_individual);

        mClient = Singleton.Instance().mClientMethod(this);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        branchId = userDetails.get(SessionManager.KEY_BRANCH_ID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        monthPercent = (TextView) findViewById(R.id.monthCountIndividualStudentReview);
        monthSelector = (Spinner) findViewById(R.id.attendance_month_selector);

        monthList = new ArrayList<>();
        monthList.add(0, "Select Month");
        monthAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, monthList);
        monthAdapter.setDropDownViewResource(R.layout.spinner_item);
        monthSelector.setAdapter(monthAdapter);

        combinedChart = (CombinedChart) findViewById(R.id.individualStudentAttendanceCombinedChart);
        TextView monthlyReportTextView = (TextView) findViewById(R.id.month_report_Detail);
        monthlyReportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monthSelector.getSelectedItem() != null) {
                    if (!monthSelector.getSelectedItem().toString().equals("Select Month")) {
                        Intent monthlyAttendaceReportIntent = new Intent(AttendanceStudentIndividual.this, MonthlyAttendanceReport.class);
                        monthlyAttendaceReportIntent.putExtra("Student_id", userDetails.get(SessionManager.KEY_STUDENT_ID));
                        monthlyAttendaceReportIntent.putExtra("Selected_month", monthSelector.getSelectedItem().toString());
                        monthlyAttendaceReportIntent.putStringArrayListExtra("MonthList", (ArrayList<String>) monthList);
                        monthlyAttendaceReportIntent.putExtra("AttendanceReportMap", (Serializable) studentAttendanceReportMap);
                        startActivity(monthlyAttendaceReportIntent);
                    } else {
                        FancyToast.makeText(AttendanceStudentIndividual.this, "Select Month", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                } else {
                    System.out.println(" No data Available ");
                }
            }
        });


        if (userRole.equals("Student")) {
            studentId = userDetails.get(SessionManager.KEY_STUDENT_ID);
            System.out.println(" studentId IF userRole " + studentId);
        } else {
            studentId = getIntent().getStringExtra("studentId");
            System.out.println(" studentId else userRole " + studentId);
            String selectedStudentName = getIntent().getStringExtra("studentName");
        }

        studentAttendanceReportMap = new HashMap<>();
        attendanceStudentHashMap = new HashMap<>();
        monthNameHashMap = new HashMap<>();
        monthNameHashMap.put(1, "January");
        monthNameHashMap.put(2, "February");
        monthNameHashMap.put(3, "March");
        monthNameHashMap.put(4, "April");
        monthNameHashMap.put(5, "May");
        monthNameHashMap.put(6, "June");
        monthNameHashMap.put(7, "July");
        monthNameHashMap.put(8, "August");
        monthNameHashMap.put(9, "September");
        monthNameHashMap.put(10, "October");
        monthNameHashMap.put(11, "November");
        monthNameHashMap.put(12, "December");

        if (connectionDetector.isConnectingToInternet()) {
            FetchStudentAttendance();
        } else {
            FancyToast.makeText(AttendanceStudentIndividual.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        monthSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) monthSelector.getItemAtPosition(position);
                Log.d("selected Item Position", "" + s);
                switch (s) {
                    case "January":
                        percentageOfAttendance = getPercentageOfAttendance(1);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "February":

                        percentageOfAttendance = getPercentageOfAttendance(2);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "March":

                        percentageOfAttendance = getPercentageOfAttendance(3);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "April":

                        percentageOfAttendance = getPercentageOfAttendance(4);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "May":

                        percentageOfAttendance = getPercentageOfAttendance(5);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "June":

                        percentageOfAttendance = getPercentageOfAttendance(6);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "July":

                        percentageOfAttendance = getPercentageOfAttendance(7);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "August":

                        percentageOfAttendance = getPercentageOfAttendance(8);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "September":

                        percentageOfAttendance = getPercentageOfAttendance(9);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "October":

                        percentageOfAttendance = getPercentageOfAttendance(10);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "November":

                        percentageOfAttendance = getPercentageOfAttendance(11);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;
                    case "December":

                        percentageOfAttendance = getPercentageOfAttendance(12);
                        percentageInInteger = Math.round(percentageOfAttendance);
                        monthPercent.setText(Integer.toString(percentageInInteger) + "%");
                        break;


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void FetchStudentAttendance() {
        progressDialog = DialogsUtils.showProgressDialog(this, getString(R.string.loading));

        JsonObject jsonObjectToFetchAttendance = new JsonObject();
        jsonObjectToFetchAttendance.addProperty("studentId", studentId);
        jsonObjectToFetchAttendance.addProperty("userRole", "Student");

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("attendanceReviewApi", jsonObjectToFetchAttendance);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Attendance Individual exception    " + exception.getMessage());
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AttendanceStudentIndividual.this)
                                .setMessage(R.string.check_network)
                                .setCancelable(false)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FetchStudentAttendance();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 5000);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Attendance Individual    response    " + response);
                AttendanceJsonParse(response);
            }
        });
    }

    private void AttendanceJsonParse(JsonElement attendanceResponse) {
        try {
            JsonArray attendanceJsonArray = attendanceResponse.getAsJsonArray();
            if (attendanceJsonArray.size() == 0) {
                progressDialog.dismiss();
            } else {
                attendanceReportList = new ArrayList<AttendanceListData>();
                for (int loop = 0; loop < attendanceJsonArray.size(); loop++) {
                    JsonObject jsonObjectIteration = attendanceJsonArray.get(loop).getAsJsonObject();
                    String attendanceStatus = jsonObjectIteration.get("attendanceStatus").toString().replace("\"", "");
                    String attendanceDate = jsonObjectIteration.get("attendanceDate").toString().replace("\"", "");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy", Locale.getDefault());
//                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault());
                    Date attendanceDateInDate;
                    int totalNoOfDays;
                    try {
                        attendanceDateInDate = simpleDateFormat.parse(attendanceDate);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());

                        String studentAttendanceDate = targetDateFormat.format(attendanceDateInDate);
                        int month = (attendanceDateInDate.getMonth() + 1);

                        if (!attendanceStudentHashMap.containsKey(month)) {
                            System.out.println("In IF Condition...........");
                            presentStatusCount = 0;
                            totalNoOfDays = 0;
                            if (attendanceStatus.contains("P")) {
                                presentStatusCount++;
                            }
                            studentAttendanceListData = new StudentAttendanceListData(month, presentStatusCount, totalNoOfDays + 1);
                            attendanceStudentHashMap.put(month, studentAttendanceListData);

                            attendanceReportList = new ArrayList<AttendanceListData>();
                            attendanceReportList.add(new AttendanceListData(studentAttendanceDate, attendanceStatus));
                            System.out.println("Month ------  IF Part ---- " + month + "     -------------  studentAttendanceDate  " + studentAttendanceDate);
                            studentAttendanceReportMap.put(month, attendanceReportList);
                        } else {
                            System.out.println("In ELSE Condition...........");
                            StudentAttendanceListData value = attendanceStudentHashMap.get(month);
                            totalNoOfDays = value.getTotalDays() + 1;
                            if (attendanceStatus.contains("P")) {
                                presentStatusCount = value.getPresentDays() + 1;
                            }
                            studentAttendanceListData = new StudentAttendanceListData(month, presentStatusCount, totalNoOfDays);
                            attendanceStudentHashMap.put(month, studentAttendanceListData);


                            attendanceReportList = studentAttendanceReportMap.get(month);

                            attendanceReportList.add(new AttendanceListData(studentAttendanceDate, attendanceStatus));
                            System.out.println("Month ------  Else Part ---- " + month + "  ======================   studentAttendanceDate  " + studentAttendanceDate);
                            studentAttendanceReportMap.put(month, attendanceReportList);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                keys = new TreeSet<Integer>(attendanceStudentHashMap.keySet());
                for (Integer key : keys) {
                    monthList.add(0, monthNameHashMap.get(key));
                }

                for (Map.Entry<Integer, List<AttendanceListData>> entry : studentAttendanceReportMap.entrySet()) {
                    System.out.println(entry.getKey() + "--------------------////////////////////////////---------" + entry.getValue().size());
                }
//                monthAdapter.notifyDataSetChanged();
                monthAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, monthList);
                monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                monthSelector.setAdapter(monthAdapter);
                DrawGraph();
                progressDialog.dismiss();

            }
        } catch (Exception e) {
            System.out.println("Exception AttendanceJsonParse " + e.getMessage());
        }

    }

    private void DrawGraph() {
        CombinedData data = new CombinedData(getXAxisValues());

        data.setData(barData());
        data.setData(lineData());

        combinedChart.setData(data);
        combinedChart.setDrawGridBackground(false);
        combinedChart.getXAxis().setDrawGridLines(false);

        combinedChart.setNoDataTextDescription("Data unavailable");
        combinedChart.setDescription(null);
        combinedChart.animateXY(1000, 1000);
        combinedChart.setScaleEnabled(false);
        combinedChart.setHighlightPerDragEnabled(false);
        combinedChart.setHighlightPerTapEnabled(false);

        combinedChart.getAxisRight().setDrawLabels(false);
        combinedChart.getAxisRight().setEnabled(false);
        combinedChart.getAxisLeft().setEnabled(false);
        combinedChart.getAxisLeft().setEnabled(false);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    // this method is used to create data for line graph
    public LineData lineData() {
        ArrayList<Entry> line = new ArrayList<>();
        int lineDataPos = 0;
        keys = new TreeSet<Integer>(attendanceStudentHashMap.keySet());
        for (Integer key : keys) {
            if (key == 0) {
                lineDataPos = key + 1;
            } else {
                lineDataPos = key - 1;
            }

            int presentDay = attendanceStudentHashMap.get(key).getPresentDays();
            float newFloat = (float) presentDay;
            line.add(new Entry(newFloat, lineDataPos));
            lineDataPos++;
        }

        LineDataSet lineDataSet = new LineDataSet(line, "Days Present");

        lineDataSet.setColor(Color.parseColor("#FF4081"));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleRadius(9.0f);
        lineDataSet.setCubicIntensity(0.1f);
        lineDataSet.setValueFormatter(new FloatToIntFormatter());
        lineDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setValueTextSize(16f);

        LineData lineData = new LineData(getXAxisValues(), lineDataSet);

        return lineData;
    }

    public BarData barData() {

        ArrayList<BarEntry> group1 = new ArrayList<>();
        int barDataPos = 0;
        keys = new TreeSet<Integer>(attendanceStudentHashMap.keySet());

        for (Integer key : keys) {
            if (key == 0) {
                barDataPos = key + 1;
            } else {
                barDataPos = key - 1;
            }

            int totalDays = attendanceStudentHashMap.get(key).getTotalDays();
            int presentDay = attendanceStudentHashMap.get(key).getPresentDays();
            float newFloat = (float) totalDays;
            group1.add(new BarEntry(newFloat, barDataPos));
            barDataPos++;
        }

        BarDataSet barDataSet = new BarDataSet(group1, "Total Days");
        barDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        barDataSet.setValueFormatter(new FloatToIntFormatter());
        barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(getXAxisValues(), barDataSet);
        return barData;
    }

    // creating list of x-axis values
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Jan");
        labels.add("Feb");
        labels.add("Mar");
        labels.add("Apr");
        labels.add("May");
        labels.add("Jun");
        labels.add("Jul");
        labels.add("Aug");
        labels.add("Sep");
        labels.add("Oct");
        labels.add("Nov");
        labels.add("Dec");

        return labels;
    }

    public class FloatToIntFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return "" + ((int) value);
        }
    }

    public float getPercentageOfAttendance(int i) {
        /* method to calculate percentage of Attendance*/
        int totalPresentDays = attendanceStudentHashMap.get(i).getPresentDays();
        int totalDays = attendanceStudentHashMap.get(i).getTotalDays();
        float percentage = (float) ((totalPresentDays) * 100 / totalDays);
        return percentage;
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
