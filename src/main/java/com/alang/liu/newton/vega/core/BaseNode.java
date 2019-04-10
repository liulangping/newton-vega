package com.alang.liu.newton.vega.core;

import com.alang.liu.newton.vega.module.Response;

import static org.apache.logging.log4j.util.Strings.EMPTY;

/**
 * 所有流程节点的基础Node
 * 里面包含了每个流程节点可执行的方法声明
 *
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2019-03-31 17:29
 */
public abstract class BaseNode<T> {

    /**
     * 执行正常业务处理流程
     *
     * @param context 上下文
     * @return 返回
     */
    public abstract Response doProcess(Context<T> context) throws Exception;

    /**
     * 配置获取下一个节点的路由规则
     *
     * @return 返回下一个节点定义
     */
    public String configSwitchNode(Context<T> context) {
        return EMPTY;
    }


    /**
     * 在doProcess 中发成了错误,如果需要后续进行处理的动作,可以overWrite此方法
     *
     * @param errorResponse
     * @param context
     */
    public void afterError(Response errorResponse, Context<T> context) {
    }


}
