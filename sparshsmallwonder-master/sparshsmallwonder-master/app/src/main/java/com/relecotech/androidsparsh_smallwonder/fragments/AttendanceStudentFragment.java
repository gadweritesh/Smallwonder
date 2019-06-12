package com.relecotech.androidsparsh_smallwonder.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.activities.AttendanceStudentIndividual;
import com.relecotech.androidsparsh_smallwonder.models.AlertStudentListData;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DatabaseHandler;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceStudentFragment extends Fragment {

    private Spinner classSpinner, divisionSpinner, studentNameSpinner;
    private DatabaseHandler databaseHandler;
    private ArrayList<String> classList, divisionList;
    private HashMap<String, String> conditionHashMap;
    private Cursor resultSet;
    private String schoolClassId;
    private Map<String, List<AlertStudentListData>> schoolClassIdStudentHashMap;
    private List<String> studentNameList;
    private List<String> studentIdList;
    private ArrayAdapter<String> adapterStudentName;
    private ConnectionDetector connectionDetector;
    private MobileServiceClient mClient;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance_teacher_student_fragment, container, false);

        connectionDetector = new ConnectionDetector(getActivity());
        mClient = Singleton.Instance().mClientMethod(getActivity());

        classSpinner = (Spinner) rootView.findViewById(R.id.attendanceClassSpinner);
        divisionSpinner = (Spinner) rootView.findViewById(R.id.attendanceDivisionSpinner);
        studentNameSpinner = (Spinner) rootView.findViewById(R.id.attendanceStudentNameSpinner);

        Button attendanceViewButton = (Button) rootView.findViewById(R.id.attendanceView);

        classList = new ArrayList<>();
        divisionList = new ArrayList<>();
        studentNameList = new ArrayList<>();
        studentIdList = new ArrayList<>();

        conditionHashMap = new HashMap<>();
        schoolClassIdStudentHashMap = new HashMap<>();

        databaseHandler = new DatabaseHandler(getActivity());
        resultSet = databaseHandler.getClassDataByCursor();

        resultSet.moveToFirst();
        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!classList.contains(resultSet.getString(resultSet.getColumnIndex("class")))) {
                classList.add(resultSet.getString(resultSet.getColumnIndex("class")));
            }
            resultSet.moveToNext();
        }

        classList.add("[ Class ]");
        ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, classList);
        adapterClass.setDropDownViewResource(R.layout.spinner_item);
        classSpinner.setAdapter(adapterClass);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String classString = classSpinner.getSelectedItem().toString();
                divisionList.clear();
                if (!classString.contains("[ Class ]")) {
                    conditionHashMap.put("class", classString);
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    for (int i = 0; i < resultSet.getCount(); i++) {
                        if (!divisionList.contains(resultSet.getString(resultSet.getColumnIndex("division")))) {
                            divisionList.add(resultSet.getString(resultSet.getColumnIndex("division")));
                        }
                        resultSet.moveToNext();
                    }
                } else {
                    divisionList = new ArrayList<>();
                }
                divisionList.add("[ Division ]");
                ArrayAdapter<String> adapterDivision = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, divisionList);
                adapterDivision.setDropDownViewResource(R.layout.spinner_item);
                divisionSpinner.setAdapter(adapterDivision);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String divisionString = divisionSpinner.getSelectedItem().toString();

                if (!divisionString.equals("[ Division ]")) {
                    conditionHashMap.put("class", classSpinner.getSelectedItem().toString());
                    conditionHashMap.put("division", divisionString);
                    resultSet = databaseHandler.getClassDivSubjectData(conditionHashMap);
                    resultSet.moveToFirst();
                    schoolClassId = resultSet.getString(resultSet.getColumnIndex("schoolClassId"));

                    if (connectionDetector.isConnectingToInternet()) {
                        FetchStudentList();
                    } else {
                        FancyToast.makeText(getActivity(), getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        attendanceViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedStudentId = studentIdList.get(studentNameSpinner.getSelectedItemPosition());
                System.out.println(" selectedStudentId " + selectedStudentId);

                if (!classSpinner.getSelectedItem().toString().equals("[ Class ]")) {
                    if (!divisionSpinner.getSelectedItem().toString().equals("[ Division ]")) {
                        if (!studentNameSpinner.getSelectedItem().toString().equals("[ Select Student ]")) {

                            Intent studIndividualAttendanceIntent = new Intent(getActivity(), AttendanceStudentIndividual.class);
                            studIndividualAttendanceIntent.putExtra("studentId", selectedStudentId);
                            studIndividualAttendanceIntent.putExtra("studentName", studentNameSpinner.getSelectedItem().toString());
                            startActivity(studIndividualAttendanceIntent);
                        } else {
                            FancyToast.makeText(getActivity(), "Select Student", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        }
                    } else {
                        FancyToast.makeText(getActivity(), "Select Division", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    }
                } else {
                    FancyToast.makeText(getActivity(), "Select Class", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                }
            }
        });

        studentNameList.add("[ Select Student ]");
        studentIdList.add("DummyId");
        adapterStudentName = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, studentNameList);
        adapterStudentName.setDropDownViewResource(R.layout.spinner_dropdown_item);
        studentNameSpinner.setAdapter(adapterStudentName);
        studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);

        return rootView;
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
        try {
            studentNameList.clear();
            studentIdList.clear();
            JsonArray getJsonListResponse = response.getAsJsonArray();
            if (getJsonListResponse.size() == 0) {
                System.out.println("json not received");
                studentNameList.add("[ Select Student ]");
                studentIdList.add("DummyId");
            } else {
                for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                    JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                    String student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String student_fullName = student_firstName + " " + student_lastName;
                    System.out.println("student_Id" + student_Id);
                    System.out.println("student_count" + loop);
                    System.out.println("student_fullName" + student_fullName);

                            /*two lists, one to maintain name and other for id.
                            we could have instead used custom adapter with list<object>
                            but that results in addition of list data class and writting custom adapter
                            so to avoid that 2 lists are used for trial. this logic may sound bad but easy to write and deal with*/
                    studentNameList.add(student_fullName);
                    studentIdList.add(student_Id);

                }
                studentNameList.add("[ Select Student ]");
                studentIdList.add("DummyId");

                adapterStudentName.notifyDataSetChanged();
                studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);
            }

            System.out.println("studentSpinnerList  " + studentNameList.size());
            System.out.println("studentIdList  " + studentIdList.size());
        } catch (Exception e) {

        }
    }

}
