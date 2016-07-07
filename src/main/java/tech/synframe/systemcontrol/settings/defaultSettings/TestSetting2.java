package tech.synframe.systemcontrol.settings.defaultSettings;

import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.settings.ProjectSetting;
import tech.synframe.systemcontrol.settings.Setting;
import tech.synframe.systemcontrol.settings.SettingData;

import java.util.List;

/**
 * Created by Nathaniel on 7/6/2016.
 */
public class TestSetting2 {
    @ProjectSetting(type="text",name="testSet2",method= Setting.GET,label="Test Setting",placeholder="test")
    public String getJavaArgs(SettingData data){
        try {
            return "test";
        }catch(Exception e){}
        return "";
    }
    @ProjectSetting(type="text",name="testSet2",method= Setting.SET,label="Test Setting",placeholder="test")
    public boolean setJavaArgs(SettingData data){
        try {
            return true;
        }catch(Exception e){}
        return false;
    }
}
