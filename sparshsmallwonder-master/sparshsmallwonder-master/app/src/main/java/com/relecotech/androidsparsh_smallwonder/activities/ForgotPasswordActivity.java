package com.relecotech.androidsparsh_smallwonder.activities;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.fragments.DatePickerFragment;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Relecotech on 12-01-2018.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements DatePickerFragment.DateDialogListener {

    public EditText dobEditText, mobileNoEditText;
    private EditText firstNameEditText, lastNameEditText;
    private Button sign_in;
    private Calendar cal;
    private String dob,dob1;
    private Date date;
    private MobileServiceClient mClient;
    private String userEmail, userPassword;
    private ProgressDialog forgotProgressDialog;
    private String userFirstName, userLastName;
    private ConnectionDetector connectionDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mClient = Singleton.Instance().mClientMethod(this);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        mobileNoEditText = (EditText) findViewById(R.id.mobileNoEditText);
        dobEditText = (EditText) findViewById(R.id.dobEditText);
        dobEditText.setInputType(InputType.TYPE_NULL);
        sign_in = (Button) findViewById(R.id.sign_in_button);

        mobileNoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    textView.clearFocus();
                    sign_in.requestFocus();
                    sign_in.performClick();
                }
                return true;
            }
        });



        forgotProgressDialog = new ProgressDialog(this);
        forgotProgressDialog.setMessage("Please Wait...");
        forgotProgressDialog.setCancelable(false);

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                System.out.println(" Inside if date " +dobEditText.getText().length());
                if (dobEditText.getText().length() == 0) {
                    cal = Calendar.getInstance();
                    dob = format1.format(cal.getTime());
                    System.out.println(" Inside if " +dob);
                } else {
                    try {
                        dob1 = dobEditText.getText().toString();
                        System.out.println(" Date " +dob1);
                        Date date1 = format1.parse(dob1);
                        System.out.println(" Date " +date1);
                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dob = targetFormat.format(date1);
                        System.out.println(" Date " +dob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(" Inside else " +dob);
                }

                DialogFragment newFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("dob", dob);
                bundle.putString("value", "lower");
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public void onFinishDialog(String date) {
        dobEditText.setText(date);
        System.out.println(" Date " + date);
    }

    public void getPasswordClick(View view) {
        userFirstName = firstNameEditText.getText().toString().replaceAll(" ", "").trim();
        userLastName = lastNameEditText.getText().toString().replaceAll(" ", "").trim();

        if(!(dobEditText.getText().length() == 0)) {
            try {
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                dob1 = dobEditText.getText().toString();
                System.out.println(" Date " + dob1);
                Date date1 = format1.parse(dob1);
                System.out.println(" Date " + date1);
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                dob = targetFormat.format(date1);
                System.out.println(" Date " + dob);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
        {
            dob = dobEditText.getText().toString();
        }

        if (connectionDetector.isConnectingToInternet()) {
            if (userFirstName.isEmpty()) {
                firstNameEditText.setError("Enter First Name");
                firstNameEditText.requestFocus();
            } else {
                if (userLastName.isEmpty()) {
                    lastNameEditText.setError("Enter Last Name");
                    lastNameEditText.requestFocus();
                } else {
//                    if (dob.isEmpty() || mobileNoEditText.getText().toString().length() == 0) {
//                        FancyToast.makeText(ForgotPasswordActivity.this, "Enter DOB/Mobile No.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
//                    } else {
//                        fetchPassword();
//                    }
                    if (dob.length()>0 || mobileNoEditText.getText().toString().length() > 0) {
                        fetchPassword();
                    }else {
                        FancyToast.makeText(ForgotPasswordActivity.this, "Enter DOB/Mobile No.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }

                }
            }
        } else {
            FancyToast.makeText(ForgotPasswordActivity.this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    private void fetchPassword() {
        forgotProgressDialog.show();

        JsonObject jsonFetchUserDetails = new JsonObject();
        jsonFetchUserDetails.addProperty("userFirstName", userFirstName);
        jsonFetchUserDetails.addProperty("userLastName", userLastName);
//        jsonFetchUserDetails.addProperty("dob", dob);

        if (dob.length() == 0) {
            System.out.println("if dob -- null");
            jsonFetchUserDetails.addProperty("dob", "");
        } else {
            System.out.println("else dob -- null");
            jsonFetchUserDetails.addProperty("dob",dob);
        }


        if (mobileNoEditText.getText().toString().length() == 0) {
            System.out.println("if--- null");
            jsonFetchUserDetails.addProperty("mobileNo", "null");
        } else {
            System.out.println("else--- null");
            jsonFetchUserDetails.addProperty("mobileNo", mobileNoEditText.getText().toString());
        }

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("forgotPasswordApi", jsonFetchUserDetails);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception.getMessage());
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        forgotProgressDialog.cancel();
                        new AlertDialog.Builder(ForgotPasswordActivity.this)
                                .setTitle("Oops ! Something Went Wrong.")
                                .setMessage("Please Try Again")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
                System.out.println("response    " + response);
                parseForgotPasswordJson(response);
            }
        });
    }

    private void parseForgotPasswordJson(JsonElement response) {
        forgotProgressDialog.dismiss();
        if (response.toString().contains("false")) {
            System.out.println("False");

            new AlertDialog.Builder(ForgotPasswordActivity.this)
                    .setTitle("Oops ! No Record Found.")
                    .setMessage("\nPlease Contact School Admin.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).show();

        } else {
            JsonArray userDataJsonArray = response.getAsJsonArray();

            if (userDataJsonArray.size() != 0) {
                for (int i = 0; i < userDataJsonArray.size(); i++) {
                    JsonObject jsonObjectForIteration = userDataJsonArray.get(i).getAsJsonObject();
                    userEmail = jsonObjectForIteration.get("userEmail").toString().replace("\"", "");
                    userPassword = jsonObjectForIteration.get("userPassword").toString().replace("\"", "");
                }

                System.out.println("userEmail-------------------  " + userEmail);
                System.out.println("userPassword-------------------  " + userPassword);

                CustomAlertDialog(userEmail, userPassword);
            } else {
                System.out.println("json array is null ");
            }
        }
    }

    private void CustomAlertDialog(String userID, String userPassword) {

        LayoutInflater inflater = ForgotPasswordActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.forgot_dialoge_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle("Your Login Detail ");
        TextView dialogUserID = (TextView) view.findViewById(R.id.userIDTextView);
        TextView dialogPassword = (TextView) view.findViewById(R.id.passwordTextView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //checkOnBackPressed = true;
                dialogInterface.dismiss();
                //  onBackPressed();
            }
        });

        dialogUserID.setText(Html.fromHtml("<font color=\"#000000\">" + "User : " + "</font>" + userID));
        dialogPassword.setText(Html.fromHtml("<font color=\"#000000\">" + "Password : " + "</font>" + userPassword));

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
