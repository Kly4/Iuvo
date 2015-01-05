package com.iuvo.iuvo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.server.AsyncServer;
import com.iuvo.iuvo.server.CourseInfo;
import com.iuvo.iuvo.server.Deserializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    LinearLayout list;
    //Button addClass;

    /** Map for getting a courseInfo object from a full course code ("CSC123") */
    Map<String, CourseInfo> courseCodeMap;

    ArrayAdapter<String> schoolAdapter;
    ArrayAdapter<String> courseAdapter;

    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";
    public ProgressDialog myDialog;
    InputMethodManager imm;


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
            CourseInfo[] courseInfoList =
                    getIuvoServer().getCourseList(spinner.getSelectedItem().toString());

            if (isCancelled())
                return null;

            // Create a list of full course codes ("CSC123") and populate courseCodeMap
            String[] newCourses = new String[courseInfoList.length];
            for (int i = 0; i < courseInfoList.length; i++) {
                newCourses[i] = courseInfoList[i].getSubject() + courseInfoList[i].getCode();
                courseCodeMap.put(newCourses[i], courseInfoList[i]);
            }
            return newCourses;
        }

        @Override
        protected void onPostExecute(String[] newCourses) {
            super.onPostExecute(newCourses);

            courseAdapter.clear();
            courseAdapter.addAll(newCourses);
        }
    }

    /** Opens MainActivity when complete. */
    private class SaveCoursesToLocalDB extends AsyncServer<CourseInfo,Void,Void> {
        public static final String TAG = "CourseDetailTask";

        public SaveCoursesToLocalDB() {
            super();
            this.deserializer = new Deserializer(context);
        }

        @Override
        protected Void doInBackground(CourseInfo... courseInfoList) {
            super.doInBackground(courseInfoList);

            Realm realm = null;
            try {
                realm = Realm.getInstance(context);
                ArrayList<Course> newCourses = new ArrayList<>();

                String school = mPrefs.getString(getResources().getString(R.string.pSchool), "");

                // Get all the courses and save them.
                int i = 0;
                for (CourseInfo ci : courseInfoList) {
                    String s = ci.getSubject(), c = ci.getCode();

                    // Only create the course if it's not already in the DB
                    Course course = realm.where(Course.class)
                            .equalTo("school", school)
                            .equalTo("subject", s)
                            .equalTo("code", c)
                            .findFirst();
                    if (course == null)
                        newCourses.add(getIuvoServer().getCourse(school, s, c));
                }

                // For some reason I couldn't get the school to be returned, so we have to
                // set it manually.
                try {
                    realm.beginTransaction();
                    for (Course c : newCourses)
                        c.setSchool(school);
                    realm.commitTransaction();
                }
                catch(Exception ex) {
                    realm.cancelTransaction();
                    Log.e(TAG, "Could not save courses to LocalDB", ex);
                }

                return null;
            }
            finally {
                if (realm != null) realm.close();
            }
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);

            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        Realm.deleteRealmFile(this);
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

        courseCodeMap = new HashMap<>();

        pickClass = (AutoCompleteTextView) findViewById(R.id.autoCompleteClass);
        spinner = (Spinner) findViewById(R.id.pickUniversity);
        list = (LinearLayout) findViewById(R.id.list);
//        addClass = (Button) findViewById(R.id.addClass);
//        addClass.setOnClickListener(onAddClass());

        imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);


        realm = Realm.getInstance(this);
        result = realm.where(Course.class).findAll();


        schoolAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new ArrayList<String>());
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(schoolAdapter);

        SchoolsTask sTask = new SchoolsTask();
        sTask.execute();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            CourseTask cTask;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(getResources().getString(R.string.pSchool),
                        spinner.getSelectedItem().toString());
                editor.commit();

                if (cTask != null)
                    cTask.cancel(true);

                cTask = new CourseTask();
                cTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        courseAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        pickClass.setAdapter(courseAdapter);

        listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClickTemp(parent, view, position, id);
            }
        };

        pickClass.setOnItemClickListener(listener);
    }

    OnItemClickListener listener;
    public void onItemClickTemp(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "HIDE KEYBOARD");
        imm.hideSoftInputFromWindow(pickClass.getWindowToken(), 0);

        AutoCompleteTextView temp = createNewTextView();
        temp.setAdapter(courseAdapter);
        temp.setOnItemClickListener(listener);
        list.addView(temp);

    }


//    private View.OnClickListener onAddClass() {
//        return new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        };
//    }

    private AutoCompleteTextView createNewTextView() {
        //final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final AutoCompleteTextView auto = new AutoCompleteTextView(this);
        auto.setHint("Pick your class");
        //textView.setLayoutParams(lparams);
        //textView.setText("New text: " + text);
        return auto;
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

            // For each nonempty selected course code, look up the CourseInfo object and
            // start the task of saving the full course to the local database.
            Collection<CourseInfo> courseInfo = new ArrayList<CourseInfo>();
            for (int i = 0; i < list.getChildCount(); i++) {
                String courseCode = ((AutoCompleteTextView) list.getChildAt(i)).getText().toString();
                CourseInfo info = courseCodeMap.get(courseCode);
                if (info != null) {
                    courseInfo.add(info);
                }
            }

            // When finished, it will start up MainActivity.
            SaveCoursesToLocalDB task = new SaveCoursesToLocalDB();
            task.execute(courseInfo.toArray(new CourseInfo[1]));


            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
