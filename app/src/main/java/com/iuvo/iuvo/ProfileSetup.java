package com.iuvo.iuvo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;


public class ProfileSetup extends ActionBarActivity {
    public static final String TAG = "ProfileSetup";
    Context context;
    Realm realm;
    RealmResults<Course> result;
    Spinner spinner;
    AutoCompleteTextView pickClass;

    ArrayAdapter<String> schoolAdapter;
    ArrayAdapter<String> courseAdapter;
    SchoolsTask sTask;
    CourseTask cTask;

    AsyncServer.CourseInfo[] courseInfoList;

    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";
    public ProgressDialog myDialog;


    private class SchoolsTask extends AsyncServer<Void,Void,String[]> {

        public static final String TAG = "SchoolsTask";

        public SchoolsTask() {
            super();
            this.deserializer = new Deserializer(context);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            super.doInBackground(params);
            return getIuvoServer().getSchools();
        }

        @Override
        protected void onPostExecute(String[] newSchools) {
            super.onPostExecute(newSchools);
            for (String school : newSchools)
                Log.v(TAG, school);

            schoolAdapter.clear();
            schoolAdapter.addAll(newSchools);
        }
    }

    private class CourseTask extends AsyncServer<Void,Void,String[]> {

        public static final String TAG = "CourseTask";

        public CourseTask() {
            super();
            this.deserializer = new Deserializer(context);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            super.doInBackground(params);

            Log.v(TAG, "CourseTask schoolname: " + spinner.getSelectedItem().toString());
            courseInfoList = getIuvoServer().getCourseList(spinner.getSelectedItem().toString());

            if (isCancelled())
                return null;

            String[] newCourses = new String[courseInfoList.length];
            for (int i = 0; i < courseInfoList.length; i++)
                newCourses[i] = courseInfoList[i].getSubject() + courseInfoList[i].getCode();
            return newCourses;
        }

        @Override
        protected void onPostExecute(String[] newCourses) {
            super.onPostExecute(newCourses);

            courseAdapter.clear();
            courseAdapter.addAll(newCourses);

            CourseDetailTask task = new CourseDetailTask();
            task.execute("UMass-Amherst");
        }
    }

    private class CourseDetailTask extends AsyncServer<String,Void,Course[]> {
        public static final String TAG = "CourseDetailTask";

        public CourseDetailTask() {
            super();
            this.deserializer = new Deserializer(context);
        }

        @Override
        protected Course[] doInBackground(String... params) {
            super.doInBackground(params);
            String school = params[0];

            Realm realm = null;
            try {
                realm = Realm.getInstance(context);
                Course[] ret = new Course[courseInfoList.length];

                int i = 0;
                while (i < courseInfoList.length) {
                    CourseInfo ci = courseInfoList[i];
                    ret[i++] = getIuvoServer().getCourse(school, ci.getSubject(), ci.getCode());
                }

                realm.beginTransaction();
                for (Course c : ret)
                    c.setSchool(school);
                realm.commitTransaction();

                return ret;
            }
            catch (Exception ex) {
                if (realm != null) realm.cancelTransaction();
                throw ex;
            }
            finally {
                if (realm != null) realm.close();
            }
        }

        @Override
        protected void onPostExecute(Course[] param) {
            super.onPostExecute(param);
            EventsTask eTask = new EventsTask();
            eTask.execute(param);
        }
    }

    private class EventsTask extends AsyncServer<Course,Integer,Void> {
        public static final String TAG = "EventsTask";

        public EventsTask() {
            super();
            this.deserializer = new Deserializer(context);
        }

        @Override
        protected Void doInBackground(Course... courses) {
            super.doInBackground(courses);

            try {
                Realm realm = Realm.getInstance(context);
                for (Course c : realm.allObjects(Course.class)) {
                    Log.v(TAG, c.getSchool()+"/"+c.getSubject()+"/"+c.getCode());
                    getIuvoServer().getEventList(c.getSchool(), c.getSubject(), c.getCode());
                }
                return null;
            }
            finally {
                if (realm != null) realm.close();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);


        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // second argument is the default to use if the preference can't be found
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);


//        if(welcomeScreenShown){
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            // This actually will still execute all the below code...
//            finish();
//        }

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(welcomeScreenShownPref, true);
        editor.commit();

        context = (Context) this;
        pickClass = (AutoCompleteTextView) findViewById(R.id.autoCompleteClass);
        spinner = (Spinner) findViewById(R.id.pickUniversity);

        realm = Realm.getInstance(this);
        result = realm.where(Course.class).findAll();


        schoolAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new ArrayList<String>());
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(schoolAdapter);

        sTask = new SchoolsTask();
        sTask.execute();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cTask != null)
                    cTask.cancel(true);

                cTask = new CourseTask();
                cTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //CustomAdapter courseAdapter = new CustomAdapter(this, result);
        courseAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        pickClass.setAdapter(courseAdapter);


        pickClass.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView addClass = new AutoCompleteTextView(context);
                addClass = (AutoCompleteTextView) findViewById(R.id.autoCompleteClass);

            }
        });
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
            String code = values.get(position).getCode();
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            TextView txt = (TextView) convertView.findViewById(android.R.id.text1);
            txt.setText(subject + code);
            return convertView;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        realm.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_setup, menu);
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
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("University", spinner.getSelectedItem().toString());
            editor.commit();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
