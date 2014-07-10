package david.sceneapp;

/**
 * Created by David on 7/9/2014.
 */
public class SceneTriggerData {

    static final int TYPE_WIFI_SWITCH = 1;
    static final int TYPE_NOTIFICATION = 2;

    private int id;

    private int sceneId;
    private int triggerType;
    private String name;
    private String[] parameters;

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
