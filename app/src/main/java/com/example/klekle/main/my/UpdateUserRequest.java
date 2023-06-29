package com.example.klekle.main.my;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserRequest extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL="https://yewon-txuxl.run.goorm.io/klekle/main/my/updateUser.php";
    private Map<String,String>map;

    public UpdateUserRequest(String email, String userpw, String userid, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("email", email);
        map.put("userpw", userpw);
        map.put("userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}