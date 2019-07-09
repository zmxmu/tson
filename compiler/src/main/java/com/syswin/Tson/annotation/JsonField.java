package com.syswin.Tson.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要json序列化的类注解。
 *
 * @author zhengmin.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface JsonField {
    String value() default "";
}
