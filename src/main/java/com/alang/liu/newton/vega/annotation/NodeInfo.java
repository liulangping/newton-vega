package com.alang.liu.newton.vega.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2019-04-06
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NodeInfo {

    /**
     * 设置节点的描述
     *
     * @return
     */
    String value() default "";


    boolean monitor() default true;

    /**
     * 设置节点类
     *
     * @return
     */
    Class node();
}
