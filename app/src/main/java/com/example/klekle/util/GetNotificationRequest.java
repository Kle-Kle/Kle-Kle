package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GetNotificationRequest extends StringRequest {
    static ServerBaseURL serverBaseURL = new ServerBaseURL();
    static final String baseURL = serverBaseURL.getBaseURL();
    final static private String URL = baseURL + "/main/notification/getNotifications.php";
    // 서버 url(API 호출)
    private Map<String,String>map;

    public GetNotificationRequest(String userid, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("article_author_userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}