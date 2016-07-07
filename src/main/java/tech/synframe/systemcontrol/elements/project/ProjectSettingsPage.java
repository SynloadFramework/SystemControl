package tech.synframe.systemcontrol.elements.project;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleResource;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.settings.SettingData;
import tech.synframe.systemcontrol.utils.NewVersionCheck;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
    public HashMap<String, String[]> setting = new HashMap<String, String[]>();
    public ProjectSettingsPage(WSHandler session, List<String> templateCache, Project p, User u){
        project = p;
        user = u;
        for(Entry<String, Map<String, String>> set : settings.entrySet()) {
            setting.put(set.getKey(), new String[]{ getValue(p, set.getKey(), set.getValue()), set.getValue().get("type")});
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
    /*
        Static Parameters
     */
    private static Map<String, Map<String, String>> settings = new HashMap<String, Map<String, String>>();
    public static boolean addSetting(String clazz, String getFunction, String setFunction, String type, String settingName){
        if(!settings.containsKey(settingName)) {
            Map<String, String> setting = new HashMap<String, String>();
            if(getFunction!=null) {
                setting.put("get", getFunction);
                setting.put("getClass", clazz);
            }
            if(setFunction!=null) {
                setting.put("set", setFunction);
                setting.put("setClass", clazz);
            }
            setting.put("type", type);
            settings.put(settingName, setting);
            return true;
        }else if(settings.containsKey(settingName)){
            Map<String, String> sett = settings.get(settingName);
            if(getFunction!=null) {
                sett.put("get", getFunction);
                sett.put("getClass", clazz);
            }
            if(setFunction!=null) {
                sett.put("set", setFunction);
                sett.put("setClass", clazz);
            }
            sett.put("type", type);
            settings.put(settingName, sett);
            return true;
        }
        return false;
    }
    public static Map<String, Map<String, String>> getSettings(){
            return settings;
    }
    public static String getValue(Project p, String settingName, Map<String, String> setting){
        ModuleLoader classLoader = (new ModuleLoader(Thread.currentThread().getContextClassLoader()));
        Class c =  classLoader.loadClass(setting.get("getClass"));
        try {
            return (String) c.getMethod(setting.get("get"), c, SettingData.class).invoke(c.newInstance(), new SettingData(p.getId(), settingName));
        }catch(Exception e){}
        return "";
    }
    public static boolean setValue(Project p, String setting, String value){
        if(settings.containsKey(setting)){
            ModuleLoader classLoader = (new ModuleLoader(Thread.currentThread().getContextClassLoader()));
            Map<String, String> sett = settings.get(setting);
            try {
                Class c =  classLoader.loadClass(sett.get("setClass"));
                c.getMethod(sett.get("set"), c, SettingData.class).invoke(c.newInstance(), new SettingData(p.getId(), setting, value));
            }catch(Exception e){}
        }
        return false;
    }
}
