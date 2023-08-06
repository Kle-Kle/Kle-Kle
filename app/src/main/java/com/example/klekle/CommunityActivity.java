package com.example.klekle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.klekle.util.GetArticlesRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommunityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        LinearLayout container = findViewById(R.id.article_list);
        LayoutInflater layoutInflater = LayoutInflater.from(this);


        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);

                Log.d("jsonObject", String.valueOf(jsonObject));

                boolean success = jsonObject.getBoolean("success");
                JSONArray result = jsonObject.getJSONArray("result");

                if (success) {
                    for (int i = 0; i < result.length(); i++) {
                        View articleListLayout = layoutInflater.inflate(R.layout.activity_article_list, null, false);
                        TextView userName = articleListLayout.findViewById(R.id.user_id);
                        String userid = result.getJSONObject(i).getString("userid");
                        userName.setText(userid);

                        View article = articleListLayout.findViewById(R.id.btn_goToArticle);
                        moveToArticleDetail(userid, article);

                        View profileImage = articleListLayout.findViewById(R.id.profile_imege);
                        moveToUserPage(userid, profileImage);

                        container.addView(articleListLayout);
                    }
                    return;
                }

                printMessageWhenArticleListIsEmpty(container);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        GetArticlesRequest getArticlesRequest = new GetArticlesRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(CommunityActivity.this);
        queue.add(getArticlesRequest);
    }

    private void moveToArticleDetail(String userid, View article) {
        article.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
            intent.putExtra("userid", userid);
            startActivity(intent);
        });
    }

    private void moveToUserPage(String userid, View profileImage) {
        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UserPageActivity.class);
            intent.putExtra("userid", userid);
            startActivity(intent);
        });
    }

    private void printMessageWhenArticleListIsEmpty(LinearLayout container) {
        TextView text = new TextView(getApplicationContext());
        text.setText("등록된 게시물이 없습니다.");
        container.addView(text);
    }
}
