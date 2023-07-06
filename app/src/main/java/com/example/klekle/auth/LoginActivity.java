package com.example.klekle.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.MainActivity;
import com.example.klekle.R;
import com.example.klekle.auth.signup.RegisterActivity;
import com.example.klekle.util.LoginRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText loginId, loginPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생성

        // 계정이 없으신가요? link가 연결된 TextView 생성
        TextView goToRegister = (TextView) findViewById(R.id.go_to_register);
        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TextView 클릭 시 실행되는 부분
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        TextView goToFindpw = (TextView) findViewById(R.id.go_to_findpw);
        goToFindpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비밀번호 찾기 페이지로 연결
            }
        });

        loginId = findViewById(R.id.login_id);
        loginPw = findViewById(R.id.login_pw);
        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String userid = loginId.getText().toString();
                String userpw = loginPw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);
                            boolean success = jasonObject.getBoolean("success");
                            if (success) { // 로그인 성공한 경우
                                String userid = jasonObject.getString("userid");
                                String nickname = jasonObject.getString("nickname");
                                Toast.makeText(getApplicationContext(), "어서오세요, " + nickname + "님!", Toast.LENGTH_SHORT).show();

                                // 한 번이라도 로그인 한 적 있다면 -> 자동 로그인되도록
                                SharedPreferences sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLogin = sharedPreferences.edit();
                                autoLogin.putString("loginedId", userid);
                                autoLogin.putString("nickname", nickname); // shared preference 에 nickname 저장
                                autoLogin.apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("log", "user");
                                intent.putExtra("userid", userid);
                                finishAffinity(); // 스택에 있는 모든 Activity 종료
                                // 로그인이 안 된 상태면(회원이 아니면) 앱 사용 자체가 불가하도록 통제
                                startActivity(intent);
                            }

                            else{
                                Snackbar.make(v, "로그인 정보를 다시 확인해 주세요.", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userid, userpw, responseListener);
                RequestQueue queue= Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
                //Login php 연결 -> LoginRequest.java
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