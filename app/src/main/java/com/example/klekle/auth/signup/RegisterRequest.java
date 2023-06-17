package com.example.klekle.auth.signup;

import android.app.Activity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL = "https://yewon-txuxl.run.goorm.io/klekle/auth/register.php";
    private Map<String,String>map;

    public RegisterRequest(String userid, String email, String userpw, String nickname, String height, String weight, String reach, Response.Listener<String>listener){
        super(Method.POST, URL, listener,null); // 위 url에 post방식으로 값을 전송

        map = new HashMap<>();
        map.put("userid", userid);
        map.put("email", email);
        map.put("userpw", userpw);
        map.put("nickname", nickname);

        map.put("height", height);
        map.put("weight", weight);
        map.put("reach", reach);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}