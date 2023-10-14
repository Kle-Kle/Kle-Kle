package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateCommentsRequest extends StringRequest {
    static ServerBaseURL serverBaseURL = new ServerBaseURL();
    static final String baseURL = serverBaseURL.getBaseURL();
    final static private String URL = baseURL + "/main/community/createComment.php";
    // 서버 url 설정(php파일 연동)
    private Map<String,String>map;

    public CreateCommentsRequest(String content, String userid, String article_no, Response.Listener<String>listener){
        super(Method.POST, URL, listener,null); // 위 url에 post방식으로 값을 전송

        map = new HashMap<>();
        map.put("content", content);
        map.put("userid", userid);
        map.put("article_no", article_no);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}