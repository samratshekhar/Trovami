package com.trovami.activities;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trovami.R;

/**
 * Created by TULIKA on 08-Apr-18.
 */

public class NotificationListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] maintitle;
        private final String[] subtitle;
        private final Integer[] imgid;
        public Button button;


        public NotificationListAdapter(Activity context, String[] maintitle,String[] subtitle, Integer[] imgid) {
            super(context, R.layout.notification_list, maintitle);
            // TODO Auto-generated constructor stub
            this.context=context;
            this.maintitle=maintitle;
            this.subtitle=subtitle;
            this.imgid=imgid;

        }

        public View getView(int position,View view,ViewGroup parent) {
            LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.notification_list, null,true);

            TextView titleText = (TextView) rowView.findViewById(R.id.title);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
            Button button= (Button) rowView.findViewById(R.id.btnStatus);


            titleText.setText(maintitle[position]);
            imageView.setImageResource(imgid[position]);
            subtitleText.setText(subtitle[position]);
            button.setText("Status");


            return rowView;

        };
}
