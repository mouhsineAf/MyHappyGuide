package com.devm22.happyguide.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.devm22.happyguide.R;
import com.devm22.happyguide.adapter.NotificationsAdapter;
import com.devm22.happyguide.helper.DatabaseHelperNotifications;
import com.devm22.happyguide.model.Notifications;
import com.devm22.happyguide.utils.Utils;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = "NotificationsActivity";

    ArrayList<Notifications> notificationsArrayList = new ArrayList<>();
    NotificationsAdapter notificationsAdapter;
    RecyclerView recyclerViewNotifications;
    DatabaseHelperNotifications databaseHelperNotifications;
    TextView textNoNotifications;

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerViewNotifications = findViewById(R.id.recycleViewNotifications);
        textNoNotifications = findViewById(R.id.textNoNotifications);
        btnBack = findViewById(R.id.btn_back);



        databaseHelperNotifications = new DatabaseHelperNotifications(this);


        onClick();
        buildRecycleNotifications();
        loadNotifications();

    }
    
    private void onClick() {
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void buildRecycleNotifications() {
        recyclerViewNotifications.setHasFixedSize(true);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        notificationsAdapter = new NotificationsAdapter(this);
        recyclerViewNotifications.setAdapter(notificationsAdapter);

        notificationsAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Utils.openUrl(NotificationsActivity.this, notificationsArrayList.get(position).getNotificationLaunchURL());
            }
        });

    }

    private void loadNotifications() {
        notificationsArrayList = new ArrayList<>();
        notificationsArrayList = databaseHelperNotifications.getNotifications();
        notificationsAdapter.addNotifications(notificationsArrayList);

        if (notificationsArrayList.size() == 0){
            textNoNotifications.setVisibility(View.VISIBLE);
        }else {
            textNoNotifications.setVisibility(View.GONE);
            databaseHelperNotifications.updateShowedNotifications();
        }
    }



}