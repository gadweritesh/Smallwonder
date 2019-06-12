package com.relecotech.androidsparsh_smallwonder.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.EditAlertAdapter;
import com.relecotech.androidsparsh_smallwonder.azureControllers.Alert;
import com.relecotech.androidsparsh_smallwonder.fragments.DatePickerFragment;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

public class EditAlertActivity extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.DateDialogListener  {
    private ArrayList<Alert> alertList;
    private LinearLayoutManager layoutManager;
    private MobileServiceClient mClient;
    private String userRole;
    private MobileServiceTable<Alert> alertListDataMobileServiceTable;
    private String alertId;
    private Alert alertItem;
    private List<String> alertClassList;
    private List<String> alertDivisionList;
    private List<String> alertCategoryList;
    private List<String> alertPriorityList;
    private EditAlertAdapter editAlertAdapter;
    private EditText editAlert;
    private Calendar calendar;
    private int year, month, day;
    private Calendar cal;
    private String post;
    private Date postForCalendar;
    private Calendar postCalendar;
    SimpleDateFormat format;
    RecyclerView recyclerView;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private MenuItem saveMenuItem;
    ArrayList<Alert> alertListData;
    private List<String> studentSpinnerList;
    private List<String> studentList;
    private String schoolClassId;
    private DatabaseHandler databaseHandler;
    private Cursor resultSet;
    private Map<String, String> schoolClassMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alert);
        mClient = Singleton.Instance().mClientMethod(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();
        postCalendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(getApplicationContext(), sharedPrefValue);
        userDetails = sessionManager.getUserDetails();

        userRole = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ROLE);

        alertListDataMobileServiceTable = mClient.getTable(Alert.class);

        alertItem = new Alert();

        alertClassList = new ArrayList<>();
        alertDivisionList = new ArrayList<>();
        alertCategoryList = new ArrayList<>();
        alertPriorityList = new ArrayList<>();
        alertList = new ArrayList<>();
        //studentSpinnerList = new ArrayList<>();
        //studentList = new ArrayList<>();
        schoolClassMap = new TreeMap<>();

        resultSet = databaseHandler.getClassDataByCursor();
        resultSet.moveToFirst();
        System.out.println(" resultSet.getCount() " + resultSet.getCount());
        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!schoolClassMap.containsKey(resultSet.getString(resultSet.getColumnIndex("class")) + " " + resultSet.getString(resultSet.getColumnIndex("division")))) {
                System.out.println("KEY "+resultSet.getString(resultSet.getColumnIndex("class"))+ " " + resultSet.getString(resultSet.getColumnIndex("division"))+" VALUE "+resultSet.getString(resultSet.getColumnIndex("schoolClassId")));
                schoolClassMap.put(resultSet.getString(resultSet.getColumnIndex("class")) + " " + resultSet.getString(resultSet.getColumnIndex("division")), resultSet.getString(resultSet.getColumnIndex("schoolClassId")));
            }
            resultSet.moveToNext();
        }

        for(Map.Entry map  : schoolClassMap.entrySet()){
            System.out.println("KEY "+map.getKey()+" VALUE "+map.getValue());

        }

        recyclerView = (RecyclerView) findViewById(R.id.userEditRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent getInfoIntent = getIntent();

        alertId = getInfoIntent.getStringExtra("alertID");
        alertItem = (Alert) getIntent().getSerializableExtra("alertObj");
        schoolClassId = schoolClassMap.get(alertItem.getAlertClass()+" "+alertItem.getAlertDivision());
        System.out.println("schoolClassId.........."+schoolClassId);

        alertListData = new ArrayList<Alert>();
        alertListData = (ArrayList<Alert>) getIntent().getSerializableExtra("setAlertListData");
        //alertListData.clear();

        System.out.println(alertListData.size());
        for (int loop = 0; loop < alertListData.size()-1; loop++) {
            alertList.add(new Alert(alertListData.get(loop).getTitle(), alertListData.get(loop).getDescription(), alertListData.get(loop).getEditable()));

            System.out.println("title " + alertListData.get(loop).getTitle());
            System.out.println("Description " + alertListData.get(loop).getDescription());
        }

        //FetchStudentList();

        editAlertAdapter = new EditAlertAdapter(EditAlertActivity.this, alertList);
        recyclerView.setAdapter(editAlertAdapter);

        recyclerView.addOnItemTouchListener(new EditAlertAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new EditAlertAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (alertList.get(position).getEditable()) {

                    String columnTitle = alertList.get(position).getTitle();
                    String columnName = alertList.get(position).getTitle();
                    String desc = alertList.get(position).getDescription();

                    System.out.println("AlERT_ID " + alertId);
                    System.out.println("AlERT_TITLE " + columnTitle);
                    System.out.println("AlERT_TITLE_2 " + columnName);
                    System.out.println("AlERT_DESCRIPTION " + desc);

                    if (columnName.equals("Class")) {
                        pickListDialog(position);
                    } else if (columnName.equals("Division")) {
                        pickDivisionDialog(position);
                    } else if (columnName.equals("Category")) {
                        pickCategoryDialog(position);
                    } else if (columnName.equals("Priority")) {
                        pickPriorityDialog(position);
                    }
                    /*else if (columnName.equals("Student")) {
                        pickStudentDialog(position);
                    }*/
                    else {
                        editDialog(position, columnTitle, columnName, desc);
                    }
                } else {
                    Toast.makeText(EditAlertActivity.this, "Cannot update this", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    /*private void pickStudentDialog(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student List ");


        System.out.println("studentSpinnerList "+studentSpinnerList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.clear();
        for (int i = 0; i < studentSpinnerList.size(); i++) {
            arrayAdapter.add(studentSpinnerList.get(i));
        }
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                alertList.get(positionOfColumn).setDescription(studentSpinnerList.get(position));
                alertItem.setAlertStudId(studentList.get(position));
                editAlertAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }*/

    private void pickDivisionDialog(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Division");
        alertDivisionList = Arrays.asList(getResources().getStringArray(R.array.division));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.clear();

        for (int i = 0; i < alertDivisionList.size(); i++) {
            arrayAdapter.add(alertDivisionList.get(i));
        }
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                alertList.get(positionOfColumn).setDescription(alertDivisionList.get(position));
                alertItem.setAlertDivision(alertDivisionList.get(position));

                //schoolClassId = schoolClassMap.get(alertItem.getAlertClass()+" "+alertItem.getAlertDivision());
                //FetchStudentList();
                //alertList.get(6).setDescription("All");
                //alertItem.setAlertStudId(studentList.get(2));
                alertItem.setAlertStudId("All");
                editAlertAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void pickPriorityDialog(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Priority");
        alertPriorityList = Arrays.asList(getResources().getStringArray(R.array.priority));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        for (int i = 0; i < alertPriorityList.size(); i++) {
            arrayAdapter.add(alertPriorityList.get(i));
        }
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                alertList.get(positionOfColumn).setDescription(alertPriorityList.get(position));
                alertItem.setAlert_priority(alertPriorityList.get(position));
                editAlertAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void pickCategoryDialog(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category");
        alertCategoryList = Arrays.asList(getResources().getStringArray(R.array.alertCategory));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        for (int i = 0; i < alertCategoryList.size(); i++) {
            arrayAdapter.add(alertCategoryList.get(i));
        }
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                alertList.get(positionOfColumn).setDescription(alertCategoryList.get(position));
                alertItem.setCategory(alertCategoryList.get(position));
                editAlertAdapter.notifyDataSetChanged();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pickListDialog(final int positionOfColumn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Class");
        alertClassList = Arrays.asList(getResources().getStringArray(R.array.classes));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        for (int i = 0; i < alertClassList.size(); i++) {
            arrayAdapter.add(alertClassList.get(i));
        }

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,  int position) {
                alertList.get(positionOfColumn).setDescription(alertClassList.get(position));
                alertItem.setAlertClass(alertClassList.get(position));

                //schoolClassId = schoolClassMap.get(alertItem.getAlertClass()+" "+alertItem.getAlertDivision());
                //FetchStudentList();
                //alertList.get(6).setDescription(studentSpinnerList.get(2));
                //alertList.get(6).setDescription("All");
                alertItem.setAlertStudId("All");
                editAlertAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editDialog(final int position, String columnTitle, final String columnName, String description) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_alert_custom_dialog, null);
        dialogBuilder.setView(dialogView);
        editAlert = (EditText) dialogView.findViewById(R.id.edit1);

        System.out.println(" columnName " + columnName);

        if (description != null) {
            editAlert.setText(description);
            editAlert.setSelection(description.length());
        }

        dialogView.requestFocus();

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage("Edit " + columnTitle);
        dialogBuilder.setPositiveButton("Save", null);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (columnName.equalsIgnoreCase("Title")) {
                    if (editAlert.getText().toString().trim().length() > 0) {
                        alertList.get(position).setDescription(editAlert.getText().toString());
                        editAlertAdapter.notifyDataSetChanged();
                        switchCaseForUpdate(columnName, editAlert.getText().toString().trim());
                        dialog.dismiss();
                    } else {
                        editAlert.setError("Please Enter Short Name");
                    }
                }
                else if (columnName.equals("Submitted_By")) {
                    if (editAlert.getText().length() > 0) {
                        alertList.get(position).setDescription(editAlert.getText().toString());
                        editAlertAdapter.notifyDataSetChanged();
                        switchCaseForUpdate(columnName, editAlert.getText().toString());
                        dialog.dismiss();
                    } else {
                        editAlert.setError("Please Enter Name");
                    }
                }
                else {
                    alertList.get(position).setDescription(editAlert.getText().toString());
                    editAlertAdapter.notifyDataSetChanged();
                    switchCaseForUpdate(columnName, editAlert.getText().toString());
                    dialog.dismiss();
                }
            }
        });
    }

    private void switchCaseForUpdate(String columnName, String value) {
        System.out.println("columnName " + columnName);
        System.out.println("value " + value);

        alertItem.setAlertId(alertId);
        System.out.println("id " + alertId);

        switch (columnName) {
            case "Title":
                System.out.println("Title " + value);
                alertItem.setTitle(value);
                break;
            case "Description":
                System.out.println("Descri " + value);
                alertItem.setDescription(value);
                break;
            case "Priority":
                alertItem.setAlert_priority(value);
                break;
            case "Category":
                System.out.println("Category" + value);
                alertItem.setCategory(value);
                break;
            case "Submitted_By":
                System.out.println("Submitee" + value);
                alertItem.setSubmitted_By_to(value);
                break;
            case "Student_Id":
                System.out.println("Std_id" + value);
                alertItem.setAlertStudId(value);
                break;
            case "Class":
                System.out.println("class" + value);
                alertItem.setAlertClass(value);
                break;
            case "Division":
                System.out.println("Division" + value);
                alertItem.setAlertDivision(value);
                break;
            default:
                System.out.println("Not in 10, 20 or 30");
        }
        new AsyncTaskRunner().execute();
    }

    @Override
    public void onFinishDialog(String date) {
        editAlert.setText(date);
        System.out.println(" Date " + date);
    }


    private class AsyncTaskRunner extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                alertItem.setBranch_id(userDetails.get(SessionManager.KEY_BRANCH_ID));
                alertItem.setAlertId(alertId);

                Alert alert = alertListDataMobileServiceTable.update(alertItem).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == editAlert) {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            if (editAlert.getText().length() == 0) {
                cal = Calendar.getInstance();
                post = format1.format(cal.getTime());
                System.out.println(" Inside if ");
            } else {
                System.out.println(" Inside else");
                post = editAlert.getText().toString();
            }

            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("post", post);
            bundle.putString("value", "lower");
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        }
    }

    @Override
    protected void onRestart() {
        alertListData.clear();
        super.onRestart();
        System.out.println(" INSIDE onRestart ");
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finish();
    }

    private void saveMethod()
    {
        //alertItem.setAlertStudId(" ");
        //saveMenuItem.setEnabled(true);
        new AsyncTaskRunner().execute();
        Toast.makeText(EditAlertActivity.this,"Save Changes Successfully...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alert_edit_menu, menu);
        saveMenuItem = menu.findItem(R.id.action_save);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                saveMethod();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void FetchStudentList() {

        JsonObject jsonObjectForStudentList = new JsonObject();
        jsonObjectForStudentList.addProperty("SchoolClassId", schoolClassId);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("fetchStudentListApi", jsonObjectForStudentList);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Fetch Student List Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Fetch Student List  API   response    " + response);
                StudentListJsonParse(response);
            }
        });
    }

    private void StudentListJsonParse(JsonElement response) {
        JsonArray getJsonListResponse = response.getAsJsonArray();
        studentSpinnerList = new ArrayList<>();
        studentList = new ArrayList<>();
        try {
            if (getJsonListResponse.size() == 0) {
                System.out.println("json not received");
            } else {
                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                    String student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String student_phoneNo = jsonObjectForIteration.get("phone").toString().replace("\"", "");
                    String student_rollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");

                    String student_fullName = student_firstName + " " + student_lastName;

                    studentSpinnerList.add(student_fullName);
                    System.out.println("studentSpinnerList "+studentSpinnerList);
                    studentList.add(student_Id);
                }
                studentList.add("All");
                studentSpinnerList.add("All");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
