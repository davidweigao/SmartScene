package david.sceneapp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 7/16/14.
 */
public class ExceptionScene implements SceneAppData, Serializable {
    private int id = -1;
    private int volume;
    private boolean vibrate;
    private String pkgName;
    private String name;
    private int iconRes;
    private List<String> params;
    private boolean activated;

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public ExceptionScene(int vol, boolean vib, String pkg, String name, int iconRes, String... param) {
        volume = vol;
        vibrate = vib;
        pkgName = pkg;
        this.name = name;
        this.iconRes = iconRes;
        params = new ArrayList<String>(param.length);
        for(String s : param) {
            params.add(s);
        }
    }


    @Override
    public String toString() {
        return pkgName;
    }


    public boolean isActivated() {
        return activated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
