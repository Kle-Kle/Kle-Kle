package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostArticleRequest extends StringRequest {
    final static private String URL="https://ywww-zzauz.run.goorm.site/klekle/main/community/postArticle.php";
    private Map<String,String> map;

    public PostArticleRequest(String image, String userid ,String content,Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("content",content);
        map.put("userid",userid);
        map.put("image", image);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
