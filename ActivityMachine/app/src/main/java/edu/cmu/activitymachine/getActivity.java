package edu.cmu.activitymachine;

import android.app.Activity;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/* Author: Xiawei He
*  Andrew-id: xiaweih
*/

/*
 * This class provides capabilities to generate an activity on boredapi.com given a type. The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of inner class BackgroundTask that will do the network
 * operations in a separate worker thread. However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 *
 * Method BackgroundTask.doInBackground( ) does the background work
 * Method BackgroundTask.onPostExecute( ) is called when the background work is done;
 * it calls *back* to ip to report the results
 * */
public class getActivity {
    ActivityMachine ip = null; // for callback
    String searchType = null;    // search boredapi for this type
    JSONObject activityReturned = null;  // returned from boredapi
    static boolean internetError = false;

    // search( )
    // Parameters:
    // String searchType: the type of activity to search for on boredapi
    // Activity activity: the UI thread activity
    // ActivityGenerator ip: the callback method's class; here, it will be ip.activityReady( )
    public void search(String searchType, Activity activity, ActivityMachine ip) {
        this.ip = ip;
        this.searchType = searchType;
        new BackgroundTask(activity).execute();
    }

    private class BackgroundTask {

        private Activity activity; // The UI thread

        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {

                    doInBackground();
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                onPostExecute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        }

        private void execute() {
            startBackground();
        }

        // doInBackground( ) implements whatever you need to do on the background thread.
        private void doInBackground() {
            activityReturned = search(searchType);
        }

        // onPostExecute( ) will run on the UI thread after the background thread completes.
        public void onPostExecute() throws JSONException {
            ip.activityReady(activityReturned);
        }
    }

    /*
     * Search boredapi.com for the searchTerm argument, and return a JsonObject that can be put in an TextView
     */
    private JSONObject search(String searchType) {
        JSONObject json = null;

        // Call boredapi to get the JsonObject
        StringBuilder response = new StringBuilder();
        try {
            URL url;

            // modify url according to user's input
            if (searchType.isEmpty()) {
                url = new URL("https://safe-stream-72232.herokuapp.com/getActivity?device=" + getDeviceName());
            } else {
                url = new URL("https://safe-stream-72232.herokuapp.com/getActivity?device=" + getDeviceName() + "&type=" + searchType);
            }

            // test purpose
            System.out.println(url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            String content;
            // Read each line of "in" until done, adding each to "response"
            while ((content = in.readLine()) != null) {
                // content is one line of text readLine() strips newline characters
                response.append(content);
            }

            // test purpose
            System.out.println(response);
            System.out.println("Device Name: " + getDeviceName());

            // convert StringBuilder to json file
            json = new JSONObject(response.toString());
            in.close();
        } catch (IOException | JSONException e) {
            System.out.println("Eeek, an exception");
            internetError = true;
        }
        return json;
    }

    /*
     * Method for getting the device name
     * code referred to
     * https://stackoverflow.com/questions/1995439/get-android-phone-model-programmatically-how-to-get-device-name-and-model-prog
     * solution by Idolon
     */
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
