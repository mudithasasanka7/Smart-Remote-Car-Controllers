package com.example.carcontroller;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private ImageView joystickHandle;
    private RelativeLayout joystickBase;
    private float baseCenterX, baseCenterY, baseRadius, handleRadius;

    private WebSocketClient webSocketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystickBase = findViewById(R.id.joystickBase);
        joystickHandle = findViewById(R.id.joystickHandle);

        joystickBase.post(() -> {
            baseCenterX = joystickBase.getX() + joystickBase.getWidth() / 2;
            baseCenterY = joystickBase.getY() + joystickBase.getHeight() / 2;
            baseRadius = joystickBase.getWidth() / 2;
            handleRadius = joystickHandle.getWidth() / 2;
        });

        joystickHandle.setOnTouchListener((v, event) -> {
            float x = event.getRawX();
            float y = event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = x - baseCenterX;
                    float dy = y - baseCenterY;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance > baseRadius - handleRadius) {
                        double ratio = (baseRadius - handleRadius) / distance;
                        dx *= ratio;
                        dy *= ratio;
                    }

                    joystickHandle.setX(baseCenterX + dx - joystickHandle.getWidth() / 2);
                    joystickHandle.setY(baseCenterY + dy - joystickHandle.getHeight() / 2);

                    // Send WebSocket message
                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (dx > 0) sendCommand("RIGHT");
                        else sendCommand("LEFT");
                    } else {
                        if (dy > 0) sendCommand("DOWN");
                        else sendCommand("UP");
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    joystickHandle.setX(baseCenterX - joystickHandle.getWidth() / 2);
                    joystickHandle.setY(baseCenterY - joystickHandle.getHeight() / 2);
                    sendCommand("RESET");
                    break;
            }
            return true;
        });

        connectWebSocket();
    }

    private void connectWebSocket() {
        try {
            URI uri = new URI("ws://192.168.4.1:81");
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    runOnUiThread(() -> System.out.println("Connected to WebSocket"));
                }

                @Override
                public void onMessage(String message) {}

                @Override
                public void onClose(int code, String reason, boolean remote) {}

                @Override
                public void onError(Exception ex) {}
            };
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(String command) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(command);
        }
    }
}