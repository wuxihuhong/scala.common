package huhong.scala.common;

import huhong.scala.common.hibernate.query.QueryBuilder;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huhong on 15/2/11.
 */
@Target({java.lang.annotation.ElementType.FIELD,
        java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface querybuilder {
    Class<? extends QueryBuilder> builder();
}
