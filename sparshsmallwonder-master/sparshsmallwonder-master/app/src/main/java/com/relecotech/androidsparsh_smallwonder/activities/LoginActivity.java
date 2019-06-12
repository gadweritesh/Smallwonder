package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;


import com.relecotech.androidsparsh_smallwonder.MainActivity;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.utils.AppNotificationHandler;
import com.relecotech.androidsparsh_smallwonder.utils.AzureConfiguration;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.DialogsUtils;
import com.relecotech.androidsparsh_smallwonder.utils.RegistrationIntentService;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.relecotech.androidsparsh_smallwonder.utils.UserData;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;

//import com.shashank.sony.fancytoastlib.FancyToast;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private ShowHidePasswordEditText mPasswordView;
    private ArrayAdapter<String> roleAdapter;
    private Spinner roleSelector;
    private ProgressDialog progressDialog;
    private JsonObject jsonObjectLoginCredentials;
    private SessionManager sessionManager;
    private String username;
    private String password;
    private String role;
    private ConnectionDetector connectionDetector;

    int activeStatusCheck = 0;
    UserData userData = null;
    SessionManager sessionManager1 = null;
    ArrayList<String> userNameList;
    private ArrayList<String> notificationTagList;
    private String userId, userEmail, userPassword, userRole, schoolClassId, studentId, branchId, adminId;
    private String branchName;
    private String userPin;
    private String activity;
    String firstName;
    String lastName, lastName2;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeSSLContext(this);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mEmailView = (EditText) findViewById(R.id.emailEditText);
        mPasswordView = (ShowHidePasswordEditText) findViewById(R.id.passwordEditText);
        roleSelector = (Spinner) findViewById(R.id.roleSpinner);

        roleSelector.setAdapter(roleAdapter);
        roleSelector.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.fontcolor), PorterDuff.Mode.SRC_ATOP);
        roleSelector.setAdapter(roleAdapter);
        roleSelector.getSolidColor();
        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        Button mCancelSignInButton = (Button) findViewById(R.id.cancel_in_button);
        TextView forgotPasswordTextView = (TextView) findViewById(R.id.forgotPassTextView);

        String[] roleList = getResources().getStringArray(R.array.role);

        roleAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.role_spinner_listitem, roleList);
        //roleAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_checked, roleList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        roleSelector.setAdapter(roleAdapter);


        Intent getNewUserIntent = getIntent();
        activity = getNewUserIntent.getStringExtra("Name");
        lastName2 = getNewUserIntent.getStringExtra("LastName");
        System.out.println("ACTIVITY "+activity);
        System.out.println("lastName2 "+ lastName2);



        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    textView.clearFocus();
                    roleSelector.requestFocus();
                    roleSelector.performClick();
                }
                return true;
            }
        });


        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = mEmailView.getText().toString().replaceAll(" ", "").trim();
                password = mPasswordView.getText().toString().replaceAll(" ", "").trim();
                role = roleSelector.getSelectedItem().toString();


                if (connectionDetector.isConnectingToInternet()) {
                    if (username.isEmpty()) {
                        mEmailView.setError("Enter Username");
                        mEmailView.requestFocus();
                    } else {
                        if (password.isEmpty()) {
                            mPasswordView.setError("Enter Password");
                            mPasswordView.requestFocus();
                        } else {
                            attemptLogin();
                        }
                    }
                } else {
                    FancyToast.makeText(LoginActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }

            }
        });

        mCancelSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.setText("");
                mPasswordView.setText("");
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

    }


    private void attemptLogin() {
        progressDialog = DialogsUtils.showProgressDialog(LoginActivity.this, getString(R.string.loading));

        jsonObjectLoginCredentials = new JsonObject();
        jsonObjectLoginCredentials.addProperty("userEmail", username);
        jsonObjectLoginCredentials.addProperty("userPassword", password);
        jsonObjectLoginCredentials.addProperty("userRole", role);

        System.out.println("username" + username);
        System.out.println("password" + password);
        System.out.println("role" + role);

        MobileServiceClient mClient = Singleton.Instance().mClientMethod(this);
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("loginApi", jsonObjectLoginCredentials);
        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);

                exception.printStackTrace();

                if (exception.getMessage().equals("false")) {
                    progressDialog.cancel();
                    DialogsUtils.showAlertDialog(LoginActivity.this, "Login Failed !", "Username or Password entered is incorrect.", false);
                } else if (exception.getMessage().equals("inactive")) {
                    progressDialog.cancel();
                    DialogsUtils.showAlertDialogOnlyTitle(LoginActivity.this, "User Inactive !", false);
                } else {
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage(R.string.check_network)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptLogin();
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //onBackPressed();
                                }
                            }).show();
                        }
                    };
                    Handler pdCanceller = new Handler();
                    pdCanceller.postDelayed(progressRunnable, 5000);
                }
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("response    " + response);
                System.out.println("response    " + response);

                if (response.toString().equalsIgnoreCase("\"inactive\"")) {
                    progressDialog.cancel();
                    DialogsUtils.showAlertDialogOnlyTitle(LoginActivity.this, "User Inactive !", false);
                } else {

                    JsonArray jsonArray = response.getAsJsonArray();
                    userNameList = new ArrayList<>();
                    notificationTagList = new ArrayList<>();

                    for (int loop = 0; loop < jsonArray.size(); loop++) {
                        JsonObject jsonObjectForProcessing = jsonArray.get(loop).getAsJsonObject();

                        userId = jsonObjectForProcessing.get("Users_id").toString().replace("\"", "");
                        userEmail = jsonObjectForProcessing.get("userEmail").toString().replace("\"", "");
                        userPassword = jsonObjectForProcessing.get("userPassword").toString().replace("\"", "");
                        userRole = jsonObjectForProcessing.get("userRole").toString().replace("\"", "");
                        activeStatusCheck = Integer.parseInt(jsonObjectForProcessing.get("userActive").toString());
                        userPin = jsonObjectForProcessing.get("userPin").toString().replace("\"", "");

                       /* firstName = jsonObjectForProcessing.get("firstName").toString().replace("\"", "");
                        lastName = jsonObjectForProcessing.get("lastName").toString().replace("\"", "");*/

                        String firstName = jsonObjectForProcessing.get("firstName").toString().replace("\"", "");
                        String lastName = jsonObjectForProcessing.get("lastName").toString().replace("\"", "");

                        String fullName = firstName + " " + lastName;



                        //Used nameList for Multi User Login
                        userNameList.add(fullName);

                        branchId = jsonObjectForProcessing.get("Branch_id").toString().replace("\"", "");
                        branchName = jsonObjectForProcessing.get("branchName").toString().replace("\"", "");

                        //Used notificationTagList for registration in notification Hub
                        notificationTagList.add(userRole);
                        notificationTagList.add(branchId);

//                    if (!LauncherActivity.studentSession.containsKey(studentId)) {
//                        System.out.println(" studentId " + studentId);
//                        LauncherActivity.studentSession.put(studentId, userData);
//
                        sessionManager = new SessionManager(getApplicationContext(), fullName);
                        if (userRole.equals("Student")) {
//                    if (!LauncherActivity.studentSession.containsKey(studentId)) {
//                        System.out.println(" studentId " + studentId);
//                        LauncherActivity.studentSession.put(studentId, userData);
//
                            studentId = jsonObjectForProcessing.get("id").toString().replace("\"", "");
                            schoolClassId = jsonObjectForProcessing.get("School_Class_id").toString().replace("\"", "");
                            String schoolClass = jsonObjectForProcessing.get("class").toString().replace("\"", "");
                            String division = jsonObjectForProcessing.get("division").toString().replace("\"", "");
                            String hostelStudentOrSchool = jsonObjectForProcessing.get("hostelStudents").toString().replace("\"", "");

                            notificationTagList.add(studentId);
                            notificationTagList.add(schoolClassId);
                            notificationTagList.add(hostelStudentOrSchool);

                            sessionManager.createLoginSession(userId, branchId, schoolClassId, fullName, studentId, "", "", userRole, branchName, userPin, schoolClass, division, "");
                        } else if (userRole.equals("Teacher")) {
                            String teacherId = jsonObjectForProcessing.get("id").toString().replace("\"", "");
                            String designation = jsonObjectForProcessing.get("designation").toString().replace("\"", "");
                            schoolClassId = jsonObjectForProcessing.get("school_class_id").toString().replace("\"", "");

                            notificationTagList.add(teacherId);

                            sessionManager.createLoginSession(userId, branchId, schoolClassId, fullName, "", teacherId, "", userRole, branchName, userPin, "", "", designation);
                        } else {
                            String designation = jsonObjectForProcessing.get("designation").toString().replace("\"", "");
                            adminId = jsonObjectForProcessing.get("id").toString().replace("\"", "");
                            sessionManager.createLoginSession(userId, branchId, "", fullName, "", "", adminId, userRole, branchName, userPin, "", "", designation);
                            notificationTagList.add(adminId);
                        }

                    }
                    sessionManager1 = new SessionManager(getApplicationContext());
                    ///////////////////////////////////////////////////////////////
                    if(activity.equals("NewUser"))
                    {
                        System.out.println("lastName "+ lastName);
                        System.out.println("lastName2 "+ lastName2);
                        if(lastName.equals(lastName2))
                        {
                            sessionManager1.createList2(userNameList);
                        }
                    }
                    else {
                        sessionManager1.createList(userNameList);
                    }
                    //sessionManager1 = new SessionManager(getApplicationContext());
                    //sessionManager1.createList(userNameList);

                    NotificationsManager.handleNotifications(LoginActivity.this, AzureConfiguration.SenderId, AppNotificationHandler.class);
                    registerWithNotificationHubs();

                    progressDialog.dismiss();
                    i = new Intent(getApplicationContext(), MainActivity.class);
                    if(activity.equals("NewUser"))
                    {
                        if(lastName.equals(lastName2)) {
                            i.putExtra("activity", "User");
                            i.putExtra("userNameList", userNameList);
                            i.putExtra("LastName", lastName);
                            i.putExtra("LastName2", lastName2);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            finish();
                        }
                        else
                        {
                            Toast toast = FancyToast.makeText(LoginActivity.this, "Sibling can not be added..Please check username", FancyToast.LENGTH_LONG, FancyToast.ERROR, false);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            onBackPressed();
                        }
                    }
                    else
                    {
                        i.putExtra("activity","Login");
                        i.putExtra("LastName",lastName);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                        finish();
                    }
                }
            }
        });
    }

    public void registerWithNotificationHubs() {
        //if (checkPlayServices()) {
        // Start IntentService to register this application with FCM.
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra("notificationTagList", notificationTagList);
        startService(intent);
        // }
    }

    public void initializeSSLContext(Context mContext) {
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


}

