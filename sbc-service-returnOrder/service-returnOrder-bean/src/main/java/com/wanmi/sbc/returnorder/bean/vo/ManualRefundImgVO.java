package com.wanmi.sbc.returnorder.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ManualRefundImgVO {

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
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
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
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 退款所属单据类型1订单2退单
     */
    @ApiModelProperty(name = "退款所属单据类型1订单2退单")
    private Integer refundBillType;
}
