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
import java.util.TreeMap;

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


    static final int DATA_SCENE = 0;
    static final int DATA_TRIGGER = 1;
    static final int DATA_EXCEPTION = 2;
    static final String[] KEYS_TREEMAP = new String[]{KEY_ALL_SCESE,
                                                      KEY_ALL_TRIGGER,
                                                      KEY_ALL_EXCEPTION};
    static final String[] KEYS_MAXID = new String[]{KEY_MAXID_SCENE,
                                                   KEY_MAXID_TRIGGER,
                                                   KEY_MAXID_EXCEPTION};
//    static final TreeMap[] MAPS = new TreeMap[]{new TreeMap<Integer, Scene>(),
//                                            new TreeMap<Integer, SceneTriggerData>(),
//                                            new TreeMap<Integer, ExceptionScene>()};


    private SharedPreferences sp;

    public SceneStorageManager(Context context) {
        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveObject(SceneAppData data, int dataType) {
//        String key = null;
//        String keyMaxId = null;
//        TreeMap map = null;
//
//        if(data instanceof Scene) {
//            key = KEY_ALL_SCESE;
//            keyMaxId = KEY_MAXID_SCENE;
//            map = new TreeMap<Integer, Scene>();
//
//        } else if(data instanceof SceneTriggerData) {
//            key = KEY_ALL_TRIGGER;
//            keyMaxId
//            map = new TreeMap<Integer, SceneTriggerData>();
//        } else if(data instanceof ExceptionScene) {
//            key = KEY_ALL_EXCEPTION;
//            map = new TreeMap<Integer, ExceptionScene>();
//        }


        Map map = getAllObject(dataType);

        int maxId = sp.getInt(KEYS_MAXID[dataType], -1);
        data.setId(++maxId);

        map.put(data.getId(), data);
        String newJsonString = new Gson().toJson(map);
        sp.edit().putString(KEYS_TREEMAP[dataType], newJsonString).commit();
    }

    public void deleteObject(int id, int dataType) {
        Gson gson = new Gson();
        String key = KEYS_TREEMAP[dataType];
        String keyMaxId = KEYS_MAXID[dataType];
        Map map = getAllObject(dataType);

        map.remove(id);
        String newJsonString = new Gson().toJson(map);
        sp.edit().putString(KEYS_TREEMAP[dataType], newJsonString).commit();

    }

    public Map getAllObject(int dataType) {
        String key = KEYS_TREEMAP[dataType];
        String s = sp.getString(key, null);
        TreeMap<Integer, ? extends SceneAppData> map = null;
        switch (dataType) {
            case DATA_SCENE:
                map = new TreeMap<Integer, Scene>();
                break;
            case DATA_TRIGGER:
                map = new TreeMap<Integer, SceneAppData>();
                break;
            case DATA_EXCEPTION:
                map = new TreeMap<Integer, ExceptionScene>();
                break;
        }
        if(s != null) {
            map = new Gson().fromJson(s, map.getClass());
        }
        return map;
    }



    public void saveScene(Scene scene) {

//        String scenesJson = sp.getString(KEY_ALL_SCESE, null);
//        ArrayList<String> scenes = new ArrayList<String>();
//        if(scenesJson != null) {
//            scenes = new Gson().fromJson(scenesJson, scenes.getClass());
//        }
//
//        int maxId = 0;
//        Map<Integer, Scene> allScene = getAllScene();
//        for(Scene s : allScene.values()) {
//            if(s.getId() > maxId) {
//                maxId = s.getId();
//            }
//        }
//        maxId++;
//        scene.setId(maxId);
//
//        scenes.add(new Gson().toJson(scene));
//        // update data
//        String newScenesJson = new Gson().toJson(scenes);
//        final SharedPreferences.Editor editor = sp.edit();
//        editor.putString(KEY_ALL_SCESE, newScenesJson);
//        editor.commit();

        saveObject(scene,DATA_SCENE);

        LALALAService.currentInstance.updateScenes();
    }



    public Map<Integer, Scene> getAllScene() {
//        ArrayList<String> scenes = new ArrayList<String>();
//        String s = sp.getString(KEY_ALL_SCESE, null);
//        if (s != null)
//            scenes = new Gson().fromJson(s, scenes.getClass());
//
//        Map<Integer, Scene> sceneMap = new HashMap<Integer, Scene>();
//        for(String ss : scenes) {
//            Scene s1 = new Gson().fromJson(ss, Scene.class);
//            sceneMap.put(s1.getId(), s1);
//
//        }
//        return sceneMap;
        return (TreeMap<Integer, Scene>) getAllObject(DATA_SCENE);
    }

    public void saveTrigger(SceneTriggerData triggerData) {

//        String scenesJson = sp.getString(KEY_ALL_TRIGGER, null);
//        ArrayList<String> triggers = new ArrayList<String>();
//        if(scenesJson != null) {
//            triggers = new Gson().fromJson(scenesJson, triggers.getClass());
//        }
//        int maxId = 0;
//        Map<Integer, SceneTriggerData> allTrigger = getAllTrigger();
//        for(SceneTriggerData std : allTrigger.values()) {
//            if(std.getId() > maxId) {
//                maxId = std.getId();
//            }
//        }
//        maxId++;
//        triggerData.setId(maxId);
//        triggers.add(new Gson().toJson(triggerData));
//        // update data
//        String newTriggersJson = new Gson().toJson(triggers);
//        final SharedPreferences.Editor editor = sp.edit();
//        editor.putString(KEY_ALL_TRIGGER, newTriggersJson);
//        editor.commit();
        saveObject(triggerData, DATA_TRIGGER);
        LALALAService.currentInstance.updateTriggers();
    }

    public Map<Integer, SceneTriggerData> getAllTrigger() {
//        ArrayList<String> triggers = new ArrayList<String>();
//        String s = sp.getString(KEY_ALL_TRIGGER, null);
//        if (s != null)
//            triggers = new Gson().fromJson(s, triggers.getClass());
//
//        Map<Integer, SceneTriggerData> triggerDataMap = new HashMap<Integer,SceneTriggerData>();
//        for(String ss : triggers) {
//            SceneTriggerData std = new Gson().fromJson(ss, SceneTriggerData.class);
//            triggerDataMap.put(std.getId(), std);
//        }
//        return triggerDataMap;
        return (TreeMap<Integer, SceneTriggerData>) getAllObject(DATA_TRIGGER);

    }

    public void dumpAll() {
        sp.edit().clear().commit();
    }

    public void deleteScene(int id) {
//        String scenesJson = sp.getString(KEY_ALL_SCESE, null);
//        ArrayList<String> scenes = new ArrayList<String>();
//        if(scenesJson != null) {
//            scenes = new Gson().fromJson(scenesJson, scenes.getClass());
//        }
//
//        for(String s : scenes) {
//            Scene ss = new Gson().fromJson(s, Scene.class);
//            if(ss.getId() == id) {
//                scenes.remove(s);
//                break;
//            }
//        }
//
//        // update data
//        String newScenesJson = new Gson().toJson(scenes);
//        final SharedPreferences.Editor editor = sp.edit();
//        editor.putString(KEY_ALL_SCESE, newScenesJson);
//        editor.commit();

        deleteObject(id, DATA_SCENE);

        LALALAService.currentInstance.updateScenes();
    }

    public void deleteTrigger(int id) {
//        String scenesJson = sp.getString(KEY_ALL_TRIGGER, null);
//        ArrayList<String> triggers = new ArrayList<String>();
//        if(scenesJson != null) {
//            triggers = new Gson().fromJson(scenesJson, triggers.getClass());
//        }
//
//        for(String s : triggers) {
//            SceneTriggerData data = new Gson().fromJson(s, SceneTriggerData.class);
//            if(data.getId() == id) {
//                triggers.remove(s);
//                break;
//            }
//        }
//        // update data
//        String newTriggersJson = new Gson().toJson(triggers);
//        final SharedPreferences.Editor editor = sp.edit();
//        editor.putString(KEY_ALL_TRIGGER, newTriggersJson);
//        editor.commit();
        deleteObject(id, DATA_TRIGGER);
        LALALAService.currentInstance.updateTriggers();
    }

    public void saveException(ExceptionScene exp) {
//        String exceptionsJson = sp.getString(KEY_ALL_EXCEPTION, null);
//        ArrayList<String> exceptions = new ArrayList<String>();
//        if(exceptionsJson != null) {
//            exceptions = new Gson().fromJson(exceptionsJson, exceptions.getClass());
//        }
//
//        int id = -1;
//        for(String s : exceptions) {
//            ExceptionScene es = new Gson().fromJson(s, ExceptionScene.class);
//            if(es.getId() > id) {
//                id = es.getId();
//            }
//        }
//        exp.setId(id + 1);
//
//        String newExp = new Gson().toJson(exp);
//        exceptions.add(newExp);
//        String newExceptionsJson = new Gson().toJson(exceptions);
//        sp.edit().putString(KEY_ALL_EXCEPTION, newExceptionsJson).commit();
        saveObject(exp, DATA_EXCEPTION);
    }

    public void deleteException(int id) {
//        String exceptionsJson = sp.getString(KEY_ALL_EXCEPTION, null);
//        ArrayList<String> exceptions = new ArrayList<String>();
//        if(exceptionsJson != null) {
//            exceptions = new Gson().fromJson(exceptionsJson, exceptions.getClass());
//        }
//
//        for(String s : exceptions) {
//            ExceptionScene es = new Gson().fromJson(s, ExceptionScene.class);
//            if(es.getId() == id) {
//                exceptions.remove(s);
//                break;
//            }
//        }
//
//        String newExceptionsJson = new Gson().toJson(exceptions);
//        sp.edit().putString(KEY_ALL_EXCEPTION, newExceptionsJson).commit();
//        if(LALALAService.currentInstance != null)
//            LALALAService.currentInstance.updateExceptions();

        deleteObject(id, DATA_EXCEPTION);
    }

    public List<ExceptionScene> getAllException() {
//        String exceptionsJson = sp.getString(KEY_ALL_EXCEPTION, null);
//        ArrayList<String> exceptions = new ArrayList<String>();
//        if(exceptionsJson != null) {
//            exceptions = new Gson().fromJson(exceptionsJson, exceptions.getClass());
//        }
//
//        ArrayList<ExceptionScene> allExps = new ArrayList<ExceptionScene>();
//        Gson gson = new Gson();
//        for(String s : exceptions) {
//            allExps.add(gson.fromJson(s, ExceptionScene.class));
//        }
//        return allExps;
        Map map = (TreeMap<Integer, ExceptionScene>) getAllObject(DATA_EXCEPTION);
        return new ArrayList<ExceptionScene>(map.values());
    }
}
