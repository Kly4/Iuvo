package com.iuvo.iuvo;

import com.iuvo.iuvo.schemas.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // idk lol
        Realm.deleteRealmFile(this);
        // This will produce duplicates in our current setup. I'll fix...
        Realm realm = Realm.getInstance(this);
        LocalDB.update(this);


        ListView view = (ListView) findViewById(R.id.event_list);
        view.setAdapter(new CustomAdapter(this, realm.where(Event.class).findAll()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RealmBaseAdapter<Event> {
        private static final String TAG = "CustomAdapter";
        private final Context context;
        private final RealmResults<Event> values;
        private HashMap<Course, String> courseColors;
        // private Realm realm;


        public CustomAdapter(Context context, RealmResults<Event> values) {
            super(context, values, false);
            this.context = context;
            this.values = values;

            Realm realm = Realm.getInstance(context);
            RealmResults<Course> courses = realm.where(Course.class).findAll();
            courseColors = new HashMap<>();
            String[] colors = getResources().getStringArray(R.array.colors);

            int i = 0;
            for (Course course : courses) {
                courseColors.put(course, colors[i++ % colors.length]);
            }

            Log.v(TAG, "Launched with " + values.size() + " elements");
        }

        // http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
        class ViewHolder {
            TextView subject;
            TextView code;
            TextView date;
            RadioButton radio;
            TextView attendance;

            public ViewHolder(View convertView) {
                subject = (TextView) convertView.findViewById(R.id.classname);
                code = (TextView) convertView.findViewById(R.id.classnumber);
                date = (TextView) convertView.findViewById(R.id.date);

                radio = (RadioButton) convertView.findViewById(R.id.radioButton);
                attendance = (TextView) convertView.findViewById(R.id.attendance);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder view;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_layout, parent, false);
                view = new ViewHolder(convertView);
                convertView.setTag(view);
            } else {
                view = (ViewHolder) convertView.getTag();
            }

            Event item = realmResults.get(position);
            Course course = item.getCourse();
            String color = courseColors.get(course);
            // TODO: Set the color background color for view.shape

            view.subject.setText(course.getSubject());
            view.code.setText(course.getCourseCode());
            view.date.setText("Dec 21");
            view.attendance.setText(String.valueOf(item.getNumAttendees()));

            return convertView;
        }
    }
}
