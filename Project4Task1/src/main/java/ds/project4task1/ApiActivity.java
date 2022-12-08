package ds.project4task1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/* Author: Xiawei He
 *  Andrew-id: xiaweih
 */

@WebServlet(name = "ActivityGenerator", value = "/getActivity")
public class ApiActivity extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject returnValue = new JsonObject();

        // check whether the path matches /getActivity
        if (request.getServletPath().equals("/getActivity")) {
            // get activity type
            String typeRequested = request.getParameter("type");
            System.out.println("Type requested: " + typeRequested);

            // set api according to user's input
            String api;
            if (typeRequested == null) {
                api = "http://www.boredapi.com/api/activity/";
            }
            else {
                api = "http://www.boredapi.com/api/activity?type=" + typeRequested;
            }

            // get the response
            String api_result = fetch(api);

            /*
            * check api_result
            * if api_result equals certain error code, return the error status
            * else return the JsonObject of the activity
            */
            if (api_result.equals("400")) {
                returnValue.addProperty("status", "Invalid data sent from your web application.");
            } else if (api_result.equals("500")) {
                returnValue.addProperty("status", "Your web application could not reach the api. Possible network failure.");
            } else {
                JsonObject return_obj = JsonParser.parseString(api_result).getAsJsonObject();

                // get the response
                String activity = return_obj.get("activity").getAsString();
                String type = return_obj.get("type").getAsString();
                String participants = return_obj.get("participants").getAsString();
                String price = return_obj.get("price").getAsString();
                String link = return_obj.get("link").getAsString();

                // format the response to the Android
                returnValue.addProperty("activity", activity);
                returnValue.addProperty("type", type);
                returnValue.addProperty("participants", participants);
                returnValue.addProperty("price", price);
                returnValue.addProperty("link", link);

                System.out.println("Get request by visiting with /api");
            }
        }

        // send response
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(returnValue);
        out.flush();
    }

    /*
     * Make an HTTP request to a given URL
     *
     * @param urlString The URL of the request
     * @return A string of the response from the HTTP GET.  This is identical
     * to what would be returned from using curl on the command line.
     */
    private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            int responseCode = connection.getResponseCode();

            /*
            * get the response code
            * if 200, return the activity content
            * others, return the error code
            */
            if (responseCode == 200) {
                String str;
                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }
            } else if (responseCode == 400) {
                return Integer.toString(responseCode);
            } else if (responseCode == 500) {
                return Integer.toString(responseCode);
            }

            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
        }
        return response;
    }

}