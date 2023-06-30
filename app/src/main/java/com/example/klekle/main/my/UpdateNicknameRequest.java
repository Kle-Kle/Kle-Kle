package com.example.klekle.main.my;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateNicknameRequest extends StringRequest {
    // 서버 url(API 호출)
    final static private String URL="https://yewon-txuxl.run.goorm.io/klekle/main/my/updateNickname.php";
    private Map<String,String>map;

    public UpdateNicknameRequest(String newNickname, String nickname, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("newNickname", newNickname);
        map.put("nickname", nickname);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}