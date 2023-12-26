package com.wanmi.sbc.setting.api.request.publishInfo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author lwp
 * @date 2023/10/18
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishUserRequest implements Serializable{
    private static final long serialVersionUID = 1L;


    /**
     * 用户名
     */
    private String userName;


    /**
     * 密码
     */
    private String userPass;

    /**
     * 确认密码
     */
    private String confirmPass;

}