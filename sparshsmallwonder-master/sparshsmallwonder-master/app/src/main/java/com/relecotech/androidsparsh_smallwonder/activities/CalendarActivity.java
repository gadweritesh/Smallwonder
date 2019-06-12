package com.relecotech.androidsparsh_smallwonder.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.adapters.CalendarEntryListAdapter;
import com.relecotech.androidsparsh_smallwonder.models.CalendarHolidaysAndEventsListData;
import com.relecotech.androidsparsh_smallwonder.utils.AlarmReceiver;
import com.relecotech.androidsparsh_smallwonder.utils.ConnectionDetector;
import com.relecotech.androidsparsh_smallwonder.utils.EventDecorator;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.relecotech.androidsparsh_smallwonder.MainActivity.sharedPrefValue;

/**
 * Created by Relecotech on 27-02-2018.
 */

public class CalendarActivity extends AppCompatActivity implements OnMonthChangedListener {

    private ConnectionDetector connectionDetector;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private String userRole;
    private Calendar calendarForMapFunctions;
    private List calendarEntryList;
    public static int actualCurrentMonthIndex;
    public static int PENDING_INTENT_REQUEST_CODE = 0;
    private Map<String, List<CalendarHolidaysAndEventsListData>> dateEntryMap;
    private Map<Integer, Map<String, List<CalendarHolidaysAndEventsListData>>> monthEntriesMap;

    // public static Map<String, Map<Integer, Map<String, List<CalendarHolidaysAndEventsListData>>>> combinedEntryMap;


    public static Calendar currentCalendar;
    // actual month index (W.R.T. january)
    private Date todaysDate;

    private JsonObject calendarParamsJsonObject;


    List<String> calendarHolidaysList;
    private ListView calendarListView;
    private MaterialCalendarView materilaCalendarView;


    private int currentTileWidth;
    private int currentTileHeight;
    private TextView noDataAvailableTextView;

    private CalendarEntryListAdapter adapter;

    private List<CalendarDay> markAllCalendarEventAndHolidayList;
    private List<CalendarHolidaysAndEventsListData> addAllCurrentMonthEventsAndHolidaysList;
    private PendingIntent pendingIntentForSetNotification;
    private List<PendingIntent> pendingIntentList;
    private AlarmManager alarmManagerForSetNotificaion;
    private long timeInMillisecondForAlarm;
    private Bundle bundle;
    private Map<String, CalendarDay> calendarDayStringMap;
    private MobileServiceClient mClient;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        mClient = Singleton.Instance().mClientMethod(this);

        //Set Width and Height of MaterialCalendarView
        currentTileWidth = MaterialCalendarView.DEFAULT_TILE_SIZE_DP;
        currentTileHeight = MaterialCalendarView.DEFAULT_TILE_SIZE_DP;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        materilaCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarListView = (ListView) findViewById(R.id.calendarListView);
        noDataAvailableTextView = (TextView) findViewById(R.id.no_data_available_text_view);
        materilaCalendarView.setOnMonthChangedListener(this);

        materilaCalendarView.setTileWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        materilaCalendarView.setTileHeightDp(40);

        currentCalendar = Calendar.getInstance(Locale.getDefault());
        todaysDate = currentCalendar.getTime();
        materilaCalendarView.setSelectedDate(todaysDate);

        pendingIntentList = new ArrayList<>();


        //dateEntryMap used for to store events and holidays against respective date.
        dateEntryMap = new HashMap<>();

        //monthEntriesMap used for to store All events and holidays of respective Month.
        monthEntriesMap = new HashMap<>();

        //No need fof combined entry Map because currently we have only events and holidays.
        calendarDayStringMap = new HashMap<>();

        markAllCalendarEventAndHolidayList = new ArrayList<>();

        //Set Activity Back Arrow and Activity Name on tool bar.

        calendarForMapFunctions = Calendar.getInstance(Locale.getDefault());
        calendarEntryList = new ArrayList();
        dateEntryMap = new HashMap<>();
        actualCurrentMonthIndex = calendarForMapFunctions.get(Calendar.MONTH);

