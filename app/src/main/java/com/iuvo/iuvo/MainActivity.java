package com.iuvo.iuvo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

;


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
        private Realm realm;
        LinearLayout leftblock;
        GradientDrawable shape;


//        View v = findViewById(R.id.layout_id);
//
//        LayerDrawable bgDrawable = (LayerDrawable)v.getBackground();
//        final GradientDrawable shape = (GradientDrawable)   bgDrawable.findDrawableByLayerId(R.id.shape_id);
//        shape.setColor(----);


        public CustomAdapter(Context context, RealmResults<Event> values) {
            super(context, values, false);
            this.context = context;
            this.values = values;

            realm = Realm.getInstance(context);
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
            TextView timeAt;
            TextView timeTill;
            TextView location;
            CheckBox checkbox;
            TextView attendance;



            public ViewHolder(View convertView, int position) {
                subject = (TextView) convertView.findViewById(R.id.classname);
                code = (TextView) convertView.findViewById(R.id.classnumber);
                date = (TextView) convertView.findViewById(R.id.date);
                timeAt = (TextView) convertView.findViewById(R.id.timeat);
                timeTill = (TextView) convertView.findViewById(R.id.timetill);
                location = (TextView) convertView.findViewById(R.id.location);
                checkbox = (CheckBox) convertView.findViewById(R.id.button);
                attendance = (TextView) convertView.findViewById(R.id.attendance);
                Event item = realmResults.get(position);
                checkbox.setTag(item);
                checkbox.setChecked(item.isCheckState());
                leftblock = (LinearLayout) convertView.findViewById(R.id.leftblock);
                shape = (GradientDrawable) leftblock.getBackground();


            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder view;
            Event item = realmResults.get(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_layout, parent, false);

                view = new ViewHolder(convertView, position);
                convertView.setTag(view);


                view.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                        // checkedId is the RadioButton selected
                        Log.v(TAG, "Clicked");
                        realm.beginTransaction();
                        Event item = (Event) button.getTag();
                        if(isChecked){
                            item.setNumAttendees(item.getNumAttendees() + 1);
                            item.setCheckState(true);
                        }
                        else {
                            item.setNumAttendees(item.getNumAttendees() - 1);
                            item.setCheckState(false);
                        }
                        realm.commitTransaction();
                       notifyDataSetChanged();
                    }
                });

            } else {
                view = (ViewHolder) convertView.getTag();
            }


            Course course = item.getCourse();
            String color = courseColors.get(course);
            shape.setColor(Color.parseColor(color));

            view.timeAt.setText(item.getTimeAt());
            view.timeTill.setText(item.getTimeTill());
            view.location.setText(item.getLocation());
            view.subject.setText(course.getSubject());
            view.code.setText(course.getCourseCode());
            view.date.setText("Dec 21");
            view.attendance.setText(String.valueOf(item.getNumAttendees()));

            return convertView;
        }
    }
}
