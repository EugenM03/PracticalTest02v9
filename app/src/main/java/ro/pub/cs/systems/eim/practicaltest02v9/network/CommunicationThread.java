package ro.pub.cs.systems.eim.practicaltest02v9.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ro.pub.cs.systems.eim.practicaltest02v9.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v9.general.Utilities;

public class CommunicationThread extends Thread {

    private Socket socket;

    public CommunicationThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[Server] Socket is null!");
            return;
        }

        try {
            // citire cerere
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            String request = bufferedReader.readLine();
            if (request == null || request.isEmpty()) {
                Log.e(Constants.TAG, "[Server] Received empty request!");
                socket.close();
                return;
            }

            Log.i(Constants.TAG, "[Server] Received request: " + request);

            // <cuvant>,<min_litere>
            String[] parts = request.split(",");

            String word = parts[0].trim();
            int minLen;
            try {
                minLen = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                socket.close();
                return;
            }

            // http get
            String urlString = Constants.WEB_SERVICE_ADDRESS + word;
            Log.i(Constants.TAG, "[Server] Getting data from: " + urlString);

            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Citim raspunsul de la site
                BufferedReader urlReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = urlReader.readLine()) != null) {
                    content.append(line);
                }
                urlReader.close();
                urlConnection.disconnect();

            } catch (Exception e) {
                Log.e(Constants.TAG, "[Server] HTTP Error: " + e.getMessage());
                printWriter.println("Error accessing web service");
                socket.close();
                return;
            }

            String jsonResponse = content.toString();
            Log.d(Constants.TAG, "[Server] Response from website: " + jsonResponse);

            // json parsing
            List<String> validAnagrams = new ArrayList<>();
            try {
                // api returneaza un obiect care contine un array "all"
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray allAnagramsArray = jsonObject.getJSONArray("all");

                for (int i = 0; i < allAnagramsArray.length(); i++) {
                    String anagram = allAnagramsArray.getString(i);
                    // filtrare in functie de parametri alesi
                    if (anagram.length() >= minLen) {
                        validAnagrams.add(anagram);
                    }
                }
            } catch (JSONException e) {
                Log.e(Constants.TAG, "[Server] JSON Parsing error: " + e.getMessage());
            }

            // rasp final
            StringBuilder resultBuilder = new StringBuilder();
            for (String s : validAnagrams) {
                resultBuilder.append(s).append(" ");
            }
            String finalResult = resultBuilder.toString().trim();

            Log.i(Constants.TAG, "[Server] Filtered anagrams: " + finalResult);



            // trimitere anagrame
            printWriter.println(finalResult);

            socket.close();

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[Server] An exception has occurred: " + ioException.getMessage());
        }
    }
}