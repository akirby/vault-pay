package com.vaultpayment.vault;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button btnTestMain;


    private TextView mTextMessage;
    private TextView paymentdesc;
    private FloatingActionButton btnAdd;
    int MY_PERMISSIONS_REQUEST_NFC;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    btnAdd.hide();
                    paymentdesc.setText(R.string.pay_desc);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.payment_methods_title);
                    btnAdd.show();
                    paymentdesc.setText("");
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_history);
                    btnAdd.hide();
                    paymentdesc.setText("");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatActivity thisActivity = this;
        if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(thisActivity, new String[]{ Manifest.permission.NFC},
                    MY_PERMISSIONS_REQUEST_NFC);
        }
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        btnAdd = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btnAdd.hide();
        paymentdesc = (TextView) findViewById(R.id.payscreendescription);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
