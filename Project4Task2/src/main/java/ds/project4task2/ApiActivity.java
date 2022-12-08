package ds.project4task2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

/* Author: Xiawei He
 *  Andrew-id: xiaweih
 */

@WebServlet(name = "ActivityGenerator", urlPatterns = {"/getActivity"})
public class ApiActivity extends HttpServlet {
    int id = 1;
    // the item created for logging in info on mongodb
    Document item;
    // timestamp for calculating latency
    Timestamp timestamp;
    Timestamp responseTime;
    long latency;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
         * Mongodb log data operation
         */
        ConnectionString connectionString = new ConnectionString("mongodb://xiaweih:Aa13505712920@cluster0-shard-00-00.2dpe2.mongodb.net:27017,cluster0-shard-00-01.2dpe2.mongodb.net:27017,cluster0-shard-00-02.2dpe2.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("ActivityDB");

        MongoCollection collection = database.getCollection("ActivityCollection");

        // record the timestamp of the request received
        timestamp = new Timestamp(System.currentTimeMillis());
        // test purpose
        System.out.println("Timestamp: " + timestamp);

        // create a document item for adding to mongodb
        item = new Document("id", id);
        id++;

        JsonObject returnValue = new JsonObject();

        // check whether the path matches /getActivity
        if (request.getServletPath().equals("/getActivity")) {
            // test purpose
            System.out.println("Mobile request: " + request.getRequestURI());

            // get activity type
            String typeRequested = request.getParameter("type");
            // test purpose
            System.out.println("Type requested: " + typeRequested);

            // get the device name of the sender
            String deviceName = request.getParameter("device");
            // test purpose
            System.out.println("Device name: " + deviceName);

            // set api according to user's input
            String api;
            if (typeRequested == null) {
                api = "http://www.boredapi.com/api/activity/";
            } else {
                api = "http://www.boredapi.com/api/activity?type=" + typeRequested;
            }

            // record the request from mobile phone
            item.append("Timestamp", timestamp)
                    .append("Phone Model", deviceName)
                    .append("Search Type", typeRequested)
                    .append("Mobile Request", request.getRequestURI());

            // get the response
            String api_result = fetch(api);

            /*
             * Api already implements error handling
             * error such as "Endpoint not found" or "No activity found with the specified parameters"
             * will be detected and returns the error message
             * else activity is returned
             */
            JsonObject return_obj = JsonParser.parseString(api_result).getAsJsonObject();

            // get the response
            // if error message exists, return the error message
            if (return_obj.has("error")) {
                String error = return_obj.get("error").getAsString();
                returnValue.addProperty("error", error);
            } else {
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
            }

            System.out.println("Get request by visiting with /api");

            // test purpose
            System.out.println("Response from web app to mobile: " + returnValue);
            // record the response from web app to mobile
            item.append("Web Application Reply", returnValue.toString());

        }

        // upload item info to mongodb
        collection.insertOne(item);

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
        // record the request sent from web app to api
        item.append("Web Application Request", urlString);
        // test purpose
        System.out.println("Web app request: " + urlString);

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

            // get the response timestamp and calculate latency
            responseTime = new Timestamp(System.currentTimeMillis());
            latency = responseTime.getTime() - timestamp.getTime();
            // test purpose
            System.out.println("Latency: " + latency);
            // record the latency
            item.append("latency", latency);

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
            } else {
                return Integer.toString(responseCode);
            }

            String api_response = "Response Code: " + responseCode + ", Content: " + response;
            // test purpose
            System.out.println(api_response);
            // record the response from api to web app
            item.append("API Reply", api_response);

            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
        }
        return response;
    }

}