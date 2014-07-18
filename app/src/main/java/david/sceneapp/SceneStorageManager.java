package david.sceneapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 7/4/14.
 */
public class SceneStorageManager {
    static final String PREFS_NAME = "sceneInfoShared";
    static final String KEY_ALL_SCESE = "ssjidfj2982f";
    static final String KEY_ALL_TRIGGER = "wefsdge3twrf34";
    static final String KEY_ALL_EXCEPTION = "jasldfj2ef2";

    private SharedPreferences sp;

    public SceneStorageManager(Context context) {
        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveScene(Scene scene) {

        String scenesJson = sp.getString(KEY_ALL_SCESE, null);
        ArrayList<String> scenes = new ArrayList<String>();
        if(scenesJson != null) {
            scenes = new Gson().fromJson(scenesJson, scenes.getClass());
        }

        int maxId = 0;
        Map<Integer, Scene> allScene = getAllScene();
        for(Scene s : allScene.values()) {
            if(s.getId() > maxId) {
                maxId = s.getId();
            }
        }
        maxId++;
        scene.setId(maxId);

        scenes.add(new Gson().toJson(scene));
        // update data
        String newScenesJson = new Gson().toJson(scenes);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ALL_SCESE, newScenesJson);
        editor.commit();

        LALALAService.currentInstance.updateScenes();
    }

    public Map<Integer, Scene> getAllScene() {
        ArrayList<String> scenes = new ArrayList<String>();
        String s = sp.getString(KEY_ALL_SCESE, null);
        if (s != null)
            scenes = new Gson().fromJson(s, scenes.getClass());

        Map<Integer, Scene> sceneMap = new HashMap<Integer, Scene>();
        for(String ss : scenes) {
            Scene s1 = new Gson().fromJson(ss, Scene.class);
            sceneMap.put(s1.getId(), s1);

        }
        return sceneMap;
    }

    public void saveTrigger(SceneTriggerData triggerData) {

        String scenesJson = sp.getString(KEY_ALL_TRIGGER, null);
        ArrayList<String> triggers = new ArrayList<String>();
        if(scenesJson != null) {
            triggers = new Gson().fromJson(scenesJson, triggers.getClass());
        }
        int maxId = 0;
        Map<Integer, SceneTriggerData> allTrigger = getAllTrigger();
        for(SceneTriggerData std : allTrigger.values()) {
            if(std.getId() > maxId) {
                maxId = std.getId();
            }
        }
        maxId++;
        triggerData.setId(maxId);
        triggers.add(new Gson().toJson(triggerData));
        // update data
        String newTriggersJson = new Gson().toJson(triggers);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ALL_TRIGGER, newTriggersJson);
        editor.commit();
        LALALAService.currentInstance.updateTriggers();
    }

    public Map<Integer, SceneTriggerData> getAllTrigger() {
        ArrayList<String> triggers = new ArrayList<String>();
        String s = sp.getString(KEY_ALL_TRIGGER, null);
        if (s != null)
            triggers = new Gson().fromJson(s, triggers.getClass());

        Map<Integer, SceneTriggerData> triggerDataMap = new HashMap<Integer,SceneTriggerData>();
        for(String ss : triggers) {
            SceneTriggerData std = new Gson().fromJson(ss, SceneTriggerData.class);
            triggerDataMap.put(std.getId(), std);
        }
        return triggerDataMap;
    }

    public void dumpAll() {
        sp.edit().clear().commit();
    }

    public void deleteScene(int id) {
        String scenesJson = sp.getString(KEY_ALL_SCESE, null);
        ArrayList<String> scenes = new ArrayList<String>();
        if(scenesJson != null) {
            scenes = new Gson().fromJson(scenesJson, scenes.getClass());
        }

        for(String s : scenes) {
            Scene ss = new Gson().fromJson(s, Scene.class);
            if(ss.getId() == id) {
                scenes.remove(s);
                break;
            }
        }

        // update data
        String newScenesJson = new Gson().toJson(scenes);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ALL_SCESE, newScenesJson);
        editor.commit();

        LALALAService.currentInstance.updateScenes();
    }

    public void deleteTrigger(int id) {
        String scenesJson = sp.getString(KEY_ALL_TRIGGER, null);
        ArrayList<String> triggers = new ArrayList<String>();
        if(scenesJson != null) {
            triggers = new Gson().fromJson(scenesJson, triggers.getClass());
        }

        for(String s : triggers) {
            SceneTriggerData data = new Gson().fromJson(s, SceneTriggerData.class);
            if(data.getId() == id) {
                triggers.remove(s);
                break;
            }
        }
        // update data
        String newTriggersJson = new Gson().toJson(triggers);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ALL_TRIGGER, newTriggersJson);
        editor.commit();
        LALALAService.currentInstance.updateTriggers();
    }

    public void saveException(ExceptionScene exp) {
        String exceptionsJson = sp.getString(KEY_ALL_EXCEPTION, null);
        ArrayList<String> exceptions = new ArrayList<String>();
        if(exceptionsJson != null) {
            exceptions = new Gson().fromJson(exceptionsJson, exceptions.getClass());
        }

        int id = -1;
        for(String s : exceptions) {
            ExceptionScene es = new Gson().fromJson(s, ExceptionScene.class);
            if(es.getId() > id) {
                id = es.getId();
            }
        }
        exp.setId(id + 1);

        String newExp = new Gson().toJson(exp);
        exceptions.add(newExp);
        String newExceptionsJson = new Gson().toJson(exceptions);
        sp.edit().putString(KEY_ALL_EXCEPTION, newExceptionsJson).commit();
    }

    public void deleteException(int id) {
        String exceptionsJson = sp.getString(KEY_ALL_EXCEPTION, null);
        ArrayList<String> exceptions = new ArrayList<String>();
        if(exceptionsJson != null) {
            exceptions = new Gson().fromJson(exceptionsJson, exceptions.getClass());
        }

        for(String s : exceptions) {
            ExceptionScene es = new Gson().fromJson(s, ExceptionScene.class);
            if(es.getId() == id) {
                exceptions.remove(s);
                break;
            }
        }

        String newExceptionsJson = new Gson().toJson(exceptions);
        sp.edit().putString(KEY_ALL_EXCEPTION, newExceptionsJson).commit();
        if(LALALAService.currentInstance != null)
            LALALAService.currentInstance.updateExceptions();
    }

    public List<ExceptionScene> getAllException() {
        String exceptionsJson = sp.getString(KEY_ALL_EXCEPTION, null);
        ArrayList<String> exceptions = new ArrayList<String>();
        if(exceptionsJson != null) {
            exceptions = new Gson().fromJson(exceptionsJson, exceptions.getClass());
        }

        ArrayList<ExceptionScene> allExps = new ArrayList<ExceptionScene>();
        Gson gson = new Gson();
        for(String s : exceptions) {
            allExps.add(gson.fromJson(s, ExceptionScene.class));
        }
        return allExps;
    }
}
