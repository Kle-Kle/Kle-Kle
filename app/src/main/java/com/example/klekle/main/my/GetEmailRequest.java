package com.example.klekle.main.my;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GetEmailRequest extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL="https://yewon-txuxl.run.goorm.io/klekle/main/my/getEmail.php";
    private Map<String,String>map;

    public GetEmailRequest(String userid, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}