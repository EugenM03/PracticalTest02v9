package ro.pub.cs.systems.eim.practicaltest02v9.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02v9.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v9.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String word;
    private int minLen;
    private TextView resultTextView;

    private Socket socket;

    public ClientThread(String address, int port, String word, int minLen, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.minLen = minLen;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {

            socket = new Socket(address, port);
            Log.v(Constants.TAG, "[Client] Connected to: " + address + ":" + port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);


            String request = word + "," + minLen;
            printWriter.println(request);
            Log.v(Constants.TAG, "[Client] Sent request: " + request);


            String response = bufferedReader.readLine();
            Log.v(Constants.TAG, "[Client] Received response: " + response);


            if (response != null) {
                final String finalResponse = response;
                resultTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextView.setText(finalResponse);
                    }
                });
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[Client] An exception has occurred: " + ioException.getMessage());
            final String errorMessage = ioException.getMessage();
            resultTextView.post(new Runnable() {
                @Override
                public void run() {
                    resultTextView.setText("Error: " + errorMessage);
                }
            });
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[Client] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}