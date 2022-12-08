package edu.cmu.activitymachine;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.activitymachine.databinding.ActivityMainBinding;

/* Author: Xiawei He
 *  Andrew-id: xiaweih
 */

public class ActivityMachine extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    ActivityMachine me = this;
    ActivityMachine ma;
    String searchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        /*
         * The click listener will need a reference to this object, so that upon successfully finding a activity from boredapi, it
         * can callback to this object with the resulting activity JSON.  The "this" of the OnClick will be the OnClickListener, not
         * this ActivityGenerator.
         */
        ma = this;

    }

    /*
    * submitButton defines the function of the submit button
    * this method is used in the button property "onClick"
    *
    * referred to solution on:
    * https://stackoverflow.com/questions/35374153/have-to-click-a-button-twice-for-it-to-work-in-android-studio
    * solution by marktani
    * */
    public void submitButton(View v) {
        // restore the internetError value to false
        getActivity.internetError = false;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);

        // get the user input
        searchType = ((EditText)findViewById(R.id.searchType)).getText().toString();

        // if input format is wrong, a alert window will pop up to warn user
        if (!searchType.matches("^[a-zA-Z]*$")) {
            DialogFragment popUp = new StartErrorDialogFragment();
            popUp.show(getSupportFragmentManager(), "error");
        }

        // search for activity
        searchType = ((EditText)findViewById(R.id.searchType)).getText().toString();

        // test purpose
        System.out.println("searchType = " + searchType);

        getActivity ga = new getActivity();
        ga.search(searchType, me, ma); // Done asynchronously in another thread.  It calls ip.activityReady() in this thread when complete.

        // transfer from first_fragment to second_fragment
        // code referred to
        // https://stackoverflow.com/questions/53902494/navigation-component-cannot-find-navcontroller/59149512#59149512?newreg=c1a73a1e08de41c4bf838f3067f11a1c
        // solution by JunaidKhan
        Navigation.findNavController(ActivityMachine.this, R.id.FirstFragment)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);

    }

    /*
     * This is called by the GetActivity object when the activity is ready.
     * This allows for passing back the JSON activity for updating the TextView.
     */
    public void activityReady(JSONObject activity) throws JSONException {
        TextView resultLabel = findViewById(R.id.searchResult);
        TextView searchResult = findViewById(R.id.activityReturned);
        if (activity != null) {
            searchResult.setText(stringFormatter(activity));
            System.out.println("Activity found");
        } else {
            resultLabel.setText("No activity found.\nPlease try again.");
            System.out.println("No activity found.");
        }

        if (getActivity.internetError == true) {
            DialogFragment popUp = new InternetErrorDialogFragment();
            popUp.show(getSupportFragmentManager(), "error");
        }

        resultLabel.invalidate();
        searchResult.invalidate();
    }

    /*
     * This method is used to parse JSON response into formatted string
     */
    public String stringFormatter(JSONObject return_obj) throws JSONException {
        String response;
        String activity = return_obj.getString("activity");
        String type = return_obj.getString("type");
        String participants = return_obj.getString("participants");
        String price = return_obj.getString("price");
        String link = return_obj.getString("link");
        response = "Activity: " + activity + "\n" + "Type: " + type + "\n" + "Participants: " + participants + "\n" + "Price: " + price;

        if (!link.isEmpty()) {
            response = "Activity: " + activity + "\n" + "Type: " + type + "\n" + "Participants: " + participants + "\n" + "Price: " + price + "\n" + "Link: " + link;
        }

        return response;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}