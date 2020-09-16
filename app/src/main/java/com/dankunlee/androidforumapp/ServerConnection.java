package com.dankunlee.androidforumapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dankunlee.androidforumapp.request.HttpRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ServerConnection {
    private HttpRequest request;
    private Context context = null; // for accessing shared preferences (for session ID)
    private boolean isLogInConnection = false;

    public ServerConnection(HttpRequest request, Context context) {
        this.request = request;
        this.context = context;
    }

    // special function to save sessionID when logging in
    public void setLogInConnection() {
        isLogInConnection = true;
    }

    public static boolean checkConnection() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(MainActivity.host);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            if (connection.getResponseCode() == 200) return true;
        }
        catch (Exception e) {
            Log.e("Connection Failure: ", e.toString());
        }
        finally {
            if (connection != null) connection.disconnect();
        }
        return false;
    }

    public String makeRequest() {
        HttpURLConnection connection = null;
        String requestURL = request.generateFullRequestURL();
        String stringResponse = "Connection Error";
        try {
            URL url = new URL(requestURL);
            connection = (HttpURLConnection) url.openConnection(); // opens a connection to the url

            // configures the connection
            connection.setConnectTimeout(3000); // connection time out: 3s
            connection.setReadTimeout(3000);
            connection.setRequestMethod(request.getRequestMethod());
            connection.setRequestProperty("Content-Language", request.getContentLanguage());
            connection.setRequestProperty("Accept-Charset", request.getAcceptCharest());
            connection.setRequestProperty("Content-Type", request.getContentType());
            connection.setDoOutput(request.isDoOutput());

            // if logged in (session ID exists), sends the session ID along the connection
            SharedPreferences preferences = context.getSharedPreferences("sessionCookie", MODE_PRIVATE);
            String sessionID = preferences.getString("sessionID", null);
            if (sessionID != null)
                connection.setRequestProperty("Cookie", sessionID);


            // writes the request body to the connection
            if (request.getJsonInput() != null) {
                OutputStream writer = connection.getOutputStream();
                byte[] input = request.getJsonInput().getBytes("utf-8");
                writer.write(input, 0, input.length);
            }

            // reads the response body from the connection
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")) ;
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = reader.readLine()) != null)
                response.append(responseLine.trim());
            stringResponse = response.toString();

            // for a successful log in connection, saves session ID to the shared preferences
            if (isLogInConnection) {
                List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
                if (cookies != null && stringResponse.equals("Logged In")) {
                    String returnedSessionID = cookies.get(0).split(";\\s*")[0];
                    SharedPreferences.Editor editor = preferences.edit();
                    if (preferences.getString("sessionID", null) == null) // first time logging in (no prev sessionID has been saved)
                        Log.i(requestURL, "First Time Log In Session=" + returnedSessionID);
                    else if (!preferences.getString("sessionID", null).equals(returnedSessionID))
                        Log.i(requestURL, "Previous Session has Expired. New Session=" + returnedSessionID);
                    editor.putString("sessionID", returnedSessionID);
                    editor.commit();
                }
            }

            Log.i(requestURL, request.getRequestMethod() + "|" + String.valueOf(connection.getResponseCode())); // prints response code
        }
        catch (Exception e) {
            Log.e(requestURL, e.toString());
        }
        finally {
            if (connection != null) // disconnects the connection no matter what
                connection.disconnect();
        }

        return stringResponse;
    }
}
