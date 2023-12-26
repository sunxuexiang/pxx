package com.wanmi.sbc.returnorder.bean.vo;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.dto.PickGoodsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ReturnItemVO implements Serializable {

    private static final long serialVersionUID = -3797266710914781701L;

    @ApiModelProperty(value = "skuId")
    private String skuId;

    @ApiModelProperty(value = "skuName")
    private String skuName;

    @ApiModelProperty(value = "skuNo")
    private String skuNo;

    @ApiModelProperty(value = "devanningId")
    private Long devanningId;

    /**
     * 规格信息
     */
    @ApiModelProperty(value = "规格信息")
    private String specDetails;

    /**
     * 退货商品单价 = 商品原单价 - 商品优惠单价
     */
    @ApiModelProperty(value = "退货商品单价")
    private BigDecimal price;

    /**
     * 平摊价格
     */
    @ApiModelProperty(value = "平摊价格")
    private BigDecimal splitPrice;

    /**
     * 供货价
     */
    @ApiModelProperty(value = "供货价")
    private BigDecimal supplyPrice;

    /**
     * 供货价小计
     */
    @ApiModelProperty(value = "供货价小计")
    private BigDecimal providerPrice;

    /**
     * 订单平摊价格
     */
    @ApiModelProperty(value = "订单平摊价格")
    private BigDecimal orderSplitPrice;

    /**
     * 余额支付金额
     */
    private BigDecimal balancePrice;

    /**
     * 申请退货数量
     */
    @ApiModelProperty(value = "申请退货数量")
    private Long num;

    @ApiModelProperty(value = "下单数量")
    private Long byNum;

    /**
     * 退货商品图片路径
     */
    @ApiModelProperty(value = "退货商品图片路径")
    private String pic;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 仍可退数量
     */
    @ApiModelProperty(value = "仍可退数量")
    private Integer canReturnNum;

    /**
     * erpskuNo
     */
    @ApiModelProperty(value = "erpskuNo")
    private String erpSkuNo;

    /**
     * 步长
     */
    private BigDecimal addStep;

    /**
     *批次号
     */
    private String goodsBatchNo;

    /**
     * 实际收货数量
     */
    private String receivedQty;


    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品副标题")
    private String goodsSubtitle;

    @ApiModelProperty(value = "囤货单编号")
    private String newPileOrderNo;

    /**
     * 每个商品的价格
     */
    private List<InventoryDetailSamountTradeVO> inventoryDetailSamountTrades = new ArrayList<>();

    /**
     * 每个退货商品来源囤货单信息
     */
    private List<PickGoodsDTO> returnGoodsList;

    /**
     * @param returnItem
     * @return
     */
    public DiffResult diff(ReturnItemVO returnItem) {
        return new DiffBuilder(this, returnItem, ToStringStyle.JSON_STYLE)
                .append("num", num, returnItem.getNum())
                .build();
    }

    public void merge(ReturnItemVO newReturnItem) {
        DiffResult diffResult = this.diff(newReturnItem);
        diffResult.getDiffs().stream().forEach(
                diff -> {
                    String fieldName = diff.getFieldName();
                    switch (fieldName) {
                        case "num":
                            mergeSimple(fieldName, diff.getRight());
                            break;
                        default:
                            break;
                    }

                }
        );
    }

    private void mergeSimple(String fieldName, Object right) {
        Field field = ReflectionUtils.findField(ReturnItemVO.class, fieldName);
        try {
            field.setAccessible(true);
            field.set(this, right);
        } catch (IllegalAccessException e) {
            throw new SbcRuntimeException("K-050113", new Object[]{ReturnItemVO.class, fieldName});
        }
    }

    /**
     * 拼接所有diff
     *
     * @param returnItem
     * @return
     */
    public List<String> buildDiffStr(ReturnItemVO returnItem) {
        DiffResult diffResult = this.diff(returnItem);
        return diffResult.getDiffs().stream().map(
                diff -> {
                    String result = "";
                    switch (diff.getFieldName()) {
                        case "num":
                            result = String.format("商品 %s 购买数量 由 %s 变更为 %s",
                                    skuId,
                                    diff.getLeft().toString(),
                                    diff.getRight().toString()
                            );
                        default:
                            break;
                    }
                    return result;
                }
        ).collect(Collectors.toList());

    }

}
