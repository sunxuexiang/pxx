package com.wanmi.sbc.order.api.request.manualrefund;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.order.bean.vo.ManualRefundImgVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@Builder
public class ManualRefundImgRequest extends BaseRequest {

    /**
     * id
     */
    @ApiModelProperty(name = "id")
    private Long manualRefundImgId;

    /**
     * 退款单id
     */
    @ApiModelProperty(name = "退款单id")
    private String refundId;

    /**
     * 退款单id
     */
    @ApiModelProperty(name = "退款单id")
    private List<String> refundIdList;

    /**
     * 打款凭证图片地址
     */
    @ApiModelProperty(name = "打款凭证图片地址")
    private String manualRefundPaymentVoucherImg;

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
    private List<ManualRefundImgVO> addManualRefundImgVOList;

    /**
     * 删
     */
    @ApiModelProperty(name = "删")
    private List<Long> deleteManualRefundImgVOList;

    /**
     * 退款所属单据类型1订单2退单
     */
    @ApiModelProperty(name = "退款所属单据类型1订单2退单")
    private Integer refundBillType;

    /**
     * 退款所属单据编号
     */
    @ApiModelProperty(value = "退款所属单据编号")
    private String refundBelongBillId;
}
