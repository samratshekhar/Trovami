package com.trovami.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.trovami.R;
import com.trovami.models.ListItem;

public class HomeActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<ListItem>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setBackgroundColor(Color.GRAY);
        Toast.makeText(getApplicationContext(), "Sign In Successful", Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final String str = "Group: " + String.valueOf(groupPosition) + " and Child: " + String.valueOf(childPosition);
                Toast.makeText(getApplicationContext(),
                                str,
                                Toast.LENGTH_SHORT).show();

                Button btn = v.findViewById(R.id.btn_listbtn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),
                                "Button: " + str,
                                Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
        });

    }

     /*
     * Preparing the list data
     */

    private void prepareListData() {
        String photo_url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQupuLmBDzs0P1tyM7FLGRL9ctFamagiGAiYoLJbbMuxt1OvcSx";
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<ListItem>>();

        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<ListItem> top250 = new ArrayList<ListItem>();
        top250.add(new ListItem(photo_url,"The Shawshank Redemption","Great Movie"));
        top250.add(new ListItem(photo_url,"The Godfather","Great Movie"));
        top250.add(new ListItem(photo_url,"The Dark Knight","Great Movie"));

        List<ListItem> nowShowing = new ArrayList<ListItem>();
        nowShowing.add(new ListItem(photo_url,"Despicable Me 2","Great Movie"));
        nowShowing.add(new ListItem(photo_url,"The Wolverine","Great Movie"));
        nowShowing.add(new ListItem(photo_url,"Red 2","Great Movie"));

        List<ListItem> comingSoon = new ArrayList<ListItem>();
        comingSoon.add(new ListItem(photo_url, "The Smurfs 2","Great Movie"));
        comingSoon.add(new ListItem(photo_url, "The Spectacular Now","Great Movie"));
        comingSoon.add(new ListItem(photo_url, "Europa Report","Great Movie"));


        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }


}
