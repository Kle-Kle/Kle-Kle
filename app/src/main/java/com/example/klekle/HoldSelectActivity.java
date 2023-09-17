package com.example.klekle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.domain.Hold;
import com.example.klekle.util.BitmapConverter;
import com.example.klekle.util.DetectHoldRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HoldSelectActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private ImageView ivInputImage;
    private Button btnPost;
    private final int HOLD_SUGGEST_COUNT = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hold_select);

        ivInputImage = findViewById(R.id.iv_inputImage);
        btnPost = findViewById(R.id.btn_post);

        sharedPreferences = getSharedPreferences("tempData", Activity.MODE_PRIVATE);
        String wallImage = sharedPreferences.getString("tempWallImage", null);
        Bitmap bitmapD = BitmapConverter.stringToBitmap(wallImage).copy(Bitmap.Config.ARGB_8888, true);

        ivInputImage.setImageBitmap(bitmapD);

        Canvas canvas = new Canvas(bitmapD);
        List<Hold> randomHolds = new ArrayList<>();
        detectHoldRequest(wallImage, canvas, randomHolds);

        NumberPicker startHoldPicker = findViewById(R.id.start_hold_picker);
        NumberPicker topHoldPicker = findViewById(R.id.top_hold_picker);
        setMinValueAndMaxValue(startHoldPicker, topHoldPicker);

        btnPost.setOnClickListener(view -> {
            int startHoldNumber = startHoldPicker.getValue();
            int topHoldNumber = topHoldPicker.getValue();

            if (startHoldNumber == topHoldNumber) {
                Toast.makeText(this, "시작 홀드와 탑 홀드는 달라야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(HoldSelectActivity.this, HoldCountActivity.class);
            Hold startHold = randomHolds.get(startHoldNumber - 1);
            Hold topHold = randomHolds.get(topHoldNumber - 1);

            intent.putExtra("startHold", startHold);
            intent.putExtra("topHold", topHold);

            finish();
            startActivity(intent);
        });
    }

    private void detectHoldRequest(String wallImage, Canvas canvas, List<Hold> randomHolds) {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean success = jsonObject.getBoolean("success");
                JSONArray result = jsonObject.getJSONArray("results");
                if (success) {
                    Log.d("D:Test", String.valueOf(result));

                    int length = result.length();

                    Random random = new Random();
                    Paint paintForPoint = makePaintForPoint();
                    Paint paintWithText = makePaintForText();

                    for (int i = 0; i < HOLD_SUGGEST_COUNT; i++) {
                        int randomValue = random.nextInt(length);
                        JSONObject hold = result.getJSONObject(randomValue);
                        int xmax = hold.getInt("xmax");
                        int ymax = hold.getInt("ymax");

                        canvas.drawPoint(xmax, ymax, paintForPoint);
                        canvas.drawText(String.valueOf(i+1), xmax, ymax, paintWithText);
                        randomHolds.add(new Hold(xmax, ymax));
                    }
                }
                else {
                    Toast.makeText(this, "서버와 통신에 실패했습니다.\n잠시 뒤에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        DetectHoldRequest detectHoldRequest = new DetectHoldRequest(wallImage, responseListener);
        RequestQueue queue = Volley.newRequestQueue(HoldSelectActivity.this);
        queue.add(detectHoldRequest);
    }

    @NonNull
    private Paint makePaintForPoint() {
        Paint paintWithPoint = new Paint();
        paintWithPoint.setColor(Color.RED);
        paintWithPoint.setStrokeWidth(30f);
        return paintWithPoint;
    }

    @NonNull
    private Paint makePaintForText() {
        Paint paintWithText = new Paint();
        paintWithText.setTextSize(50);
        return paintWithText;
    }

    private void setMinValueAndMaxValue(NumberPicker startHoldPicker, NumberPicker topHoldPicker) {
        startHoldPicker.setMaxValue(HOLD_SUGGEST_COUNT);
        startHoldPicker.setMinValue(1);
        startHoldPicker.setValue(1);

        topHoldPicker.setMaxValue(HOLD_SUGGEST_COUNT);
        topHoldPicker.setMinValue(1);
        topHoldPicker.setValue(1);
    }
}