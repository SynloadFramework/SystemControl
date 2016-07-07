package tech.synframe.systemcontrol.settings.defaultSettings;

import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.settings.ProjectSetting;
import tech.synframe.systemcontrol.settings.Setting;
import tech.synframe.systemcontrol.settings.SettingData;

import java.util.List;

/**
 * Created by Nathaniel on 7/6/2016.
 */
public class JavaArgs {
    @ProjectSetting(type="text",name="javaArgs",method= Setting.GET,label="Java Arguments",placeholder="-Xmx1G")
    public String getJavaArgs(SettingData data){
        try {
            List<Project> projects = Project._find(Project.class, "id=?", Long.valueOf(data.getProjectId())).exec(Project.class);
            if(projects.size()>0 && data.getSettingName().equalsIgnoreCase("javaArgs")){
                Project p = projects.get(0);
                return p.getJava_arguments();
            }
        }catch(Exception e){}
        return "";
    }
    @ProjectSetting(type="text",name="javaArgs",method= Setting.SET,label="Java Arguments",placeholder="-Xmx1G")
    public boolean setJavaArgs(SettingData data){
        try {
            List<Project> projects = Project._find(Project.class, "id=?", Long.valueOf(data.getProjectId())).exec(Project.class);
            if(projects.size()>0 && data.getSettingName().equalsIgnoreCase("javaArgs")){
                Project p = projects.get(0);
                p._save("java_arguments", data.getValue());
                return true;
            }
        }catch(Exception e){}
        return false;
    }
}
