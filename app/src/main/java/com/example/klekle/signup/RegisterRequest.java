package com.example.klekle.signup;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="https://yewon-txuxl.run.goorm.io/klekle/register.php";
    private Map<String,String>map;

    public RegisterRequest(String userid, String userpw, String nickname, Response.Listener<String>listener){
        super(Method.POST, URL, listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("userid", userid);
        map.put("userpw", userpw);
        map.put("nickname", nickname);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}