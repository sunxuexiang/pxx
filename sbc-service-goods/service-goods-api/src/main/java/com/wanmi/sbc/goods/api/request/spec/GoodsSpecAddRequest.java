package com.wanmi.sbc.goods.api.request.spec;

import com.wanmi.sbc.goods.bean.vo.GoodsSpecVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 *  @author: chenjun
 *  @createDate: 2020/6/1 19:51
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsSpecAddRequest implements Serializable {
    private static final long serialVersionUID = 4714605436551316789L;

    @ApiModelProperty(value = "goodsSpecVO")
    private List<GoodsSpecVO> goodsSpecVOs;
}
