package com.wanmi.sbc.goods.response;

import com.wanmi.sbc.common.enums.CompanyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-07-12 11:38
 **/
@Data
public class GoodsMallPlatformSupplierVO implements Serializable {
    private static final long serialVersionUID = 4239429839327965050L;

    @ApiModelProperty(value = "商家Id")
    private Long companyInfoId;
    @ApiModelProperty(value = "店铺名称")
    private String storeName;
    @ApiModelProperty(value = "店铺Id")
    private Long storeId;
    @ApiModelProperty(value = "店铺logo")
    private String storeLogo;
    @ApiModelProperty(value = "店铺招牌")
    private String storeSign;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;
    @Enumerated
    private CompanyType companyType;

    private Boolean waitingForStoreId =false;

    @ApiModelProperty(value = "gif地址")
    private String gifUrl;

    private String imageUrl;

    @ApiModelProperty(value = "直播中")
    private Boolean liveNow;

    private List<Integer> liveRooms;

    private String liveDesc;

    private Boolean customerBought;
}
