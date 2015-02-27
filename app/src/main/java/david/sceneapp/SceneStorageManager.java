package david.sceneapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import david.sceneapp.Model.ExceptionScene;
import david.sceneapp.Model.Scene;
import david.sceneapp.Model.SceneAppData;
import david.sceneapp.Model.SceneTriggerData;

/**
 * Created by david on 7/4/14.
 */
public class SceneStorageManager {
    static final String PREFS_NAME = "sceneInfoShared";
    static final String KEY_ALL_SCESE = "ssjidfj2982f";
    static final String KEY_ALL_TRIGGER = "wefsdge3twrf34";
    static final String KEY_ALL_EXCEPTION = "jasldfj2ef2";
    static final String KEY_MAXID_SCENE = "kjslf";
    static final String KEY_MAXID_TRIGGER = "jsdfw";
    static final String KEY_MAXID_EXCEPTION = "w34r";

    static final String KEY_WIFI_ENABLED = "wifienabled";


    static final int DATA_SCENE = 0;
    static final int DATA_TRIGGER = 1;
    static final int DATA_EXCEPTION = 2;
    static final String[] KEYS_TREEMAP = new String[]{KEY_ALL_SCESE,
                                                      KEY_ALL_TRIGGER,
                                                      KEY_ALL_EXCEPTION};
    static final String[] KEYS_MAXID = new String[]{KEY_MAXID_SCENE,
                                                   KEY_MAXID_TRIGGER,
                                                   KEY_MAXID_EXCEPTION};

    private SharedPreferences sp;

    public SceneStorageManager(Context context) {
        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private String serializeMap(Map map) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(bos);
            os.writeObject(map);

            String s = bytesToHex(bos.toByteArray());
            Log.e("dfkljd", s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private Object deserializeMap(String s) {
        byte[] bytes = hexStringToByteArray(s);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    public void saveObject(SceneAppData data, int dataType) {
        Map map = getAllObject(dataType);
        if(!map.containsKey(data.getId())) {
            int maxId = sp.getInt(KEYS_MAXID[dataType], -1);
            data.setId(++maxId);
            sp.edit().putInt(KEYS_MAXID[dataType], maxId).commit();
        }
        map.put(data.getId(), data);
        String s = serializeMap(map);
        sp.edit().putString(KEYS_TREEMAP[dataType], s).commit();
    }

    public void deleteObject(int id, int dataType) {
        Map map = getAllObject(dataType);
        map.remove(id);
        String s = serializeMap(map);
        sp.edit().putString(KEYS_TREEMAP[dataType], s).commit();

    }

    public TreeMap getAllObject(int dataType) {
        String key = KEYS_TREEMAP[dataType];
        String s = sp.getString(key, null);
        TreeMap<Integer, ? extends SceneAppData> map = null;
        if(s == null){
            switch (dataType) {
                case DATA_SCENE:
                    map = new TreeMap<Integer, Scene>();
                    break;
                case DATA_TRIGGER:
                    map = new TreeMap<Integer, SceneTriggerData>();
                    break;
                case DATA_EXCEPTION:
                    map = new TreeMap<Integer, ExceptionScene>();
                    break;
            }
        }else {
            switch (dataType) {
                case DATA_SCENE:
                    map = (TreeMap<Integer, Scene>)deserializeMap(s);
                    break;
                case DATA_TRIGGER:
                    map = (TreeMap<Integer, SceneTriggerData>)deserializeMap(s);
                    break;
                case DATA_EXCEPTION:
                    map = (TreeMap<Integer, ExceptionScene>)deserializeMap(s);
                    break;
            }
        }


        return map;
    }



    public void saveScene(Scene scene) {
        saveObject(scene,DATA_SCENE);
        SceneManageService.currentInstance.updateScenes();
    }



    public TreeMap<Integer, Scene> getAllScene() {
        return (TreeMap<Integer, Scene>) getAllObject(DATA_SCENE);
    }

    public void saveTrigger(SceneTriggerData triggerData) {
        saveObject(triggerData, DATA_TRIGGER);
        SceneManageService.currentInstance.updateTriggers();
    }

    public TreeMap<Integer, SceneTriggerData> getAllTrigger() {
        return (TreeMap<Integer, SceneTriggerData>) getAllObject(DATA_TRIGGER);
    }

    public void dumpAll() {
        sp.edit().clear().commit();
    }

    public void deleteScene(int id) {
        deleteObject(id, DATA_SCENE);
        SceneManageService.currentInstance.updateScenes();
    }

    public void deleteTrigger(int id) {
        deleteObject(id, DATA_TRIGGER);
        SceneManageService.currentInstance.updateTriggers();
    }

    public void saveException(ExceptionScene exp) {
        saveObject(exp, DATA_EXCEPTION);
    }

    public void deleteException(int id) {
        deleteObject(id, DATA_EXCEPTION);
    }

    public Map<Integer, ExceptionScene> getAllException() {
        Map map = (TreeMap<Integer, ExceptionScene>) getAllObject(DATA_EXCEPTION);
        return map;
    }



    public static void setWifiEnabled(Context context, boolean enabled) {
        PreferenceManager.getDefaultSharedPreferences(
                context).edit().putBoolean(KEY_WIFI_ENABLED, enabled).commit();
    }
    public static boolean getWifiEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_WIFI_ENABLED, false);
    }
}
