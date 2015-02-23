package david.sceneapp.Model;

import java.io.Serializable;

import david.sceneapp.SceneManageService;

/**
 * Created by David on 7/9/2014.
 */
public class SceneTriggerData implements SceneAppData, Serializable {

    public static final int TYPE_WIFI_SWITCH = 1;
    public static final int TYPE_NOTIFICATION = 2;

    private int id;

    private int sceneId;
    private int triggerType;
    private String name;
    private String[] parameters;

    @Override
    public String toString() {
        String sceneName = "none";
        if(SceneManageService.currentInstance != null) {
            Scene s =  SceneManageService.currentInstance.getSceneMap().get(sceneId);
            if(s == null) return "broken";
            sceneName =s.getName();
        }

        switch (triggerType) {
            case TYPE_WIFI_SWITCH:
                return "wifi trigger: " + parameters[0] + "--->" + sceneName;
            case TYPE_NOTIFICATION:
                return "notification: " + parameters[0] + "--->" + sceneName;
            default:return super.toString();
        }

    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
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
}
