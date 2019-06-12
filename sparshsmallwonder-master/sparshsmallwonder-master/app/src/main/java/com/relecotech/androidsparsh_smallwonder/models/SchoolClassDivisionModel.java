package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

/**
 * Created by Relecotech on 20-03-2018.
 */

public class SchoolClassDivisionModel implements Serializable {
    String schoolClassId;
    String schoolClass;
    String schoolDivision;

    public SchoolClassDivisionModel(String schoolClassId, String schoolClass, String schoolDivision) {
        this.schoolClassId = schoolClassId;
        this.schoolClass = schoolClass;
        this.schoolDivision = schoolDivision;
    }

    public String getSchoolClassId() {
        return schoolClassId;
    }

    public void setSchoolClassId(String schoolClassId) {
        this.schoolClassId = schoolClassId;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }

    public String getSchoolDivision() {
        return schoolDivision;
    }

    public void setSchoolDivision(String schoolDivision) {
        this.schoolDivision = schoolDivision;
    }
}
