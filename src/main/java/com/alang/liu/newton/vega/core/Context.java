package com.alang.liu.newton.vega.core;

import com.alang.liu.newton.vega.module.MonitorBase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2019-03-31 17:24
 */
@Slf4j
public class Context<T> {

    /**
     * 请求对象,只提供get方法
     * 不提供set方法,保障在流程流转过程中参数一致
     */
    @Getter
    private T data;

    @Getter
    MonitorBase monitorBase;

    /**
     * 如果该流程的某个节点需要重跑,设置重跑的起点
     */
    @Getter
    @Setter
    private String repaireStartNode;

    private Context() {
    }

    /**
     * 只提供含有 data 的构造函数
     *
     * @param data
     */
    public Context(T data) {
        this.data = data;
    }

    /**
     * 含有 monitorBase 的构造函数
     *
     * @param data
     * @param monitorBase
     */
    public Context(T data, MonitorBase monitorBase) {
        this.data = data;
        this.monitorBase = monitorBase;
    }

    /**
     * 利用 map 存放 流程处理上下文需要使用的对象
     */
    @Getter
    private HashMap<String, Object> ctx = new HashMap<>();

    /**
     * 存放流程上下文变量
     *
     * @param key 名称
     * @param obj 对象
     */
    public void put(Enum key, Object obj) {
        ctx.put(key.name(), obj);
    }


    /**
     * 获取String 变量
     *
     * @param key
     * @return
     */
    public String getString(Enum key) {

        return get(key, String.class);
    }


    /**
     * 根据类型 获取数据
     *
     * @param key
     * @param requiredType
     * @param <R>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <R> R get(Enum key, Class<R> requiredType) {
        if (ctx.get(key.name()) == null) {
            log.warn("注意!ctx 中没有找到key={} 的对象!", key.name());
            return null;
        }
        return (R) ctx.get(key.name());
    }
}
