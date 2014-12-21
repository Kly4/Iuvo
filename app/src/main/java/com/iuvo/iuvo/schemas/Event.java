package com.iuvo.iuvo.schemas;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
* Created by Dan on 2014-12-21.
*/
public class Event extends RealmObject {
    private String subject;
    private String courseCode;
    private String title;
    private String description;
    private int numAttendees;
    private int numComments;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumAttendees() {
        return numAttendees;
    }

    public void setNumAttendees(int numAttendees) {
        this.numAttendees = numAttendees;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }
}
