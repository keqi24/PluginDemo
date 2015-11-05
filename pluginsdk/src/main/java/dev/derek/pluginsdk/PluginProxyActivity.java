package dev.derek.pluginsdk;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import dalvik.system.DexClassLoader;


public class PluginProxyActivity extends AppCompatActivity {
    IPluginActivity mPluginActivity;
    String mPluginApkFilePath;
    String mLaunchActivity;
    private String mPluginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mPluginName = bundle.getString(PluginStatic.PARAM_PLUGIN_NAME);
        mLaunchActivity = bundle.getString(PluginStatic.PARAM_LAUNCH_ACTIVITY);
        File pluginFile = PluginUtils.getInstallPath(PluginProxyActivity.this, mPluginName);
        if (!pluginFile.exists()) {
            return;
        }
        mPluginApkFilePath = pluginFile.getAbsolutePath();
        try {
            initPlugin();
            super.onCreate(savedInstanceState);
            mPluginActivity.IOnCreate(savedInstanceState);
        } catch (Exception e) {
            mPluginActivity = null;
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPluginActivity != null) {
            mPluginActivity.IOnResume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPluginActivity != null) {
            mPluginActivity.IOnStart();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (mPluginActivity != null) {
            mPluginActivity.IOnRestart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPluginActivity != null) {
            mPluginActivity.IOnStop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPluginActivity != null) {
            mPluginActivity.IOnPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPluginActivity != null) {
            mPluginActivity.IOnDestroy();
        }
    }

    private void initPlugin() throws Exception {
        PackageInfo packageInfo;
        try {
            PackageManager pm = getPackageManager();
            packageInfo = pm.getPackageArchiveInfo(mPluginApkFilePath, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            throw e;
        }

        if (mLaunchActivity == null || mLaunchActivity.length() == 0) {
            mLaunchActivity = packageInfo.activities[0].name;
        }

        final File optimizedDexOutputPath = getApplicationContext().getDir("odex", Context.MODE_PRIVATE);
//        String optimizedDexOutputPath = getDir("odex", Context.MODE_PRIVATE).getAbsolutePath();
//        ClassLoader classLoader = PluginStatic.getOrCreateClassLoaderByPath(this, mPluginName, mPluginApkFilePath);

        ClassLoader classLoader = new DexClassLoader(mPluginApkFilePath,
                optimizedDexOutputPath.getAbsolutePath(),
                null, this.getApplicationContext().getClassLoader());

        if (mLaunchActivity == null || mLaunchActivity.length() == 0) {
            if (packageInfo == null || (packageInfo.activities == null) || (packageInfo.activities.length == 0)) {
                throw new ClassNotFoundException("Launch Activity not found");
            }
            mLaunchActivity = packageInfo.activities[0].name;
        }
        Class<?> mClassLaunchActivity = (Class<?>) classLoader.loadClass(mLaunchActivity);

        getIntent().setExtrasClassLoader(classLoader);
        mPluginActivity = (IPluginActivity) mClassLaunchActivity.newInstance();
        mPluginActivity.IInit(mPluginApkFilePath, this, classLoader, packageInfo);
    }


    protected Class<? extends PluginProxyActivity> getProxyActivity(String pluginActivityName) {
        return getClass();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        boolean pluginActivity = intent.getBooleanExtra(PluginStatic.PARAM_IS_IN_PLUGIN, false);
        if (pluginActivity) {
            String launchActivity = null;
            ComponentName componentName = intent.getComponent();
            if (null != componentName) {
                launchActivity = componentName.getClassName();
            }
            intent.putExtra(PluginStatic.PARAM_IS_IN_PLUGIN, false);
            if (launchActivity != null && launchActivity.length() > 0) {
                Intent pluginIntent = new Intent(this, getProxyActivity(launchActivity));

                pluginIntent.putExtra(PluginStatic.PARAM_PLUGIN_NAME, mPluginName);
                pluginIntent.putExtra(PluginStatic.PARAM_PLUGIN_PATH, mPluginApkFilePath);
                pluginIntent.putExtra(PluginStatic.PARAM_LAUNCH_ACTIVITY, launchActivity);
                startActivityForResult(pluginIntent, requestCode);
            }
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }



}

