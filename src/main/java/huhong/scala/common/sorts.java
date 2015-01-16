package huhong.scala.common;

import java.lang.annotation.*;

/**
 * Created by huhong on 15/1/16.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface sorts {
    sort[] value() default {};
}
