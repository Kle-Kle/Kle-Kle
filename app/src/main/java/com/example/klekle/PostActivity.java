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
import android.widget.Button;
import android.widget.ImageView;
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

public class PostActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private ImageView ivInputImage;
    private Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ivInputImage = findViewById(R.id.iv_inputImage);
        btnPost = findViewById(R.id.btn_post);

        sharedPreferences = getSharedPreferences("tempData", Activity.MODE_PRIVATE);
        String wallImage = sharedPreferences.getString("tempWallImage", null);
        Bitmap bitmapD = BitmapConverter.stringToBitmap(wallImage).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapD);

        ivInputImage.setImageBitmap(bitmapD);

        int holdCount = getIntent().getIntExtra("holdCount", 0);
        ArrayList<Hold> holds = new ArrayList<>();
        detectHoldRequest(wallImage, canvas, holdCount, holds);

        Hold startHold = (Hold) getIntent().getSerializableExtra("startHold");
        Hold topHold = (Hold) getIntent().getSerializableExtra("topHold");

        Paint paint = makePaintForStartHoldAndTopHold();
        canvas.drawPoint(startHold.getXmax(), startHold.getYmax(), paint);
        canvas.drawPoint(topHold.getXmax(), topHold.getYmax(), paint);

        btnPost.setOnClickListener(view -> {
            Intent intent = new Intent(PostActivity.this, ArticleActivity.class);
            intent.putExtra("startHold", startHold);
            intent.putExtra("topHold", topHold);
            intent.putExtra("holds", holds);
            finish();
            startActivity(intent);
        });
    }

    @NonNull
    private Paint makePaintForStartHoldAndTopHold() {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(30f);
        return paint;
    }

    private void detectHoldRequest(String wallImage, Canvas canvas, int holdCount, List<Hold> holds) {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean success = jsonObject.getBoolean("success");
                JSONArray result = jsonObject.getJSONArray("results");
                if (success) {
                    Log.d("D:Test", String.valueOf(result));

                    int length = result.length();
                    Random random = new Random();
                    Paint paint = makePaintForPoint();

                    for (int i = 0; i < holdCount; i++) {
                        int randomValue = random.nextInt(length);
                        JSONObject hold = result.getJSONObject(randomValue);
                        int xmax = hold.getInt("xmax");
                        int ymax = hold.getInt("ymax");

                        canvas.drawPoint(xmax, ymax, paint);
                        holds.add(new Hold(xmax, ymax));
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
        RequestQueue queue = Volley.newRequestQueue(PostActivity.this);
        queue.add(detectHoldRequest);
    }

    @NonNull
    private Paint makePaintForPoint() {
        Paint paintWithPoint = new Paint();
        paintWithPoint.setColor(Color.RED);
        paintWithPoint.setStrokeWidth(30f);
        return paintWithPoint;
    }
}