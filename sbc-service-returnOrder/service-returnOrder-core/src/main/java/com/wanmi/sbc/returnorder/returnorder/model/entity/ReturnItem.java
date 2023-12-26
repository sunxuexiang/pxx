package com.wanmi.sbc.returnorder.returnorder.model.entity;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.dto.PickGoodsDTO;
import com.wanmi.sbc.returnorder.inventorydetailsamounttrade.model.root.InventoryDetailSamountTrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 退货商品类目
 * Created by jinwei on 19/4/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReturnItem {

    private String skuId;

    private String skuName;

    private String skuNo;
    private Long devanningId;
    /**
     * 规格信息
     */
    private String specDetails;

    /**
     * 退货商品单价 = 商品原单价 - 商品优惠单价
     */
    private BigDecimal price;

    /**
     * 平摊价格
     */
    private BigDecimal splitPrice;

    /**
     * 供货价
     */
    private BigDecimal supplyPrice;

    /**
     * 供货价小计
     */
    private BigDecimal providerPrice;

    /**
     * 供货商id
     */
    private Long providerId;
    /**
     * 订单平摊价格
     */
    private BigDecimal orderSplitPrice;

    /**
     * 余额支付金额
     */
    private BigDecimal balancePrice;

    /**
     * 申请退货数量
     */
    private BigDecimal num;

    /**
     * 退货商品图片路径
     */
    private String pic;

    /**
     * 单位
     */
    private String unit;

    /**
     * 仍可退数量
     */
    private BigDecimal canReturnNum;

    /**
     * 应退积分
     */
    private Long splitPoint;

    /**
     *批次号
     */
    private String goodsBatchNo;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    private  Integer goodsInfoType;

    /**
     * erpId
     */
    private String erpSkuNo;

    /**
     * 步长
     */
    private BigDecimal addStep;

    /**
     * 实际收货数量
     */
    private String receivedQty;

    /**
     * 商品副标题
     */
    private String goodsSubtitle;


    /**
     * 是否是赠送商品 默认为false
     */
    private Boolean fIsFree =false;

    /**
     * 每个商品的价格
     */
    private List<InventoryDetailSamountTrade> inventoryDetailSamountTrades = new ArrayList<>();

    /**
     * 每个退货商品来源囤货单信息
     */
    private List<PickGoodsDTO> returnGoodsList;

    /**
     *
     * @param returnItem
     * @return
     */
    public DiffResult diff(ReturnItem returnItem){
        return new DiffBuilder(this, returnItem, ToStringStyle.JSON_STYLE)
            .append("num", num, returnItem.getNum())
            .build();
    }

    public void merge(ReturnItem newReturnItem){
        DiffResult diffResult = this.diff(newReturnItem);
        diffResult.getDiffs().stream().forEach(
            diff -> {
                String fieldName = diff.getFieldName();
                switch(fieldName){
                    case "num":
                        mergeSimple(fieldName, diff.getRight());
                        break;
                    default:
                        break;
                }

            }
        );
    }

    private void mergeSimple(String fieldName, Object right){
        Field field = ReflectionUtils.findField(ReturnItem.class, fieldName);
        try {
            field.setAccessible(true);
            field.set(this, right);
        } catch (IllegalAccessException e) {
            throw new SbcRuntimeException("K-050113", new Object[]{ReturnItem.class, fieldName });
        }
    }

    /**
     * 拼接所有diff
     * @param returnItem
     * @return
     */
    public List<String> buildDiffStr(ReturnItem returnItem){
        DiffResult diffResult = this.diff(returnItem);
        return diffResult.getDiffs().stream().map(
            diff -> {
                String result = "";
                switch(diff.getFieldName()){
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
