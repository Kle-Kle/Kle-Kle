package com.example.klekle.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.CameraActivity;
import com.example.klekle.LoginActivity;
import com.example.klekle.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class InsertUserInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private String userid;
    public String getUserid() {
        return userid;
    }

    private String userpw;
    public String getUserpw() {
        return userpw;
    }

    private String userpwch;
    public String getUserpwch() {
        return userpwch;
    }

    private String nickname;
    public String getNickname() {
        return nickname;
    }

    private Button btnNext;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_user_info);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생성

        EditText etUserid = findViewById(R.id.join_id);
        EditText etUserpw = findViewById(R.id.join_pw);
        EditText etUserpwch = findViewById(R.id.join_pw_check);
        EditText etNickname = findViewById(R.id.join_nickname);

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userid = etUserid.getText().toString();
                userpw = etUserpw.getText().toString();
                userpwch = etUserpwch.getText().toString();
                nickname = etNickname.getText().toString();

                if (userid.equals("")) {
                    Snackbar.make(view, "아이디는 빈 칸일 수 없습니다.", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    if (userpw.equals("")) {
                        Snackbar.make(view, "비밀번호는 빈 칸일 수 없습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (userpw.equals(userpwch)) {
                        if (nickname.equals("")) {
                            Snackbar.make(view, "닉네임을 설정해 주세요.", Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success) {
                                            validate = true;

                                            Intent intent = new Intent(InsertUserInfoActivity.this, InsertBodyInfoActivity.class);
                                            startActivity(intent);
                                        }
                                        else {
                                            Snackbar.make(view, "사용할 수 없는 아이디 입니다.", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            ValidateRequest validateRequest = new ValidateRequest(userid, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(InsertUserInfoActivity.this);
                            queue.add(validateRequest);
                        }
                    }
                    else {
                        Snackbar.make(view, "비밀번호가 비밀번호 확인란과 일치하지 않습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                }
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