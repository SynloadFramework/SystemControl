package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.dashboard.DashboardMain;
import tech.synframe.systemcontrol.elements.project.CreateProjectPage;
import tech.synframe.systemcontrol.elements.project.ProjectPage;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.util.HashMap;
import java.util.List;

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
}
