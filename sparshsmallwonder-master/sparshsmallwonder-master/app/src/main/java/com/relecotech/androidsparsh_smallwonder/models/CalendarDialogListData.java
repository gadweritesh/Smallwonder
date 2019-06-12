package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by ajinkya on 4/20/2016.
 */
public class CalendarDialogListData {

    String entryType;
    String calendarEntry;

    public CalendarDialogListData() {
    }

    public CalendarDialogListData(String entryType, String calendarEntry) {
        this.entryType = entryType;
        this.calendarEntry = calendarEntry;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getCalendarEntry() {
        return calendarEntry;
    }

    public void setCalendarEntry(String calendarEntry) {
        this.calendarEntry = calendarEntry;
    }
}
