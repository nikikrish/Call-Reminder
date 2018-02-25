package com.example.nikikrish.remdemo;

import android.Manifest;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecycleAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;
    List<UserDetails> details;
    DbHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new DbHandler(getApplicationContext());
        details = handler.getAllReminders();
        if(details.size()>0){
            setContentView(R.layout.activity_main);

            recyclerView = findViewById(R.id.recyclerView);
            coordinatorLayout= findViewById(R.id.coordinatorLayout);

            mAdapter = new RecycleAdapter(getApplicationContext(), details);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(mAdapter);


        }else{
            setContentView(R.layout.empty_layout);
        }

        Dexter.withActivity(this).withPermissions(
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted())
                    Log.e("Main Activity", "All Permission Granted");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Toast.makeText(getApplicationContext(), "Permissions are needed to run this app", Toast.LENGTH_LONG).show();
            }
        }).check();

    }

    @Override
    protected void onResume() {
        super.onResume();
        details = handler.getAllReminders();
        if(details.size()>0){
            mAdapter = new RecycleAdapter(getApplicationContext(), details);
            recyclerView.setAdapter(mAdapter);
        }

    }
}

