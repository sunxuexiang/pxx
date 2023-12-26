package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.bean.enums.ShipperType;
import com.wanmi.sbc.order.bean.vo.LogisticsVO;
import com.wanmi.sbc.order.bean.vo.ShippingItemVO;
import com.wanmi.sbc.order.bean.vo.TradeDeliverVO;
import com.wanmi.sbc.setting.bean.vo.ExpressCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */
@Data
@ApiModel
public class TradeDeliverRequestDTO extends BaseQueryRequest {

    /**
     * 公司名称(选其他的时候的填)
     */
    @ApiModelProperty(value = "公司名称(选其他的时候的填)")
    private String logisticCompanyName;

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

    @ApiModelProperty(value = "接货点ID")
    private String shipmentSiteId;

    @ApiModelProperty(value = "接货点名称")
    private String shipmentSiteName;



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
     * 物流公司电话
     */
    @ApiModelProperty(value = "物流公司电话")
    private String logisticPhone;

    /**
     * 发货时间
     */
    @ApiModelProperty(value = "发货时间")
    private String deliverTime;

    @ApiModelProperty(value = "附件,逗号隔开")
    private String encloses;
    private String logisticStandardCode;

    public TradeDeliverDTO getTradeDeliverDTO(String tradeId){
        TradeDeliverDTO tradeDeliverDTO = new TradeDeliverDTO();
        tradeDeliverDTO.setTradeId(tradeId);
        tradeDeliverDTO.setShippingItems(this.shippingItemList);
        tradeDeliverDTO.setGiftItemList(this.giftItemList);
        tradeDeliverDTO.setShipperType(ShipperType.SUPPLIER);
        LogisticsDTO logistics = LogisticsDTO.builder()
                .logisticCompanyId(deliverId)
                .logisticCompanyName(logisticCompanyName)
                .logisticNo(deliverNo)
                .shipmentSiteId(shipmentSiteId)
                .shipmentSiteName(shipmentSiteName)
                .encloses(encloses)
                .logisticStandardCode(logisticStandardCode).logisticPhone(logisticPhone)
                .build();
        tradeDeliverDTO.setLogistics(logistics);
        if(StringUtils.isBlank(this.deliverTime)) {
            tradeDeliverDTO.setDeliverTime(LocalDateTime.now());
        }else{
            tradeDeliverDTO.setDeliverTime(DateUtil.getLocalDateTimeByDateTime(DateUtil.stringToDate(deliverTime)));
        }
        tradeDeliverDTO.setDeliverWay(supplierDeliverWay);
        return tradeDeliverDTO;
    }

    /**
     * @return
     */
    public void wrapTradeDevlier(TradeDeliverVO tradeDeliver, String dblogisticCompanyName, String dblogisticStandardCode) {
        if(StringUtils.isNotBlank(logisticCompanyName)){
            dblogisticCompanyName=logisticCompanyName;
        }
        if(StringUtils.isNotBlank(logisticStandardCode)){
            dblogisticStandardCode=logisticStandardCode;
        }

        LogisticsVO logistics = LogisticsVO.builder()
                .logisticCompanyId(deliverId)
                .logisticCompanyName(dblogisticCompanyName)
                .logisticNo(deliverNo)
                .shipmentSiteId(shipmentSiteId)
                .shipmentSiteName(shipmentSiteName)
                .encloses(encloses)
                .logisticStandardCode(dblogisticStandardCode).logisticPhone(logisticPhone)
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
