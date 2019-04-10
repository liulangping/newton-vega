package com.alang.liu.newton.vega.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2019-04-06 15:37
 */
@Component
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 设置 spring 的上下文参数到本地 静态中
     *
     * @param applicationContext spring上下文
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.applicationContext = applicationContext;

    }

    /**
     * 获取 bean
     *
     * @param name bean名字
     * @param <T>  返回泛型
     * @return 返回
     * @throws BeansException 异常
     */
    public static <T> T getBean(String name) throws BeansException {
        return (T) getApplicationContext().getBean(name);
    }

    /**
     * 获取  context
     *
     * @return 返回 spring 上下文
     * @throws BeansException 异常
     */
    public static ApplicationContext getApplicationContext() throws BeansException {
        return SpringBeanUtil.applicationContext;
    }
}
