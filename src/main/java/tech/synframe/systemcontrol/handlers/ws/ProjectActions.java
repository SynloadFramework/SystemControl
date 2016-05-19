package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.Log;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.utils.ExecuteShellSynFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class ProjectActions {
    @WSEvent(method = "create", action = "project", description = "Create project under the user logged in", enabled = true, name = "CreateNewProject")
    public void create(RequestEvent e){
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("name") &&
            e.getRequest().getData().containsKey("path") &&
            e.getRequest().getData().containsKey("port")
        ){
            String name = e.getRequest().getData().get("name");
            String path = e.getRequest().getData().get("path");
            int port = Integer.valueOf(e.getRequest().getData().get("port"));
            User u = (User) e.getSession().getSessionData().get("user");
            Project p = new Project();
            p.setName(name);
            p.setPath(path);
            p.setPort(port);
            try{
                p._insert();
                p._set(u); // sets relation between project and user
            }catch (Exception err){
            }
        }else{
            Log.info("Not logged in", ProjectActions.class);
        }
    }
    @WSEvent(method = "get", action = "project", description = "get project by id", enabled = true, name = "GetProjectData")
    public void get(RequestEvent e){
        if(
                e.getSession().getSessionData().containsKey("user") &&
                e.getRequest().getData().containsKey("id") &&
                e.getRequest().getData().containsKey("trigger")
        ){
            User u = (User) e.getSession().getSessionData().get("user");
            int id = Integer.valueOf(e.getRequest().getData().get("id"));
            String trigger = e.getRequest().getData().get("trigger");
            try{
                final List<Project> project = Project._find(Project.class, "id=? and user=?", id, u.getId()).exec(Project.class);
                if(project.size()>0) {
                    e.respond(
                        new Data(
                            new HashMap<String, Object>() {{
                                put("project", project.get(0));
                            }},
                            trigger
                        )
                    );
                }
            }catch (Exception err){
            }
        }else{
            Log.info("Not logged in", ProjectActions.class);
        }
    }
    @WSEvent(method = "delete", action = "project", description = "Delete a project by id", enabled = true, name = "DeleteProject")
    public void delete(RequestEvent e){
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("id")
        ){
            User u = (User) e.getSession().getSessionData().get("user");
            int id = Integer.valueOf(e.getRequest().getData().get("id"));
            try{
                final List<Project> project = Project._find(Project.class, "id=? and user=?", id, u.getId()).exec(Project.class);
                if(project.size()>0) {
                    u._unset(project.get(0));
                    project.get(0)._delete();
                }
            }catch (Exception err){
            }
        }else{
            Log.info("Not logged in", ProjectActions.class);
        }
    }
    @WSEvent(method = "status", action = "project", description = "Show project running status", enabled = true, name = "StatusProject")
    public void status(RequestEvent e){
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("id")
        ){
            User u = (User) e.getSession().getSessionData().get("user");
            int id = Integer.valueOf(e.getRequest().getData().get("id"));
            try{
                HashMap<String, Object> objects = new HashMap<String, Object>();
                final List<Project> project = Project._find(Project.class, "id=? and user=?", id, u.getId()).exec(Project.class);
                if(project.size()>0) {
                    if(ExecuteShellSynFrame.instances.containsKey(project.get(0).getId())){
                        ExecuteShellSynFrame instance = ExecuteShellSynFrame.instances.get(project.get(0).getId());
                        if(instance.isRunning()){
                            objects.put("status", true);
                            objects.put("project", project.get(0));
                        }else{
                            objects.put("status", false);
                            objects.put("project", project.get(0));
                        }
                    }else{
                        objects.put("status", false);
                        objects.put("project", project.get(0));
                    }
                }else{
                    objects.put("status", false);
                    objects.put("project", false);
                }
                e.respond(
                    new Data(
                        objects,
                        "projectStatus"
                    )
                );
            }catch (Exception err){
            }
        }else{
            Log.info("Not logged in", ProjectActions.class);
        }
    }

}
