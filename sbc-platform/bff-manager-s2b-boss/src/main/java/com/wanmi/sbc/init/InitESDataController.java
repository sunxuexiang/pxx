package com.wanmi.sbc.init;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.ares.CustomerAresProvider;
import com.wanmi.sbc.es.elastic.*;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.es.elastic.request.searchterms.EsSearchAssociationalWordPageRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.order.api.provider.ares.AresProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.setting.api.provider.searchterms.SearchAssociationalWordQueryProvider;
import com.wanmi.sbc.setting.api.request.searchterms.SearchAssociationalWordPageRequest;
import com.wanmi.sbc.setting.bean.vo.SearchAssociationalWordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: bail
 * Time: 2017/10/23.8:42
 */
@RestController
@RequestMapping("/init")
@Slf4j
@Api(description = "初始化ES服务",tags ="InitESDataController")
public class InitESDataController {
    @Autowired
    private CustomerAresProvider customerAresProvider;

    @Autowired
    private GoodsAresProvider goodsAresProvider;

    @Autowired
    private  AresProvider aresProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    @Autowired
    private EsBulkGoodsInfoElasticService esBulkGoodsInfoElasticService;

    @Autowired
    private SearchAssociationalWordQueryProvider searchAssociationalWordQueryProvider;

    @Autowired
    private com.wanmi.sbc.es.elastic.EsSearchAssociationalWordService EsSearchAssociationalWordService;

    @ApiOperation(value = "初始化ES")
    @RequestMapping(value = "/initES", method = RequestMethod.GET)
    public BaseResponse initES(String type) {
        try {
            //1.不传type默认初始化所有数据
            if(StringUtils.isEmpty(type)){
                customerAresProvider.initCustomerData();
                customerAresProvider.initCustomerLevelData();
                customerAresProvider.initStoreCustomerRelaData();
                customerAresProvider.initEmployeeData();
                customerAresProvider.initStoreData();
                goodsAresProvider.initGoodsES();
                goodsAresProvider.initGoodsBrandES();
                goodsAresProvider.initGoodsCateES();
                goodsAresProvider.initStoreCateES();

                aresProvider.initOrderES();
                aresProvider.initPayOrderES();
                aresProvider.initReturnOrderES();
            }

            //2.传入type,根据type进行初始化数据
            if("customer".equals(type)){
                customerAresProvider.initCustomerData();
            }else if("customer_level".equals(type)){
                customerAresProvider.initCustomerLevelData();
            }else if("store_customer".equals(type)){
                customerAresProvider.initStoreCustomerRelaData();
            }else if("employee".equals(type)){
                customerAresProvider.initEmployeeData();
            }else if("store".equals(type)){
                customerAresProvider.initStoreData();
            }else if("goods".equals(type)){
                goodsAresProvider.initGoodsES();
            }else if("goods_brand".equals(type)){
                goodsAresProvider.initGoodsBrandES();
            }else if("goods_cate".equals(type)){
                goodsAresProvider.initGoodsCateES();
            }else if("store_cate".equals(type)){
                goodsAresProvider.initStoreCateES();
            }else if("order".equals(type)){
                aresProvider.initOrderES();
            }else if("pay_order".equals(type)){
                aresProvider.initPayOrderES();
            }else if("return_order".equals(type)){
                aresProvider.initReturnOrderES();
            }
            return BaseResponse.SUCCESSFUL();
        } catch (Exception e) {
            log.error("Get customer level distribute view fail,the thrift request error,", e);
            return BaseResponse.FAILED();
        }
    }

