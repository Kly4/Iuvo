package com.iuvo.iuvo;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * For viewing the main event list. Pass it some RealmResults of Events.
 */
class EventListAdapter extends RealmBaseAdapter<Event> {
    private static final String TAG = "CustomAdapter";
    private final Context context;
    private Realm realm;
    private RealmResults<Event> values;
    private HashMap<String, String> courseColors;
    LinearLayout leftBlock;
    GradientDrawable shape;

    String[] colors;
    int colorCount;

    public EventListAdapter(Context context, RealmResults<Event> values) {
        super(context, values, false);
        this.context = context;
        this.values = values;

        realm = Realm.getInstance(context);
        RealmResults<Course> courses = realm.where(Course.class).findAll();
        courseColors = new HashMap<>();
        colors = context.getResources().getStringArray(R.array.colors);

        colorCount = 0;
        for (Course course : courses) {
            String courseCode = course.getSubject()+course.getCode();
            if (courseColors.get(courseCode) == null)
                courseColors.put(courseCode, colors[colorCount++ % colors.length]);
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
            leftBlock = (LinearLayout) convertView.findViewById(R.id.leftblock);
            shape = (GradientDrawable) leftBlock.getBackground();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG, "getView " + String.valueOf(position));

        ViewHolder view;
        Event item = realmResults.get(position);
        Course course = item.getCourse();

        //if (convertView == null) {
        convertView = inflater.inflate(R.layout.row_layout, parent, false);

        view = new ViewHolder(convertView, position);
        convertView.setTag(view);

        String courseCode = course.getSubject() + course.getCode();
        String color = courseColors.get(courseCode);
        // This course does not already have a color mapping
        if (color == null) {
            color = colors[colorCount++ % colors.length];
            courseColors.put(courseCode, color);
        }
        shape.setColor(Color.parseColor(color));

//          } else {
//              view = (ViewHolder) convertView.getTag();
//          }


        view.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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


        // Should produce: "12:13 PM"
        DateFormat time = new SimpleDateFormat("hh:mm a");
        view.timeAt.setText(time.format(item.getStartTime()));
        view.timeTill.setText(time.format(item.getEndTime()));

        view.location.setText(item.getLocation());
        view.subject.setText(course.getSubject());
        view.code.setText(course.getCode());

        // Should produce: "Jul 4"
        DateFormat d = new SimpleDateFormat("MMM d");
        view.date.setText(d.format(item.getStartTime()));

        view.attendance.setText(String.valueOf(item.getNumAttendees()));

        return convertView;
    }
}
