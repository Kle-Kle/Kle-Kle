package com.example.klekle.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateCommentRequest extends StringRequest {
    final static private String URL="https://ywww-zzauz.run.goorm.site/klekle/main/community/updateComment.php";
    private Map<String,String> map;

    public UpdateCommentRequest(String existingCommentNo, String content, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("comment_no",existingCommentNo);
        map.put("content",content);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
