package com.iuvo.iuvo.server;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;
import com.iuvo.iuvo.server.Deserializer;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;


/**
 * Created by Dan on 2014-12-31.
 */
public abstract class AsyncServer<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    public static String TAG;
    private static final String SERVER = "http://ec2-54-173-210-203.compute-1.amazonaws.com";
    public Deserializer deserializer;
    private IuvoServer server;
    private RestAdapter restAdapter;

    protected AsyncServer() {
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
