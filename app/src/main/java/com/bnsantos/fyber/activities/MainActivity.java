package com.bnsantos.fyber.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toast;

import com.bnsantos.fyber.Constants;
import com.bnsantos.fyber.R;
import com.bnsantos.fyber.adapters.OffersAdapter;
import com.bnsantos.fyber.model.Offer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String uid;
    private String apiKey;
    private String appId;
    private String pub0;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OffersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extractIntentData(getIntent());
        if (uid == null || apiKey == null || appId == null) {
            Toast.makeText(this, R.string.error_cant_get_offers, Toast.LENGTH_SHORT).show();
            finish();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new OffersAdapter(new ArrayList<Offer>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
    }

    private void extractIntentData(Intent intent) {
        if (intent != null) {
            uid = intent.getStringExtra(Constants.INTENT_EXTRA_UID);
            apiKey = intent.getStringExtra(Constants.INTENT_EXTRA_API_KEY);
            appId = intent.getStringExtra(Constants.INTENT_EXTRA_APP_ID);
            pub0 = intent.getStringExtra(Constants.INTENT_EXTRA_PUB0);
        }
    }

    private void reload() {

    }
}
