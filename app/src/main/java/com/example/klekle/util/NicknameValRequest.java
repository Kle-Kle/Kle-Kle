package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NicknameValRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="https://yewon-txuxl.run.goorm.io/klekle/auth/nicknameValidate.php";
    private Map<String,String> map;

    public NicknameValRequest(String nickname, Response.Listener<String>listener){
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("nickname", nickname);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}