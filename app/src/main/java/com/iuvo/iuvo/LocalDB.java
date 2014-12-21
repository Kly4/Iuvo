package com.iuvo.iuvo;

import org.json.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Dan on 2014-12-20.
 */
public class LocalDB {
    // For now we just work with JSON objects directly. I don't see
    // a reason not to in the long run either.

    /**
     * This will send an HTTP call to the remote server, requesting updates from the user's classes.
     */
    public static void update() {

    }

    /**
     * Returns all the events pertinent to the user. This will query the local DB.
     */
    public static ArrayList<JSONObject> getEvents() {
        ArrayList<JSONObject> ret = new ArrayList<JSONObject>();

        try {
            InputStream is = new FileInputStream("events.json");
            String jsonTxt = convertStreamToString(is);

            JSONArray json = new JSONArray(jsonTxt);
            for (int i = 0; i < json.length(); i++)
                ret.add(json.getJSONObject(i));

            return ret;
        }
        catch (JSONException |FileNotFoundException ex) {
            return ret;
        }
    }

    // LOL IDK http://stackoverflow.com/a/5445161
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
