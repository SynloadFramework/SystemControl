package tech.synframe.systemcontrol.settings;

import java.lang.annotation.*;

/**
 * Created by Nathaniel on 7/6/2016.
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProjectSetting {
    String type();
    String name();
    Setting method();
}
