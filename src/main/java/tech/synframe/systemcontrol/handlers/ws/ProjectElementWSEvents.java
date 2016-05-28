package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.Log;
import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.dashboard.DashboardMain;
import tech.synframe.systemcontrol.elements.project.CreateProjectPage;
import tech.synframe.systemcontrol.elements.project.EditPage;
import tech.synframe.systemcontrol.elements.project.ProjectDataPage;
import tech.synframe.systemcontrol.elements.project.ProjectPage;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Nathaniel on 5/15/2016.
 */
public class ProjectElementWSEvents {

    @WSEvent(name="CreateProject", description="", enabled = true, method = "get", action = "createproject")
    public void createProject(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user")){
            e.respond(
                new CreateProjectPage(
                    e.getSession(),
                    e.getRequest().getTemplateCache(),
                    (User) e.getSession().getSessionData().get("user")
                )
            );
        }
    }

    @WSEvent(name="DashboardMain", description="", enabled = true, method = "get", action = "dashboard")
    public void dashboardMain(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user")){
            e.respond(
                new DashboardMain(
                    e.getSession(),
                    e.getRequest().getTemplateCache(),
                    (User) e.getSession().getSessionData().get("user")
                )
            );
        }
    }

    @WSEvent(name="ProjectPage", description="", enabled = true, method = "get", action = "project")
    public void projectPage(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user") && e.getRequest().getData().containsKey("project")){
            int projectId = Integer.valueOf(e.getRequest().getData().get("project"));
            User user = (User) e.getSession().getSessionData().get("user");
            try {
                List<Project> projects = Project._find(Project.class, "user=? and id=?", user.getId(), projectId).exec(Project.class);
                if(projects.size()==1){
                    e.respond(
                        new ProjectPage(
                            e.getSession(),
                            e.getRequest().getTemplateCache(),
                            user,
                            projects.get(0)
                        )
                    );
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @WSEvent(name="ProjectPageData", description="", enabled = true, method = "get", action = "projectdata")
    public void projectPageData(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user") && e.getRequest().getData().containsKey("project")){
            int projectId = Integer.valueOf(e.getRequest().getData().get("project"));
            User user = (User) e.getSession().getSessionData().get("user");
            try {
                List<Project> projects = Project._find(Project.class, "user=? and id=?", user.getId(), projectId).exec(Project.class);
                if(projects.size()==1){
                    e.respond(
                        new ProjectDataPage(
                            e.getSession(),
                            e.getRequest().getTemplateCache(),
                            user,
                            projects.get(0)
                        )
                    );
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @WSEvent(name="EditPage", description="", enabled = true, method = "get", action = "edit")
    public void editPage(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user") && e.getRequest().getData().containsKey("project") && e.getRequest().getData().containsKey("file")){
            int projectId = Integer.valueOf(e.getRequest().getData().get("project"));
            String file = e.getRequest().getData().get("file");
            User user = (User) e.getSession().getSessionData().get("user");
            try {
                List<Project> projects = Project._find(Project.class, "user=? and id=?", user.getId(), projectId).exec(Project.class);
                if(projects.size()==1){
                    e.respond(
                        new EditPage(
                            e.getSession(),
                            e.getRequest().getTemplateCache(),
                            projects.get(0),
                            file
                        )
                    );
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @WSEvent(method = "save", action = "edit", description = "Save an edited file", enabled = true, name = "SaveEdit")
    public void save(RequestEvent e){
        HashMap<String, Object> objects = new HashMap<String, Object>();
        if(
            e.getSession().getSessionData().containsKey("user") &&
            e.getRequest().getData().containsKey("project") &&
            e.getRequest().getData().containsKey("file") &&
            e.getRequest().getData().containsKey("contents")
        ){
            long instance = Long.valueOf(e.getRequest().getData().get("project"));
            String file = e.getRequest().getData().get("file");
            String contents = e.getRequest().getData().get("contents");
            User u = (User) e.getSession().getSessionData().get("user");
            try {
                List<Project> projects = Project._find(Project.class, "id=?", instance).exec(Project.class);
                if(projects.size()==1){
                    Project proj = projects.get(0);
                    boolean foundAttempt = false;
                    try {
                        Pattern regex = Pattern.compile("/");
                        Matcher regexMatcher = regex.matcher(file);
                        foundAttempt = regexMatcher.find();
                    } catch (PatternSyntaxException ex) {
                        // Syntax error in the regular expression
                    }
                    if(foundAttempt){
                        // hack attempt / send to future attempt log
                    }else{
                        File fileToOpen = new File(proj.getPath()+file);
                        Files.write(fileToOpen.toPath(), contents.getBytes("UTF-8"));
                        if(e.getRequest().getData().containsKey("restart")) {
                            proj.stop();
                            proj.start();
                        }
                        objects.put("status", "success");
                        objects.put("error", "saved");
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
                "saveEdit"
            )
        );
    }
}
