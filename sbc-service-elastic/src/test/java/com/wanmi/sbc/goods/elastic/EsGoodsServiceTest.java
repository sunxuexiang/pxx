package com.wanmi.sbc.goods.elastic;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.EsConstants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticRepository;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
//import com.wanmi.sbc.goods.BaseTest;
//import org.junit.Test;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsListByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.*;

/**
 * Created by daiyitian on 16/6/27.
 * 商品分类单元测试
 */
//public class EsGoodsServiceTest extends BaseTest {
public class EsGoodsServiceTest {

//    @Autowired
//    private EsGoodsInfoElasticService esGoodsInfoElasticService;
//    @Autowired
//    private GoodsInfoQueryProvider goodsInfoQueryProvider;
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;
//    @Autowired
//    private GoodsQueryProvider goodsQueryProvider;
//    @Autowired
//    private EsGoodsInfoElasticRepository esGoodsInfoElasticRepository;
//    @Autowired
//    private Client client;
//
//
//    @Test
//    public void testQuery() {
//        esGoodsInfoElasticService.deleteByGoods(Arrays.asList("2c9381ae5caeb4e7015caebf086e0000"));
//    }
//
//    @Test
//    public void updataEsData() {
//        //添加sku
//        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
//        List<String> goodsInfoIds = new ArrayList<>();
//        goodsInfoIds.add("ff80808172220a2a017228710daa0005");
//        goodsInfoIds.add("ff80808172220a2a017225729b550003");
//
//        queryRequest.setGoodsInfoIds(goodsInfoIds);
//        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
//        Integer result = 0;
//        if (esGoodsInfoList != null) {
//            Map<String, Object> esGoodsInfoMap;
//            Map<String, Object> map;
//            for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
//                esGoodsInfoMap = new HashMap<>(4);
//                map = new HashMap<>(4);
//                map.put("goodsInfoType", 1);
//                map.put("specialPrice", 450);
//                esGoodsInfoMap.put("goodsInfo", map);
//                client.prepareUpdate()
//                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
//                result++;
//            }
//        }
//    }
//
//
//    @Test
//    public void updataEsSku() {
//        EsGoodsInfoRequest request = new EsGoodsInfoRequest();
//        List<String> goodsInfoIds = new ArrayList<>();
//        goodsInfoIds.add("ff80808172220a2a017228710daa0005");
////        goodsInfoIds.add("ff80808172220a2a017225729b550003");
//        request.setSkuIds(goodsInfoIds);
//        esGoodsInfoElasticService.initEsGoodsInfo(request);
//    }
}
