package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class GetArticlesRequest extends StringRequest {

    final static private String URL = "https://yewon-txuxl.run.goorm.io/klekle/main/my/getArticles.php";

    private Map<String,String> map;

    public GetArticlesRequest(Response.Listener<String>listener) {
        super(Method.POST, URL, listener, null);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
