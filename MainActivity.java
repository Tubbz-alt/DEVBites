package com.zebra.emdkversion;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.VersionManager;


public class MainActivity extends AppCompatActivity implements EMDKManager.EMDKListener {


    private TextView versionManagerTextView, packageManagerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        versionManagerTextView = findViewById(R.id.versionManager);
        packageManagerTextView = findViewById(R.id.packageManager);


        EMDKManager.getEMDKManager(this, this);


        try {
            String EMDKVersion = "Package Manager: " + getEMDKVersion();
            packageManagerTextView.setText(EMDKVersion);
        } catch (PackageManager.NameNotFoundException e) {
            packageManagerTextView.setText("Package Manager: Package Not Found!");
        }

    }

    public String getEMDKVersion() throws PackageManager.NameNotFoundException {
        String emdkPkgName = "com.symbol.emdk.emdkservice";
        PackageInfo pinfo = getPackageManager().getPackageInfo(emdkPkgName, 0);
        String emdkVersion = pinfo.versionName;
        return emdkVersion;
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        VersionManager versionManager = (VersionManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.VERSION);
        String version = versionManager.getVersion(VersionManager.VERSION_TYPE.EMDK);
        versionManagerTextView.setText("Version Manager: " + version);
    }

    @Override
    public void onClosed() {

    }
}
