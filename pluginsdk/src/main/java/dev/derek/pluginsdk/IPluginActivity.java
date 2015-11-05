package dev.derek.pluginsdk;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;

interface IPluginActivity {
    void IOnCreate(Bundle savedInstanceState);

    void IOnResume();

    void IOnStart();

    void IOnPause();

    void IOnStop();

    void IOnDestroy();

    void IOnRestart();

    void IInit(String path, Activity context, ClassLoader classLoader, PackageInfo packageInfo);
}
