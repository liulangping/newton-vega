package com.alang.liu.newton.vega.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.EMPTY;

/**
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2018/8/24 14:12
 */
@Slf4j
@Getter
public abstract class BaseFlow {

    /**
     * 正常流程节点的开始节点
     */
    private String startNode;
    /**
     * 上一次配置的流程节点
     */
    private String lastConfig;

    /**
     * 正常处理流程配置
     */
    private Map<String, Set<String>> normalFlowMap = new HashMap<>();

    /**
     * 配置 正常流程 的 下一个处理流程节点
     *
     * @param node 节点名称
     * @return self
     */
    public BaseFlow start(String node) {
        this.startNode = node;
        this.lastConfig = node;
        return this;
    }


    /**
     * 配置 正常流程 的 下一个处理流程节点
     *
     * @param node 节点名称
     * @return self
     */
    public BaseFlow next(String node) {

        if (isNull(normalFlowMap.get(lastConfig))) {
            Set<String> toSets = new HashSet<>();
            toSets.add(node);
            normalFlowMap.put(lastConfig, toSets);
        } else {
            normalFlowMap.get(lastConfig).add(node);
        }

        this.lastConfig = node;
        return this;
    }

    /**
     * 配置 正常流程 的 分叉节点
     *
     * @param switchNode 分叉节点
     */
    public BaseFlow switchNode(String switchNode) {
        this.lastConfig = switchNode;
        return this;
    }


    /**
     * 获取下一个节点
     *
     * @param currentNode 当前节点
     * @return 节点名称
     */
    public String getNext(String currentNode) {
        Set<String> nextNodeSets = this.normalFlowMap.get(currentNode);

        /*最后一个节点了,没有下一个节点 */
        if (CollectionUtils.isEmpty(nextNodeSets)) {
            return EMPTY;
        }
        return nextNodeSets.iterator().next();
    }


    /**
     * 校验 期望的 下一个节点 是否在 流程配置中
     *
     * @param currentNode    当前节点
     * @param expectNextNode 期望的下一个节点
     */
    public boolean vertifyExpectNode(String currentNode, String expectNextNode) {
        Set<String> nextNodeSets = this.normalFlowMap.get(currentNode);
        return nextNodeSets.contains(expectNextNode);
    }


    /**
     * 利用 PostConstruct 注解,
     * spring 启动的时候,i调用bean的 configFlowMap()方法
     * 完成流程图的 初始化
     */
    @PostConstruct
    public void initFlowMap() {
        configFlowMap();
    }

    /**
     * 所有实现具体配置流程的流程配置项,通过实现该方法,进行流程节点配置
     */
    public abstract void configFlowMap();


}
