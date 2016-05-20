package tech.synframe.systemcontrol.elements;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleResource;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathaniel on 5/7/2016.
 */
public class PageWrapper extends Response {
    public User user;
    public List<Project> projects = new ArrayList<Project>();
    public PageWrapper(WSHandler session, List<String> templateCache, User u){
        this.user = u;
        this.setTemplateId("wrap-sc"); // set the template id (stored client side)
        if(!templateCache.contains(this.getTemplateId())){ // check to see if the client has this template
            try {
                this.setTemplate(this.getTemplate(new String(ModuleResource.get("systemcontrol","templates/wrapper.html"),"UTF-8"))); // retrieve template from memory
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            projects = user._related(Project.class).exec(Project.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.setParent("body"); // parent element, css selector
        this.setParentTemplate("index"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
        this.setAction("alone"); // how to transition the element to the new content
        this.setPageId("systemControl");
    }
}
