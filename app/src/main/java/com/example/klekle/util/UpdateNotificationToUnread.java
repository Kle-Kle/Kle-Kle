package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateNotificationToUnread extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL = "https://ywww-zzauz.run.goorm.site/klekle/main/notification/updateNotificationToUnread.php";
    private Map<String,String>map;

    public UpdateNotificationToUnread(String notificationNo, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("notification_no", notificationNo);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}