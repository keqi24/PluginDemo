package dev.derek.pluginsdk;

import android.content.Context;

import java.io.*;

public class PluginUtils {
    private static final String PLUGIN_PATH = "plugins";


    public static File getInstallPath(Context context, String pluginID) {
        File pluginDir = getPluginPath(context);
        if (pluginDir == null) {
            return null;
        }
        int suffixBegin = pluginID.lastIndexOf('.');
        if (suffixBegin != -1 && !pluginID.substring(suffixBegin).equalsIgnoreCase(".apk")) {
            pluginID = pluginID.substring(0, suffixBegin) + ".apk";
        } else if (suffixBegin == -1) {
            pluginID = pluginID + ".apk";
        }
        return new File(pluginDir, pluginID);
    }

    public static File getPluginPath(Context context) {
        return context.getDir(PLUGIN_PATH, Context.MODE_PRIVATE);
    }

    public static boolean installPlugin(Context context, String pluginPath, String pluginID){
        File pluginFile = new File(context.getDir(PLUGIN_PATH, Context.MODE_PRIVATE), pluginID);
        if(pluginFile.exists()){
            return true;
        }

        BufferedInputStream bis = null;
        OutputStream dexWriter = null;

        final int BUF_SIZE = 8 * 1024;
        try {
            bis = new BufferedInputStream(new FileInputStream(pluginPath));
            dexWriter = new BufferedOutputStream(
                    new FileOutputStream(pluginFile));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }  finally {
            if (dexWriter != null) {
                try {
                    dexWriter.close();
                } catch (IOException e) {
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
