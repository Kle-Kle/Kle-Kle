package com.example.klekle.signup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText joinId, joinPw, joinPwCheck, joinNickName;
    private Button btnNext;
    private AlertDialog dialog;
    private boolean validate = false;

    private FragmentManager fragmentManager;
    private RegisterFragment registerFragment;
    private InsertBodyInfoFragment insertBodyInfoFragment;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fragmentManager = getSupportFragmentManager();

        registerFragment = new RegisterFragment();
        insertBodyInfoFragment = new InsertBodyInfoFragment();

//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.frameLayout, registerFragment).commitAllowingStateLoss();

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생성

        joinId = findViewById(R.id.join_id);
        joinPw = findViewById(R.id.join_pw);
        joinPwCheck = findViewById(R.id.join_pw_check);
        joinNickName = findViewById(R.id.join_nickname);

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // editText에 입력되어있는 값을 get(가져온다)해온다
                String userid = joinId.getText().toString();
                final String userpw = joinPw.getText().toString();
                String nickname = joinNickName.getText().toString();
                final String pwcheck = joinPwCheck.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() { // volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject=new JSONObject(response); // Register2 php에 response
                            boolean success=jasonObject.getBoolean("success");//Register2 php에 sucess
                            if(userpw.equals(pwcheck)) {
                                if (success) { // 회원등록 성공한 경우
                                    transaction.replace(R.id.frameLayout, insertBodyInfoFragment).commitAllowingStateLoss();
                                }
                            }
                            else{ // 회원등록 실패한 경우
                                Toast.makeText(getApplicationContext(),"회원 등록에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 volley를 이용해서 요청을 함
                RegisterRequest registerRequest=new RegisterRequest(userid, userpw, nickname, responseListener);
                RequestQueue queue= Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
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
