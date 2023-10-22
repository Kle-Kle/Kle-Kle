package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DetectHoldRequest extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL = "http://43.200.192.190:56495/v1/object-detection/hold";
    private Map<String,String>map;

    public DetectHoldRequest(String image, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("image", image);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}