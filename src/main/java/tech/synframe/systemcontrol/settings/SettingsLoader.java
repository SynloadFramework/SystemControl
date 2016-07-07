package tech.synframe.systemcontrol.settings;

import com.synload.framework.Log;
import com.synload.framework.modules.ModuleLoader;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map.Entry;

/**
 * Created by Nathaniel on 7/6/2016.
 */
public class SettingsLoader {
    public static void readSettings(){
        Hashtable<String, Class<?>> cache = ModuleLoader.cache;
        for(Entry<String, Class<?>> values : cache.entrySet()){
            for(Method m : values.getValue().getDeclaredMethods()){
                if(m.isAnnotationPresent(ProjectSetting.class)){
                    ProjectSetting ps = m.getAnnotation(ProjectSetting.class);
                    Setting method = ps.method();
                    String settingName = ps.name();
                    String settingType = ps.type();
                    String className = values.getValue().getName();
                    String methodName = m.getName();
                    if(m.getParameterTypes().length>0){
                        if(m.getParameterTypes()[0]==SettingData.class){
                            if(method == Setting.GET) {
                                SettingRegistry.addSetting(className, methodName, null, settingType, settingName);
                            }else if(method == Setting.SET){
                                SettingRegistry.addSetting(className, null, methodName, settingType, settingName);
                            }else{
                                Log.error(className+":"+methodName+" ( Method incorrect )", SettingsLoader.class);
                            }
                        }else{
                            Log.error(className+":"+methodName+" ( Parameter error, needs ONLY SettingData as a parameter! )", SettingsLoader.class);
                        }
                    }else{
                        Log.error(className+":"+methodName+" ( Parameter error, needs SettingData as a parameter! )", SettingsLoader.class);
                    }
                }
            }
        }
    }
}
