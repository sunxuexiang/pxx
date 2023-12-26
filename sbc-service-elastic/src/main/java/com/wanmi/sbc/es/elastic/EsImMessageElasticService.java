package com.wanmi.sbc.es.elastic;


import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.EsConstants;
import com.wanmi.sbc.common.util.MD5Util;
import com.wanmi.sbc.es.elastic.request.EsImMessageQueryRequest;
import com.wanmi.sbc.es.elastic.response.message.IMHistoryChatPageResponse;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.search.FuzzyQuery;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketsort.BucketSortPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketsort.BucketSortPipelineAggregator;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description 腾讯IM消息Elasticsearch持久化服务接口
 * @Author zhouzhenguo
 * @Date 2023/08/15 11:18
 **/
@Slf4j
@Service
public class EsImMessageElasticService {

    @Resource(name = "messageElasticsearchTemplate")
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource(name = "messageTransportClient")
    private Client client;

    /**
     * 批量写入到ElasticSearch
     * @param messageList
     * @return
     */
    public boolean writeMessageList(List<EsImMessage> messageList) {
        if (ObjectUtils.isEmpty(messageList)) {
            return true;
        }
        log.info("写入ES数量 {} - {}", messageList.size(), messageList.get(0).getMsgContent());
//        for (EsImMessage esImMessage : messageList) {
//            log.info("写入ES内容 {}", esImMessage.getMsgContent());
//        }

        Map<String, EsImMessage> messageMap = new HashMap<>();
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        messageList.forEach(message -> {
            String md5 = message.getFromAccount()+message.getToAccount()+message.getMsgTimestamp()+message.getMsgContent();
            md5 = MD5Util.md5Hex(md5);
            log.info("消息md5 {} - {}", md5, message.getMsgContent());
            messageMap.put(md5, message);
            multiGetRequest.add(new MultiGetRequest.Item(EsConstants.DOC_IM_MESSAGE, null, md5));
        });

        ActionFuture<MultiGetResponse> actionFuture = client.multiGet(multiGetRequest);
        MultiGetResponse multiGetResponse = actionFuture.actionGet();
        MultiGetItemResponse[] multiGetItemResponseArray = multiGetResponse.getResponses();
        for (MultiGetItemResponse multiGetItemResponse : multiGetItemResponseArray) {
            log.info("根据ID查询 {}", JSON.toJSONString(multiGetItemResponse));
            if (multiGetItemResponse.isFailed()) {
                continue;
            }
            GetResponse getResponse = multiGetItemResponse.getResponse();
            if (getResponse.isExists()) {
                messageMap.remove(multiGetItemResponse.getId());
            }
        }
        if (messageMap.isEmpty()) {
            return true;
        }

        List<IndexQuery> bulkList = new ArrayList<>();
        messageMap.forEach((key, message) -> {
            log.info("写入ES聊天消息：{} - {}", key, message.getMsgContent());
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(key);
            indexQuery.setIndexName(EsConstants.DOC_IM_MESSAGE);
            indexQuery.setObject(message);
            bulkList.add(indexQuery);
        });
        elasticsearchTemplate.bulkIndex(bulkList);
        return true;
    }


    public boolean writeMessage (EsImMessage message) {
        if (message == null) {
            return true;
        }
        List<EsImMessage> messageList = new ArrayList<>();
        messageList.add(message);
        return writeMessageList(messageList);
    }

    public boolean createIndex () {
        return elasticsearchTemplate.createIndex(EsImMessage.class);
    }

