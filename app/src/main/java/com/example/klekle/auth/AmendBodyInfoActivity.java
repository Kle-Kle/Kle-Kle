package com.example.klekle.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.klekle.R;

public class AmendBodyInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText inputHeight, inputWeight, inputReach;
    private Button btnAmend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amend_body_info);

        btnAmend = (Button) findViewById(R.id.btn_amend);

        btnAmend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '계정 생성' 버튼 클릭 시 실행되는 부분
                // 계정 생성 후, 다시 로그인 페이지로 연결
//                Intent intent = new Intent(AmendBodyInfoActivity.this, LoginActivity.class);
//                startActivity(intent);
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