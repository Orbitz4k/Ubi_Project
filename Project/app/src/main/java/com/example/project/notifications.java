package com.example.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class notifications extends BroadcastReceiver {

    public static String recent;
    public URL url;
    public static Context c;

    //call from main activity for the input for URL to update
    MainActivity m = new MainActivity();

    @Override
    public void onReceive(Context context, Intent intent) {
        c = context;

        try{
            //this is the input from the text area which reads the data in
            url = new URL(m.input);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        //executes the input from above with a smaller version of the RSS reader made
        (new task()).execute(url);

    }

    static class task extends AsyncTask<URL, View, String>{

        String sb = null;
        String title = "";

        protected String doInBackground(URL... urls){
            //Connection
            URL url = urls[0];
            HttpURLConnection urlConnection = null;

            try{
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return sb;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final JSONObject j;
            try{
                //Converting string into JSON for printing title
                j = new JSONObject(sb);
                final JSONArray data = j.getJSONArray("article");
                title = data.getString(Integer.parseInt("title"));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            if(recent != null){
                if (recent.equals(title)){

                }else {
                    //Building the notification with a title and description
                    recent = title;
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(c, "news")
                            .setContentTitle("RSS")
                            .setContentText(title)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat n = NotificationManagerCompat.from(c);
                    n.notify(15, notification.build());
                }
            }
            else {
                recent = title;
            }
        }
    }
}
