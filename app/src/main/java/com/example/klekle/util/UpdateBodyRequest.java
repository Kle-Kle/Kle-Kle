package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateBodyRequest extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL="https://ywww-zzauz.run.goorm.site/klekle/main/my/updateBody.php";
    private Map<String,String>map;

    public UpdateBodyRequest(String height, String weight, String reach, String userid, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("height", height);
        map.put("weight", weight);
        map.put("reach", reach);
        map.put("userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}