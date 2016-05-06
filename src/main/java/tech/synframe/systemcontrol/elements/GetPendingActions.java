package tech.synframe.systemcontrol.elements;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.handlers.ws.PendingActions;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Nathaniel on 5/5/2016.
 */
public class GetPendingActions extends Response {
    public List<Project> projects;
    public List<PendingAction> pending_actions;
    public GetPendingActions(WSHandler user, List<String> templateCache, User u){
        try{
            projects = u._related(Project.class).exec(Project.class);
            for(Project p: projects){
                pending_actions.addAll(p._related(PendingAction.class).exec(PendingAction.class));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        this.setTemplateId("gpa-sc"); // set the template id (stored client side)
        if(!templateCache.contains(this.getTemplateId())){ // check to see if the client has this template
            try {
                this.setTemplate(this.getTemplate(new String(ModuleLoader.resources.get("systemcontrol").get("templates/actions/pendingactions.html"),"UTF-8"))); // retrieve template from memory
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        this.setParent(".pendingAction"); // parent element, css selector
        this.setParentTemplate("index"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
        this.setAction("alone"); // how to transition the element to the new content
        this.setPageId("getPendingActions");
    }
}
