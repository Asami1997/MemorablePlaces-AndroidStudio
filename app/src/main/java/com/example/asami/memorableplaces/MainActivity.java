package com.example.asami.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ListView listView ;
    //put these two arraylists static so we can update them in other classes or files.
    static  ArrayList<String> arrayList = new ArrayList<String>();
    static  ArrayList<LatLng> latlngs = new ArrayList<LatLng>(); //will contain latlings of memeos
    static ArrayAdapter<String> arrayAdapter;

    int indicate = 0;

    Bundle extras;
    String message;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList <String> lats2 = new ArrayList<>();
        ArrayList<String> longs2 = new ArrayList<>();
        latlngs.clear();
        longs2.clear();
        lats2.clear();
        arrayList.clear();



        sharedPreferences = this.getSharedPreferences("com.example.asami.memorableplaces", Context.MODE_PRIVATE);

        /*
        SharedPreferences preferences = getSharedPreferences("com.example.asami.memorableplaces", 0);
        preferences.edit().remove("address").commit();
     */

        try {
            arrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("address",ObjectSerializer.serialize(new ArrayList<String>())));
            lats2 = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longs2 = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize(new ArrayList<String>())));


        } catch (IOException e) {
            e.printStackTrace();
        }


        if(arrayList.size() > 0 && lats2.size() > 0 && longs2.size() > 0)
           {


                 //extra check
                 if(lats2.size() == longs2.size()&& longs2.size() == arrayList.size())
                    {

                        indicate=1;
                         for(int i=0;i< lats2.size();i++)
                            {
                               latlngs.add(new LatLng(Double.parseDouble(lats2.get(i)),Double.parseDouble(longs2.get(i))));

                            }
                    }

           }else
               {
                   arrayList.add("Add a new place...");
                   latlngs.add(new LatLng(0,0));
               }



          // for the "add new place " not neccessary but put it to avoid problems

        listView =  (ListView) findViewById(R.id.listViewId);


        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);

        extras = new Bundle();

        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                if(arrayList.get(i) == arrayList.get(0) )
                {


                           Intent intent1 =  new Intent(getApplicationContext(),MapsActivity.class);
                           extras.clear();
                           extras.putString("types","add");
                           intent1.putExtras(extras);
                           startActivity(intent1);


                }else
                   {

                       Intent intent1 =  new Intent(getApplicationContext(),MapsActivity.class);
                       extras.clear();
                       extras.putString("types","display");
                       //position in the listview
                       extras.putInt("memo",i);
                       intent1.putExtras(extras);
                       startActivity(intent1);

                   }

            }
        });


    }



}
