package tech.synframe.systemcontrol.settings;

import com.synload.framework.modules.ModuleLoader;
import tech.synframe.systemcontrol.models.Project;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathaniel on 7/6/2016.
 */
public class SettingRegistry {
    private static Map<String, Map<String, String>> settings = new HashMap<String, Map<String, String>>();
    public static boolean addSetting(String clazz, String getFunction, String setFunction, ProjectSetting ps){
        if(!settings.containsKey(ps.name())) {
            Map<String, String> setting = new HashMap<String, String>();
            if(getFunction!=null) {
                setting.put("get", getFunction);
                setting.put("getClass", clazz);
            }
            if(setFunction!=null) {
                setting.put("set", setFunction);
                setting.put("setClass", clazz);
            }
            if(!ps.type().equals("")) {
                setting.put("type", ps.type());
            }
            if(!ps.placeholder().equals("")) {
                setting.put("placeholder", ps.placeholder());
            }
            if(!ps.label().equals("")) {
                setting.put("label", ps.label());
            }
            settings.put(ps.name(), setting);
            return true;
        }else if(settings.containsKey(ps.name())){
            Map<String, String> setting = settings.get(ps.name());
            if(getFunction!=null) {
                setting.put("get", getFunction);
                setting.put("getClass", clazz);
            }
            if(setFunction!=null) {
                setting.put("set", setFunction);
                setting.put("setClass", clazz);
            }
            if(!ps.type().equals("")) {
                setting.put("type", ps.type());
            }
            if(!ps.placeholder().equals("")) {
                setting.put("placeholder", ps.placeholder());
            }
            if(!ps.label().equals("")) {
                setting.put("label", ps.label());
            }
            settings.put(ps.name(), setting);
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
            return (String) c.getMethod(setting.get("get"), SettingData.class).invoke(c.newInstance(), new SettingData(p.getId(), settingName));
        }catch(Exception e){ e.printStackTrace();}
        return "";
    }
    public static boolean setValue(Project p, String setting, String value){
        if(settings.containsKey(setting)){
            ModuleLoader classLoader = (new ModuleLoader(Thread.currentThread().getContextClassLoader()));
            Map<String, String> sett = settings.get(setting);
            try {
                Class c =  classLoader.loadClass(sett.get("setClass"));
                c.getMethod(sett.get("set"), SettingData.class).invoke(c.newInstance(), new SettingData(p.getId(), setting, value));
            }catch(Exception e){}
        }
        return false;
    }
}
