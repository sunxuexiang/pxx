package com.wanmi.sbc.goods.api.request.company;

import com.wanmi.sbc.common.enums.SortType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCompanyListRequest implements Serializable {

    private static final long serialVersionUID = 4390819159191294564L;
    /**
     * and 精准查询，厂商名称
     */
    @ApiModelProperty(value = "and 精准查询，品牌名称")
    private String companyName;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;


    /**
     * 多重排序
     * 内容：key:字段,value:desc或asc
     */
    @ApiModelProperty(value = "品牌编号")
    private Map<String, String> sortMap = new LinkedHashMap<>();
    /**
     * 填序排序
     *
     * @param column
     * @param sort
     */
    public void putSort(String column, String sort) {
        sortMap.put(column, sort);
    }

    public Sort getSort() {
        //多重排序
        if (MapUtils.isNotEmpty(sortMap)) {
            List<Sort.Order> orders =
                    sortMap.keySet().stream().filter(StringUtils::isNotBlank)
                            .map(column -> new Sort.Order(SortType.ASC.toValue().equalsIgnoreCase(sortMap.get(column)
                            ) ? Sort.Direction.ASC : Sort.Direction.DESC, column))
                            .collect(Collectors.toList());
            return Sort.by(orders);
        }
        return null;
    }
}
