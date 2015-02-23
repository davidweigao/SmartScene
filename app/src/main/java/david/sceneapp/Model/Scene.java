package david.sceneapp.Model;

import android.media.AudioManager;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by david on 7/1/14.
 */
public class Scene implements SceneAppData, Serializable{
    public static final String TAG = Scene.class.getSimpleName();
    private int id;
    private String name;

    private int alarmVolume = -1;
    private int musicVolume = -1;
    private int voiceCallVolume = -1;
    private int systemVolume = -1;
    private int ringerMode = -1;

    @Override
    public String toString() {
        return name;
    }

    public void setSystemVolume(int systemVolume) {
        this.systemVolume = systemVolume;
    }

    public void setAlarmVolume(int alarmVolume) {
        this.alarmVolume = alarmVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    public void setVoiceCallVolume(int voiceCallVolume) {
        this.voiceCallVolume = voiceCallVolume;
    }

    public int getAlarmVolume() {
        return alarmVolume;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public int getVoiceCallVolume() {
        return voiceCallVolume;
    }

    public int getSystemVolume() {
        return systemVolume;
    }

    public int getRingerMode() {
        return ringerMode;
    }

    public void setRingerMode(int ringerMode) {
        this.ringerMode = ringerMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrentSystemSeeting(AudioManager am) {
        return alarmVolume == am.getStreamVolume(AudioManager.STREAM_ALARM)
                && musicVolume == am.getStreamVolume(AudioManager.STREAM_MUSIC)
                && systemVolume == am.getStreamVolume(AudioManager.STREAM_SYSTEM)
                && voiceCallVolume == am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
                && ringerMode == am.getRingerMode();
    }

}
