package com.wanmi.sbc.goods.api.response.freight;

import com.wanmi.sbc.goods.bean.vo.FreightTemplateGoodsVO;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 根据店铺id查询默认单品运费模板响应
 * Created by daiyitian on 2018/10/31.
 */
@ApiModel
@Data
public class FreightTemplateGoodsDefaultByStoreIdResponse implements Serializable {

    private static final long serialVersionUID = 6826514907980533216L;

    List<FreightTemplateGoodsVO> defultFreightTemplate;

}
