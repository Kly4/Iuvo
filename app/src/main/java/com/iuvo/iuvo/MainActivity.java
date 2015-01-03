package com.iuvo.iuvo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;
import com.iuvo.iuvo.slidingmenu.CommunityFragment;
import com.iuvo.iuvo.slidingmenu.FindPeopleFragment;
import com.iuvo.iuvo.slidingmenu.HomeFragment;
import com.iuvo.iuvo.slidingmenu.NavDrawerItem;
import com.iuvo.iuvo.slidingmenu.PagesFragment;
import com.iuvo.iuvo.slidingmenu.PhotosFragment;
import com.iuvo.iuvo.slidingmenu.WhatsHotFragment;
import com.iuvo.iuvo.slidingmenu.adapter.NavDrawerListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;



public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    SharedPreferences mPrefs;
    final String LocalDbUpdate = "updated";
    Realm realm;
    Context context;
    CustomAdapter eventAdapter;

    //DEMO CODE BEGINS
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter navAdapter;
    //DEMO CODE ENDS


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = (Context) this;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        ListView view = (ListView) findViewById(R.id.event_list);
        realm = Realm.getInstance(this);
        eventAdapter = new CustomAdapter(this, realm.where(Event.class).findAll());
        view.setAdapter(eventAdapter);

        realm.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                eventAdapter.notifyDataSetChanged();
            }
        });



        //DEMO CODE BEGINS
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        navAdapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(navAdapter);

        // enabling action bar app icon and behaving it as toggle button
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        )
        {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
           // displayView(0);
        }


        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new FindPeopleFragment();
                break;
            case 2:
                fragment = new PhotosFragment();
                break;
            case 3:
                fragment = new CommunityFragment();
                break;
            case 4:
                fragment = new PagesFragment();
                break;
            case 5:
                fragment = new WhatsHotFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

        //DEMO CODE ENDS


    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
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

        //DEMO CODE
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, NewEvent.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_add).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //DEMO CODE END


    class CustomAdapter extends RealmBaseAdapter<Event> {
        private static final String TAG = "CustomAdapter";
        private final Context context;
        private final RealmResults<Event> values;
        private HashMap<String, String> courseColors;
        LinearLayout leftblock;
        GradientDrawable shape;

        String[] colors;
        int colorCount;


        public CustomAdapter(Context context, RealmResults<Event> values) {
            super(context, values, false);
            this.context = context;
            this.values = values;


            RealmResults<Course> courses = realm.where(Course.class).findAll();
            courseColors = new HashMap<>();
            colors = getResources().getStringArray(R.array.colors);

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
                leftblock = (LinearLayout) convertView.findViewById(R.id.leftblock);
                shape = (GradientDrawable) leftblock.getBackground();
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
            DateFormat date = new SimpleDateFormat("MMM d");
            view.date.setText(date.format(item.getStartTime()));

            view.attendance.setText(String.valueOf(item.getNumAttendees()));

            return convertView;
        }
    }


    private class EventsTask extends AsyncServer<Void,Void,Void> {
        public static final String TAG = "EventsTask";

        public EventsTask() {
            super();
            this.deserializer = new Deserializer(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            super.doInBackground(params);

            Realm realm = null;
            try {
                realm = Realm.getInstance(context);
                for (Course c : realm.allObjects(Course.class)) {
                    Log.v(TAG, c.getSchool()+"/"+c.getSubject()+"/"+c.getCode());
                    Event[] events = getIuvoServer().getEventList(c.getSchool(), c.getSubject(), c.getCode());

                    realm.beginTransaction();
                    for (Event e : events)
                        e.setCourse(c);
                    realm.commitTransaction();
                }
                return null;
            }
            finally {
                if (realm != null) realm.close();
            }
        }
    }

}



