package com.iuvo.iuvo;

import com.iuvo.iuvo.schemas.*;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

        // This will produce duplicates in our current setup. I'll fix...
        LocalDB.update(this);
        Realm realm = Realm.getInstance(this);

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
            String[] colors = {"1", "2", "3", "4", "5", "6", "7"};
            int i = 0;
            for (Course course : courses) {
                courseColors.put(course, colors[i++ % 6]);
            }

            Log.v(TAG, "Launched with " + values.size() + " elements");
        }

        // http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
        class ViewHolder {
            TextView title;
            int position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder1;
            ViewHolder viewHolder2;
            ViewHolder viewHolder3;
            ViewHolder viewHolder4;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_layout, parent, false);
                viewHolder1 = new ViewHolder();
                viewHolder2 = new ViewHolder();
                viewHolder3 = new ViewHolder();
                viewHolder4 = new ViewHolder();
                viewHolder1.title = (TextView) convertView.findViewById(R.id.clssname);
                viewHolder2.title =(TextView) convertView.findViewById(R.id.clssnmbr);
                viewHolder3.title =(TextView) convertView.findViewById(R.id.date);
                viewHolder4.title =(TextView) convertView.findViewById(R.id.attd);
                convertView.setTag(viewHolder1);
                convertView.setTag(viewHolder2);
                convertView.setTag(viewHolder3);
                convertView.setTag(viewHolder4);
            } else {
                viewHolder1 = (ViewHolder) convertView.getTag();
                viewHolder2 = (ViewHolder) convertView.getTag();
                viewHolder3 = (ViewHolder) convertView.getTag();
                viewHolder4 = (ViewHolder) convertView.getTag();
            }

            Event item = realmResults.get(position);
            viewHolder1.title.setText(item.getSubject());
            viewHolder2.title.setText(item.getCourseCode());
            viewHolder3.title.setText("Dec 21");
            viewHolder4.title.setText(String.valueOf(item.getNumAttendees()));
            return convertView;
        }
    }
}
