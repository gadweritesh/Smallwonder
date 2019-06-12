package com.relecotech.androidsparsh_smallwonder.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.relecotech.androidsparsh_smallwonder.models.AbsentStudentNameNumber;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Amey on 11-07-2017.
 */

public class SendSmsManager {

//            String url = "http://www.smsjust.com/sms/user/urlsms.php?username=gurukul12&pass=swamiji1!&senderid=GURKUL&dest_mobileno=" + getAbsentList.toString().replace(", ", ",").replace("[", "").replace("]", "") + "&message=" + SmsDescription.replace(" ", "%20") + "&response=Y;";
//            String urlj = "http://103.16.101.52:8080/bulksms/bulksms?username=aspi-aspire&password=aspire&type=1&dlr=1&destination=" + getAbsentList.toString().replace(", ", ",").replace("[", "").replace("]", "") + "&source=Aspire&message=" + SmsDescription.replace(" ", "%20") + ";";
//            String url = "http://103.16.101.52:8080/bulksms/bulksms?username=" + username + "&password=" + password + "&type=1&dlr=1&destination=" + getAbsentList.toString().replace(", ", ",").replace("[", "").replace("]", "") + "&source=Aspire&message=" + SmsDescription.replace(" ", "%20") + ";";
//            String url = "http://bhashsms.com/api/sendmsg.php?user=success&pass=654321&sender=SUCCES&phone=" +  getAbsentList.toString().replace(", ", ",").replace("[", "").replace("]", "") + "&text=" + SmsDescription.replace(" ", "%20") + "&priority=ndnd&stype=normal";
//            String url = "https://control.msg91.com/api/sendhttp.php?authkey=150280AEpvRxUV59002bf5&mobiles=" + getAbsentList.toString().replace(", ", ",").replace("[", "").replace("]", "") + "&message=" + SmsDescription.replace(" ", "%20") + "&sender=Sparsh&route=4&country=91";
//            String url = "http://sms.hspsms.com/sendSMS?username=hspdemo&message="+SmsDescription.replace(" ", "%20")+"&sendername=HSPSMS&smstype=TRANS&numbers="+ getAbsentList.toString().replace(", ", ",").replace("[", "").replace("]", "")+"&apikey=10124518-c732-4b52-9948-4da8723fa79c";


    private final Context _context;
    SessionManager sessionManager;

    public SendSmsManager(Context context) {
        this._context = context;
        sessionManager = new SessionManager(context);
    }

    public void SendSmsToAbsentStudents(String smsUrl, List<AbsentStudentNameNumber> getAbsentList) {

        if (getAbsentList.size() > 0) {

            Date getToDate = new Date();
            SimpleDateFormat simpleDateFormatForTodaysDate = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
            String getFormatedTodays = simpleDateFormatForTodaysDate.format(getToDate);

//            String SmsDescription = "Your ward is absent on " + getFormatedTodays;
            String SmsDescription;
            String url = null;
            for (int i = 0; i < getAbsentList.size(); i++) {
                SmsDescription = getAbsentList.get(i).getName() + " is absent on " + getFormatedTodays;
                String number = getAbsentList.get(i).getMobileNo();
                url = smsUrl.replace("MobileNoList",number).replace("MessageContent", SmsDescription.replace(" ", "%20"));
                System.out.println(" url " + url);
                VolleyCall(url);
            }

        } else {
            System.out.println("getAbsentList------------- is null  ");
        }
    }


    public void SendAlertSmsToStudents(String smsUrl, List<String> getStudentList,String title,String description) {
        if (getStudentList.size() > 0) {
            String SmsDescription = title + "\n" +description;
            System.out.println("getStudentList " + getStudentList);
            String url = smsUrl.replace("MobileNoList", getStudentList.toString().replace(", ", ",").replace("[", "").replace("]", "")).replace("MessageContent", SmsDescription.replace(" ", "%20"));

            VolleyCall(url);
        } else {
            System.out.println("getAbsentList------------- is null  ");
        }
    }

    private void VolleyCall(String url) {
        RequestQueue queue = Volley.newRequestQueue(_context);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error.Response");
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }


}
