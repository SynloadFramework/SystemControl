package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.eventsystem.events.STMessageReceivedEvent;
import com.synload.eventsystem.events.annotations.Event;
import com.synload.framework.Log;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import com.synload.talksystem.statistics.StatisticDocument;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.utils.ActionEnum;
import tech.synframe.systemcontrol.utils.ExecuteShellSynFrame;
import tech.synframe.systemcontrol.utils.Queue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class ProjectActions {
    @WSEvent(method = "create", action = "project", description = "Create instance", enabled = true, name = "CreateNewInstance")
    public void create(RequestEvent e){
        HashMap<String, Object> objects = new HashMap<String, Object>();
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
            if(!Project._exists(Project.class,"port=?", port)) {
                Project p = new Project();
                p.setName(name);
                p.setPath(path);
                p.setPort(port);
                try {
                    p._insert();
                    p._set(u); // sets relation between project and user
                    objects.put("status", "success");
                    objects.put("project", p);
                } catch (Exception err) {
                    objects.put("status", "error");
                    objects.put("error", "sqlerror");
                }
            }else{
                objects.put("status", "error");
                objects.put("error", "exists");
            }
        }else{
            Log.info("Not logged in", ProjectActions.class);
            objects.put("status", "error");
            objects.put("error", "notloggedin");
        }
        e.respond(
            new Data(
                objects,
                "projectStatus"
            )
        );
    }

    @WSEvent(method = "copy", action = "project", description = "Copy existing instance", enabled = true, name = "CopyExistingInstance")
    public void copy(RequestEvent e){
        HashMap<String, Object> objects = new HashMap<String, Object>();
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("instance") &&
            e.getRequest().getData().containsKey("port")
        ){
            long instance = Long.valueOf(e.getRequest().getData().get("instance"));
            int port = Integer.valueOf(e.getRequest().getData().get("port"));
            User u = (User) e.getSession().getSessionData().get("user");
            try {
                List<Project> projects = Project._find(Project.class, "id=?", instance).exec(Project.class);
                if(projects.size()==1){
                    Project proj = projects.get(0);
                    proj.setId(0);
                    proj.setPort(port);
                    if(!Project._exists(Project.class,"port=?", port)) {
                        proj._insert();
                        proj._set(u); // sets relation between project and user
                        objects.put("status", "success");
                        objects.put("project", proj);
                    }else{
                        objects.put("status", "error");
                        objects.put("error", "exists");
                    }
                }else{
                    objects.put("status", "error");
                    objects.put("error", "notexist");
                }
            }catch (Exception x){
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
                        "projectStatus"
                )
        );
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
    @WSEvent(method = "start", action = "project", description = "Start a project by id", enabled = true, name = "StartProject")
    public void start(RequestEvent e){
        HashMap<String, Object> objects = new HashMap<String, Object>();
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("id")
        ){
            User u = (User) e.getSession().getSessionData().get("user");
            int id = Integer.valueOf(e.getRequest().getData().get("id"));
            try{
                final List<Project> project = Project._find(Project.class, "id=? and user=?", id, u.getId()).exec(Project.class);
                if(project.size()>0) {
                    PendingAction pa = new PendingAction();
                    pa.setProject(project.get(0).getId());
                    pa.setAction("start");
                    pa._insert();
                    Queue.add(pa);
                    objects.put("status", "success");
                    project.get(0).checkStatus();
                    objects.put("project", project.get(0));
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
                "startProject"
            )
        );
    }
    @WSEvent(method = "stop", action = "project", description = "Stop a project by id", enabled = true, name = "StopProject")
    public void stop(RequestEvent e){
        HashMap<String, Object> objects = new HashMap<String, Object>();
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("id")
        ){
            User u = (User) e.getSession().getSessionData().get("user");
            int id = Integer.valueOf(e.getRequest().getData().get("id"));
            try{
                final List<Project> project = Project._find(Project.class, "id=? and user=?", id, u.getId()).exec(Project.class);
                if(project.size()>0) {
                    PendingAction pa = new PendingAction();
                    pa.setAction("stop");
                    pa.setProject(project.get(0).getId());
                    pa._insert();
                    Queue.add(pa);
                    objects.put("status", "success");
                    project.get(0).checkStatus();
                    objects.put("project", project.get(0));
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
                "stopProject"
            )
        );
    }

    @Event(name="ProjectStats", description = "Project statistics received over the server talk line", enabled = true)
    public void projectStatistics(STMessageReceivedEvent e){
        if(StatisticDocument.class.isInstance(e.getData())){
            // stat doc received \o/
            Log.info("Got some", null);
            StatisticDocument sd = (StatisticDocument) e.getData();
            int projectId = Integer.valueOf(sd.getIdentifier());
            Map<String, Object> statistics = new HashMap<String, Object>();
            statistics.put("free", sd.getFree());
            statistics.put("total", sd.getTotal());
            statistics.put("max", sd.getMax());
            statistics.put("clients", sd.getClients());
            Project.projectStatistics.put(projectId,statistics);
        }
    }
}
