package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.Log;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.module.AddModulePage;
import tech.synframe.systemcontrol.elements.module.InfoModulePage;
import tech.synframe.systemcontrol.elements.project.CreateProjectPage;
import tech.synframe.systemcontrol.elements.project.ProjectDataPage;
import tech.synframe.systemcontrol.models.Modules;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.utils.NewVersionCheck;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nathaniel on 5/28/2016.
 */
public class ModuleElements {

    @WSEvent(name="AddModule", description="", enabled = true, method = "get", action = "addmodule")
    public void addModule(RequestEvent e){
        try {
            if (e.getSession().getSessionData().containsKey("user")
                && e.getRequest().getData().containsKey("project")
            ){
                User user = (User) e.getSession().getSessionData().get("user");
                Project project = Project._find(Project.class, "id=? and user=?", e.getRequest().getData().get("project"), user.getId()).exec(Project.class).get(0);
                e.respond(
                    new AddModulePage(
                        e.getSession(),
                        e.getRequest().getTemplateCache(),
                        user,
                        project
                    )
                );
            }
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

    @WSEvent(method = "save", action = "addmodule", description = "add module to project", enabled = true, name = "StopProject")
    public void stop(RequestEvent e){
        HashMap<String, Object> objects = new HashMap<String, Object>();
        if(
            e.getSession().getSessionData().containsKey("user")
            && e.getRequest().getData().containsKey("project")
            && e.getRequest().getData().containsKey("jenkinsurl")
        ){
            User u = (User) e.getSession().getSessionData().get("user");
            int id = Integer.valueOf(e.getRequest().getData().get("project"));
            String jenkinsUrl = e.getRequest().getData().get("jenkinsurl");
            try{
                final List<Project> project = Project._find(Project.class, "id=? and user=?", id, u.getId()).exec(Project.class);
                if(project.size()>0) {
                    Project proj = project.get(0);
                    if(NewVersionCheck.downloadModuleAndInstall(proj, jenkinsUrl)) {
                        proj.stop();
                        proj.start();
                        objects.put("status", "success");
                        objects.put("project", project.get(0));
                    }else{
                        objects.put("status", "error");
                        objects.put("error", "failed");
                    }
                }else{
                    objects.put("status", "error");
                    objects.put("error", "notexist");
                }
            }catch (Exception err){
                objects.put("status", "error");
                objects.put("error", "sqlerror");
            }
        }else{
            Log.info("Not logged in", ProjectActions.class);
            objects.put("status", "error");
            objects.put("error", "notloggedin");
        }
        e.respond(
            new Data(
                objects,
                "addModule"
            )
        );
    }

    @WSEvent(name="InfoModule", description="", enabled = true, method = "get", action = "infomodule")
    public void infoModule(RequestEvent e){
        try {
            if (e.getSession().getSessionData().containsKey("user")
                && e.getRequest().getData().containsKey("project")
                && e.getRequest().getData().containsKey("name")
                && e.getRequest().getData().containsKey("build")
            ){
                User user = (User) e.getSession().getSessionData().get("user");
                long projectId = Long.valueOf(e.getRequest().getData().get("project"));
                String name = e.getRequest().getData().get("name");
                int build = Integer.valueOf(e.getRequest().getData().get("build"));
                Project project = Project._find(Project.class, "id=? and user=?", projectId, user.getId()).exec(Project.class).get(0);
                Modules module = Modules._find(Modules.class, "project=? and name=? and build=?", projectId, name, build).exec(Modules.class).get(0);
                e.respond(
                    new InfoModulePage(
                        e.getSession(),
                        e.getRequest().getTemplateCache(),
                        user,
                        project,
                        module
                    )
                );
            }
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

}
