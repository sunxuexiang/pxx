package com.wanmi.sbc.setting.api.request.publishInfo;

import com.wanmi.sbc.common.base.BaseQueryRequest;
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
public class PublishInfoRequest extends BaseQueryRequest implements Serializable{
    private static final long serialVersionUID = 1L;


    /**
     * 标题
     */
    private String title;


    /**
     * 内容
     */
    private String content;

}