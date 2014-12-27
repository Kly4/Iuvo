package com.iuvo.iuvo.schemas;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
* Created by Dan on 2014-12-21.
*/
public class Course extends RealmObject {
    private String subject;
    private String courseCode;
    private String instructor;
    private String title;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
