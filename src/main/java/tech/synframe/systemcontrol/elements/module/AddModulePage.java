package tech.synframe.systemcontrol.elements.module;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleResource;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Nathaniel on 5/28/2016.
 */
public class AddModulePage extends Response {
    public User user;
    public Project project = null;
    public AddModulePage(WSHandler session, List<String> templateCache, User u, Project p){
        boolean foundAttempt = false;
        this.setTemplateId("modadd-sc"); // set the template id (stored client side)
        if (!templateCache.contains(this.getTemplateId())) { // check to see if the client has this template
            try {
                this.setTemplate(this.getTemplate(new String(ModuleResource.get("systemcontrol", "templates/module/add.html"), "UTF-8"))); // retrieve template from memory
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        user=u;
        project = p;
        this.setParent(".projectAction-wrapper"); // parent element, css selector
        this.setParentTemplate("wrapper"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
        this.setAction("alone"); // how to transition the element to the new content
        this.setPageId("modadd-sc");
    }
}
