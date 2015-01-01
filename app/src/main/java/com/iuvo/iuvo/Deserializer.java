package com.iuvo.iuvo;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import io.realm.Realm;

/**
 * Created by Dan on 2014-12-31.
 */
public class Deserializer {
    Context context;

    public Deserializer(Context ctx) {
        this.context = ctx;
    }

    public CourseDeserializer getCourseDeserializer() {
        return new CourseDeserializer();
    }
    public EventDeserializer getEventDeserializer() {
        return new EventDeserializer();
    }


    public class CourseDeserializer implements JsonDeserializer<Course> {
        public static final String TAG = "CourseDeserializer";

        @Override
        public Course deserialize(JsonElement jsonElement, Type t, JsonDeserializationContext jctx) {
            Log.v(TAG, "Initiated");
            Realm realm = null;
            try {
                realm = Realm.getInstance(context);
                realm.beginTransaction();

                JsonObject json = jsonElement.getAsJsonObject();
                Course course = realm.createObject(Course.class);

                course.setId(json.get("_id").getAsString());
                course.setSubject(json.get("subject").getAsString());
                course.setCode(json.get("code").getAsString());
                course.setInstructor(json.get("instructor").getAsString());
                course.setTitle(json.get("title").getAsString());

                realm.commitTransaction();
                Log.v(TAG, "Created Course in realm: " + course.getTitle());

                return course;
            }
            catch (Exception ex) {
                if (realm != null)
                    realm.cancelTransaction();

                Log.e(TAG, "Failed to create Course ", ex);
                Log.e(TAG, jsonElement.toString());

                throw ex;
            }
            finally {
                if (realm != null)
                    realm.close();
            }
        }
    }

    public class EventDeserializer implements JsonDeserializer<Event> {
        public static final String TAG = "EventDeserializer";

        @Override
        public Event deserialize(JsonElement jsonElement, Type t, JsonDeserializationContext jctx) {
            Log.v(TAG, "Initiated");

            Realm realm = null;
            try {
                realm = Realm.getInstance(context);
                realm.beginTransaction();

                JsonObject json = jsonElement.getAsJsonObject();

                Event event = realm.createObject(Event.class);
                event.setId(json.get("_id").getAsString());

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                event.setStartTime(df.parse(json.get("start_date").getAsString()));
                event.setEndTime(df.parse(json.get("end_date").getAsString()));

                event.setDescription(json.get("description").getAsString());
                event.setLocation(json.get("location").getAsString());
                event.setNumAttendees(json.get("num_attendees").getAsInt());

                realm.commitTransaction();
                Log.v(TAG, "Created Event in realm: " + event.getId());

                return event;
            }
            catch (ParseException ex) {
                if (realm != null) realm.cancelTransaction();

                Log.e(TAG, "Failed to create Event");
                Log.e(TAG, jsonElement.toString());

                return null;
            }
            catch (Exception ex) {
                if (realm != null) realm.cancelTransaction();

                Log.e(TAG, "Failed to create Event");
                Log.e(TAG, jsonElement.toString());

                throw ex;
            }
            finally {
                if (realm != null)
                    realm.close();
            }
        }
    }

}
