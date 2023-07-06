package com.example.klekle.main.my;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.R;
import com.example.klekle.util.GetBodyRequest;
import com.example.klekle.util.UpdateBodyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateBodyActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText inputHeight, inputWeight, inputReach;
    private Button btnAmend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_body);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생성

        inputHeight = findViewById(R.id.input_height);
        inputWeight = findViewById(R.id.input_weight);
        inputReach = findViewById(R.id.input_reach);

        // 현재 로그인 중인 사용자의 ID
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);
        String userid = sharedPreferences.getString("loginedId", null);

        // 기존에 저장돼 있던 신체 정보를 불러옴
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        // Database에 저장되어 있던, 사용자의 신체 정보 가져오기에 성공
                        inputHeight.setText(jsonObject.getString("height"));
                        inputWeight.setText(jsonObject.getString("weight"));
                        inputReach.setText(jsonObject.getString("reach"));
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "신체 정보 불러오기에 실패했습니다.\n잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetBodyRequest getBodyRequest = new GetBodyRequest(userid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(UpdateBodyActivity.this);
        queue.add(getBodyRequest);

        // '수정' 버튼 클릭 시 실행되는 부분
        btnAmend = (Button) findViewById(R.id.btn_amend);
        btnAmend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String height = inputHeight.getText().toString();
                final String weight = inputWeight.getText().toString();
                final String reach = inputReach.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);
                            boolean success = jasonObject.getBoolean("success");
                            if (success) { // 정보 수정에 성공한 경우
                                Toast.makeText(getApplicationContext(), "신체 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "신체 정보 수정에 실패했습니다.\n잠시 뒤에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                UpdateBodyRequest updateBodyRequest = new UpdateBodyRequest(height, weight, reach, userid, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UpdateBodyActivity.this);
                queue.add(updateBodyRequest);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}