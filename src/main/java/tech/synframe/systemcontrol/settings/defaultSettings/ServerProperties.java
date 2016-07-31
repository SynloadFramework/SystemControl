package tech.synframe.systemcontrol.settings.defaultSettings;

import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.settings.ProjectSetting;
import tech.synframe.systemcontrol.settings.Setting;
import tech.synframe.systemcontrol.settings.SettingData;

import java.util.List;

/**
 * Created by Nathaniel on 7/31/2016.
 */
public class ServerProperties {
    @ProjectSetting(type="text",name="serverPort",method= Setting.GET,label="Server Port",placeholder="2009")
    public String getServerPorts(SettingData data){
        try {
            List<Project> projects = Project._find(Project.class, "id=?", Long.valueOf(data.getProjectId())).exec(Project.class);
            if(projects.size()>0 && data.getSettingName().equalsIgnoreCase("serverPort")){
                Project p = projects.get(0);
                return String.valueOf(p.getPort());
            }
        }catch(Exception e){}
        return "";
    }
    @ProjectSetting(type="text",name="serverPort",method= Setting.SET,label="Server Port",placeholder="2009")
    public boolean setServerPorts(SettingData data){
        try {
            List<Project> projects = Project._find(Project.class, "id=?", Long.valueOf(data.getProjectId())).exec(Project.class);
            if(projects.size()>0 && data.getSettingName().equalsIgnoreCase("serverPort")){
                Project p = projects.get(0);
                p._save("port", data.getValue());
                return true;
            }
        }catch(Exception e){}
        return false;
    }
    @ProjectSetting(type="text",name="serverPath",method= Setting.GET,label="Server Path",placeholder="/path/")
    public String getServerPath(SettingData data){
        try {
            List<Project> projects = Project._find(Project.class, "id=?", Long.valueOf(data.getProjectId())).exec(Project.class);
            if(projects.size()>0 && data.getSettingName().equalsIgnoreCase("serverPort")){
                Project p = projects.get(0);
                return p.getPath();
            }
        }catch(Exception e){}
        return "";
    }
    @ProjectSetting(type="text",name="serverPath",method= Setting.SET,label="Server Path",placeholder="/path/")
    public boolean setServerPath(SettingData data){
        try {
            List<Project> projects = Project._find(Project.class, "id=?", Long.valueOf(data.getProjectId())).exec(Project.class);
            if(projects.size()>0 && data.getSettingName().equalsIgnoreCase("serverPath")){
                Project p = projects.get(0);
                p._save("path", data.getValue());
                return true;
            }
        }catch(Exception e){}
        return false;
    }
}
