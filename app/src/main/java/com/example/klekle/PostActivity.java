package com.example.klekle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.util.BitmapConverter;
import com.example.klekle.util.DetectHoldRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Bitmap bitmapD = BitmapConverter.stringToBitmap(wallImage);
        ivInputImage.setImageBitmap(bitmapD);

        detectHoldRequest(wallImage);

        btnPost.setOnClickListener(view -> {
            Intent intent = new Intent(PostActivity.this, ArticleActivity.class);
            finish();
            startActivity(intent);
        });
    }

    private void detectHoldRequest(String wallImage) {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean success = jsonObject.getBoolean("success");
                JSONArray result = jsonObject.getJSONArray("results");
                if (success) {
                    Log.d("D:Test", String.valueOf(result));
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
}