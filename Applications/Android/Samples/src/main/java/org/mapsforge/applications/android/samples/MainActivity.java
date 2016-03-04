package org.mapsforge.applications.android.samples;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Long Huynh on 09.02.2016.
 */
public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    ListView varselListView;
    List<String> varselList;
    ArrayAdapter<String> adapter;
    NotificationManager nManager;
    boolean varselOpen = false;
    static Button notifCount;
    static int mNotifCount = 0;


    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = "SafeSail";

        //Setter opp navigasjon menyen
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
         nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Setter opp notifikasjon lista og arraList og adapter for å legge inn data dynamisk
        varselListView = (ListView) findViewById(R.id.notification_list_view);
        varselList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, varselList);
        varselListView.setAdapter(adapter);

        //Kjører en test varsel
        getVarsel(1, "Death incoming");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment objFragment = null;
         switch (position) {
         case 0:
            objFragment = new KartView();
             break;
         case 1:
             objFragment = null;
             break;
         case 2:
            break;
         case 3:
         objFragment = new Instillinger();
         break;
         }
         // Fragmenter som skal vises ved onclick skal settes her.
         FragmentManager fragmentManager = getFragmentManager();
         fragmentManager.beginTransaction()
         .replace(R.id.container, objFragment)
         .commit();
         }

    public void onSectionAttached(int number) {
    //Metoder som skal utføres ved onclick skal settes her
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                startActivity(new Intent(MainActivity.this, testMain.class));
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.action_settings);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
}

    /** Lager varsel icon og en button som kan åpne varsel lista*/
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            final View count = menu.findItem(R.id.varselicon).getActionView();
            notifCount = (Button) count.findViewById(R.id.notif_count);
            notifCount.setText(String.valueOf(mNotifCount));
        notifCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(varselOpen != true) {
                        varselListView.setVisibility(View.VISIBLE);
                        varselOpen = true;
                    }else {
                        varselListView.setVisibility(View.GONE);
                        varselOpen = false;
                        mNotifCount = 0;
                        notifCount.setText(String.valueOf(mNotifCount));
                    }
                }
            });
            return super.onCreateOptionsMenu(menu);
        }

    private void setNotifCount(int count){
        //En teller for varsel
        mNotifCount = +count;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    /** Her blir varsel laget. Alt av icon, lyd osv kan gjøres her*/
    private void getVarsel(int mNotificationId, String innmelding){
        String time = DateFormat.getDateTimeInstance().format(new Date());

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String melding = innmelding + "         " + time;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.varsel);
        mBuilder.setContentTitle("SafeSail");
        mBuilder.setContentText(melding);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setPriority(2);
        // builder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        nManager.notify(mNotificationId, mBuilder.build());
        setNotifCount(1);
        varselList.add(melding);
        adapter.notifyDataSetChanged();
    }


}