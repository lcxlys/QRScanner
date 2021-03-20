package com.lcx.qrscanner;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;

import com.lcx.scanner.ScannerView;

public class MainActivity extends Activity {

    private ScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannerView = findViewById(R.id.main_scanner);
        scannerView.setScanRect(new Rect(300, 300, 600, 600));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stop();
    }
}