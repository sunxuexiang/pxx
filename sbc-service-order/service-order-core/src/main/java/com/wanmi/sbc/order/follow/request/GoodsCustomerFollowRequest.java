package com.wanmi.sbc.order.follow.request;

import com.wanmi.sbc.order.enums.FollowFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import com.wanmi.sbc.order.api.request.follow.validGroups.*;
import javax.persistence.Enumerated;
import java.util.List;

/**
 * 商品客户收藏请求
 * Created by daiyitian on 2017/5/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCustomerFollowRequest {

    /**
     * 编号
     */
    private List<Long> followIds;

    /**
     * SKU编号
     */
    @NotBlank(groups = { FollowAdd.class})
    private String goodsInfoId;

    /**
     * SKU编号
     */
    @NotEmpty(groups = {FollowDelete.class, FollowFilter.class})
    private List<String> goodsInfoIds;

    /**
     * 会员编号
     */
    private String customerId;

    /**
     * 购买数量
     */
    private Long goodsNum;

    /**
     * 收藏标识
     */
    @Enumerated
    private FollowFlag followFlag;

    /**
     * 收藏所属仓库
     */
    private Long wareId;

    @ApiModelProperty(value = "散批仓库")
    private Long bulkWareId;

    //subType 默认 0
    //0是非囤货
    //1是囤货
    //2是散批
    @ApiModelProperty(value = "区分囤货/散批")
    private Integer subType = 0;
}
