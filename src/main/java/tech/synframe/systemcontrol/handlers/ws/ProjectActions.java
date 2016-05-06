package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.Log;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.GetPendingActions;
import tech.synframe.systemcontrol.elements.user.LoginForm;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class ProjectActions {
    @WSEvent(method = "create", action = "project", description = "Create project under the user logged in", enabled = true, name = "CreateNewProject")
    public void createProject(RequestEvent e){
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
    @WSEvent(method = "json", action = "project", description = "get project by id", enabled = true, name = "GetProjectData")
    public void getProject(RequestEvent e){
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
                    e.getSession().send(
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
}
