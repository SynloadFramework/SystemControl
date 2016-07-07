package tech.synframe.systemcontrol.elements.project;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleResource;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.settings.SettingRegistry;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Nathaniel on 7/5/2016.
 */
public class ProjectSettingsPage extends Response {
    public User user;
    public Project project = null;
    public List<Map<String, String>> setting = new ArrayList<Map<String, String>>();
    public ProjectSettingsPage(WSHandler session, List<String> templateCache, Project p, User u){
        project = p;
        user = u;
        for(Entry<String, Map<String, String>> set : SettingRegistry.getSettings().entrySet()) {
            Map<String, String> indiSetting = new HashMap<String, String>();
            indiSetting.put("type", set.getValue().get("type"));
            indiSetting.put("setting", set.getKey());
            indiSetting.put("value", SettingRegistry.getValue(p, set.getKey(), set.getValue()));
            setting.add(indiSetting);
        }
        this.setTemplateId("prjctsett-sc"); // set the template id (stored client side)
        if (!templateCache.contains(this.getTemplateId())) { // check to see if the client has this template
            try {
                this.setTemplate(this.getTemplate(new String(ModuleResource.get("systemcontrol", "templates/project/settings.html"), "UTF-8"))); // retrieve template from memory
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        user = u;
        project = p;
        this.setParent(".projectAction-wrapper"); // parent element, css selector
        this.setParentTemplate("wrapper"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
        this.setAction("alone"); // how to transition the element to the new content
        this.setPageId("prjctsett-sc");
    }
}
