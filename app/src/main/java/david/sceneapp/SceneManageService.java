package david.sceneapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import david.sceneapp.Model.ExceptionScene;
import david.sceneapp.Model.NotificationSceneTrigger;
import david.sceneapp.Model.Scene;
import david.sceneapp.Model.SceneTrigger;
import david.sceneapp.Model.SceneTriggerData;
import david.sceneapp.Model.WifiSceneTrigger;


//TODO http://stackoverflow.com/questions/6896746/android-is-there-a-broadcast-action-for-volume-changes
public class SceneManageService extends NotificationListenerService {
    private String TAG = this.getClass().getSimpleName();
    public static SceneManageService currentInstance;

    public static final String KEY_NOTIF_PKG_NAME           = "notification_packagename";
    public static final String ACTION_GET_NOTIFICATION      = "david.notification";
    public static final String ACTION_CLICK_NOTIFICATION    = "com.gaowei.notif";
    public static final String ACTION_SCENE_IMPLEMENTED     = "com.david.sceneImplemented";
    public static final String ACTION_SCENES_UPDATED        = "com.david.sceneUpdated";
    public static final String ACTION_TRIGGERS_UPDATED      = "com.david.triggerUpdated";
    public static final String ACTION_EXCEPTIONS_UPDATED    = "com.david.exceptionsUpdated";
    public static final String EXTRA_SCENE_INDEX            = "scene_index";
    public static final String EXTRA_FROM_TRIGGER           = "from_trigger";
    public static final String EXTRA_SCENE_ID               = "com.david.extraSceneId";

    private static final int SCENE_CAPACITY     = 4;
    private static final int NOTIFICATION_ID    = 100;

    public static Scene currentScene = null;