    /**
     * 搜索历史聊天消息
     */
    public List<EsImMessage> searchHistoryMessage(EsImMessageQueryRequest esRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(esRequest.getGroupId())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("groupId.keyword", esRequest.getGroupId()));
        }
        if (!StringUtils.isEmpty(esRequest.getFromAccount())) {
            MultiMatchQueryBuilder formAccountTermsQuery = QueryBuilders.multiMatchQuery(esRequest.getFromAccount(), "fromAccount", "toAccount");
            boolQueryBuilder.must(formAccountTermsQuery);
        }
        if (!StringUtils.isEmpty(esRequest.getToAccount())) {
            MultiMatchQueryBuilder toAccountTermsQuery = QueryBuilders.multiMatchQuery(esRequest.getToAccount(), "fromAccount", "toAccount");
            boolQueryBuilder.must(toAccountTermsQuery);
        }

        parseSearchTimeCondition(esRequest, boolQueryBuilder);

        if (!StringUtils.isEmpty(esRequest.getMsgContent())) {
            AnalyzeRequestBuilder requestBuilder = client.admin().indices().prepareAnalyze(esRequest.getMsgContent()).setAnalyzer(EsConstants.DEF_ANALYZER);
            AnalyzeResponse response = client.admin().indices().analyze(requestBuilder.request()).actionGet();
            Set<String> res = new HashSet<>();
            if (CollectionUtils.isNotEmpty(response.getTokens())) {
                res.addAll(response.getTokens().stream().map(AnalyzeResponse.AnalyzeToken::getTerm).collect(Collectors.toList()));
            }
            res.addAll(Arrays.asList(esRequest.getMsgContent().split("[^0-9]+")));
            res.add(esRequest.getMsgContent());
            log.info("分词结果 {}", JSON.toJSONString(res));
            BoolQueryBuilder contentBoolQuery = QueryBuilders.boolQuery();
            Pattern pattern = Pattern.compile("^[\\w]+$");
            for (String value : res) {
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    continue;
                }
                contentBoolQuery.should(QueryBuilders.wildcardQuery("msgContent", "*"+value+"*"));
//                contentBoolQuery.should(QueryBuilders.matchQuery("msgContent", value));
            }
            boolQueryBuilder.must(contentBoolQuery);

