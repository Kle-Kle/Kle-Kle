package com.example.klekle;

import static com.example.klekle.util.BitmapConverter.bitmapToByteArray;
import static com.example.klekle.util.BitmapConverter.byteArrayToBinaryString;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.domain.Hold;
import com.example.klekle.util.BitmapConverter;
import com.example.klekle.util.DetectHoldRequest;
import com.example.klekle.util.PostArticleRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PostActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private ImageView ivInputImage;
    private Button btnPost;
    public TextView tvCurrentArticleLength;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ivInputImage = findViewById(R.id.iv_inputImage);
        btnPost = findViewById(R.id.btn_post);
        tvCurrentArticleLength = findViewById(R.id.tv_CurrentArticleLength);
        editText = findViewById(R.id.content);

        sharedPreferences = getSharedPreferences("tempData", Activity.MODE_PRIVATE);
        String wallImage = sharedPreferences.getString("tempWallImage", null);
        Bitmap bitmapD = BitmapConverter.stringToBitmap(wallImage).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapD);

        ivInputImage.setImageBitmap(bitmapD);

        int holdCount = getIntent().getIntExtra("holdCount", 0);
        ArrayList<Hold> holds = new ArrayList<>();

        Hold startHold = (Hold) getIntent().getSerializableExtra("startHold");
        Hold topHold = (Hold) getIntent().getSerializableExtra("topHold");

        detectHoldRequest(wallImage, canvas, holdCount, holds, startHold, topHold);

        Paint paint = makePaintForStartHoldAndTopHold();
        canvas.drawPoint(startHold.getXmax(), startHold.getYmax(), paint);
        canvas.drawPoint(topHold.getXmax(), topHold.getYmax(), paint);

        btnPost.setOnClickListener(view -> {
            String base64Bitmap = bitmapToByteArray(bitmapD);
            String content = editText.getText().toString();
            postArticle(base64Bitmap, content);
        });

        // 게시글 본문 내용 글자 수 세기
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentArticleLength = editText.length();
                tvCurrentArticleLength.setText("("+currentArticleLength+"/280)");

                if (currentArticleLength == 0) {
                    btnPost.setEnabled(false);
                    btnPost.setAlpha(0.5F);
                }
                else {
                    btnPost.setEnabled(true);
                    btnPost.setAlpha(1F);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });
        btnPost.setEnabled(false); // 첫 입장 시, '게시' 버튼 비활성화
    }

    @NonNull
    private Paint makePaintForStartHoldAndTopHold() {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(30f);
        return paint;
    }

    private void detectHoldRequest(String wallImage, Canvas canvas, int holdCount, List<Hold> holds, Hold startHold, Hold topHold) {
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

                        if (xmax < startHold.getXmax() || xmax > topHold.getXmax()) {
                            Log.d("D:ttung", "ymax:" + ymax + "startHold Ymax: " +  startHold.getYmax() + "topHold Ymax:" +  topHold.getYmax());
                            Log.d("D:ttung", "xmax:" + xmax + "startHold Xmax: " +  startHold.getXmax() + "topHold Xmax:" +  topHold.getXmax());
                            i--;
                            continue;
                        }

                        Log.d("D:final xmax:", String.valueOf(xmax));

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

    private void postArticle(String image, String content) {

        SharedPreferences preferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE);
        String userid = preferences.getString("loginedId", null);

        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean success = jsonObject.getBoolean("success");
                if (success) {
                    this.finish();
                    Intent intent = new Intent(PostActivity.this, CommunityActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(this, "업로드에 실패하였습니다.\n" +
                            "잠시 뒤 다시 시도해 주세요.", Toast.LENGTH_LONG);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        PostArticleRequest postArticleRequest = new PostArticleRequest(image, userid, content, responseListener);
        RequestQueue queue = Volley.newRequestQueue(PostActivity.this);
        queue.add(postArticleRequest);
    }

    @NonNull
    private Paint makePaintForPoint() {
        Paint paintWithPoint = new Paint();
        paintWithPoint.setColor(Color.RED);
        paintWithPoint.setStrokeWidth(30f);
        return paintWithPoint;
    }
}