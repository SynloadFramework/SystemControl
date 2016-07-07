package tech.synframe.systemcontrol.settings;

import java.lang.annotation.*;

/**
 * Created by Nathaniel on 7/6/2016.
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProjectSetting {
    String type() default "";
    String name();
    Setting method();
    String label() default "";
    String placeholder() default "";
}
