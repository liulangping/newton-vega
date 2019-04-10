package com.alang.liu.newton.vega.utils;

import com.alang.liu.newton.vega.annotation.NodeInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * 初始化需要进行非空校验类的 配置
 *
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2018/9/4 14:47
 */
@Slf4j
public class NodeInfoUtils {

    private static Map<String, String> nodeDescMap = new ConcurrentHashMap<>();
    private static Map<String, Boolean> nodeMonitorMap = new ConcurrentHashMap<>();

    /**
     * 所有继承类在这里继承实现该方法,进行配置
     */
    public static void configNodeClass(Class... classes) {
        for (Class clazz : classes) {
            initRequirdMap(clazz);
        }
    }


    /**
     * 初始化 配置
     *
     * @param clazz 需要验证的 类
     */
    private static void initRequirdMap(Class clazz) {
        log.info("======>开始初始化{}", clazz.getSimpleName());
        Field[] fields = clazz.getDeclaredFields();
        String nodeName = EMPTY;
        String nodeDesc = EMPTY;
        Boolean monitor;

        log.info("fields.length={}", fields.length);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            NodeInfo nodeInfo = field.getAnnotation(NodeInfo.class);
            try {
                field.setAccessible(true);
                nodeName = (String) field.get(clazz);
                if (isEmpty(nodeName)) {
                    log.info("field:{}获取到nodeName为null,跳到下一次循环", field.getName());
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("field:{},获取nodeName异常!{},结束本次循环", field.getName(), e.getMessage());
                continue;
            }

            if (nonNull(nodeInfo)) {
                nodeDesc = nodeInfo.value();
                monitor = nodeInfo.monitor();
                log.info("{},{}-[{}]", i, nodeName, nodeDesc);
                nodeDescMap.put(nodeName, nodeDesc);
                nodeMonitorMap.put(nodeName, monitor);
            } else {
                nodeDescMap.put(nodeName, nodeName);
                nodeMonitorMap.put(nodeName, Boolean.TRUE);
                log.info("{},{}-[{}]", i, nodeName, nodeName);
            }

        }
    }

    /**
     * 返回节点的中文描述
     *
     * @param nodeName
     * @return
     */
    public static String desc(String nodeName) {
        String nodeDesc = nodeDescMap.get(nodeName);
        return isEmpty(nodeDesc) ? nodeName : nodeDesc;
    }

    /**
     * 返回告知是否需要监控
     *
     * @param nodeName
     * @return
     */
    public static Boolean needMonitor(String nodeName) {
        Boolean monitor = nodeMonitorMap.get(nodeName);
        return isEmpty(monitor) ? Boolean.TRUE : monitor;
    }

}


