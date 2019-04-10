package com.alang.liu.newton.vega.exception;

import com.alang.liu.newton.vega.module.Response;
import lombok.Getter;

/**
 * 用于记录流程异常
 *
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2018/10/19 13:57
 */
@Getter
public class RpcException extends RuntimeException {
    private Integer errorCode;
    private Response response;

    /**
     * @param errorCode 错误码
     * @param message   错误描述
     */
    public RpcException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * @param errorCode 错误码
     * @param message   错误描述
     * @param response  远程rpc调用,返回远程 response
     */
    public RpcException(Integer errorCode, String message, Response response) {
        super(message);
        this.errorCode = errorCode;
        this.response = response;
    }

    /**
     * @param errorCode 错误码
     * @param message   错误描述
     * @param cause     根错误
     */
    public RpcException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
