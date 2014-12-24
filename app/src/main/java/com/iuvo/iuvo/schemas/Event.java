package com.iuvo.iuvo.schemas;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
* Created by Dan on 2014-12-21.
*/
public class Event extends RealmObject {
    private Course course;
    private String title;
    private String description;
    private int numAttendees;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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
}
