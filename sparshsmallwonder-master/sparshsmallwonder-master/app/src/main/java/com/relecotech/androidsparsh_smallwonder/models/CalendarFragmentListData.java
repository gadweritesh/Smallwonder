package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Relecotech on 28-02-2018.
 */

public class CalendarFragmentListData {
    String calendarDate;
    String calendarTitle;
    String calendarBody;
    String calendarTag;
    String calendarTime;

    public CalendarFragmentListData(String calendarTitle, String calendarBody, String calendarDate, String calendarTime, String calendarTag) {
        this.calendarDate = calendarDate;
        this.calendarTitle = calendarTitle;
        this.calendarBody = calendarBody;
        this.calendarTag = calendarTag;
        this.calendarTime = calendarTime;
    }

    public String getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(String calendarDate) {
        this.calendarDate = calendarDate;
    }

    public String getCalendarTitle() {
        return calendarTitle;
    }

    public void setCalendarTitle(String calendarTitle) {
        this.calendarTitle = calendarTitle;
    }

    public String getCalendarBody() {
        return calendarBody;
    }

    public void setCalendarBody(String calendarBody) {
        this.calendarBody = calendarBody;
    }

    public String getCalendarTag() {
        return calendarTag;
    }

    public void setCalendarTag(String calendarTag) {
        this.calendarTag = calendarTag;
    }

    public String getCalendarTime() {
        return calendarTime;
    }

    public void setCalendarTime(String calendarTime) {
        this.calendarTime = calendarTime;
    }
}
