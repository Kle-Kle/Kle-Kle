package com.example.klekle.signup;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InputBodyInfoRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL = "https://yewon-txuxl.run.goorm.io/klekle/inputBodyInfo.php";
    private Map<String,String> map;

    public InputBodyInfoRequest(String userid, String height, String weight, String reach, Response.Listener<String>listener){
        super(Method.POST, URL, listener,null); // 위 url에 post방식으로 값을 전송

        map = new HashMap<>();
        map.put("userid", userid);
        map.put("height", height);
        map.put("weight", weight);
        map.put("reach", reach);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}