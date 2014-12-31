package com.iuvo.iuvo;

import org.json.*;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
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
    private static final String SERVER = "http://ec2-54-173-210-203.compute-1.amazonaws.com";

    public interface IuvoServer {

        /**
         * @return A list of all schools in the database
         */
        @GET("/schools")
        void getSchools(Callback<List<String>> schools);


        /**
         * @param school
         * @return A list of course info (namely a the subject and course code)
         */
        @GET("/courses/{school}")
        void getCourseList(@Path("school") String school, Callback<List<Course>> courses);

        /**
         * @param school
         * @param subject
         * @param code
         * @return Extra information (instructor, course title) to make a Course object
         */
        @GET("/events/{school}/{subject}/{code}")
        void getCourse(@Path("school") String school,
                       @Path("subject") String subject,
                       @Path("code") String code,
                       Callback<JSONObject> courseInfo);

        /**
         * @param school
         * @param subject
         * @param code
         * @return Study sessions pertaining to the course
         */
        @GET("/events/{school}/{subject}/{code}")
        void getEventList(@Path("school") String school,
                          @Path("subject") String subject,
                          @Path("code") String code,
                          Callback<List<Event>> eventList);

        @POST("/events/new")
        void createEvent(@Body Event event, Callback<Event> cb);
    }

    final IuvoServer server;
    final RestAdapter restAdapter;
    private Context ctx;


    LocalDB(Context ctx) {
        this.ctx = ctx;

        JSONSchema schema = new JSONSchema(ctx);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Course.class, schema.getCourseDeserializer())
                .create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVER)
                .setConverter(new GsonConverter(gson))
                .build();
        server = restAdapter.create(IuvoServer.class);
    }

    static abstract class Cb<T> implements Callback<T> {
        @Override
        public void failure(RetrofitError error) {
            Log.v(TAG, "Retrofit Callback failure:");
            if (error.getResponse() == null)
                Log.v(TAG, "Error of type " + error.getKind(), error.getCause());
            else
                Log.v(TAG,
                        error.getResponse().getReason() + " " +
                        error.getResponse().getStatus() + "\n" +
                        error.getResponse().getBody());
        }
    }

    public void getEvents() {
        Realm realm = Realm.getInstance(ctx);
        RealmResults<Course> courses = realm.where(Course.class).findAll();
        for (Course course : courses) {
            final String school = course.getSchool();
            final String subject = course.getSubject();
            final String code = course.getCode();

            Log.v(TAG, "Getting /events/"+school+"/"+subject+"/"+code);
            server.getEventList(school, subject, code, new Cb<List<Event>>() {
                @Override
                public void success(List<Event> events, Response response) {
                    Realm realm = Realm.getInstance(ctx);
                    for (Event event : events) {
                        realm.beginTransaction();
                        // Unfortunately, necessary because the previous course
                        // object is in another thread.
                        Course course = realm.where(Course.class)
                                             .equalTo("school", school)
                                             .equalTo("subject", subject)
                                             .equalTo("code", code)
                                             .findFirst();
                        event.setCourse(course);
                        realm.commitTransaction();
                    }
                }
            });
        }
    }

    public void getCourses(final String school) {
        server.getCourseList(school, new Cb<List<Course>>() {
            @Override
            public void success(List<Course> courseResults, Response response) {
                Realm realm = Realm.getInstance(ctx);
                RealmResults<Course> courses = realm.where(Course.class).findAll();

                // Unfortunately necessary because the previous course
                // object is in another thread.
                realm.beginTransaction();
                for (int i = 0; i < courses.size(); i++) {
                    courses.get(i).setSchool(school);
                }
                realm.commitTransaction();

                getEvents();
            }
        });
    }

    //            @Override
//            public void success(List<JSONObject> list, Response response) {
//                Log.v(TAG, "getCourses onSuccess");
//                try {
//                    for (JSONObject obj : list) {
//                        Course course = JSONSchema.createCourse(ctx, obj);
//                        getEvents(ctx, course);
//                    }
////                    for (int i = 0; i < jsonArray.length(); i++) {
////                        Course course = JSONSchema.createCourse(ctx, (JSONObject) jsonArray.get(i));
////                        getEvents(ctx, course);
////                    }
//                }
//                catch (Exception ex) {
//                    Log.v(TAG, "List accessed out of bounds", ex);
//                }
//            }

    // TODO: Consolidate HTTP calls - we make O(n) calls, n = number of courses
    /**
     * This will send an HTTP call to the remote server, requesting updates from the user's classes.
     */
    public void update(Context ctx) {
        Log.v(TAG, "UPDATE");
        getCourses("UMass-Amherst");
    }
}
