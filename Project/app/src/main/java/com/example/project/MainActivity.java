package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputContentInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    JobScheduler jobScheduler;
    JobInfo jobInfo;
    ListView lvRss;
    Button button;
    EditText text;
    ArrayList<String> titles;
    ArrayList <Drawable> images;
    ArrayList<String> links;
    ArrayList<String> date;
    ArrayList<String> description;
    ArrayAdapter<String> adapter;
    ProcessInBackground pro;
    String input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvRss = (ListView) findViewById(R.id.lvRss);
        titles = new ArrayList<String>();
        links= new ArrayList<String>();
        button = findViewById(R.id.button2);
        text = findViewById(R.id.editText);
        date = new ArrayList<String>();
        description = new ArrayList<String>();

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //executes the method
        new ProcessInBackground().execute();
    }

    public InputStream getInputStream(URL url) {
        //verifies connection
        try{
            return  url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public void onClick(View view) {
        //the onclick takes the text from the text field and feeds it to the parser by executing the processinbackground class
        input = String.valueOf(text.getText());
        adapter.clear();
        pro = new ProcessInBackground();
        pro.execute();
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>
    {

        //starts the progresdialog once the method starts
        ProgressDialog p = new ProgressDialog(MainActivity.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            p.setMessage("Loading feed");
            p.show();
        }

        @Override
        protected Exception doInBackground(Integer... params) {

            try {
                //takes the input from the Textfield and converts it into URL
                new URL(input).getContent();
                URL url = new URL(input);

                //is the XMl parser but this method for parsing is inefficient but the only one i could get working
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");
                boolean insideItem = false;
                int eventType = xpp.getEventType();

                //this is to detect if the reader has reached the end of the feed
                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    //bunch of if statements to detect the XML tags to display a wide variety of different RSS feeds
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        if(xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("title"))
                        {
                            if(insideItem)
                            {
                                titles.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("link"))
                        {
                            if(insideItem){
                               links.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("pubDate"))
                        {
                            if(insideItem){
                               date.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("description"))
                        {
                            if(insideItem){
                                description.add(xpp.nextText());
                            }
                        }
                    }
                    //ends the results when there is no more item left in feed
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
            }
            catch (MalformedURLException e){
                exception = e;
            }
            //IDE said these were necessary so they stay
            catch (XmlPullParserException e){
                exception = e;
            }
            catch (IOException e){
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
            //set the listview to the adapter and dismisses the progress bar
            adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
            lvRss.setAdapter(adapter);
            p.dismiss();
        }

    }

}