//            WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("msgContent", "*"+esRequest.getMsgContent()+"*");
//            boolQueryBuilder.must(wildcardQueryBuilder);
        }
        if (!StringUtils.isEmpty(esRequest.getMsgType())) {
            if ("TIMVideoFileElem".equals(esRequest.getMsgType()) || "TIMImageElem".equals(esRequest.getMsgType())) {
                boolQueryBuilder.must(QueryBuilders.termsQuery("msgType.keyword", "TIMVideoFileElem", "TIMImageElem"));
            }
            else {
                boolQueryBuilder.must(QueryBuilders.termQuery("msgType.keyword", esRequest.getMsgType()));
            }
        }
        log.info(JSON.toJSONString(boolQueryBuilder));
        log.info(JSON.toJSONString(boolQueryBuilder.toString()));
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(PageRequest.of(esRequest.getPageNum(), esRequest.getPageSize(), Sort.by(Sort.Direction.DESC, "msgTimestamp")));
        searchQueryBuilder.withQuery(boolQueryBuilder);
        log.info(searchQueryBuilder.toString());
        log.info(JSON.toJSONString(searchQueryBuilder));
        SearchQuery searchQuery = searchQueryBuilder.build();
        log.info(JSON.toJSONString(searchQuery));
        // log.info(searchQuery.toString());
        AggregatedPage<EsImMessage> aggregatedPage = elasticsearchTemplate.queryForPage(searchQuery, EsImMessage.class);
        return aggregatedPage.getContent();
    }

    private void parseSearchTimeCondition(EsImMessageQueryRequest esRequest, BoolQueryBuilder boolQueryBuilder) {
        try {
            if (!StringUtils.isEmpty(esRequest.getStartTime()) && !StringUtils.isEmpty(esRequest.getEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("msgTimestamp").gt(dateStrToTimestamp(esRequest.getStartTime())).lt(dateStrToTimestamp(esRequest.getEndTime())));
            }
            else if (!StringUtils.isEmpty(esRequest.getStartTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("msgTimestamp").gt(dateStrToTimestamp(esRequest.getStartTime())));
            }
            else if (!StringUtils.isEmpty(esRequest.getEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("msgTimestamp").lt(dateStrToTimestamp(esRequest.getEndTime())));
            }
        }
        catch (Exception e) {
            log.error("查询IM历史消息异常-解析时间条件错误", e);
        }
    }

    private Long dateStrToTimestamp (String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(dateStr);
        return date.getTime() / 1000;
    }

    public IMHistoryChatPageResponse searchGroupByUser(EsImMessageQueryRequest esRequest) {
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        if (!StringUtils.isEmpty(esRequest.getFromAccount())) {
//            MultiMatchQueryBuilder formAccountTermsQuery = QueryBuilders.multiMatchQuery(esRequest.getFromAccount(), "fromAccount", "toAccount");
//            boolQueryBuilder.must(formAccountTermsQuery);
//        }
//        if (!StringUtils.isEmpty(esRequest.getMsgContent())) {
//            WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("msgContent.keyword", "*"+esRequest.getMsgContent()+"*");
//            boolQueryBuilder.must(wildcardQueryBuilder);
//        }
//        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
//        searchQueryBuilder.withQuery(boolQueryBuilder);
//        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("groupIdAggs").field("groupId");
//        searchQueryBuilder.addAggregation(termsAggregationBuilder);
//        SearchQuery searchQuery = searchQueryBuilder.build();

        List<String> groupList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(EsConstants.DOC_IM_MESSAGE);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("storeId", esRequest.getStoreId()));
        if (!StringUtils.isEmpty(esRequest.getFromAccount())) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(esRequest.getFromAccount(), "fromAccount", "toAccount"));
        }
        if (!StringUtils.isEmpty(esRequest.getMsgContent())) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("msgContent.keyword", "*"+esRequest.getMsgContent()+"*"));
        }
        parseSearchTimeCondition(esRequest, boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(AggregationBuilders.terms("groupIdAggs").field("groupId.keyword").size(9999999)
                .subAggregation(new BucketSortPipelineAggregationBuilder("bucket_sort", null)
                        .from(0).size(9999999)));
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        log.info("群组 查询 {}", searchRequest.toString());
        ActionFuture<SearchResponse> actionFuture = elasticsearchTemplate.getClient().search(searchRequest);
        SearchResponse searchResponse = actionFuture.actionGet();
        Aggregations aggregations = searchResponse.getAggregations();
        Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
        Aggregation aggregation = aggregationMap.get("groupIdAggs");
        Map<String, Object> objectMap = aggregation.getMetaData();
        log.info("aggResult {}", JSON.toJSONString(objectMap));
        StringTerms object = aggregations.get("groupIdAggs");
        log.info("{}", object.getClass());
        List<StringTerms.Bucket> bucketList = object.getBuckets();
        int start = esRequest.getPageNum() * esRequest.getPageSize();
        int end = start + esRequest.getPageSize();
        for (int i=start; i<bucketList.size() && i<end; i++) {
            String key = bucketList.get(i).getKeyAsString();
            log.info("key {}", key);
            groupList.add(key);
        }
        IMHistoryChatPageResponse response = new IMHistoryChatPageResponse(groupList, bucketList.size());
        return response;
    }

    public IMHistoryChatPageResponse searchTodayChatHistory(EsImMessageQueryRequest esRequest) {
        List<String> groupList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(EsConstants.DOC_IM_MESSAGE);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("storeId", esRequest.getStoreId()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("msgTimestamp").gte(getDailyStartTime()).lte(getDailyEndTime()));

        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(AggregationBuilders.terms("groupIdAggs").field("groupId.keyword").size(9999999)
                .subAggregation(new BucketSortPipelineAggregationBuilder("bucket_sort", null)
                        .from(0).size(9999999)));
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        log.info("群组 查询 {}", searchRequest.toString());
        ActionFuture<SearchResponse> actionFuture = elasticsearchTemplate.getClient().search(searchRequest);
        SearchResponse searchResponse = actionFuture.actionGet();
        Aggregations aggregations = searchResponse.getAggregations();
        Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
        Aggregation aggregation = aggregationMap.get("groupIdAggs");
        Map<String, Object> objectMap = aggregation.getMetaData();
        log.info("aggResult {}", JSON.toJSONString(objectMap));
        StringTerms object = aggregations.get("groupIdAggs");
        log.info("{}", object.getClass());
        List<StringTerms.Bucket> bucketList = object.getBuckets();
        int start = esRequest.getPageNum() * esRequest.getPageSize();
        int end = start + esRequest.getPageSize();
        for (int i=0; i<bucketList.size(); i++) {
            String key = bucketList.get(i).getKeyAsString();
            log.info("key {}", key);
            groupList.add(key);
        }
        IMHistoryChatPageResponse response = new IMHistoryChatPageResponse(groupList, bucketList.size());
        return response;
    }

    /**
     * 获取指定某一天的开始时间戳
     * @return
     */
    public static Long getDailyStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 获取指定某一天的结束时间戳
     * @return
     */
    public static Long getDailyEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis() / 1000;
    }
}
