package com.example.carcontroller;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private ImageView joystickHandle;
    private RelativeLayout joystickBase;
    private float baseCenterX, baseCenterY;
    private float baseRadius, handleRadius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joystickBase = findViewById(R.id.joystickBase);
        joystickHandle = findViewById(R.id.joystickHandle);

        // Calculate joystick base and handle dimensions
        joystickBase.post(() -> {
            baseCenterX = joystickBase.getX() + joystickBase.getWidth() / 2;
            baseCenterY = joystickBase.getY() + joystickBase.getHeight() / 2;
            baseRadius = joystickBase.getWidth() / 2;
            handleRadius = joystickHandle.getWidth() / 2;
        });

        joystickHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        // Calculate the distance from the center
                        float dx = x - baseCenterX;
                        float dy = y - baseCenterY;
                        double distance = Math.sqrt(dx * dx + dy * dy);

                        // Restrict handle movement to the joystick base radius
                        if (distance > baseRadius - handleRadius) {
                            double ratio = (baseRadius - handleRadius) / distance;
                            dx *= ratio;
                            dy *= ratio;
                        }

                        // Move the joystick handle
                        joystickHandle.setX(baseCenterX + dx - joystickHandle.getWidth() / 2);
                        joystickHandle.setY(baseCenterY + dy - joystickHandle.getHeight() / 2);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Reset joystick handle to the center
                        joystickHandle.setX(baseCenterX - joystickHandle.getWidth() / 2);
                        joystickHandle.setY(baseCenterY - joystickHandle.getHeight() / 2);
                        break;
                }
                return true;
            }
        });
    }
}