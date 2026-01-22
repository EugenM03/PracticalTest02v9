package ro.pub.cs.systems.eim.practicaltest02v9;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ro.pub.cs.systems.eim.practicaltest02v9.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v9.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02v9.network.ServerThread;

public class PracticalTest02v9MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button connectButton;

    private EditText wordEditText;
    private EditText minLenEditText;
    private Button getAnagramsButton;
    private TextView resultTextView;

    private ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v9_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // init views
        serverPortEditText = findViewById(R.id.server_port_edit_text);
        connectButton = findViewById(R.id.connect_button);

        wordEditText = findViewById(R.id.word_edit_text);
        minLenEditText = findViewById(R.id.min_len_edit_text);
        getAnagramsButton = findViewById(R.id.get_anagrams_button);
        resultTextView = findViewById(R.id.result_text_view);

        // Listener
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverPort = serverPortEditText.getText().toString();
                if (serverPort == null || serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (serverThread != null) {
                    Log.v(Constants.TAG, "[MAIN] Stopping previous server thread");
                    serverThread.stopThread();
                }
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                serverThread.start();
                Toast.makeText(getApplicationContext(), "Server started!", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener pentru client
        getAnagramsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverPort = serverPortEditText.getText().toString();
                String word = wordEditText.getText().toString();
                String minLen = minLenEditText.getText().toString();

                if (serverPort.isEmpty() || word.isEmpty() || minLen.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }


                ClientThread clientThread = new ClientThread(
                        "localhost",
                        Integer.parseInt(serverPort),
                        word,
                        Integer.parseInt(minLen),
                        resultTextView
                );
                clientThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN] onDestroy callback method was invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}