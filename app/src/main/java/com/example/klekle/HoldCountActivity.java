package com.example.klekle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.klekle.domain.Hold;

import java.io.Serializable;

public class HoldCountActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hold_count_page);

        NumberPicker numberPicker = findViewById(R.id.number_picker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(5);

        Hold startHold = (Hold) getIntent().getSerializableExtra("startHold");
        Hold topHold = (Hold) getIntent().getSerializableExtra("topHold");

        View confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PostActivity.class);
            intent.putExtra("holdCount", numberPicker.getValue());
            intent.putExtra("startHold", startHold);
            intent.putExtra("topHold", topHold);
            startActivity(intent);
        });
    }
}
