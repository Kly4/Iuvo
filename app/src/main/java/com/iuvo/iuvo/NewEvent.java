package com.iuvo.iuvo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;


public class NewEvent extends ActionBarActivity {

    Realm realm;
    RealmResults<Course> result;

    Spinner spinner;
    EditText date;
    EditText location;
    EditText time;
    EditText note;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        spinner = (Spinner) findViewById(R.id.pickClass);
        date = (EditText) findViewById(R.id.Date);
        time = (EditText) findViewById(R.id.Time);
        location = (EditText) findViewById(R.id.Location);
        note = (EditText) findViewById(R.id.Note);


        realm = Realm.getInstance(this);
        result = realm.where(Course.class).findAll();
        CustomAdapter cs = new CustomAdapter(this, result);
        //ArrayAdapter cs = new ArrayAdapter(this, android.R.layout.simple_spinner_item, result.toArray());
        //cs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(cs);
    }


    class CustomAdapter extends RealmBaseAdapter<Course> {
        RealmResults<Course> values;
        public CustomAdapter(Context ctx, RealmResults<Course> values) {
            super(ctx, values, false);
            this.values = values;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            return getView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String subject = values.get(position).getSubject();
            String code = values.get(position).getCourseCode();
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            TextView txt = (TextView) convertView.findViewById(android.R.id.text1);
            txt.setText(subject + code);
            return convertView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            Course selectedCourse = (Course) spinner.getSelectedItem();
            Intent intent = new Intent(this, MainActivity.class);
            realm.beginTransaction();
            Event newEvent = realm.createObject(Event.class);
            newEvent.setCourse(selectedCourse);
            newEvent.setNumAttendees(newEvent.getNumAttendees() + 1);
            newEvent.setCheckState(true);
            newEvent.setTimeAt(time.getText().toString());
            newEvent.setLocation(location.getText().toString());
            newEvent.setDescription(note.getText().toString());
            realm.commitTransaction();
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
