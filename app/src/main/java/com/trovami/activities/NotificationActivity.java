package com.trovami.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.trovami.R;

    public class NotificationActivity extends AppCompatActivity {

        ListView list;

        String[] maintitle ={
                "Chris Hemsworth","Bradley Cooper",
                "Beyonce","Dwayne Johnson",
                "George Clooney",
        };

        String[] subtitle ={
                "You got a follow request from Chris Hemsworth","You got a follow request from Bradley Cooper",
                "You sent a follow request to Beyonce","You got a follow request from Dwayne Johnson",
                "You sent a follow request to George Clooney",
        };

        Integer[] imgid={
                R.drawable.listimg1,R.drawable.listimg1,
                R.drawable.listimg1,R.drawable.listimg1,
                R.drawable.listimg1
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notification);

            NotificationListAdapter adapter=new NotificationListAdapter(this, maintitle, subtitle,imgid);
            list=(ListView)findViewById(R.id.list);
            list.setAdapter(adapter);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    // TODO Auto-generated method stub
                    if(position == 0) {
                        //code specific to first list item
                        Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                    }

                    else if(position == 1) {
                        //code specific to 2nd list item
                        Toast.makeText(getApplicationContext(),"Place Your Second Option Code",Toast.LENGTH_SHORT).show();
                    }

                    else if(position == 2) {

                        Toast.makeText(getApplicationContext(),"Place Your Third Option Code",Toast.LENGTH_SHORT).show();
                    }
                    else if(position == 3) {

                        Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
                    }
                    else if(position == 4) {

                        Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
}

