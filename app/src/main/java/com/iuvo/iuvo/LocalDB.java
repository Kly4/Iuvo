package com.iuvo.iuvo;

import android.content.Context;
import android.util.Log;

import com.iuvo.iuvo.schemas.JSONSchema;

import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iuvo.iuvo.schemas.*;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.*;


/**
 * Created by Dan on 2014-12-20.
 */
public class LocalDB {
    private static final String TAG = "LocalDB";
    private Context ctx;


    LocalDB(Context ctx) {
        this.ctx = ctx;

        JSONSchema schema = new JSONSchema(ctx);

    }

    static abstract class Cb<T> implements Callback<T> {
        @Override
        public void failure(RetrofitError error) {
            Log.v(TAG, "Retrofit Callback failure:");
            if (error.getResponse() == null)
                Log.v(TAG, "Error of type " + error.getKind() + " " + error.getCause().getClass());
            else
                Log.v(TAG,
                        error.getResponse().getReason() + " " +
                        error.getResponse().getStatus() + "\n" +
                        error.getResponse().getBody());
        }
    }
//
//    public void getEvents() {
//        Log.v(TAG, "getEvents");
//        Realm realm = Realm.getInstance(ctx);
//        RealmResults<Course> courses = realm.where(Course.class).findAll();
//        for (Course course : courses) {
//
//            final String school = course.getSchool();
//            final String subject = course.getSubject();
//            final String code = course.getCode();
//            final String id = course.getId();
//            Log.v(TAG, "Getting /events/"+school+"/"+subject+"/"+code);
//
//
//            server.getEventList(school, subject, code, new Cb<JSONSchema.EventIds>() {
//                @Override
//                public void success(JSONSchema.EventIds events, Response response) {
//                    Realm realm = Realm.getInstance(ctx);
//
//                    Course course = realm.where(Course.class)
//                            .equalTo("id", id)
//                            .findFirst();
//
//                    realm.beginTransaction();
//                    try {
//                        for (String eventId : events.ids) {
//                            Log.v(TAG, "Getting event " + eventId);
//                            Log.v(TAG, "All events I can see:");
//                            for (Event e : realm.where(Event.class).findAll()) {
//                                Log.v(TAG, "Event " + e.getId());
//                            }
//
//                            Event event = realm.where(Event.class)
//                                    .equalTo("id", eventId)
//                                    .findFirst();
//
//                            Log.v(TAG, "Course: " + String.valueOf(course != null));
//                            Log.v(TAG, "Event: " + String.valueOf(event != null));
//
//                            event.setCourse(course);
//                        }
//                        realm.commitTransaction();
//                    }
//                    catch (Exception ex) {
//                        Log.e(TAG, "Could not update event list", ex);
//                    }
//
//                    realm.close();
//                }
//            });
//        }
//        realm.close();
//    }
//
//    public void getCourses(final String school) {
//        server.getCourseList(school, new Cb<JSONSchema.CourseIds>() {
//            @Override
//            public void success(JSONSchema.CourseIds courseResults, Response response) {
//                Realm realm = Realm.getInstance(ctx);
//                RealmResults<Course> courses = realm.allObjects(Course.class);
//
//                // Unfortunately necessary because the previous course
//                // object is in another thread.
//                realm.beginTransaction();
//                Log.v(TAG, "Setting school for " + courses.size() + " Courses");
//                Log.v(TAG, "Supposed to have " + courseResults.ids.length + " Courses");
//                for (int i = 0; i < courses.size(); i++) {
//                    courses.get(i).setSchool(school);
//                }
//                realm.commitTransaction();
//                realm.close();
//                getEvents();
//            }
//        });
//    }


    // TODO: Consolidate HTTP calls - we make O(n) calls, n = number of courses
    /**
     * This will send an HTTP call to the remote server, requesting updates from the user's classes.
     */
    public void update(Context ctx) {
        Log.v(TAG, "UPDATE");
        //getCourses("UMass-Amherst");
    }
}
