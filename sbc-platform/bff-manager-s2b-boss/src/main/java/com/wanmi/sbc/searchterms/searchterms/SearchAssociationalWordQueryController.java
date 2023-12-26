package com.wanmi.sbc.searchterms.searchterms;


import com.wanmi.sbc.common.base.BaseResponse;

import com.wanmi.sbc.es.elastic.EsSearchAssociationalWordQueryService;
import com.wanmi.sbc.es.elastic.request.searchterms.EsSearchAssociationalWordPageRequest;
import com.wanmi.sbc.es.elastic.response.searchterms.EsSearchAssociationalWordPageResponse;
import com.wanmi.sbc.es.elastic.vo.searchterms.EsAssociationLongTailWordVO;
import com.wanmi.sbc.es.elastic.vo.searchterms.EsSearchAssociationalWordVO;
import com.wanmi.sbc.setting.api.provider.searchterms.SearchAssociationalWordQueryProvider;
import com.wanmi.sbc.setting.api.request.searchterms.AssociationLongTailWordByIdsRequest;
import com.wanmi.sbc.setting.api.response.searchterms.AssociationLongTailWordByIdsResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>搜索词APIProvider</p>
 *
 * @author weiwenhao
 * @date 2020-04-20
 */
@RestController
@ApiModel
@Api(tags = "SearchAssociationalWordQueryController", description = "搜索词查询服务API")
@RequestMapping("/search_associational_word")
public class SearchAssociationalWordQueryController {

    @Autowired
    private EsSearchAssociationalWordQueryService esSearchAssociationalWordService;

    @Autowired
    private SearchAssociationalWordQueryProvider searchAssociationalWordQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 搜索词分页查询
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "搜索词分页查询")
    @PostMapping("/page")
    BaseResponse<EsSearchAssociationalWordPageResponse> page(@RequestBody @Valid EsSearchAssociationalWordPageRequest request) {
        operateLogMQUtil.convertAndSend("搜索词", "搜索词&联想词列表查询", "热门搜索词列表查询");
        BaseResponse<EsSearchAssociationalWordPageResponse> page = esSearchAssociationalWordService.page(request);
        if (CollectionUtils.isNotEmpty(page.getContext().getSearchAssociationalWordPage().getContent())) {

            List<Long> longList = page.getContext().getSearchAssociationalWordPage().getContent().stream()
                    .map(EsSearchAssociationalWordVO::getId)
                    .collect(Collectors.toList());
            Map<Long, List<EsAssociationLongTailWordVO>> groupingMap = this.groupingMap(longList);
            if (MapUtils.isNotEmpty(groupingMap)) {
                page.getContext().getSearchAssociationalWordPage().forEach(source -> {
                    List<EsAssociationLongTailWordVO> vos = groupingMap.get(source.getId());
                    source.setAssociationLongTailWordList(vos);
                });

            }
        }
        return page;
    }

    private Map<Long, List<EsAssociationLongTailWordVO>> groupingMap(List<Long> longList) {
        AssociationLongTailWordByIdsRequest request = AssociationLongTailWordByIdsRequest.builder().idList(longList).build();
        AssociationLongTailWordByIdsResponse idsResponse = searchAssociationalWordQueryProvider.listByIds(request).getContext();

        return Optional.ofNullable(idsResponse.getLongTailWordVOList())
                .orElse(Collections.emptyList()).stream()
                .map(source -> {
                    EsAssociationLongTailWordVO target = EsAssociationLongTailWordVO.builder().build();
                    BeanUtils.copyProperties(source, target);
                    return target;
                }).collect(Collectors.groupingBy(EsAssociationLongTailWordVO::getSearchAssociationalWordId));
    }
}
