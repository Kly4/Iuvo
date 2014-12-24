package com.iuvo.iuvo;

import org.json.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.iuvo.iuvo.schemas.*;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Dan on 2014-12-20.
 */
public class LocalDB {
    private static final String TAG = "LocalDB";

    /**
     * This will send an HTTP call to the remote server, requesting updates from the user's classes.
     */
    public static void update(Context ctx) {
        try {
            InputStream is = ctx.getAssets().open("courses.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonTxt = new String(buffer, "UTF-8");
            JSONArray json = new JSONArray(jsonTxt);

            Log.v(TAG, "Creating " + json.length() + " new Courses");
            for (int i = 0; i < json.length(); i++)
                JSONSchema.createCourse(ctx, json.getJSONObject(i));


            is = ctx.getAssets().open("events.json");
            size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonTxt = new String(buffer, "UTF-8");
            json = new JSONArray(jsonTxt);

            Log.v(TAG, "Creating " + json.length() + " new Events");
            for (int i = 0; i < json.length(); i++)
                JSONSchema.createEvent(ctx, json.getJSONObject(i));
        }
        catch (JSONException | IOException ex) {
            Log.e(TAG, "Failed to update", ex);
        }
    }

    // LOL IDK http://stackoverflow.com/a/5445161
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
