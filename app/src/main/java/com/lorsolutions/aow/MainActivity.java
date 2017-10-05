package com.lorsolutions.aow;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    Button btn;
    TextView txtS;
    TextView txtI;
    ImageView img;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btnChAdb);
        img = (ImageView) findViewById(R.id.imgChAdb);
        txtI = (TextView) findViewById(R.id.txtIP);
        txtS = (TextView) findViewById(R.id.txtStatus);

        addListenerOnButton();
        isWifiAdbSet();
    }

    public void chAdbPort(Boolean t) {
        String port;
        if (t) {
            port = "5555\n";
        } else {
            port = "\"\"\n";
        }
        try{
            Process sh = Runtime.getRuntime().exec("sh");
            DataOutputStream outputStream = new DataOutputStream(sh.getOutputStream());

            outputStream.writeBytes("setprop persist.adb.tcp.port " + port);
            outputStream.flush();

            outputStream.writeBytes("stop adbd\n");
            outputStream.flush();

            outputStream.writeBytes("start adbd\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            sh.waitFor();
            isWifiAdbSet();
        } catch(IOException e) {
            Log.d("AoW", "- Error!");
        } catch(InterruptedException e) {
            Log.d("AoW", "- Error!");
        }
    }

    public void getWifiAddress() {
        WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        txtI.setText(ip);
    }

    public void isWifiAdbSet() {
        try {
            Process p = Runtime.getRuntime().exec("getprop persist.adb.tcp.port");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = in.readLine();
            if (line.equals("5555")) {
                img.setImageAlpha(255);
                txtS.setTextColor(Color.BLACK);
                txtS.setText("ENABLED");
                btn.setText("DISABLE");
            } else {
                img.setImageAlpha(69);
                txtS.setTextColor(Color.RED);
                txtS.setText("DISABLED");
                btn.setText("ENABLE");
            }
        } catch (Exception e) {
            Log.d("AoW", "- Error!");
        }
        getWifiAddress();
    }

    public void addListenerOnButton() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (btn.getText().equals("Enable")) {
                    chAdbPort(true);
                    btn.setText("Disable");
                } else {
                    chAdbPort(false);
                    btn.setText("Enable");
                }
            }
        });
    }
}
