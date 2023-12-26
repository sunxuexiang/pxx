package com.wanmi.sbc.customer.api.request.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApiModel
@Data
public class BaseQueryRequest implements Serializable {

    final static String ASC = "asc";

    final static String DESC = "desc";
    /**
     * 第几页
     */
    @ApiModelProperty(value = "第几页")
    private Integer pageNum = 0;

    /**
     * 每页显示多少条
     */
    @ApiModelProperty(value = "每页显示多少条")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private String sortColumn;

    /**
     * 排序规则 desc asc
     */
    @ApiModelProperty(value = "排序规则 desc asc")
    private String sortRole;

    /**
     * 排序类型
     */
    @ApiModelProperty(value = "排序类型")
    private String sortType;

    /**
     * 多重排序
     * 内容：key:字段,value:desc或asc
     */
    @ApiModelProperty(value = "多重排序", notes = "内容：key:字段,value:desc或asc")
    private Map<String, String> sortMap = new LinkedHashMap<>();

    /**
     * 获取分页参数对象与排序条件
     *
     * @return
     */
    public PageRequest getPageRequest() {
        //无排序
        Sort sort = getSort();
        if (Objects.nonNull(sort)) {
            return PageRequest.of(pageNum, pageSize, sort);
        } else {
            return PageRequest.of(pageNum, pageSize,Sort.unsorted());
        }
    }

    public Sort getSort() {
        // 单个排序
        if (StringUtils.isNotBlank(sortColumn)) {
            // 判断规则 DESC ASC
            Sort.Direction direction = ASC.equalsIgnoreCase(sortRole) ? Sort.Direction.ASC : Sort
                    .Direction.DESC;
            return new Sort(direction, sortColumn);
        }

        //多重排序
        if (sortMap != null && sortMap.size() > 0) {
            List<Sort.Order> orders =
                    sortMap.keySet().stream().filter(StringUtils::isNotBlank)
                            .map(column -> new Sort.Order(ASC.equalsIgnoreCase(sortMap.get(column)
                            ) ? Sort.Direction.ASC : Sort.Direction.DESC, column))
                            .collect(Collectors.toList());
            return Sort.by(orders);
        }
        return Sort.unsorted();
    }

    /**
     * 获取分页参数对象
     *
     * @return
     */
    public PageRequest getPageable() {
        return PageRequest.of(pageNum, pageSize);
    }

    /**
     * 填序排序
     *
     * @param column
     * @param sort
     */
    public void putSort(String column, String sort) {
        sortMap.put(column, sort);
    }
}
