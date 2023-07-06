package com.example.klekle.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;

public class BitmapConverter {
    public static Bitmap stringToBitmap(String data){
        Bitmap bitmap = null;
        byte[] byteArray = Base64.decode(data, Base64.DEFAULT);
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
        bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }

    // byte[] to String
    // 바이너리 바이트 배열을 스트링으로
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    // 바이너리 바이트를 스트링으로
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }
}
