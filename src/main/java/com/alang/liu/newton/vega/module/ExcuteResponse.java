package com.alang.liu.newton.vega.module;

import com.alang.liu.newton.vega.core.BaseNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2018/9/6 17:13
 */
@Getter
@AllArgsConstructor
public class ExcuteResponse<T> {
    /**
     * 当前执行的 node
     */
    private BaseNode<T> curentNode;
    /**
     * 当前执行node 的返回结果
     */
    private Response nodeRespose;

}
