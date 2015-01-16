package huhong.scala.common;


import org.apache.lucene.search.SortField;

import java.lang.annotation.*;

/**
 * Created by huhong on 15/1/16.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface sort {
    String value();

    SortField.Type sortType() default SortField.Type.STRING;

    boolean reverse() default false;
}
