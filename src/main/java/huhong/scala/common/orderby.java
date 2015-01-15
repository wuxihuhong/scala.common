package huhong.scala.common;

import java.lang.annotation.*;

/**
 * Created by huhong on 15/1/15.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface orderby {

    String value();
}
