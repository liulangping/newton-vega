package com.alang.liu.newton.vega.core;

import com.alang.liu.newton.vega.exception.RpcException;
import com.alang.liu.newton.vega.module.ExcuteResponse;
import com.alang.liu.newton.vega.module.MonitorBase;
import com.alang.liu.newton.vega.module.Response;
import com.alang.liu.newton.vega.utils.NodeInfoUtils;
import com.alang.liu.newton.vega.utils.SpringBeanUtil;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import static com.alang.liu.newton.vega.constant.RespCode.*;
import static com.alang.liu.newton.vega.constant.RespCode.SERVER_ERR_DESC;
import static com.alang.liu.newton.vega.module.Response.error;
import static com.alang.liu.newton.vega.utils.NodeInfoUtils.needMonitor;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * vega 流程执行引擎,
 * 主要用于在gateway层,对所有业务节点进行串联
 *
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2018/8/27 10:37
 */
@Slf4j
public class VegaEngine {

    @Value("${spring.application.name}")
    private String applicationName;


    /**
     * 启动正常流程
     *
     * @param context 构造的 上下文参数
     * @param flowMap 该流程配置好的流程模板
     */
    @SuppressWarnings("unchecked")
    public <T> Response perform(Context<T> context, BaseFlow flowMap) {
        String startNodeName = flowMap.getStartNode();
        return this.excuteFlow(context, flowMap, startNodeName);
    }

    /**
     * 启动修复流程
     *
     * @param context 构造的 上下文参数
     * @param flowMap 该流程配置好的流程模板
     */
    @SuppressWarnings("unchecked")
    public <T> Response performRepaire(Context<T> context, BaseFlow flowMap) {
        String startNodeName = context.getRepaireStartNode();
        log.info("执行修复处理流程,开始节点={}", startNodeName);
        return this.excuteFlow(context, flowMap, startNodeName);
    }

    /**
     * 执行流程引擎
     *
     * @param context 构造的 上下文参数
     * @param flowMap 该流程配置好的流程模板
     */
    private <T> Response excuteFlow(Context<T> context, BaseFlow flowMap, String startNodeName) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("启动流程处理,开始节点={}", startNodeName);
        ExcuteResponse<T> excResp = excuteNode(flowMap, startNodeName, context);

        Response nodeResp = excResp.getNodeRespose();
        if (SUCC == nodeResp.getCode()) {
            log.info("perform() success,耗时:{}", stopwatch.toString());
            return nodeResp;
        }


        log.info("perform(),rollback,耗时:{}", stopwatch.toString());
        return nodeResp;

    }

    /**
     * @param flowMap  流程图
     * @param nodeName 执行的节点
     * @param context  流程执行的上下文
     * @return 返回
     */
    private <T> ExcuteResponse excuteNode(BaseFlow flowMap, String nodeName, Context<T> context) {
        BaseNode<T> curentNode;
        Stopwatch stopwatch;
        Response nodeResp;
        String nextSwitchNode;
        do {
            log.info("开始执行==[{}:{}]=======", nodeName, NodeInfoUtils.desc(nodeName));
            curentNode = SpringBeanUtil.getBean(nodeName);
            stopwatch = Stopwatch.createStarted();
            nodeResp = doProcess(nodeName, curentNode, context);
            log.info("执行 {}.doProcess()完成, 耗时:{}", nodeName, stopwatch.toString());

            if (isNull(nodeResp)) {
                log.error("节点{} 的doProcess()方法返回为 空,请检查", nodeName);
                break;
            }

            if (SUCC != nodeResp.getCode()) {
                log.error("节点{}:{}.doProcess()处理异常!,code={},msg={},requst={}", nodeName, NodeInfoUtils.desc(nodeName),
                        nodeResp.getCode(), nodeResp.getMsg(),
                        nonNull(context.getData()) ? context.getData().toString() : EMPTY);
                log.info("节点{},doProcess()处理异常,执行afterDoProcessError()", nodeName);
                curentNode.afterError(nodeResp, context);
                monitor(nodeName, curentNode, context, nodeResp);
                break;
            }
            /* 执行 configSwitchNode() 方法,得到 下一个节点 nextSwitchNode */
            nextSwitchNode = curentNode.configSwitchNode(context);
            /*对于 configSwitchNode()返回的节点,需要验证其配置的正确性 */
            if (!isEmpty(nextSwitchNode) && !flowMap.vertifyExpectNode(nodeName, nextSwitchNode)) {
                String errMsg = String.format("节点:%s,configSwitchNode()配置的下一路由节点:%s,与flowMap配置的不一致,流程中断,请检查!", nodeName,
                        nextSwitchNode);
                log.error(errMsg);
                nodeResp = error(nodeResp.getCode(), errMsg);
                monitor(nodeName, curentNode, context, nodeResp);
                break;
            }


            monitor(nodeName, curentNode, context, nodeResp);
            if (isEmpty(nextSwitchNode)) {
                /*从流程图中,获取下一个节点路由*/
                nodeName = flowMap.getNext(nodeName);
            } else {
                nodeName = nextSwitchNode;
            }

            log.info("下一节点 {} \n", isEmpty(nodeName) ? "为空,流程结束" : nodeName);
        } while (!isEmpty(nodeName));

        return new ExcuteResponse<>(curentNode, nodeResp);
    }

    /**
     * 执行节点的 doProcess
     *
     * @param nodeName   节点名
     * @param curentNode 当前节点
     * @param context    节点上下文
     * @param <T>
     * @return
     */
    private <T> Response doProcess(String nodeName, BaseNode<T> curentNode, Context<T> context) {
        try {
            return curentNode.doProcess(context);
        } catch (RpcException rpce) {
            /*主要捕获 在调用 BaseNode.getRpcData()方法时,远程返回的 response,将其原生response返回*/
            log.error("节点 {} 处理 RpcException ,errorCode:{},message:{}", nodeName, rpce.getErrorCode(), rpce.getMessage());
            return isNull(rpce.getResponse()) ? error(SERVER_ERR, SERVER_ERR_DESC) : rpce.getResponse();
        } catch (Exception e) {
            log.error("节点 {} 处理异常 ,msg:{}", nodeName, e.getMessage());
            e.printStackTrace();
            return error(SERVER_ERR, e.getMessage());
        }
    }

    /**
     * @param nodeName   当前节点名
     * @param curentNode 当前节点service
     * @param context    流程ctx
     * @param nodeResp   当前节点返回
     * @param <T>
     */
    private <T> void monitor(String nodeName, BaseNode<T> curentNode, Context<T> context,
                             Response nodeResp) {
        try {
            /*如果 monitor=fasle 不管有没有配置 MonitorBase 直接不监控*/
            if (!needMonitor(nodeName)) {
                return;
            }
            /*如果 没有配置 monitorBase 表示该流程不需要监控*/
            if (isNull(context.getMonitorBase())) {
                return;
            }

            MonitorBase monitorBase = context.getMonitorBase();

            if (SUCC != nodeResp.getCode()) {
                context.setRepaireStartNode(nodeName);
            }
        } catch (Exception e) {
            log.warn("节点 {} 处理 sendMonitor 报错,但不影响主流程,流程继续执行.{}", nodeName, e.getMessage());
        }
    }

}
