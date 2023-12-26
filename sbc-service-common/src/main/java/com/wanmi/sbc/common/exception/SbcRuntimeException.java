package com.wanmi.sbc.common.exception;

import com.wanmi.sbc.common.util.CommonErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 统一异常处理
 * Created by jinwei on 31/3/2017.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SbcRuntimeException extends RuntimeException {

    private String errorCode = "";

    private Object data;

    private String result = "fail";

    private Object[] params;

    /**
     * 默认构造，展示系统异常
     */
    public SbcRuntimeException() {
        super();
        this.errorCode = CommonErrorCode.SUCCESSFUL;
    }

    /**
     * 默认errorCode为 空字符串
     *
     * @param cause
     */
    public SbcRuntimeException(Throwable cause) {
        this("", cause);
    }

    /**
     * 只有出错信息
     * 多用于系统自身发生的异常，此时没有上级异常
     *
     * @param errorCode 错误码
     * @param result    出错信息
     */
    public SbcRuntimeException(String errorCode, String result) {
        super();
        this.result = result;
        this.errorCode = errorCode;
    }


    /**
     * 只有出错信息
     * 多用于系统自身发生的异常，此时没有上级异常
     *
     * @param errorCode 异常码 异常码的错误信息会被messageSource读取
     */
    public SbcRuntimeException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }


    /**
     * 只有出错信息
     * 多用于系统自身发生的异常，此时没有上级异常
     *
     * @param errorCode
     * @param params
     */
    public SbcRuntimeException(String errorCode, Object[] params) {
        super();
        this.errorCode = errorCode;
        this.params = params;
    }

    /**
     * 错误码 + 上级异常
     *
     * @param errorCode
     * @param cause
     */
    public SbcRuntimeException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * 返回数据 + 异常码
     *
     * @param data
     * @param errorCode
     */
    public SbcRuntimeException(Object data, String errorCode) {
        this.data = data;
        this.errorCode = errorCode;
    }

    /**
     * 返回数据 + 异常码
     *
     * @param data
     * @param errorCode
     */
    public SbcRuntimeException(Object data, String errorCode, String result) {
        this.data = data;
        this.errorCode = errorCode;
        this.result = result;
    }

    /**
     * 返回值 + 异常链
     *
     * @param data
     * @param cause
     */
    public SbcRuntimeException(Object data, Throwable cause) {
        super(cause);
        this.data = data;
    }
}
