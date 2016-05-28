package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.eventsystem.events.STMessageReceivedEvent;
import com.synload.eventsystem.events.annotations.Event;
import com.synload.framework.Log;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import com.synload.talksystem.statistics.StatisticDocument;
import tech.synframe.systemcontrol.models.Modules;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.utils.ActionEnum;
import tech.synframe.systemcontrol.utils.ConsoleLine;
import tech.synframe.systemcontrol.utils.ExecuteShellSynFrame;
import tech.synframe.systemcontrol.utils.Queue;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
                    objects.put("status", "success");
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
                "projectStatus"
            )
        );
    }

    @WSEvent(method = "log", action = "project", description = "Show project running status", enabled = true, name = "StatusProject")
    public void log(RequestEvent e){
        HashMap<String, Object> objects = new HashMap<String, Object>();
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("id")
        ){
            User u = (User) e.getSession().getSessionData().get("user");
            int id = Integer.valueOf(e.getRequest().getData().get("id"));
            int lastId = 0;
            if(e.getRequest().getData().containsKey("lastid")) {
                lastId = Integer.valueOf(e.getRequest().getData().get("lastid"));
            }
            try{
                final List<Project> project = Project._find(Project.class, "id=? and user=?", id, u.getId()).exec(Project.class);
                if(project.size()>0) {
                    objects.put("status", "success");
                    if(lastId==0){
                        objects.put("log", project.get(0).instance().getOutput());
                    }else {
                        LinkedList<ConsoleLine> lines = new LinkedList<ConsoleLine>();
                        for(ConsoleLine line : new LinkedList<ConsoleLine>(project.get(0).instance().getOutput())){
                            if(line.getId()>lastId){
                                lines.add(line);
                            }
                        }
                        objects.put("log", lines);
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
                        "projectStatus"
                )
        );
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
                    project.get(0).start();
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
                    project.get(0).stop();
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
            StatisticDocument sd = (StatisticDocument) e.getData();
            long projectId = Long.valueOf(sd.getIdentifier());
            Map<String, Object> statistics = new HashMap<String, Object>();
            statistics.put("free", sd.getFree());
            statistics.put("total", sd.getTotal());
            statistics.put("max", sd.getMax());
            statistics.put("clients", sd.getClients());
            statistics.put("defaultPath", sd.getDefaultPath());
            statistics.put("instanceProperties", sd.getInstanceProperties());
            statistics.put("moduleProperties", sd.getModuleProperties());
            for(Map.Entry<String, Properties> module: sd.getModuleProperties().entrySet()){
                if(module.getValue().containsKey("jenkins")){
                    String jenkinsUrl = (String) module.getValue().get("jenkins");
                    int build = Integer.valueOf((String)module.getValue().get("build"));
                    String modName = (String) module.getValue().get("module");
                    if(!Modules._exists(Modules.class, "jenkinsUrl=? and project=?", jenkinsUrl, projectId)){
                        try {
                            Modules mod = new Modules();
                            mod.setJenkinsUrl(jenkinsUrl);
                            mod.setName(modName);
                            mod.setBuild(build);
                            mod._insert();
                            Project p = Project._find(Project.class, "id=?", projectId).exec(Project.class).get(0);
                            mod._set(p);
                        }catch (Exception err){
                            err.printStackTrace();
                        }
                    }else{
                        try {
                            Modules mod = Modules._find(Modules.class, "jenkinsUrl=? and project=?", jenkinsUrl, projectId).exec(Modules.class).get(0);
                            if(mod.getBuild()!=build) {
                                mod.setBuild(build);
                                mod._save("build", build);
                            }
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }
                }
            }
            statistics.put("modulePath", sd.getModulePath());
            statistics.put("configPath", sd.getConfigPath());
            Project.projectStatistics.put(projectId,statistics);
        }
    }

}
