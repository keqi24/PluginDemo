package dev.derek.plugin;

import android.os.Bundle;

import dev.derek.pluginsdk2.BasePluginActivity;

public class Plugin1Activity extends BasePluginActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
    }


    public String getDescription() {
        return "Plugin1Activity";
    }
}
