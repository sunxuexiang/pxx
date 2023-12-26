package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import com.wanmi.sbc.account.bean.vo.TicketsFormImgVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsFormImgRequest extends AccountBaseRequest {
    /**
     * id
     */
    @ApiModelProperty(name = "id")
    private Long ticketsFormImgId;

    /**
     * 申请工单号
     */
    @ApiModelProperty(name = "申请工单号")
    private Long formId;

    /**
     * 申请工单号
     */
    @ApiModelProperty(name = "申请工单号")
    private List<Long> formIdList;

    /**
     * 打款凭证图片地址
     */
    @ApiModelProperty(name = "打款凭证图片地址")
    private String ticketsFormPaymentVoucherImg;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(name = "删除标识")
    private Integer delFlag;

    /**
     * 创建人
     */
    @ApiModelProperty(name = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(name = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @ApiModelProperty(name = "修改人")
    private String updateBy;


    /**
     * 更新时间
     */
    @ApiModelProperty(name = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 增
     */
    @ApiModelProperty(name = "增")
    private List<TicketsFormImgVO> addTicketsFormImgVOList;


    /**
     * 删
     */
    @ApiModelProperty(name = "删")
    private List<Long> deleteTicketsFormImgVOList;
}
