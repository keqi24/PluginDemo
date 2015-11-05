package dev.derek.plugindemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dev.derek.pluginsdk.PluginProxyActivity;
import dev.derek.pluginsdk.PluginStatic;
import dev.derek.pluginsdk.PluginUtils;

public class HostActivity extends AppCompatActivity {


    private static final String PLUGIN_KEY = "plugin1.apk";

    TextView mTvIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTvIndicator = (TextView) findViewById(R.id.tv_indicator);
    }

    public void onLoadPluginClass(View view) {

        String pluginPath  = getPluginPath();
        if (pluginPath == null) {
            return;
        }

        final File optimizedDexOutputPath = getApplicationContext().getDir("odex", Context.MODE_PRIVATE);
        try{
            DexClassLoader classloader = new DexClassLoader(getPluginPath(),
                    optimizedDexOutputPath.getAbsolutePath(),
                    null, this.getApplicationContext().getClassLoader());
            Class<?> clazz = classloader.loadClass("dev.derek.plugin.Plugin1Activity");
            Object obj = clazz.newInstance();
            Method method = clazz.getMethod("getDescription");
            String description = (String) method.invoke(obj);
            mTvIndicator.setText(description);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onInstallPlugin(View view) {
        boolean success = PluginUtils.installPlugin(HostActivity.this, getPluginPath(), PLUGIN_KEY);
        if (success) {
            toast("install plugin success!");
        } else {
            toast("install plugin failed!");
        }
    }

    public void onStartPlugin(View view) {
        startPlugin(PLUGIN_KEY);
    }


    public String getPluginPath() {
        File apkFile = new File(Environment.getExternalStorageDirectory().getPath() + "/plugin1.apk");
        if (!apkFile.exists()) {
            mTvIndicator.setText("Please put the \"plugin.apk\" in the \"sdcard/\"");
            return null;
        }
        return apkFile.getAbsolutePath();
    }


    private void startPlugin(String pluginId){
        Intent intent = new Intent(HostActivity.this, PluginProxyActivity.class);
        intent.putExtra(PluginStatic.PARAM_PLUGIN_NAME, pluginId);
        startActivity(intent);
    }


    private void toast(String messge) {
        Snackbar.make(getWindow().getDecorView(), messge, Snackbar.LENGTH_SHORT).show();
    }

}
