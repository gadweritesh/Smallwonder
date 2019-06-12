package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

/**
 * Created by Relecotech on 09-03-2018.
 */

public class StudentLikeData implements Serializable {

    private String selectedItemId;
    private String studentId;

    public StudentLikeData(String selectedItemId, String studentId) {
        this.selectedItemId = selectedItemId;
        this.studentId = studentId;
    }

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
