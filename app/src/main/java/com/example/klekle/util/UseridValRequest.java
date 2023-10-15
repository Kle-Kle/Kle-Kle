package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;

public class UseridValRequest extends StringRequest {
    static ServerBaseURL serverBaseURL = new ServerBaseURL();
    static final String baseURL = serverBaseURL.getBaseURL();
    final static private String URL = baseURL + "/auth/useridValidate.php";
    private Map<String,String> map;

    public UseridValRequest(String userid, Response.Listener<String>listener){
        super(Request.Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}