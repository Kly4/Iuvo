package com.iuvo.iuvo;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;


/**
 * Created by Dan on 2014-12-31.
 */
public abstract class AsyncServer<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    public static class CourseInfo {
        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        private String subject;
        private String code;
    }

    // Instantiated in the restAdapter field below
    public interface IuvoServer {
        /**
         * @return A list of all schools in the database
         */
        @GET("/schools")
        String[] getSchools();

        /**
         * @param school
         * @return A list of course info (namely a the subject and course code)
         */
        @GET("/courses/{school}")
        CourseInfo[] getCourseList(@Path("school") String school);


        /**
         * @param school
         * @param subject
         * @param code
         * @return Study sessions pertaining to the course
         */
        @GET("/events/{school}/{subject}/{code}")
        Event[] getEventList(@Path("school") String school,
                             @Path("subject") String subject,
                             @Path("code") String code);

        /**
         * This URL is for getting full course details (Instructor, Course title, etc.) from
         * its basic information.
         *
         * @param school
         * @param subject
         * @param code
         * @return Full Course object in JSON
         */
        @GET("/courses/{school}/{subject}/{code}")
        Course getCourse(@Path("school") String school,
                         @Path("subject") String subject,
                         @Path("code") String code);

//            @POST("/events/new")
//            void createEvent(@Body Event event, Callback<Event> cb);
    }

    public static String TAG;
    private static final String SERVER = "http://ec2-54-173-210-203.compute-1.amazonaws.com";
    Deserializer deserializer;
    IuvoServer server;
    RestAdapter restAdapter;

    AsyncServer() {
        Log.v(TAG, "Initialized");
    }

    @Override
    protected void onPreExecute() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Course.class, deserializer.getCourseDeserializer())
                .registerTypeAdapter(Event.class, deserializer.getEventDeserializer())
                .create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVER)
                .setConverter(new GsonConverter(gson))
                .build();

        server = restAdapter.create(IuvoServer.class);
    }

    protected IuvoServer getIuvoServer() {
        return server;
    }

    @Override
    protected Result doInBackground(Params... params) {
        Log.v(TAG, "doInBackground");
        return null;
    }

    @Override
    protected void onPostExecute(Result res) {
        Log.v(TAG, "onPostExecute");
    }
}
