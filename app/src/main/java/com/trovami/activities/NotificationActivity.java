package com.trovami.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.trovami.R;

    public class NotificationActivity extends AppCompatActivity {

        ListView list;
        int pos;

        String[] maintitle = {
                "Chris Hemsworth", "Bradley Cooper",
                "Beyonce", "Dwayne Johnson",
                "George Clooney",
        };

        String[] subtitle = {
                "You got a follow request from Chris Hemsworth", "You got a follow request from Bradley Cooper",
                "You sent a follow request to Beyonce", "You got a follow request from Dwayne Johnson",
                "You sent a follow request to George Clooney",
        };

        Integer[] imgid = {
                R.drawable.listimg1, R.drawable.listimg1,
                R.drawable.listimg1, R.drawable.listimg1,
                R.drawable.listimg1
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notification);

            NotificationListAdapter adapter = new NotificationListAdapter(this, maintitle, subtitle, imgid);
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    pos = position;
                    if (position == 0) {
                        //code specific to first list item
                        Toast.makeText(getApplicationContext(), "List item at index " + position, Toast.LENGTH_SHORT).show();
                    } else if (position == 1) {
                        //code specific to 2nd list item
                        Toast.makeText(getApplicationContext(), "List item at index " + position, Toast.LENGTH_SHORT).show();
                    } else if (position == 2) {

                        Toast.makeText(getApplicationContext(), "List item at index " + position, Toast.LENGTH_SHORT).show();
                    } else if (position == 3) {

                        Toast.makeText(getApplicationContext(), "List item at index " + position, Toast.LENGTH_SHORT).show();
                    } else if (position == 4) {

                        Toast.makeText(getApplicationContext(), "List item at index " + position, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        public void clickMe(View view) {
            Button bt = (Button) view;
            //Toast.makeText(this, "Button " + bt.getText().toString(), Toast.LENGTH_LONG).show();
            if (pos == 0) {
                //code specific to first list item
                Toast.makeText(getApplicationContext(), "Button at index " + pos, Toast.LENGTH_SHORT).show();
            } else if (pos == 1) {
                //code specific to 2nd list item
                Toast.makeText(getApplicationContext(), "Button at index " + pos, Toast.LENGTH_SHORT).show();
            } else if (pos == 2) {

                Toast.makeText(getApplicationContext(), "Button at index " + pos, Toast.LENGTH_SHORT).show();
            } else if (pos == 3) {

                Toast.makeText(getApplicationContext(), "Button at index " + pos, Toast.LENGTH_SHORT).show();
            } else if (pos == 4) {

                Toast.makeText(getApplicationContext(), "Button at index " + pos, Toast.LENGTH_SHORT).show();
            }
            ;


        }

    }