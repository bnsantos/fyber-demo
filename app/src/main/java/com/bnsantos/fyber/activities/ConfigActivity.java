package com.bnsantos.fyber.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bnsantos.fyber.Constants;
import com.bnsantos.fyber.R;

public class ConfigActivity extends AppCompatActivity {
    private EditText uid;
    private EditText apiKey;
    private EditText appId;
    private EditText pub0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        uid = (EditText) findViewById(R.id.uid);
        apiKey = (EditText) findViewById(R.id.apiKey);
        appId = (EditText) findViewById(R.id.appId);
        pub0 = (EditText) findViewById(R.id.pub0);

        findViewById(R.id.offerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOffers();
            }
        });

        pub0.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getOffers();
                    return true;
                }
                return false;
            }
        });
    }

    private void getOffers() {
        if (isValid()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_UID, uid.getText().toString());
            intent.putExtra(Constants.INTENT_EXTRA_API_KEY, apiKey.getText().toString());
            intent.putExtra(Constants.INTENT_EXTRA_APP_ID, appId.getText().toString());
            intent.putExtra(Constants.INTENT_EXTRA_PUB0, pub0.getText().toString());
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.error_cant_get_offers, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        return uid.getText().length() > 0 && apiKey.getText().length() > 0 && appId.getText().length() > 0;
    }
}