        connectionDetector = new ConnectionDetector(this);
        sessionManager = new SessionManager(this, sharedPrefValue);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        if (connectionDetector.isConnectingToInternet()) {
            fetchCalendarData();
        } else {
            FancyToast.makeText(this, getString(R.string.no_internet), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

        calendarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Item Click -----------------------  " + position);
                showDetailDialog(position);
            }
        });
    }


    private void fetchCalendarData() {
        calendarParamsJsonObject = new JsonObject();
        calendarParamsJsonObject.addProperty("Role", userRole);
        calendarParamsJsonObject.addProperty("schoolClassId", userDetails.get(SessionManager.KEY_SCHOOL_CLASS_ID));
        calendarParamsJsonObject.addProperty("branchId", userDetails.get(SessionManager.KEY_BRANCH_ID));
        if (userRole.equals("Student")) {
            calendarParamsJsonObject.addProperty("StudentId", userDetails.get(SessionManager.KEY_STUDENT_ID));
        }

        System.out.println("calling fetchCalendarData......");
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("calendarDataFetch", calendarParamsJsonObject);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Calender  api exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("Calendar API   response    " + response);
                if (response == null) {
                    System.out.println("json object null $$$$$$$$$$$$$$$$$$$$$$$");
                } else {
                    // splitData(response);
                    parseCalendarJson(response);
                }

            }
        });

    }

    private void parseCalendarJson(JsonElement response) {

        //OLD EXAM & CALENDAR EVENT
//        JsonObject responseToJsonObject = response.getAsJsonObject();
//        System.out.println("responseToJsonObject " + responseToJsonObject);
//        JsonElement dataFromJsonObjectToJsonElement = responseToJsonObject.get("message");
//        JsonObject dataFromJsonElementToJsonObject = dataFromJsonObjectToJsonElement.getAsJsonObject();
//        JsonElement calendarResultsJsonElement = dataFromJsonElementToJsonObject.get("calendar_Results");
//        JsonElement examResultsJsonElement = dataFromJsonElementToJsonObject.get("exam_Results");

        SimpleDateFormat calendarDateFormat = null;
        SimpleDateFormat calendarListViewFormat = null;
        JsonArray getCalendarJsonArray = response.getAsJsonArray();
        for (int feesLoop = 0; feesLoop < getCalendarJsonArray.size(); feesLoop++) {

            JsonObject jsonObjectForIteration = getCalendarJsonArray.get(feesLoop).getAsJsonObject();
            String calendarTitle = jsonObjectForIteration.get("calendarTitle").toString().replace("\"", "");
            String calendarDescription = jsonObjectForIteration.get("calendarDescription").toString().replace("\"", "");
            String calendarStartDate = jsonObjectForIteration.get("startDate").toString().replace("\"", "");
            String typeCalendar = jsonObjectForIteration.get("calendarType").toString().replace("\"", "");

            if (calendarDescription.equals("null")){
                calendarDescription="";
            }

            calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date startDate = null;
            try {
                startDate = calendarDateFormat.parse(calendarStartDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            calendarListViewFormat = new SimpleDateFormat("dd EEE ", Locale.getDefault());
            calendarDateFormat = new SimpleDateFormat("dd MMM yyyy ", Locale.getDefault());

            String calStartDate = calendarDateFormat.format(startDate);
            String calListViewDate = calendarListViewFormat.format(startDate);


            CalendarDay day = CalendarDay.from(startDate);
            markAllCalendarEventAndHolidayList.add(day);

            calendarForMapFunctions.setTime(startDate);

            if (monthEntriesMap.containsKey(calendarForMapFunctions.get(Calendar.MONTH))) {

                dateEntryMap = new HashMap<>();
                dateEntryMap = monthEntriesMap.get(calendarForMapFunctions.get(Calendar.MONTH));

                if (dateEntryMap.containsKey(calStartDate)) {

                    calendarEntryList = new ArrayList();
                    calendarEntryList = dateEntryMap.get(calStartDate);

                    calendarEntryList.add(new CalendarHolidaysAndEventsListData(calendarTitle, calendarDescription.replace("\\n", "\n").replace("\\", ""), typeCalendar, calStartDate, calListViewDate));

                    dateEntryMap.put(calStartDate, calendarEntryList);

                } else {
                    System.out.println("IF ELSE ");

                    calendarEntryList = new ArrayList();

                    calendarEntryList.add(new CalendarHolidaysAndEventsListData(calendarTitle, calendarDescription.replace("\\n", "\n").replace("\\", ""), typeCalendar, calStartDate, calListViewDate));
                    dateEntryMap.put(calStartDate, calendarEntryList);
                }

            } else {
                System.out.println("ELSE ");

                calendarEntryList = new ArrayList();
                dateEntryMap = new HashMap<>();
                calendarEntryList.add(new CalendarHolidaysAndEventsListData(calendarTitle, calendarDescription.replace("\\n", "\n").replace("\\", ""), typeCalendar, calStartDate, calListViewDate));
                dateEntryMap.put(calStartDate, calendarEntryList);

                monthEntriesMap.put(calendarForMapFunctions.get(Calendar.MONTH), dateEntryMap);
            }

            System.out.println(" day.getDate() " + day.getDate());
            System.out.println(" todaysDate " + todaysDate);
            CompareDateWithoutTime.compareTwoDates(day.getDate(), todaysDate);
            if (CompareDateWithoutTime.compareTwoDates(day.getDate(), todaysDate)) {
                calendarDayStringMap.put(calendarTitle, day);
                System.out.println("IF OF day.getDate().after(todaysDate) " + CompareDateWithoutTime.compareTwoDates(day.getDate(), todaysDate));
            } else {
                System.out.println("IF OF day.getDate().after(todaysDate) " + CompareDateWithoutTime.compareTwoDates(day.getDate(), todaysDate));
            }

//            if (day.getDate().after(todaysDate)) {
//                calendarDayStringMap.put(calendarTitle, day);
//            }
        }
        //EventDecoratror used for to mark event on Calendar
        materilaCalendarView.addDecorator(new EventDecorator(Color.RED, markAllCalendarEventAndHolidayList));
        //Show monthly event and holidays in calendarListView
        setDataInCalendarList(actualCurrentMonthIndex);
        System.out.println("Calendar event and holiday list Size----- " + markAllCalendarEventAndHolidayList.size());
        //SetNotification
        setAlarmNotification(calendarDayStringMap);

        System.out.println("calendarDayStringMap-----------  " + calendarDayStringMap.size());
    }

    private void setAlarmNotification(Map<String, CalendarDay> map) {
        clearAllAlarm(pendingIntentList);

        alarmManagerForSetNotificaion = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        for (Map.Entry<String, CalendarDay> calendarDayStringEntry : map.entrySet()) {
//            System.out.println("Calendar day  key-------------  " + calendarDayStringEntry.getKey());
            System.out.println("Calendar day  value-------------  " + calendarDayStringEntry.getValue().getDate());

            //Set Calendar Notification
            timeInMillisecondForAlarm = GetMillisecondMethod(calendarDayStringEntry.getValue().getDate());

            if (timeInMillisecondForAlarm >= System.currentTimeMillis()) {
                Intent intentForNotification = new Intent(CalendarActivity.this, AlarmReceiver.class);
                System.out.println("timeInMillisecondForAlarm------------------------  " + timeInMillisecondForAlarm);
                bundle = new Bundle();
                bundle.putString("Notification_Tag", "Calendar");
                bundle.putString("Notification_Title", calendarDayStringEntry.getKey());
                bundle.putString("Notification_Description", String.valueOf(calendarDayStringEntry.getValue().getDate()));
                intentForNotification.putExtras(bundle);

                pendingIntentForSetNotification = PendingIntent.getBroadcast(this, PENDING_INTENT_REQUEST_CODE, intentForNotification, 0);
                PENDING_INTENT_REQUEST_CODE++;
                pendingIntentList.add(pendingIntentForSetNotification);
                alarmManagerForSetNotificaion.set(AlarmManager.RTC_WAKEUP, timeInMillisecondForAlarm, pendingIntentForSetNotification);
            }


        }
    }

    private void clearAllAlarm(List<PendingIntent> pendingIntentAlramList) {
        System.out.println("pendingIntentAlramList-----------  " + pendingIntentAlramList.size());
        for (int loop = 0; loop < pendingIntentAlramList.size(); loop++) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(this.pendingIntentList.get(loop));
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //Show monthly event and holidays in calendarListView
        setDataInCalendarList(date.getMonth());
    }

    private void setDataInCalendarList(int getCurrentMonthIndex) {
        //Condition to check weather current month index present in monthEntriesMap or not.
        if (monthEntriesMap.containsKey(getCurrentMonthIndex)) {
            //  System.out.println("actualCurrentMonthIndex value conatin");

            Map<String, List<CalendarHolidaysAndEventsListData>> getMonthlyDataMap = monthEntriesMap.get(getCurrentMonthIndex);
            addAllCurrentMonthEventsAndHolidaysList = new ArrayList<>();
            for (Map.Entry<String, List<CalendarHolidaysAndEventsListData>> entry : getMonthlyDataMap.entrySet()) {
                addAllCurrentMonthEventsAndHolidaysList.addAll(entry.getValue());


                Collections.sort(addAllCurrentMonthEventsAndHolidaysList, new Comparator<CalendarHolidaysAndEventsListData>() {
                    @Override
                    public int compare(CalendarHolidaysAndEventsListData obj1, CalendarHolidaysAndEventsListData obj2) {
                        // ## Ascending order
                        // System.out.println(obj1.getCalendarDate().compareToIgnoreCase(obj2.getCalendarDate()));
                        return obj1.getCalendarDate().compareToIgnoreCase(obj2.getCalendarDate());
                        // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                    }
                });
            }

            // System.out.println("commonlist----- " + addAllCurrentMonthEventsAndHolidaysList.size());
            adapter = new CalendarEntryListAdapter(CalendarActivity.this, addAllCurrentMonthEventsAndHolidaysList);
            adapter.notifyDataSetChanged();
            calendarListView.setAdapter(adapter);

        } else {
            calendarListView.setAdapter(null);
        }
    }

    private void showDetailDialog(int listItemPosition) {

        CalendarHolidaysAndEventsListData cdfdf = addAllCurrentMonthEventsAndHolidaysList.get(listItemPosition);

        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
        String typeColor = "#0d5787";
        String ss = cdfdf.getCalendarTitle() + "   (" + "<i><font color=\"" + typeColor + "\">" + cdfdf.getCalendarType() + "</font></i>" + " )";
        Spanned strTitle = Html.fromHtml(ss);
        builder.setTitle(strTitle);

        String s1 = "<b>" + cdfdf.getCalendarDate() + "</b>" + "\n \n";
        String s2 = cdfdf.getCalendarDescription();
        Spanned strMessage = Html.fromHtml(s1 + "<br>" + s2);
        builder.setMessage(strMessage);

        builder.create().show();
    }

    public long GetMillisecondMethod(Date date_time) {

        Calendar calculateDueDateForNotification = Calendar.getInstance();
        calculateDueDateForNotification.setTime(date_time);
        calculateDueDateForNotification.add(Calendar.DATE, 0);

        String time = sessionManager.getSharedPrefItem(SessionManager.KEY_REMINDER);
//        String time = "12:06";
        String[] hhmm = time.split(":");

        int hh = Integer.parseInt(hhmm[0]);
        int min = Integer.parseInt(hhmm[1]);

        int yy = calculateDueDateForNotification.get(Calendar.YEAR);
        int mm = calculateDueDateForNotification.get(Calendar.MONTH);
        int dd = calculateDueDateForNotification.get(Calendar.DAY_OF_MONTH);

        System.out.println("GetMillisecondMethod DATE TIME " + yy + " " + mm + " " + dd + " " + hh + " " + min);
        calculateDueDateForNotification.set(yy, mm, dd, hh, min, 00);
        Date calculatedMilliSecondForCalendarNotification = calculateDueDateForNotification.getTime();
        System.out.println("calculatedMilliSecondForCalendarNotification " + calculatedMilliSecondForCalendarNotification);

        long dueDateMilliSecond = calculatedMilliSecondForCalendarNotification.getTime();

        return dueDateMilliSecond;
    }

    private static class CompareDateWithoutTime {
        public static boolean compareTwoDates(Date startDate, Date endDate) {
            Date sDate = getZeroTimeDate(startDate);
            Date eDate = getZeroTimeDate(endDate);
            if (sDate.before(eDate)) {
//                return "Start date is before end date";
                return false;
            }
            if (sDate.after(eDate)) {
//                return "Start date is after end date";
                return true;
            }
//            return "Start date and end date are equal";
            return true;
        }

        private static Date getZeroTimeDate(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            date = calendar.getTime();
            return date;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}