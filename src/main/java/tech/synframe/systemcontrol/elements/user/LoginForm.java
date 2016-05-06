package tech.synframe.systemcontrol.elements.user;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class LoginForm extends Response {
    public LoginForm(WSHandler user, List<String> templateCache){
        this.setTemplateId("gpa-sc"); // set the template id (stored client side)
        if(!templateCache.contains(this.getTemplateId())){ // check to see if the client has this template
            try {
                this.setTemplate(this.getTemplate(new String(ModuleLoader.resources.get("systemcontrol").get("templates/user/login.html"),"UTF-8"))); // retrieve template from memory
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        this.setParent("body"); // parent element, css selector
        this.setParentTemplate("index"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
        this.setAction("alone"); // how to transition the element to the new content
        this.setPageId("getPendingActions");
        this.setPageTitle("Pending Actions Notifications");
    }
}
