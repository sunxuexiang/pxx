package com.wanmi.sbc.setting.api.response.imonlineservice;

import com.wanmi.sbc.common.enums.CustomerServiceType;
import com.wanmi.sbc.setting.api.request.imonlineservice.CommonChatMsgVo;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>onlineService座席列表结果</p>
 * * @Author shiGuangYi
 * @createDate 2023-06-05 17:30
 * @Description: 腾讯客服  IM
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineServiceListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商家配置的客服服务类型
     */
    private CustomerServiceType customerServiceType = CustomerServiceType.TENCENT_IM;

    /**
     * imOnlineService客服信息
     */
    @ApiModelProperty(value = "imOnlineService客服信息")
    private ImOnlineServiceVO imOnlineServerRop = new ImOnlineServiceVO();

    /**
     * imOnlineServiceItem座席列表结果
     */
    @ApiModelProperty(value = "imOnlineServiceItem座席列表结果")
    private List<ImOnlineServiceItemVO> imOnlineServerItemRopList = new ArrayList<>();

    @ApiModelProperty(value = "智能自动回复消息")
    private CommonChatMsgVo autoMsg;

    @ApiModelProperty(value = "快捷回复消息列表")
    private List<CommonChatMsgVo> commonMsgList = new ArrayList<>();

    @ApiModelProperty(value = "腾讯IM客服群组ID")
    private String imGroupId;

    @ApiModelProperty(value = "排队名词")
    private Integer queue = -1;

    public ImOnlineServiceListResponse(ImOnlineServiceVO imOnlineServiceVO, List<ImOnlineServiceItemVO> newList) {
        this.imOnlineServerRop = imOnlineServiceVO;
        this.imOnlineServerItemRopList = newList;
    }
}
