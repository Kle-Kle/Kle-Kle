package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GetMyArticlesThisMonthRequest extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL = "https://ywww-zzauz.run.goorm.site/klekle/main/calendar/getMyArticlesThisMonth.php";
    private Map<String,String>map;

    public GetMyArticlesThisMonthRequest(String currentDate, String userid, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("currentDate", currentDate);
        map.put("userid", userid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}