package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.Log;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.project.ProjectPage;
import tech.synframe.systemcontrol.elements.project.ProjectSettingsPage;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.settings.SettingRegistry;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nathaniel on 7/6/2016.
 */
public class ProjectSettings {
    @WSEvent(method = "get", action = "projectSettings", description = "Get project settings", enabled = true, name = "getProjectSettings")
    public void settingsPage(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user") && e.getRequest().getData().containsKey("project")){
            int projectId = Integer.valueOf(e.getRequest().getData().get("project"));
            User user = (User) e.getSession().getSessionData().get("user");
            try {
                List<Project> projects = Project._find(Project.class, "user=? and id=?", user.getId(), projectId).exec(Project.class);
                if(projects.size()==1){
                    e.respond(
                        new ProjectSettingsPage(
                            e.getSession(),
                            e.getRequest().getTemplateCache(),
                            projects.get(0),
                            user
                        )
                    );
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    @WSEvent(method = "save", action = "projectSettings", description = "Save project settings", enabled = true, name = "saveProjectSettings")
    public void settingsSave(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user") && e.getRequest().getData().containsKey("project")){
            int projectId = Integer.valueOf(e.getRequest().getData().get("project"));
            String[] settings = e.getRequest().getData().get("_settings_").split(",");
            User user = (User) e.getSession().getSessionData().get("user");
            try {
                List<Project> projects = Project._find(Project.class, "user=? and id=?", user.getId(), projectId).exec(Project.class);
                if (projects.size() == 1) {
                    for(int i=0;i<settings.length;i++) {
                        SettingRegistry.setValue(projects.get(0), settings[i], e.getRequest().getData().get(settings[i]));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
