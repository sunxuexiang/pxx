package com.wanmi.sbc.customer.api.response.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseUtilResponse <T> implements Serializable {
    
    final static String SUCCESSFUL= "K-000000";

    final static String FAILED= "K-000001";

    public static BaseUtilResponse SUCCESSFUL() {
        return new BaseUtilResponse(SUCCESSFUL);
    }

    public static BaseUtilResponse FAILED() {
        return new BaseUtilResponse(FAILED);
    }

    public BaseUtilResponse(String code) {
        this.code = code;
    }

    public BaseUtilResponse(String code, Object[] args) {
        this.code = code;
    }

    /**
     * 结果码
     */
    @ApiModelProperty(value = "结果码", required = true)
    private String code;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 错误内容
     */
    @ApiModelProperty(value = "错误内容")
    private Object errorData;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private T context;

    /**
     * 特殊提示
     *
     * @param errorCode 异常码
     * @param message   消息
     * @param obj       业务错误的时候，但是依旧要返回数据
     * @return
     */
    public static BaseUtilResponse info(String errorCode, String message, Object obj) {
        return new BaseUtilResponse<>(errorCode, message, obj, null);
    }

    /**
     * 特殊提示
     *
     * @param errorCode 异常码
     * @param message   消息
     * @return
     */
    public static <T> BaseUtilResponse<T> info(String errorCode, String message) {
        return new BaseUtilResponse<>(errorCode, message, null, null);
    }

    /**
     * 失败
     *
     * @param message 消息
     * @return
     */
    public static <T> BaseUtilResponse<T> error(String message) {
        return new BaseUtilResponse<>(FAILED, message, null, null);
    }

    /**
     * 成功
     *
     * @param context 内容
     * @return
     */
    public static <T> BaseUtilResponse<T> success(T context) {
        return new BaseUtilResponse<>(SUCCESSFUL, null, null, context);
    }

}
