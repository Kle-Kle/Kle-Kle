package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateFcmTokenRequest extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL = "https://ywww-zzauz.run.goorm.site/klekle/auth/updateFcmToken.php";
    private Map<String,String>map;

    public UpdateFcmTokenRequest(String fcmToken, String userid, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("fcmToken", fcmToken);
        map.put("userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}