    @ApiOperation(value = "初始化订单")
    @RequestMapping(value = "/initOrderEmployee", method = RequestMethod.GET)
    public BaseResponse initOrderEmployee() {
        tradeProvider.fillEmployeeId();
        returnOrderProvider.fillEmployeeId();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 将mysql 商品信息 同步到-> ES中
     */
    @ApiOperation(value = "将mysql 商品信息 同步到-> ES中")
    @RequestMapping(value = "/goodsES", method = RequestMethod.GET)
    public BaseResponse initAllGoodsES(EsGoodsInfoRequest queryRequest) {
        log.info("同步es===========================");
        esGoodsInfoElasticService.initEsGoodsInfo(queryRequest);
       // esRetailGoodsInfoElasticService.initEsRetailGoodsInfo(queryRequest);
       // esBulkGoodsInfoElasticService.initEsBulkGoodsInfo(queryRequest);
        return BaseResponse.SUCCESSFUL();
    }
    /**
     * 此方法会全部删除重新建立
     */
    @ApiOperation(value = "(会删除索引)将mysql 商品信息 同步到-> ES中")
    @RequestMapping(value = "/initAllEsGoodsInfo", method = RequestMethod.GET)
    public BaseResponse initAllEsGoodsInfo(EsGoodsInfoRequest queryRequest) {
        log.info("同步es============此方法会全部删除重新建立===============");
        esGoodsInfoElasticService.initAllEsGoodsInfo(queryRequest);
        return BaseResponse.SUCCESSFUL();
    }
    /**
     * 将mysql 商品信息 同步到-> ES中
     */
    @ApiOperation(value = "将mysql 商品信息 同步到-> ES中")
    @RequestMapping(value = "/bulk/goodsES", method = RequestMethod.GET)
    public BaseResponse initBulkGoodsES(EsGoodsInfoRequest queryRequest) {
        esBulkGoodsInfoElasticService.initEsBulkGoodsInfo(queryRequest);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 将mysql 商品信息 同步到-> ES中
     */
    @ApiOperation(value = "将mysql 商品信息 同步到-> ES中")
    @RequestMapping(value = "/retail/goodsES", method = RequestMethod.GET)
    public BaseResponse initRetailGoodsES(EsGoodsInfoRequest queryRequest) {
//        esGoodsInfoElasticService.initEsGoodsInfo(queryRequest);
        esRetailGoodsInfoElasticService.initEsRetailGoodsInfo(queryRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 将mysql 商品信息 同步到-> ES中
     */
    @ApiOperation(value = "将mysql 商品信息 同步到-> ES中")
    @RequestMapping(value = "/goodsES/special", method = RequestMethod.GET)
    public BaseResponse initSpecialES(){
        esGoodsInfoElasticService.modifySpecialGoods();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 将mysql  搜索词 同步到-> ES中
     */
    @MultiSubmit
    @ApiOperation(value = "将mysql 搜索词 同步到-> ES中")
    @RequestMapping(value = "/initSearchAssociationalWordES", method = RequestMethod.POST)
    public BaseResponse initSearchAssociationalWord(@RequestBody EsSearchAssociationalWordPageRequest request) {
        Boolean flg = Boolean.TRUE;
        int pageNum = request.getPageNum();
        int pageSize = 2000;

        SearchAssociationalWordPageRequest pageRequest = KsBeanUtil.convert(request, SearchAssociationalWordPageRequest.class);
        try {
            while (flg) {
                pageRequest.putSort("createTime", SortType.DESC.toValue());
                pageRequest.setPageNum(pageNum);
                pageRequest.setPageSize(pageSize);
                List<SearchAssociationalWordVO> searchAssociationalWordVOList = searchAssociationalWordQueryProvider.page(pageRequest).getContext().getSearchAssociationalWordPage().getContent();

                if (CollectionUtils.isEmpty(searchAssociationalWordVOList)) {
                    flg = Boolean.FALSE;
                    log.info("==========ES初始化搜索词库列表，结束pageNum:{}==============", pageNum);
                } else {
                    List<EsSearchAssociationalWord> newInfos = KsBeanUtil.convert(searchAssociationalWordVOList, EsSearchAssociationalWord.class);
                    EsSearchAssociationalWordService.init(newInfos);
                    log.info("==========ES初始化搜索词库列表成功，当前pageNum:{}==============", pageNum);
                    pageNum++;
                }
            }
        } catch (Exception e) {
            log.info("==========ES初始化搜索词库列表异常，异常pageNum:{}==============", pageNum);
            throw new SbcRuntimeException("K-120011", new Object[]{pageNum});
        }

        return BaseResponse.SUCCESSFUL();
    }

}
