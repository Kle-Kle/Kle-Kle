package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteArticleRequest extends StringRequest {
    final static private String URL = "https://ywww-zzauz.run.goorm.site/klekle/main/community/deleteArticle.php";
    private Map<String,String> map;

    public DeleteArticleRequest(String article_no, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("article_no",article_no);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
