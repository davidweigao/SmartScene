package david.sceneapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by david on 7/4/14.
 */
public class SceneStorageManager {
    static final String PREFS_NAME = "sceneInfoShared";
    static final String KEY_ALL_SCESE = "ssjidfj2982f";

    private SharedPreferences sp;

    public SceneStorageManager(Context context) {
        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveScene(Scene scene) {

        // encrypt card sensitive data as a json string
        // add the new card sensitive data
        String scenesJson = sp.getString(KEY_ALL_SCESE, null);
        ArrayList<String> scenes = new ArrayList<String>();
        if(scenesJson != null) {
            scenes = new Gson().fromJson(scenesJson, scenes.getClass());
        }
        scenes.add(new Gson().toJson(scene));
        // update data
        String newScenesJson = new Gson().toJson(scenes);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ALL_SCESE, newScenesJson);
        editor.commit();
    }

    public ArrayList<Scene> getAllScene() {
        ArrayList<String> scenes = new ArrayList<String>();
        String s = sp.getString(KEY_ALL_SCESE, null);
        if (s != null)
            scenes = new Gson().fromJson(s, scenes.getClass());

        ArrayList<Scene> scenes1 = new ArrayList<Scene>();
        for(String ss : scenes)
            scenes1.add(new Gson().fromJson(ss, Scene.class));
        return scenes1;
    }

    public void dumpAll() {
        sp.edit().clear().commit();
    }
}
