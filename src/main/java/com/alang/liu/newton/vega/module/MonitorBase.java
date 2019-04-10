package com.alang.liu.newton.vega.module;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2018/9/6 17:13
 * 流程监控基础模型
 */
@Getter
@ToString
@Builder
public class MonitorBase {
    @Tolerate
    public MonitorBase() {
    }

    /**
     * 交易流水号
     */
    private String txnSn;
    /**
     * 借据号
     */
    private String loanNo;
}
