package org.microg.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ModelsUtil {

    private static final Map<Integer, String[]> modelMap = new HashMap<Integer, String[]>();
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private static final String STATUS = "STATUS";
    private static final String DEVICE_MODEL_PARAM = "devicemodelparam";
    private static final String DEVICE_NAME = "deviceName";
    private static final String BUILD_VERSION = "buildVersion";

    //保存配置文件
    public static void writeStatus(Context context) {
        sp = context.getSharedPreferences(DEVICE_MODEL_PARAM, Context.MODE_PRIVATE);
        editor = sp.edit();

        int status = Integer.parseInt(getStatus(context));
        editor.putString(STATUS, String.valueOf(status + 1));
        editor.commit();
    }

    private static void writeDeviceName(Context context, String deviceName) {
        sp = context.getSharedPreferences(DEVICE_MODEL_PARAM, Context.MODE_PRIVATE);
        editor = sp.edit();

        editor.putString(DEVICE_NAME, deviceName);
        editor.commit();
    }

    private static void writeBuildVersion(Context context, String buildVersion) {
        sp = context.getSharedPreferences(DEVICE_MODEL_PARAM, Context.MODE_PRIVATE);
        editor = sp.edit();

        editor.putString(BUILD_VERSION, buildVersion);
        editor.commit();
    }

    public static String getDeviceName(Context context) {
        sp = context.getSharedPreferences(DEVICE_MODEL_PARAM, Context.MODE_PRIVATE);
        editor = sp.edit();

        if (sp.contains(DEVICE_NAME)) {
            return sp.getString(DEVICE_NAME, "null");
        }

        return "";
    }

    public static String getBuildVersion(Context context) {
        sp = context.getSharedPreferences(DEVICE_MODEL_PARAM, Context.MODE_PRIVATE);
        editor = sp.edit();

        if (sp.contains(BUILD_VERSION)) {
            return sp.getString(BUILD_VERSION, "null");
        }

        return "";
    }

    public static String getStatus(Context context) {
        sp = context.getSharedPreferences(DEVICE_MODEL_PARAM, Context.MODE_PRIVATE);
        editor = sp.edit();

        String status = "0";
        if (sp.contains(STATUS)) {
            status = sp.getString(STATUS, "null");
        } else {
            editor.putString(STATUS, status);
            editor.commit();
        }

        return status;
    }

    private static void loadProperty(Context context) {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("models.properties");
            properties.load(inputStream);

            Set<Object> keySet = properties.keySet();

            int index = 0;
            for (Object keyTmp : keySet) {
                String[] model = new String[2];
                model[0] = String.valueOf(keyTmp);
                model[1] = String.valueOf(properties.get(keyTmp));
                if (!"".equals(model[0]) && !"".equals(model[1])) {
                    modelMap.put(index++, model);
                }
            }
        } catch (IOException e) {
            //
        }
    }

    public static String[] getModel(Context context) {
        if (modelMap.isEmpty()) {
            loadProperty(context);
        }

        String deviceName = getDeviceName(context);
        String buildVersion = getBuildVersion(context);
        if (!"".equals(deviceName) && !"".equals(buildVersion)) {
            String[] model = {deviceName, buildVersion};
            return model;
        }
        if (modelMap.isEmpty()) {
            return new String[]{"", ""};
        }
        int modelIndex = 0;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                SecureRandom sr = SecureRandom.getInstanceStrong();
                modelIndex = sr.nextInt(modelMap.size() - 1);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (modelIndex >= modelMap.size() || modelIndex < 0) {
            modelIndex = 0;
        }

        String[] model = modelMap.get(modelIndex);
        if (model.length == 2) {
            writeDeviceName(context, model[0]);
            writeBuildVersion(context, model[1]);
        }

        return model;
    }
}
