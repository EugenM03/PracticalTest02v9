package ro.pub.cs.systems.eim.practicaltest02v9.network;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import ro.pub.cs.systems.eim.practicaltest02v9.general.Constants;

public class ServerThread extends Thread {

    private int port;
    private ServerSocket serverSocket;

    public ServerThread(int port) {
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void getServerSocket() {
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Log.v(Constants.TAG, "[Server] Started on port " + port);

            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[Server] Waiting for clients...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[Server] A client has connected!");

                // start thread
                CommunicationThread communicationThread = new CommunicationThread(socket);
                communicationThread.start();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[Server] An exception has occurred: " + ioException.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[Server] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}