package com.iuvo.iuvo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.iuvo.iuvo.schemas.Course;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;


public class ProfileSetup extends ActionBarActivity {
    Realm realm;
    RealmResults<Course> result;
    Spinner spinner;
    AutoCompleteTextView pickClass;
    String[] universities = {"UMass-Amherst", "Utorronto"};
    String[] course = {"CS 121", "ECE 211", "Bio 151", "CS240", "Phil 112"};

    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";
    public ProgressDialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);


        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // second argument is the default to use if the preference can't be found
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);

        if(welcomeScreenShown){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(welcomeScreenShownPref, true);
        editor.commit();

        final Context context = this;
        pickClass = (AutoCompleteTextView) findViewById(R.id.autoCompleteClass);
        spinner = (Spinner) findViewById(R.id.pickUniversity);

        realm = Realm.getInstance(this);
        result = realm.where(Course.class).findAll();

        //CustomAdapter courseAdapter = new CustomAdapter(this, result);
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, course );

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, universities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        pickClass.setAdapter(autoAdapter);



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
            String code = values.get(position).getCourseCode();
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
