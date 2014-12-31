package com.iuvo.iuvo.schemas;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.realm.Realm;

/**
* Created by Dan on 2014-12-21.
*/
public class JSONSchema {
    private static final String TAG = "JSONSchema";
    //private Realm realm;
    private Context ctx;
    private CourseDeserializer cd;

    public JSONSchema(Context ctx) {
        //realm = Realm.getInstance(ctx);
        this.ctx = ctx;
        cd = new CourseDeserializer();
    }

    public CourseDeserializer getCourseDeserializer() {
        return cd;
    }

    public class CourseDeserializer implements JsonDeserializer<Course> {
        public static final String TAG = "CourseDeserializer";

        @Override
        public Course deserialize(JsonElement jsonElement, Type t, JsonDeserializationContext jctx) {
            Realm realm = Realm.getInstance(ctx);
            realm.beginTransaction();
            JsonObject json = jsonElement.getAsJsonObject();

            Course course = realm.createObject(Course.class);
            course.setSubject(json.get("subject").getAsString());
            course.setCode(json.get("code").getAsString());
            course.setInstructor(json.get("instructor").getAsString());
            course.setTitle(json.get("title").getAsString());

            realm.commitTransaction();
            Log.v(TAG, "Created Course in realm: " + course.getTitle());
            return course;
//            try {
//            } catch (Exception ex) {
//                realm.cancelTransaction();
//                Log.e(TAG, "Failed to create Course ", ex);
//                Log.e(TAG, jsonElement.toString());
//                return null;
//            }
        }
    }

    public class EventDeserializer implements JsonDeserializer<Event> {
        public static final String TAG = "EventDeserializer";

        @Override
        public Event deserialize(JsonElement jsonElement, Type t, JsonDeserializationContext jctx) {
            Log.v(TAG, "Initiated");

            Realm realm = Realm.getInstance(ctx);
            realm.beginTransaction();
            JsonObject json = jsonElement.getAsJsonObject();

            try {
                Event event = realm.createObject(Event.class);
                event.setCourse(null);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                event.setStartTime(df.parse(json.get("start_time").getAsString()));
                event.setEndTime(df.parse(json.get("end_time").getAsString()));

                event.setDescription(json.get("description").getAsString());
                event.setLocation(json.get("location").getAsString());
                event.setNumAttendees(json.get("num_attendees").getAsInt());
                realm.commitTransaction();

                Log.v(TAG, "Created Event in realm");
                return event;

            } catch (ParseException ex) {
                realm.cancelTransaction();
                Log.e(TAG, "Failed to create Event ", ex);
                Log.e(TAG, jsonElement.toString());
                return null;
            }
        }
    }

//    public static Event createEvent(Context ctx, JSONObject json, Course course) {
//        Realm realm = Realm.getInstance(ctx);
//        realm.beginTransaction();
//        try {
//            String fields[] = {
//                    json.getString("subject"),
//                    json.getString("course_code"),
//                    json.getString("title"),
//                    json.optString("description", ""),
//                    json.optString("timeTill", ""),
//                    json.optString("timeAt", ""),
//                    json.optString("location", "")
//
//            };
//            int int_fields[] = {
//                    json.getInt("num-attendees")
//            };
//
////            Course course = realm.where(Course.class)
////                                 .equalTo("subject", fields[0])
////                                 .equalTo("courseCode", fields[1])
////                                 .findFirst();
//
//            Event event = realm.createObject(Event.class);
//            event.setCourse(course);
//
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//            event.setStartTime(df.parse(json.getString("start_time")));
//            event.setEndTime(df.parse(json.getString("end_time")));
//
//            event.setDescription(json.getString("description"));
//            event.setLocation(json.getString("location"));
//            event.setNumAttendees(json.getInt("num_attendees"));
//            realm.commitTransaction();
//
//            Log.v(TAG, "Created Event in realm");
//            return event;
//        }
//        catch (JSONException|ParseException ex) {
//            realm.cancelTransaction();
//            Log.e(TAG, "Failed to create Event ", ex);
//            return null;
//        }
//    }
}
