package com.wanmi.sbc.setting.api.response.publishInfo;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.PublishInfoVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class PublishInfoResponse implements Serializable{
    private static final long serialVersionUID = 1L;


    private MicroServicePage<PublishInfoVO> publishInfoVOPage;

}