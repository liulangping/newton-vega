package com.alang.liu.newton.vega.module;

import com.alang.liu.newton.vega.constant.RespCode;
import lombok.Getter;

/**
 * 节点执行返回包装类
 *
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2019-03-31 17:34
 */
@Getter
public class Response<T> {

    private T data;
    private int code;
    private String msg;

    private Response() {
    }

    public static Response succ() {
        Response<Object> resp = new Response<>();
        resp.code = RespCode.SUCC;
        return resp;
    }

    public static <T> Response succ(T t) {
        Response<T> resp = new Response<>();
        resp.code = RespCode.SUCC;
        resp.data = t;
        return resp;
    }

    public static Response error(int errCode, String msg) {
        Response<Object> resp = new Response<>();
        resp.data = errCode;
        resp.msg = msg;
        return resp;
    }

}
