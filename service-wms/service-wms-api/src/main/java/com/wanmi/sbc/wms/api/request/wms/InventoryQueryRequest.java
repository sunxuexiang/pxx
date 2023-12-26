package com.wanmi.sbc.wms.api.request.wms;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: Inventory
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 16:45
 * @Version: 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class InventoryQueryRequest extends WmsBaseRequest {


    private static final long serialVersionUID = 4279319434969070120L;

    /**
     * 仓库 ID
     */
    @JSONField(name = "WarehouseID")
    private String WarehouseID;

    /**
     * 货主 ID
     */
    @JSONField(name = "CustomerID")
    private String CustomerID;

    /**
     * SKU 编码
     */
    @JSONField(name = "SKU")
    private String SKU;

    /**
     * 特价仓的Id
     */
    @JSONField(name = "Lotatt04")
    private String Lotatt04;

    /**
     * 库存日期
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JSONField(name = "INVDate")
    private LocalDateTime INVDate;

    /**
     * 每页记录数
     */
    @JSONField(name = "PageSize")
    private Integer PageSize;

    /**
     * 页数
     */
    @JSONField(name = "PageNo")
    private Integer PageNo;


    /**
     * 仓库编码
     */
    private List<String> wareHouseCode;

}
