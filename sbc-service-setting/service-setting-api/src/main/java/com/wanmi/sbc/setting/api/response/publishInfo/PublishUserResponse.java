package com.wanmi.sbc.setting.api.response.publishInfo;

import com.wanmi.sbc.setting.bean.vo.PublishUserVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class PublishUserResponse implements Serializable{
    private static final long serialVersionUID = 1L;


    private PublishUserVO publishUserVO;


}