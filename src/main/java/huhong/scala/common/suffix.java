package huhong.scala.common;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huhong on 15/2/27.
 */
@Target({java.lang.annotation.ElementType.FIELD,
        java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface suffix {
    String value() default "";
}
