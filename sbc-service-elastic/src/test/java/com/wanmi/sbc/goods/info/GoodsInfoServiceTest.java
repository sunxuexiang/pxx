package com.wanmi.sbc.goods.info;

//import com.alibaba.fastjson.JSON;
//import com.wanmi.sbc.goods.BaseTest;
//import com.wanmi.sbc.goods.info.reponse.GoodsInfoResponse;
//import com.wanmi.sbc.goods.info.request.GoodsInfoRequest;
//import com.wanmi.sbc.goods.info.request.GoodsRequest;
//import com.wanmi.sbc.goods.price.GoodsIntervalPriceService;
//import com.wanmi.sbc.common.util.Constants;
//import com.wanmi.sbc.common.util.OsUtil;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Arrays;

//import com.wanmi.sbc.goods.elastic.EsGoodsInfoElasticService;
//import com.wanmi.sbc.goods.elastic.request.EsGoodsInfoQueryRequest;
//import com.wanmi.sbc.goods.elastic.request.EsSpecQueryRequest;
//import com.wanmi.sbc.goods.elastic.response.EsGoodsInfoResponse;

/**
 * Created by daiyitian on 16/6/27.
 * 商品分类单元测试
 */
//public class GoodsInfoServiceTest extends BaseTest {
public class GoodsInfoServiceTest {

//    @Autowired
//    private GoodsInfoService goodsInfoService;
//
////    @Autowired
////    private EsGoodsInfoElasticService esGoodsInfoElasticService;
//
//    @Autowired
//    private GoodsExcelService goodsExcelService;
//
//    @Autowired
//    private S2bGoodsExcelService s2bGoodsExcelService;
//
//    @Autowired
//    private GoodsIntervalPriceService goodsIntervalPriceService;
//
//    @Autowired
//    private OsUtil osUtil;
//
//    @Test
//    public void testAA(){
//        goodsIntervalPriceService.testS(Arrays.asList("ff8080815caeb05c015caeb915610001"), "2c9381ae60495bae0160495d39b20000");
//    }
//
//    @Test
//    public void testFind(){
//        GoodsInfoRequest request = new GoodsInfoRequest();
//        request.setGoodsInfoIds(Arrays.asList("2c9386ff5cc44365015cc446fda60005"));
//        request.setIsHavSpecText(Constants.yes);
//        GoodsInfoResponse response = goodsInfoService.findSkuByIds(request);
//        System.out.println(JSON.toJSONString(response));
//    }
//
////    @Test
////    public void testQuery(){
////        EsGoodsInfoQueryRequest request = new EsGoodsInfoQueryRequest();
////        request.setPageNum(0);
////        request.setPageSize(10);
////        //request.setKeywords("奥特曼 10克");
////        request.setSpecs(Arrays.asList(EsSpecQueryRequest.builder().name("透明").values(Arrays.asList("5克","10克")).build(), EsSpecQueryRequest.builder().name("规格1").values(Arrays.asList("1")).build()));
////        EsGoodsInfoResponse response = esGoodsInfoElasticService.page(request);
////        System.out.println("aavvaa---->"+ JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect));
////    }
//
//    @Test
//    public void testExt(){
//        try {
//            goodsExcelService.exportTemplate();
//        } catch (Exception e) {
//        }
//    }
//
//    @Test
//    public void testImpl(){
//        GoodsRequest request = new GoodsRequest();
//        request.setUserId("1");
//        request.setCompanyInfoId(1L);
//        request.setStoreId(1L);
//        s2bGoodsExcelService.implGoods(request, "xlsx");
//    }
}
