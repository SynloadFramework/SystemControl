package tech.synframe.systemcontrol.settings;

/**
 * Created by Nathaniel on 7/6/2016.
 */
public class SettingData {
    public String value = "";
    public String settingName = "";
    public long projectId = 0;
    public SettingData(Long projectId, String setting, String value){
        this.value = value;
        this.settingName = setting;
        this.projectId = projectId;
    }
    public SettingData(Long projectId, String setting){
        this.settingName = setting;
        this.projectId = projectId;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getSettingName() {
        return settingName;
    }
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }
    public long getProjectId() {
        return projectId;
    }
    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
