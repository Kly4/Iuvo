package com.iuvo.iuvo.schemas;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
* Created by Dan on 2014-12-21.
*/
public class JSONSchema {
    private static final String TAG = "JSONSchema";

    public static Course createCourse(Context ctx, JSONObject json) {
        Realm realm = Realm.getInstance(ctx);
        realm.beginTransaction();
        try {
            // If any field not present, fail all at once.
            String fields[] = {
                    json.getString("subject"),
                    json.getString("course-code"),
                    json.optString("instructor", ""),
                    json.optString("title", "")
            };
            Course course = realm.createObject(Course.class);
            course.setSubject(fields[0]);
            course.setCourseCode(fields[1]);
            course.setInstructor(fields[2]);
            course.setTitle(fields[3]);
            realm.commitTransaction();

            Log.v(TAG, "Created Course in realm: " + course.getTitle());
            return course;
        }
        catch (JSONException ex) {
            realm.cancelTransaction();
            Log.e(TAG, "Failed to create Course ", ex);
            return null;
        }
    }

    public static Event createEvent(Context ctx, JSONObject json) {
        Realm realm = Realm.getInstance(ctx);
        realm.beginTransaction();
        try {
            String fields[] = {
                    json.getString("subject"),
                    json.getString("course-code"),
                    json.getString("title"),
                    json.optString("description", "")
            };
            int int_fields[] = {
                    json.getInt("num-attendees")
            };

            Course course = realm.where(Course.class)
                                 .equalTo("subject", fields[0])
                                 .equalTo("courseCode", fields[1])
                                 .findFirst();

            Event event = realm.createObject(Event.class);
            event.setCourse(course);
            event.setTitle(fields[2]);
            event.setDescription(fields[3]);
            event.setNumAttendees(int_fields[0]);
            realm.commitTransaction();

            Log.v(TAG, "Created Event in realm: " + event.getTitle());
            return event;
        }
        catch (JSONException ex) {
            realm.cancelTransaction();
            Log.e(TAG, "Failed to create Event ", ex);
            return null;
        }
    }
}
