package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@Accessors(chain = true)
public class DevanningGoodsInfoPureVO extends DevanningGoodsInfoVO implements Serializable {


    private static final long serialVersionUID = -3278195491662076829L;

    @ApiModelProperty(value = "最大限购数量")
    private BigDecimal maxPurchase;

    @ApiModelProperty(value = "用户已经购买数量")
    private BigDecimal alreadyNum;


    @ApiModelProperty(value = "限购类型0库存，1区域限购数量，2营销总限购，3营销个人限购，-1区域限购不能购买,4囤货虚拟库存,5预售虚拟库存")
    private Integer type ;



    @ApiModelProperty(value = "当前商品对应的营销id")
    private Long marketingId;

}
