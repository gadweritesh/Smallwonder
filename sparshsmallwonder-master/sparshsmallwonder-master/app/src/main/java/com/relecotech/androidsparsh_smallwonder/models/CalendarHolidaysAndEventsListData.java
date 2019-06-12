package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Relecotech on 28-02-2018.
 */

public class CalendarHolidaysAndEventsListData {

    private String calendarTitle;
    private String calendarDescription;
    private String calendarType;
    private String calendarDate;
    private String calendarListViewDate;

    public CalendarHolidaysAndEventsListData(String calendarTitle, String calendarDescription, String calendarType, String calendarDate, String calendarListViewDate) {
        this.calendarTitle = calendarTitle;
        this.calendarDescription = calendarDescription;
        this.calendarType = calendarType;
        this.calendarDate = calendarDate;
        this.calendarListViewDate = calendarListViewDate;
    }

    public String getCalendarTitle() {
        return calendarTitle;
    }

    public void setCalendarTitle(String calendarTitle) {
        this.calendarTitle = calendarTitle;
    }

    public String getCalendarDescription() {
        return calendarDescription;
    }

    public void setCalendarDescription(String calendarDescription) {
        this.calendarDescription = calendarDescription;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    public String getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(String calendarDate) {
        this.calendarDate = calendarDate;
    }

    public String getCalendarListViewDate() {
        return calendarListViewDate;
    }

    public void setCalendarListViewDate(String calendarListViewDate) {
        this.calendarListViewDate = calendarListViewDate;
    }
}
