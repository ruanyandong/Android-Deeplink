package com.ai.deeplink;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author AI
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebActivity.class));
            }
        });

        getDataFromBrowser();
    }

    /**
     * 从deep link中获取数据
     * 'will://share/传过来的数据'
     */
    private void getDataFromBrowser() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Uri data = getIntent().getData();
        if (data == null) {
            return;
        }
        try {
            String scheme = data.getScheme();
            String host = data.getHost();
            Log.d("ryd", "getDataFromBrowser: " + scheme + " " + host);
//            List<String> params = data.getPathSegments();
//            // 从网页传过来的数据
//            String testId = params.get(0);
//            String text = "Scheme: " + scheme + "\n" + "host: " + host + "\n" + "params: " + testId;
//            Log.e("ScrollingActivity", text);
//            textView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
