package tech.synframe.systemcontrol.elements.module;

import com.mashape.unirest.http.JsonNode;
import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleResource;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.Modules;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.utils.NewVersionCheck;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Nathaniel on 5/28/2016.
 */
public class InfoModulePage extends Response {
    public User user;
    public Project project = null;
    public Modules module = null;
    public String info = null;
    public InfoModulePage(WSHandler session, List<String> templateCache, User u, Project p, Modules m){
        boolean foundAttempt = false;
        this.setTemplateId("modinfo-sc"); // set the template id (stored client side)
        if (!templateCache.contains(this.getTemplateId())) { // check to see if the client has this template
            try {
                this.setTemplate(this.getTemplate(new String(ModuleResource.get("systemcontrol", "templates/module/info.html"), "UTF-8"))); // retrieve template from memory
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        user = u;
        project = p;
        module = m;
        info = NewVersionCheck.getBuildInfoString(module, m.getBuild());
        this.setParent(".projectAction-wrapper"); // parent element, css selector
        this.setParentTemplate("wrapper"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
        this.setAction("alone"); // how to transition the element to the new content
        this.setPageId("modinfo-sc");
    }
}
