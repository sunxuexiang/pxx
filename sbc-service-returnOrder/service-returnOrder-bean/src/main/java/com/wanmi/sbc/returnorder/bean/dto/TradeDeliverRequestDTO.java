package com.wanmi.sbc.returnorder.bean.dto;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.bean.vo.LogisticsVO;
import com.wanmi.sbc.returnorder.bean.vo.ShippingItemVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeDeliverVO;
import com.wanmi.sbc.setting.bean.vo.ExpressCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */
@Data
@ApiModel
public class TradeDeliverRequestDTO extends BaseQueryRequest {

    /**
     * 物流单号
     */
    @ApiModelProperty(value = "物流单号")
    private String deliverNo;

    /**
     * 物流ID
     */
    @ApiModelProperty(value = "物流ID")
    private String deliverId;

    /**
     * 公司名称(选其他的时候的填)
     */
    @ApiModelProperty(value = "公司名称(选其他的时候的填)")
    private String logisticCompanyName;

    /**
     * 物流公司电话
     */
    @ApiModelProperty(value = "物流公司电话")
    private String logisticPhone;

    /**
     * 商家配送方式
     */
    @ApiModelProperty(value = "商家配送方式")
    private Integer supplierDeliverWay;

    /**
     * 发货信息
     */
    @ApiModelProperty(value = "发货信息")
    @Valid
    private List<ShippingItemDTO> shippingItemList = new ArrayList<>();

    /**
     * 赠品信息
     */
    @ApiModelProperty(value = "赠品信息")
    @Valid
    private List<ShippingItemDTO> giftItemList = new ArrayList<>();

    /**
     * 发货时间
     */
    @ApiModelProperty(value = "发货时间")
    private String deliverTime;

    @ApiModelProperty(value = "接货点ID")
    private String shipmentSiteId;

    @ApiModelProperty(value = "接货点名称")
    private String shipmentSiteName;
    @ApiModelProperty(value = "附件,逗号隔开")
    private String encloses;

    /**
     * @return
     */
    public void wrapTradeDevlier(TradeDeliverVO tradeDeliver, String logisticCompanyName, String logisticStandardCode) {
        LogisticsVO logistics = LogisticsVO.builder()
                .logisticCompanyId(deliverId)
                .logisticCompanyName(logisticCompanyName)
                .logisticNo(deliverNo)
                .shipmentSiteId(shipmentSiteId)
                .shipmentSiteName(shipmentSiteName)
                .encloses(encloses)
                .logisticStandardCode(logisticStandardCode).logisticPhone(logisticPhone)
                .build();
        tradeDeliver.setLogistics(logistics);
        tradeDeliver.setDeliverTime(DateUtil.getLocalDateTimeByDateTime(DateUtil.stringToDate(deliverTime)));
        tradeDeliver.setDeliverWay(supplierDeliverWay);
        tradeDeliver.setShippingItems(KsBeanUtil.convertList(shippingItemList, ShippingItemVO.class));
        tradeDeliver.setGiftItemList(KsBeanUtil.convertList(giftItemList, ShippingItemVO.class));
    }

    /**
     * @return
     */
    public TradeDeliverVO toTradeDevlier(ExpressCompanyVO expressCompany) {
        LogisticsVO logistics = null;
        if (expressCompany != null) {
            logistics = LogisticsVO.builder()
                    .logisticCompanyId(expressCompany.getExpressCompanyId().toString())
                    .logisticCompanyName(expressCompany.getExpressName())
                    .logisticNo(deliverNo)
                    .logisticStandardCode(expressCompany.getExpressCode())
                    .build();

            logistics.setLogisticNo(deliverNo);
        }
        TradeDeliverVO tradeDeliver = new TradeDeliverVO();
        tradeDeliver.setLogistics(logistics);
        tradeDeliver.setShippingItems(KsBeanUtil.convertList(shippingItemList, ShippingItemVO.class));
        tradeDeliver.setGiftItemList(KsBeanUtil.convertList(giftItemList, ShippingItemVO.class));
        tradeDeliver.setDeliverTime(DateUtil.parseDay(deliverTime));
        return tradeDeliver;
    }
}
