package com.wanmi.sbc.es.elastic;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.es.elastic.request.searchterms.EsSearchAssociationalWordPageRequest;
import com.wanmi.sbc.es.elastic.response.searchterms.EsSearchAssociationalWordPageResponse;
import com.wanmi.sbc.es.elastic.vo.searchterms.EsAssociationLongTailWordVO;
import com.wanmi.sbc.es.elastic.vo.searchterms.EsSearchAssociationalWordVO;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName: EsSearchAssociationalWordService
 * @Description: TODO
 * @Date: 2021/1/20 16:10
 * @Version: 1.0
 */
@Service
public class EsSearchAssociationalWordQueryService {

    @Autowired
    private EsSearchAssociationalWordRepository esSearchAssociationalWordRepository;

   /* @Autowired
    private SearchAssociationalWordQueryProvider searchAssociationalWordQueryProvider;*/

    public BaseResponse<EsSearchAssociationalWordPageResponse> page(EsSearchAssociationalWordPageRequest request) {

        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        bool.must(QueryBuilders.termQuery("delFlag", DeleteFlag.NO.toValue()));
        FieldSortBuilder createTime = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withSort(createTime)
                .withQuery(bool)
                .withPageable(request.getPageable())
                .build();
        Page<EsSearchAssociationalWord> associationalWordPage = esSearchAssociationalWordRepository.search(searchQuery);
        Page<EsSearchAssociationalWordVO> newPage = this.copyPage(associationalWordPage);
        MicroServicePage<EsSearchAssociationalWordVO> microServicePage = new MicroServicePage<>(newPage, request.getPageable());
        return BaseResponse.success(new EsSearchAssociationalWordPageResponse(microServicePage));
    }

    /**
     *   转VO
     * @param associationalWordPage
     * @return
     */
    private Page<EsSearchAssociationalWordVO> copyPage(Page<EsSearchAssociationalWord> associationalWordPage) {

       /* List<Long> longList = associationalWordPage.getContent().stream()
                .map(EsSearchAssociationalWord::getId)
                .collect(Collectors.toList());*/

       /* Map<Long, List<EsAssociationLongTailWordVO>> groupingMap = this.groupingMap(longList);*/

        return associationalWordPage.map(source -> {
            EsSearchAssociationalWordVO target = EsSearchAssociationalWordVO.builder().build();
            BeanUtils.copyProperties(source, target);
           /* if (MapUtils.isNotEmpty(groupingMap)) {
                List<EsAssociationLongTailWordVO> vos = groupingMap.get(source.getId());
                target.setAssociationLongTailWordList(vos);
            }*/
            return target;
        });
    }


/*    *//**
     * 根据SearchAssociationalWordId分组
     * @param longList Id
     * @return Map
     *//*
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
    }*/
}
