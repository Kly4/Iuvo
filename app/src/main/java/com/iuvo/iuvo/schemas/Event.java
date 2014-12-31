package com.iuvo.iuvo.schemas;

import java.util.Date;

import io.realm.RealmObject;

/**
* Created by Dan on 2014-12-21.
*/
public class Event extends RealmObject {
    private Course course;

    private String description;
    private String location;

    private int numAttendees;
    private boolean checkState;

    private Date startTime;
    private Date endTime;


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isCheckState() { return checkState; }

    public void setCheckState(boolean checkState) { this.checkState = checkState; }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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
