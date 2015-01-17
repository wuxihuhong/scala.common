package huhong.scala.common;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huhong on 15/1/13.
 */
@Target({java.lang.annotation.ElementType.FIELD,
        java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface query_field {
    String value() default "";


    String op() default "=";


    String tablename() default "";

    boolean lower() default false;
}
