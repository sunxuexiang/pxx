package com.wanmi.sbc.store.response;

import com.wanmi.sbc.customer.bean.enums.StoreResponseState;
import com.wanmi.sbc.customer.bean.vo.StoreCustomerFollowVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺基本信息
 * Created by bail on 2017/11/29.
 */
@ApiModel
@Data
public class StoreCustomerFollowRespVO extends StoreCustomerFollowVO implements Serializable {

    private static final long serialVersionUID = 7687228447698851748L;

    @ApiModelProperty(value = "店铺商品")
    private List<EsGoodsInfo> goodsInfoList;

}