    private RemoteViews remoteViews;
    private NotificationManager mNotificationManager;
    private AudioManager audioManager;
    private ArrayList<Scene> scenes = new ArrayList<Scene>();
    private Map<Integer, Scene> sceneMap = new LinkedHashMap<Integer, Scene>();
    private Map<Integer, ExceptionScene> exceptions = new HashMap<Integer, ExceptionScene>();
    private Map<Integer, SceneTrigger> triggers = new LinkedHashMap<Integer, SceneTrigger>();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_CLICK_NOTIFICATION)) {
                int index = intent.getIntExtra(EXTRA_SCENE_INDEX, -1);
                if (index > -1 && index < SCENE_CAPACITY) {
                    if (!intent.getBooleanExtra(EXTRA_FROM_TRIGGER, false))
                        SceneManageService.this.implementScene(scenes.get(index),false);
                }
            } else if (action.equals(ACTION_SCENE_IMPLEMENTED) ||
                    action.equals(ACTION_SCENES_UPDATED)) {
                addRemoteView();
            }
        }
    };
    private SceneStorageManager mSceneStorageManager;

    public Map<Integer, Scene> getSceneMap() {
        return sceneMap;
    }

    public ArrayList<Scene> getScenes() {
        return scenes;
    }

    public Collection<ExceptionScene> getExceptions() {
        return exceptions.values();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentInstance = this;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        updateScenes();
        updateTriggers();
        updateExceptions();

        remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
        addRemoteView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CLICK_NOTIFICATION);
        filter.addAction(ACTION_SCENE_IMPLEMENTED);
        filter.addAction(ACTION_SCENES_UPDATED);
        registerReceiver(receiver, filter);
        for(Scene s : scenes) {
            if(s.isCurrentSystemSeeting(audioManager)) {
                Log.d(TAG, "find current Scene during initialization: " + s.getName());
                currentScene = s;
                sendBroadcast(new Intent(ACTION_SCENES_UPDATED));
                break;
            }
        }
        mSceneStorageManager = new SceneStorageManager(this);
    }

    void updateTriggers() {
        SceneStorageManager ssm = new SceneStorageManager(this);
        Map<Integer, SceneTriggerData> triggerDatas = ssm.getAllTrigger();
        if(triggerDatas == null) return;
        for (SceneTriggerData st : triggerDatas.values()) {
            switch (st.getTriggerType()) {
                case SceneTriggerData.TYPE_WIFI_SWITCH:
                    triggers.put(st.getId(), new WifiSceneTrigger(sceneMap.get(st.getSceneId()),
                            this, st.getParameters()[0]));
                    break;
                case SceneTriggerData.TYPE_NOTIFICATION:
                    Set<String> pkgs = new HashSet<String>();
                    pkgs.add(st.getParameters()[0]);
                    triggers.put(st.getId(), new NotificationSceneTrigger(this,
                            sceneMap.get(st.getSceneId()), pkgs));
                    break;
            }
        }
        sendBroadcast(new Intent(ACTION_TRIGGERS_UPDATED));

    }

    public void updateScenes() {
        SceneStorageManager ssm = new SceneStorageManager(this);
        sceneMap = ssm.getAllScene();
        scenes.clear();
        if(sceneMap != null)
        scenes.addAll(sceneMap.values());

        sendBroadcast(new Intent(ACTION_SCENES_UPDATED));
    }



    public void deleteScene(int id) {
        SceneStorageManager ssm = new SceneStorageManager(this);
        ssm.deleteScene(id);
    }

    public void updateExceptions() {
        SceneStorageManager ssm = new SceneStorageManager(this);
        exceptions = (ssm.getAllException());
        sendBroadcast(new Intent(ACTION_EXCEPTIONS_UPDATED));
    }

    public void deleteException(int id) {
        SceneStorageManager ssm = new SceneStorageManager(this);
        ssm.deleteException(id);
    }


    private void addRemoteView() {
        Log.d(TAG, "update remote view");
        if (scenes.isEmpty()) return;
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int amount = Math.min(SCENE_CAPACITY, scenes.size());
        int width = size.x / amount;
        for (int i = 0; i < amount; i++) {
            if (i < scenes.size()) {
                Scene s = scenes.get(i);
                int buttonId = getIdBySceneIndex(i);
                remoteViews.setCharSequence(buttonId, "setText", s.getName());
                remoteViews.setInt(buttonId, "setWidth", width);
                if (currentScene != null && s.getId() == (currentScene.getId())) {
                    remoteViews.setInt(buttonId, "setBackgroundResource",
                            android.R.color.holo_red_dark);
                } else {
                    remoteViews.setInt(buttonId, "setBackgroundResource",
                            android.R.color.holo_blue_bright);
                }
                Intent resultIntent = new Intent();
                resultIntent.setAction(ACTION_CLICK_NOTIFICATION);
                resultIntent.putExtra(EXTRA_SCENE_INDEX, i);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, s.getId(),
                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(buttonId, pendingIntent);
            } else {
                int buttonId = getIdBySceneIndex(i);
                remoteViews.setCharSequence(buttonId, "setText", "    ");
                remoteViews.setInt(buttonId, "setBackgroundResource",
                        android.R.color.holo_blue_bright);
            }

        }
        Notification.Builder mBuilder = new Notification.Builder(
                this).setSmallIcon(R.drawable.ic_launcher).setContent(
                remoteViews).setOngoing(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private int getIdBySceneIndex(int index) {
        switch (index) {
            case 0:
                return R.id.sceneButton1;
            case 1:
                return R.id.sceneButton2;
            case 2:
                return R.id.sceneButton3;
            case 3:
                return R.id.sceneButton4;
            default:
                return 0;
        }
    }

    public void toggleTrigger(int triggerId, boolean activate) {
        if (activate)
            triggers.get(triggerId).activate();
        else
            triggers.get(triggerId).deactivate();
    }

    public void toggleAllWifiSwitch(boolean activate) {
        SceneStorageManager.setWifiEnabled(this, activate);
        for (SceneTrigger st : triggers.values()) {
            if (st instanceof WifiSceneTrigger) {
                if (activate) st.activate();
                else st.deactivate();
            }
        }

    }


    public void implementScene(Scene scene, boolean silent) {
        Log.d(TAG, scene.getName() + " is implemented");
        AudioManager am = audioManager;
        if (am == null) {
            Log.e(TAG, "sound manager is null");
            return;
        }

        int alarm = 0;
        int music = 1;
        int sys = 2;

        int max = scene.getAlarmVolume();
        int maxOne = alarm;
        if (scene.getMusicVolume() > max) {
            max = scene.getMusicVolume();
            maxOne = music;
        }
        if (scene.getSystemVolume() > max) {
            max = scene.getSystemVolume();
            maxOne = sys;
        }

        int theFlag = AudioManager.FLAG_PLAY_SOUND;
        if (scene.getSystemVolume() + scene.getMusicVolume() + scene.getSystemVolume() == 0)
            theFlag = AudioManager.FLAG_VIBRATE;

        if (scene.getAlarmVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_ALARM, scene.getAlarmVolume(),
                    maxOne == alarm ? theFlag : AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (scene.getMusicVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, scene.getMusicVolume(),
                    maxOne == music ? theFlag : AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (scene.getSystemVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, scene.getSystemVolume(),
                    maxOne == sys ? theFlag : AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (scene.getVoiceCallVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, scene.getVoiceCallVolume(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (scene.getRingerMode() != -1) am.setRingerMode(scene.getRingerMode());

        currentScene = scene;

        if (!silent) {
            Intent intent = new Intent(ACTION_SCENE_IMPLEMENTED);
            intent.putExtra(EXTRA_SCENE_ID, scene.getId());
            sendBroadcast(intent);
        }
//        Toast.makeText(SceneManageService.currentInstance,
//                scene.getName() + " mode on", Toast.LENGTH_SHORT).show();

    }

    public void implementScene(int sceneId) {
        for (Scene s : scenes) {
            if (sceneId == s.getId()) {
                implementScene(s, false);
                break;
            }
        }
    }

    public void implementException(ExceptionScene exp) {
        Ringtone r = null;
        if(exp.getVolume() > 0) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,exp.getVolume(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            r = RingtoneManager.getRingtone(this, notification);
            r.setStreamType(AudioManager.STREAM_NOTIFICATION);
            r.play();


        }
        if(exp.isVibrate()) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
        }
        if(r == null)
            implementScene(currentScene,true);
        else {
            new AsyncTask<Ringtone, Void, Void>() {

                @Override
                protected Void doInBackground(Ringtone... params) {
                    int i = 0;
                    Ringtone rr = params[0];
                    if(rr == null) return null;
                    while(i < 5) {
                        if(!rr.isPlaying()) {
                            break;
                        } else {
                            i++;
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    implementScene(currentScene, true);
                    return null;
                }
            }.execute(r);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Scene getDemoSilentMode() {
        Scene ss = new Scene();
        ss.setSystemVolume(0);
        ss.setName("silent");
        ss.setId(1);
        ss.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return ss;
    }

    Scene getDemoSoundMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        ss.setName("sound");
        ss.setId(2);
        ss.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        return ss;
    }

    Scene getDemoMiddleMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) / 2);
        ss.setName("mid");
        ss.setId(3);
        return ss;
    }

    Scene getDemoSilentMode2() {
        Scene ss = new Scene();
        ss.setSystemVolume(0);
        ss.setName("silent");
        ss.setId(4);
        return ss;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + "\t" +
                sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new Intent(ACTION_GET_NOTIFICATION);
        i.putExtra(KEY_NOTIF_PKG_NAME, sbn.getPackageName());
        sendBroadcast(i);

        String pkg = sbn.getPackageName();
        String message = sbn.getNotification().tickerText.toString();
        for(ExceptionScene es : exceptions.values()) {
            if(es.getPkgName().equals(pkg)) {
                Log.d(TAG, "notificaiton packagename is one of exception");
                if (!es.isActivated()) {
                    Log.d(TAG, "exception isn't activated");
                    return;
                }
                if (es.getParams().size() == 0) {
                    Log.d(TAG, "exception is a wild cast, implent");
                    implementException(es);
                    return;
                } else {
                    for (String s : es.getParams()) {
                        if (message.contains(s)) {
                            Log.d(TAG, "exception implemented, parameter matches");
                            implementException(es);
                            return;
                        }
                    }
                    Log.d(TAG, "exception is not implemented due to parameter not match");
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "\t" +
                sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

    }

    public void deleteTrigger(int id) {
        SceneStorageManager ssm = new SceneStorageManager(this);
        ssm.deleteTrigger(id);
    }

    public void toggleExceptionScene(int id, boolean isActivated) {
        exceptions.get(id).setActivated(isActivated);
        mSceneStorageManager.saveException(exceptions.get(id));
    }
}
