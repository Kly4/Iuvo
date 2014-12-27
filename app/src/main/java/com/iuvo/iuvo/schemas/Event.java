package com.iuvo.iuvo.schemas;

import io.realm.RealmObject;

/**
* Created by Dan on 2014-12-21.
*/
public class Event extends RealmObject {
    private Course course;
    private String title;
    private String description;
    private int numAttendees;
    private boolean checkState;

    private String timeAt;
    private String timeTill;
    private String location;

    public String getTimeAt() {
        return timeAt;
    }

    public void setTimeAt(String timeAt) {
        this.timeAt = timeAt;
    }

    public String getTimeTill() {
        return timeTill;
    }

    public void setTimeTill(String timeTill) {
        this.timeTill = timeTill;
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
