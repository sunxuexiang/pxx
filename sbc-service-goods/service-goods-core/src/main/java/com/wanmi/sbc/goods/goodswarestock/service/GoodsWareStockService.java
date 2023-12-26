package com.wanmi.sbc.goods.goodswarestock.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.IteratorUtils;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.goods.api.request.goodswarestock.*;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageQueryRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockInitListResponse;
import com.wanmi.sbc.goods.bean.dto.*;
import com.wanmi.sbc.goods.bean.enums.GoodsWareStockImportType;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockGroupVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockPageVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.goods.bean.vo.ShortagesGoodsInfoVO;
import com.wanmi.sbc.goods.brand.model.root.ContractBrand;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.ContractBrandRepository;
import com.wanmi.sbc.goods.brand.request.ContractBrandQueryRequest;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.repository.GoodsWareStockRepository;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import com.wanmi.sbc.goods.goodswarestockdetail.service.GoodsWareStockDetailService;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.request.GoodsInfoSaveRequest;
import com.wanmi.sbc.goods.info.request.GoodsSaveRequest;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
import com.wanmi.sbc.goods.stockoutdetail.service.StockoutDetailService;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import com.wanmi.sbc.goods.stockoutmanage.service.StockoutManageService;
import com.wanmi.sbc.goods.storecate.model.root.StoreCate;
import com.wanmi.sbc.goods.storecate.repository.StoreCateRepository;
import com.wanmi.sbc.goods.storecate.request.StoreCateQueryRequest;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import com.wanmi.sbc.goods.warehouse.service.WareHouseWhereCriteriaBuilder;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import sun.misc.BASE64Encoder;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>sku分仓库存表业务逻辑</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@Service("GoodsWareStockService")
@Slf4j
public class GoodsWareStockService {

    @Value("classpath:supplier_goods_stock_template.xls")
    private Resource templateFile;

    @Autowired
    private GoodsWareStockRepository goodsWareStockRepository;

    @Autowired
    private GoodsWareStockDetailService goodsWareStockDetailService;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ContractBrandRepository contractBrandRepository;

    @Autowired
    private StoreCateRepository storeCateRepository;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GoodsInfoService goodsInfoService;

    @Autowired
    private StockoutManageService stockoutManageService;

    @Autowired
    private StockoutDetailService stockoutDetailService;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 新增sku分仓库存表
     *
     * @author zhangwenchang
     */
    @Transactional
    public GoodsWareStock add(GoodsWareStock entity) {
        goodsWareStockRepository.save(entity);
        return entity;
    }

    /**
     * 批量新增sku分仓库存表
     *
     * @author zhangwenchang
     */
    @LcnTransaction
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void addList(GoodsWareStockAddListRequest goodsWareStockAddListRequest) {
        List<GoodsWareStock> goodsWareStockList = new ArrayList<>();
        List<String> goodsInfoWareIdList = new ArrayList<>();
//        List<GoodsWareStockDetail> goodsWareStockDetailList = new ArrayList<>();
        goodsWareStockAddListRequest.getGoodsWareStockAddRequestList().forEach(goodsWareStockAddRequest -> {
            GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockAddRequest, GoodsWareStock.class);
            goodsWareStock.setGoodsInfoWareId(goodsWareStock.getGoodsInfoId() + "_" + goodsWareStock.getWareId());
            goodsWareStock.setUpdateTime(LocalDateTime.now());
            goodsWareStock.setUpdateTime(LocalDateTime.now());
            goodsWareStock.setSaleType(SaleType.WHOLESALE.toValue());
            goodsWareStock.setAddStep(goodsWareStock.getAddStep());
            //子物料相对主物料换算关系 = 主物料步长/子物料步长
        //    goodsWareStock.setMainAddStep(goodsWareStock.getAddStep().divide(goodsWareStock.getAddStep(),BigDecimal.ROUND_HALF_UP));
//            goodsWareStockAddRequest.getGoodsWareStockDetailAddRequestList().forEach(goodsWareStockDetailAddRequest -> {
//                GoodsWareStockDetail goodsWareStockDetail = KsBeanUtil.convert(goodsWareStockDetailAddRequest,
//                        GoodsWareStockDetail.class);
//                goodsWareStockDetail.setGoodsInfoId(goodsWareStock.getGoodsInfoId());
//                goodsWareStockDetail.setGoodsInfoNo(goodsWareStock.getGoodsInfoNo());
//                goodsWareStockDetailList.add(goodsWareStockDetail);
//            });
            goodsWareStockList.add(goodsWareStock);
            goodsInfoWareIdList.add(goodsWareStock.getGoodsInfoWareId());
        });
        //先将原有数据删除
        goodsWareStockRepository.deleteByGoodsInfoWareIdList(goodsInfoWareIdList);
        //将新的数据插入
        goodsWareStockRepository.saveAll(goodsWareStockList);
        //插入商品库存明细数据记录
//        goodsWareStockDetailService.addList(goodsWareStockDetailList);
    }

    /**
     * 修改sku分仓库存表
     *
     * @author zhangwenchang
     */
    @Transactional
    public GoodsWareStock modify(GoodsWareStock entity) {
        goodsWareStockRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除sku分仓库存表
     *
     * @author zhangwenchang
     */
    @Transactional
    public void deleteById(GoodsWareStock entity) {
        goodsWareStockRepository.save(entity);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: 批量更新库存中间表并新增记录
     * @Return: void
     * @Author: yxb
     * @Date: 2020/6/1 16:57
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateList(GoodsWareStockUpdateListRequest goodsWareStockAddListRequest) {
        List<GoodsWareStock> goodsWareStockList = new ArrayList<>(20);
        List<GoodsWareStock> oldList = new ArrayList<>(20);
        goodsWareStockAddListRequest.getGoodsWareStockAddRequestList().forEach(goodsWareStockAddRequest -> {
            GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockAddRequest, GoodsWareStock.class);
            goodsWareStockList.add(goodsWareStock);
        });
        goodsWareStockAddListRequest.getGoodsWareStockOldRequestList().forEach(goodsWareStockAddRequest -> {
            GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockAddRequest, GoodsWareStock.class);
            oldList.add(goodsWareStock);
        });
        goodsWareStockRepository.saveAll(goodsWareStockList);
        goodsWareStockList.forEach(param->{
            param.setCreatePerson(null);
            param.setUpdatePerson(null);
        });
        //新增记录
        saveGoodsWareStockDetail(goodsWareStockList,oldList,GoodsWareStockImportType.IMPORT);
    }

    /**
     * 批量删除sku分仓库存表saveGoodsWareStockDetail
     *
     * @author zhangwenchang
     */
    @Transactional
    public void deleteByIdList(List<GoodsWareStock> infos) {
        goodsWareStockRepository.saveAll(infos);
    }

    /**
     * 单个查询sku分仓库存表
     *
     * @author zhangwenchang
     */
    public GoodsWareStock getOne(Long id, Long storeId) {
        return goodsWareStockRepository.findByIdAndStoreIdAndDelFlag(id, storeId, DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "sku分仓库存表不存在"));
    }

    /**
     * 分页查询sku分仓库存表
     *
     * @author zhangwenchang
     */
    public Page<GoodsWareStockPageVO> page(GoodsWareStockSqlWhereCriteriaBuilder queryReq) {

        Query query =
                entityManager.createNativeQuery(queryReq.getQueryResultInfo().concat(queryReq.getTableJoinInfo()).concat(queryReq.getGroupByInfo()));
        query.setFirstResult(queryReq.getPageNum() * queryReq.getPageSize());
        query.setMaxResults(queryReq.getPageSize());
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        // 查询商品库存列表
        List<GoodsWareStockPageVO> goodsWareStockList =
                GoodsWareStockSqlWhereCriteriaBuilder.converter(query.getResultList());

        //查询商品库存总数
        Query totalCountRes = entityManager.createNativeQuery(queryReq.getQueryTotalInfo()
                .concat(queryReq.getQueryResultInfo())
                .concat(queryReq.getTableJoinInfo())
                .concat(queryReq.getGroupByInfo())
                .concat(queryReq.getQueryTotalInfoEnd()));
        long totalCount = Long.parseLong(totalCountRes.getSingleResult().toString());
        return new PageImpl<GoodsWareStockPageVO>(goodsWareStockList, queryReq.getPageable(), totalCount);
    }

    /**
     * 列表查询sku分仓库存表
     *
     * @author zhangwenchang
     */
    public List<GoodsWareStock> list(GoodsWareStockQueryRequest queryReq) {
        List<GoodsWareStock> stockList = goodsWareStockRepository.findAll(GoodsWareStockWhereCriteriaBuilder.build(queryReq));
        dealStockListByParentId(stockList);
        return stockList;
    }


    /**
     * 分页推送到货通知信息
     */
    @Transactional
    public Page<StockoutManage> checkGoodsArrival(StockoutManageQueryRequest page) {
        Page<StockoutManage> stockoutManagePage = stockoutManageService.page(page);
        List<String> goodsIdList = stockoutManagePage.getContent().stream().map(StockoutManage::getGoodsInfoId).distinct().collect(Collectors.toList());
        List<GoodsWareStock> all = goodsWareStockRepository.findAll(GoodsWareStockWhereCriteriaBuilder.build(GoodsWareStockQueryRequest.builder()
                .goodsInfoIds(goodsIdList).delFlag(DeleteFlag.NO).build()));

        List<StockoutDetail> stockoutDetailList=new ArrayList<>(100);
        List<String> stockOut=new ArrayList<>(100);
        for (StockoutManage inner:stockoutManagePage.getContent()){
            Optional<GoodsWareStock> first = all.stream().filter(param -> (param.getWareId().equals(inner.getWareId())
                    && param.getGoodsInfoId().equals(inner.getGoodsInfoId()))).findFirst();
            if (first.isPresent()){
                if (first.get().getStock().compareTo(BigDecimal.ZERO) >0){
                    stockoutDetailList.addAll(inner.getStockoutDetailList());
                    stockOut.add(inner.getStockoutId());
                }
            }
        }
       if (CollectionUtils.isNotEmpty(stockOut)){
           List<String> customerIds = stockoutDetailList.stream().map(StockoutDetail::getCustomerId).distinct().collect(Collectors.toList());
           // TODO: 2020/7/31 推送短信
           //查询会员手机号
           List<CustomerDetailVO> customerDetailVOList = customerDetailQueryProvider.listCustomerDetailByCondition(CustomerDetailListByConditionRequest
                   .builder().customerIds(customerIds).build()).getContext().getCustomerDetailVOList();
           List<String> phone = customerDetailVOList.stream().map(CustomerDetailVO::getContactPhone).distinct().collect(Collectors.toList());



           stockoutManageService.upddateFlagByIdList(stockOut);
           stockoutDetailService.deleteByStockOutId(stockOut);
        }
        return stockoutManagePage;
    }

    /**会员自提订单发货回传短信模板
     * 查看某一仓库存在库存的商品数量
     *
     * @author zhangwenchang
     */
    public Long countStockGoodsByWareId(long wareId, BigDecimal stock) {
        return goodsWareStockRepository.countByWareIdAndStockGreaterThanAndDelFlag(wareId, stock, DeleteFlag.NO);
    }

    /**
     * 根据区域ids，查询商品库存
     */
    public List<GoodsWareStock> getGoodsStockByAreaIds(GoodsWareStockByAreaIdsRequest request) {
        Long provinceId = request.getProvinceId();
        Long cityId = request.getCityId();
        if (Objects.isNull(provinceId)) {
            return goodsWareStockRepository.findGoodsDefaultStock(request.getStoreId(),
                    request.getGoodsInfoIds());
        }
        List<Object> results =
                goodsWareStockRepository.getGoodsStockByAreaIdsAndGoodsInfoIds(request.getStoreId(),
                        request.getGoodsInfoIds(), provinceId, cityId);
        return resultToGoodsWareStockList(results);
    }

    /**
     * @return java.util.List<com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock>
     * @Author lvzhenwei
     * @Description 根据地区信息和商品idList查询对应商品库存信息
     * @Date 13:39 2020/4/15
     * @Param []
     **/
    public List<GoodsWareStock> getGoodsStockByAreaIdsAndGoodsInfoIds(StockQueryByAresAndInfoIdListRequest request) {
        List<Object> results =
                goodsWareStockRepository.getGoodsStockByAreaIdsAndGoodsInfoIds(request.getStoreId(),
                        request.getGoodsInfoList(), request.getProvinceId(), request.getCityId());
        return resultToGoodsWareStockList(results);
    }


    /**
     * @return
     * @Author lvzhenwei
     * @Description 根据地区信息和skuIdList查询对应商品库存信息
     * @Date 13:39 2020/4/15
     * @Param []
     **/
    public List<GoodsWareStock> getGoodsStockByAreaIdAndGoodsInfoIds(List<String> goodsInfoIds, Long wareId) {
        List<Object> results =
                goodsWareStockRepository.getGoodsStockByAreaIdAndGoodsInfoIds(wareId,goodsInfoIds);
        return resultToGoodsWareStockList(results);
    }

    public List<GoodsWareStock> getGoodsStockByWareIdAndStoreId(GoodsWareStockByWareIdAndStoreIdRequest goodsWareStockByWareIdAndStoreIdRequest){
        List<Object> results = goodsWareStockRepository.getGoodsStockByWareIdAndStoreId(goodsWareStockByWareIdAndStoreIdRequest.getWareId()
                        ,goodsWareStockByWareIdAndStoreIdRequest.getGoodsForIdList(),goodsWareStockByWareIdAndStoreIdRequest.getStoreId());
        return resultToGoodsWareStockList(results);
    }


    private List<GoodsWareStock> resultToGoodsWareStockList(List<Object> results) {
        return results.stream().map(item -> {
            GoodsWareStock goodsWareStock = new GoodsWareStock();
            goodsWareStock.setGoodsInfoNo((String) ((Object[]) item)[0]);
            goodsWareStock.setGoodsInfoId((String) ((Object[]) item)[1]);
            goodsWareStock.setStock(((BigDecimal) ((Object[]) item)[2]));
            return goodsWareStock;
        }).collect(Collectors.toList());
    }

    /**
     * 根据分仓id删除分仓库存表、分仓库存明细表
     *
     * @author huapeiliang
     */
    @Transactional
    public void deleteByWareId(long wareId) {
        List<GoodsWareStock> goodsWareStock = goodsWareStockRepository.findAllByWareIdAndDelFlag(wareId, DeleteFlag.NO);
        if (!ObjectUtils.isEmpty(goodsWareStock)) {
            List<Long> goodsWareStockIdList =
                    goodsWareStock.stream().map(entity -> entity.getId()).collect(Collectors.toList());
            goodsWareStockDetailService.deleteByGoodsWareStockIdList(goodsWareStockIdList);
        }
        goodsWareStockRepository.deleteByWareId(wareId);
    }

    /**
     * @return java.util.List<com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock>
     * @Author lvzhenwei
     * @Description 根据商品goodsInfoIdList查询对应的商品库存数量
     * @Date 18:50 2020/4/17
     * @Param [request]
     **/
    public List<GoodsWareStock> getGoodsWareStockByGoodsInfoIds(GoodsWareStockByGoodsForIdsRequest request) {
        List<GoodsWareStock> returnList =null;
        List<GoodsWareStock> goodsWareStockList=null;
        if(Objects.nonNull(request.getWareId()) && request.getWareId() > 0){
            goodsWareStockList = getListByWareIdAndInfoIdList(request.getGoodsForIdList(),request.getWareId());
        }else{
            goodsWareStockList = goodsWareStockRepository.findListByGoodsInfoIds(request.getGoodsForIdList());
        }
        if(CollectionUtils.isEmpty(goodsWareStockList)){
            returnList =new ArrayList<>(1);
        }else{
            dealStockListByParentId(goodsWareStockList);
            returnList = new ArrayList<>(goodsWareStockList.size());
            Map<String,BigDecimal> infoIdStockMap = new HashMap<>(goodsWareStockList.size());
            Map<String,BigDecimal> infoIdSelfStockMap = new HashMap<>(goodsWareStockList.size());
            Map<String,BigDecimal> infoIdLockStockMap = new HashMap<>(goodsWareStockList.size());

            for(GoodsWareStock goodsWareStock:goodsWareStockList){
                BigDecimal stockValue = infoIdStockMap.get(goodsWareStock.getGoodsInfoId());
                BigDecimal selfStockValue = infoIdSelfStockMap.get(goodsWareStock.getGoodsInfoId());
                BigDecimal lockStockValue = infoIdLockStockMap.get(goodsWareStock.getGoodsInfoId());
                if(null==stockValue){
                    stockValue = goodsWareStock.getStock();
                    selfStockValue = Objects.isNull(goodsWareStock.getSelfStock()) ? BigDecimal.ZERO : goodsWareStock.getSelfStock();
                    lockStockValue = Objects.isNull(goodsWareStock.getLockStock()) ? BigDecimal.ZERO : goodsWareStock.getLockStock();
                }else{
                    stockValue = goodsWareStock.getStock().add(stockValue);
                    selfStockValue=selfStockValue.add(Objects.isNull(goodsWareStock.getSelfStock()) ? BigDecimal.ZERO : goodsWareStock.getSelfStock());
                    lockStockValue=lockStockValue.add(Objects.isNull(goodsWareStock.getLockStock()) ? BigDecimal.ZERO : goodsWareStock.getLockStock());
                }
                infoIdStockMap.put(goodsWareStock.getGoodsInfoId(),stockValue);
                infoIdSelfStockMap.put(goodsWareStock.getGoodsInfoId(),selfStockValue);
                infoIdLockStockMap.put(goodsWareStock.getGoodsInfoId(),lockStockValue);
            }
            for(Map.Entry<String,BigDecimal> entry:infoIdStockMap.entrySet()) {
                GoodsWareStock goodsWareStock = new GoodsWareStock();
                goodsWareStock.setGoodsInfoId(entry.getKey());
                goodsWareStock.setStock(entry.getValue().subtract(infoIdLockStockMap.get(entry.getKey())));
                goodsWareStock.setSelfStock(infoIdSelfStockMap.get(entry.getKey()));
                goodsWareStock.setLockStock(infoIdLockStockMap.get(entry.getKey()));
                returnList.add(goodsWareStock);
            }
        }
        return returnList;
    }

    public List<GoodsWareStock> getListByWareIdAndInfoIdList(List<String> infoIdList,Long wareId) {
        List<GoodsWareStock> goodsWareStockList;
        goodsWareStockList = goodsWareStockRepository.findListByGIdsAndWareId(infoIdList, wareId);
        return goodsWareStockList;
    }


    /**
     * @return java.util.List<com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock>
     * @Author lvzhenwei
     * @Description 根据商品goodsInfoIdList查询对应的商品库存数量
     * @Date 18:50 2020/4/17
     * @Param [request]
     **/
    public List<GoodsWareStock> findByGoodsInfoNoIn(GoodsWareStockByGoodsForNoRequest request) {
      return goodsWareStockRepository.findGoodsDefaultStockByNo(request.getWareId(),request.getGoodsForIdList());
    }

    /**
     * @return java.util.List<com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock>
     * @Author lvzhenwei
     * @Description 根据商品goodsInfoIdList查询对应的商品库存数量
     * @Date 18:50 2020/4/17
     * @Param [request]
     **/
    public List<GoodsWareStock> findByGoodsInfoIdIn(List<String> goodsInfoIds) {
        List<GoodsWareStock> goodsWareStockList =goodsWareStockRepository.findByGoodsInfoIdIn(goodsInfoIds);
        dealStockListByParentId(goodsWareStockList);
        return goodsWareStockList;
    }

    /**
     * 保存分仓库存
     *
     * @param goodsSaveRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public List<GoodsWareStock> saveGoodsWareStock(GoodsSaveRequest goodsSaveRequest, List<GoodsInfo> allGoodsInfos) {
        String updatePerson = goodsSaveRequest.getUpdatePerson();
        LocalDateTime now = LocalDateTime.now();
        String goodsId = goodsSaveRequest.getGoods().getGoodsId();
        Long storeId = goodsSaveRequest.getStoreId();

        List<GoodsWareStock> oldrecord = goodsWareStockRepository.findAllByGoodsIdAndDelFlag(goodsId, DeleteFlag.NO);
        //1.删除老的数据
        if (StringUtils.isNotBlank(goodsId)) {
            goodsWareStockRepository.deleteAllByGoodsId(goodsId);
        } else {
            goodsId = allGoodsInfos.get(0).getGoodsId();
        }
        //2.保存新的数据
        List<GoodsWareStock> wareStockList = new ArrayList<>();
        List<GoodsWareStockGroupDTO> wareStockRequest = goodsSaveRequest.getWareStocks();
        String finalGoodsId = goodsId;
        allGoodsInfos.stream().forEach(goodsInfo -> {
            wareStockList.addAll(wareStockRequest.stream()
                    .filter(goodsWareStockGroupDTO -> goodsWareStockGroupDTO.getGoodsInfoNo().equals(goodsInfo.getGoodsInfoNo()))
                    .findFirst().get().getGoodsWareStockVOList().stream().map(goodsWareStockDTO -> {
                        GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockDTO, GoodsWareStock.class);
                        goodsWareStock.setDelFlag(DeleteFlag.NO);
                        goodsWareStock.setCreateTime(now);
                        goodsWareStock.setCreatePerson(updatePerson);
                        goodsWareStock.setGoodsId(finalGoodsId);
                        goodsWareStock.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                        goodsWareStock.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
                        goodsWareStock.setStoreId(storeId);
                        goodsWareStock.setGoodsInfoWareId(goodsInfo.getGoodsInfoId() + "_" + goodsWareStock.getWareId());
                        goodsWareStock.setStock(Objects.isNull(goodsWareStock.getStock()) ? BigDecimal.ZERO :
                                goodsWareStock.getStock());
                        return goodsWareStock;
                    }).collect(Collectors.toList()));
        });
        goodsWareStockRepository.saveAll(wareStockList);

        //保存变化记录
        saveGoodsWareStockDetail(wareStockList, oldrecord,GoodsWareStockImportType.EDIT);
        return wareStockList;
    }


    /**
     * 保存分仓库存
     *
     * @param goodsInfoSaveRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGoodsInfoWareStock(GoodsInfoSaveRequest goodsInfoSaveRequest, GoodsInfo goodsInfo) {
        String updatePerson = goodsInfoSaveRequest.getUpdatePerson();
        LocalDateTime now = LocalDateTime.now();
        String goodsId = goodsInfo.getGoodsId();
        String goodsInfoId = goodsInfo.getGoodsInfoId();
        String goodsInfoNo = goodsInfo.getGoodsInfoNo();
        Long storeId = goodsInfo.getStoreId();

        List<GoodsWareStock> oldrecord = goodsWareStockRepository.findAllByGoodsInfoIdAndDelFlag(goodsInfoId,
                DeleteFlag.NO);
        //1.删除老的数据
        goodsWareStockRepository.deleteAllByGoodsInfoId(goodsInfoId);

        //2.保存新的数据
        List<GoodsWareStock> wareStockList = new ArrayList<>();
        List<GoodsWareStockDTO> wareStockRequest = goodsInfoSaveRequest.getGoodsWareStocks();
        String finalGoodsId = goodsId;
        wareStockList.addAll(wareStockRequest.stream().map(goodsWareStockDTO -> {
            GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockDTO, GoodsWareStock.class);
            goodsWareStock.setDelFlag(DeleteFlag.NO);
            goodsWareStock.setCreateTime(now);
            goodsWareStock.setCreatePerson(updatePerson);
            goodsWareStock.setGoodsId(finalGoodsId);
            goodsWareStock.setGoodsInfoId(goodsInfoId);
            goodsWareStock.setGoodsInfoNo(goodsInfoNo);
            goodsWareStock.setStoreId(storeId);
            goodsWareStock.setGoodsInfoWareId(goodsInfoId + goodsWareStock.getWareId());
            goodsWareStock.setStock(Objects.isNull(goodsWareStock.getStock()) ? BigDecimal.ZERO :
                    goodsWareStock.getStock());
            return goodsWareStock;
        }).collect(Collectors.toList()));
        goodsWareStockRepository.saveAll(wareStockList);
        //保存变化记录
        saveGoodsWareStockDetail(wareStockList, oldrecord,GoodsWareStockImportType.EDIT);
    }

    /**
     * 列表查询sku分仓库存表
     *
     * @author zhangwenchang
     */
    public GoodsWareStockInitListResponse initList(GoodsWareStockQueryRequest queryReq) {
        GoodsWareStockInitListResponse goodsWareStockInitListResponse = new GoodsWareStockInitListResponse();
        //查询所有仓库
        List<WareHouse> wareHouseList =
                wareHouseService.list(WareHouseQueryRequest.builder().delFlag(DeleteFlag.NO).storeId(queryReq.getStoreId()).build());
        //仓库为空
        if (CollectionUtils.isEmpty(wareHouseList)) {
            return goodsWareStockInitListResponse;
        }
        if (StringUtils.isBlank(queryReq.getGoodsId()) && StringUtils.isBlank(queryReq.getGoodsInfoId())) {
            //未编辑过仓库库存，即新增sku、spu
            GoodsWareStockGroupVO goodsWareStockGroupVO = new GoodsWareStockGroupVO();
            goodsWareStockGroupVO.setGoodsWareStockVOList(KsBeanUtil.convertList(wareHouseList,
                    GoodsWareStockVO.class));
            goodsWareStockInitListResponse.setGoodsWareStockGroupVOList(Arrays.asList(goodsWareStockGroupVO));
        } else {
            //编辑单个sku
            if (StringUtils.isNotBlank(queryReq.getGoodsInfoId())) {
                goodsWareStockInitListResponse = this.initGoodsInfoWareHouses(queryReq, wareHouseList);
            } else if (StringUtils.isNotBlank(queryReq.getGoodsId())) {
                //编辑spu
                goodsWareStockInitListResponse = this.initGoodsWareHouses(queryReq, wareHouseList);
            }
        }
        return goodsWareStockInitListResponse;
    }

    /**
     * 获取单个sku初始化仓库数据
     *
     * @param queryReq
     * @param wareHouseList
     * @return
     */
    public GoodsWareStockInitListResponse initGoodsInfoWareHouses(GoodsWareStockQueryRequest queryReq,
                                                                  List<WareHouse> wareHouseList) {
        GoodsWareStockInitListResponse goodsWareStockInitListResponse = new GoodsWareStockInitListResponse();
        //查询sku信息
        GoodsInfo goodsInfo = goodsInfoService.findOne(queryReq.getGoodsInfoId());
        if (Objects.isNull(goodsInfo)) {
            return goodsWareStockInitListResponse;
        }
        GoodsWareStockGroupVO goodsWareStockGroupVO = new GoodsWareStockGroupVO();
        goodsWareStockGroupVO.setGoodsInfoId(queryReq.getGoodsInfoId());
        goodsWareStockGroupVO.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
        //查询已存在的分仓库存数据
        List<GoodsWareStock> goodsWareStockList =
                goodsWareStockRepository.findAll(GoodsWareStockWhereCriteriaBuilder.build(queryReq));

        List<Long> wareIds =
                goodsWareStockList.stream().map(GoodsWareStock::getWareId).collect(Collectors.toList());
        //补充新增的仓库
        List<WareHouse> wareHouseListNot = wareHouseList.stream()
                .filter(w -> !wareIds.contains(w.getWareId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(wareHouseListNot)) {
            goodsWareStockList.addAll(KsBeanUtil.convertList(wareHouseListNot,
                    GoodsWareStock.class));
        }

        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(goodsWareStockList)) {
            goodsWareStockVOList = KsBeanUtil.convertList(goodsWareStockList,
                    GoodsWareStockVO.class);
            IteratorUtils.zip(goodsWareStockVOList, wareHouseList,
                    (collect1, levels1) -> collect1.getWareId().equals(levels1.getWareId()),
                    (collect2, levels2) -> {
                        collect2.setWareName(levels2.getWareName());
                    }
            );
        }
        //过滤掉仓库名不存在的数据
        goodsWareStockVOList =
                goodsWareStockVOList.stream().filter(goodsWareStockVO ->
                        StringUtils.isNotBlank(goodsWareStockVO.getWareName())).collect(Collectors.toList());
        goodsWareStockGroupVO.setGoodsWareStockVOList(goodsWareStockVOList);
        goodsWareStockInitListResponse.setGoodsWareStockGroupVOList(Arrays.asList(goodsWareStockGroupVO));
        return goodsWareStockInitListResponse;
    }

    /**
     * 获取spu下所有sku仓库初始化数据
     *
     * @param queryReq
     * @param wareHouseList
     * @return
     */
    public GoodsWareStockInitListResponse initGoodsWareHouses(GoodsWareStockQueryRequest queryReq,
                                                              List<WareHouse> wareHouseList) {
        GoodsWareStockInitListResponse goodsWareStockInitListResponse = new GoodsWareStockInitListResponse();
        List<GoodsInfo> goodsInfoList = goodsInfoService.queryBygoodsId(queryReq.getGoodsId());
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            return goodsWareStockInitListResponse;
        }
        //查询已存在的分仓库存数据
        List<GoodsWareStock> goodsWareStockList =
                goodsWareStockRepository.findAll(GoodsWareStockWhereCriteriaBuilder.build(queryReq));
        if (CollectionUtils.isNotEmpty(goodsWareStockList)) {
            List<GoodsWareStockGroupVO> goodsWareStockGroupVOList = new ArrayList<>();
            //根据skuID分组
            Map<String, List<GoodsWareStock>> groupMap = IteratorUtils.groupBy(goodsWareStockList,
                    GoodsWareStock::getGoodsInfoNo);
            List<GoodsInfo> goodsInfos =
                    goodsInfoList.stream().filter(goodsInfo -> !groupMap.containsKey(goodsInfo.getGoodsInfoNo())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(goodsInfos)) {
                goodsInfos.stream().forEach(goodsInfo -> {
                    groupMap.put(goodsInfo.getGoodsInfoNo(), new ArrayList<GoodsWareStock>());
                });
            }
            //包装返回数据，并与仓库交叉对比，补充未编辑过的仓库
            for (Map.Entry<String, List<GoodsWareStock>> m : groupMap.entrySet()) {
                GoodsWareStockGroupVO goodsWareStockGroupVO = new GoodsWareStockGroupVO();
                List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
                goodsWareStockGroupVO.setGoodsInfoNo(m.getKey());
                goodsWareStockVOList.addAll(m.getValue().stream().map(entity -> wrapperVo(entity)).collect(Collectors.toList()));
                if (wareHouseList.size() != m.getValue().size()) {
                    List<Long> wareIds =
                            m.getValue().stream().map(GoodsWareStock::getWareId).collect(Collectors.toList());
                    goodsWareStockVOList.addAll(KsBeanUtil.convertList(wareHouseList.stream()
                                    .filter(w -> !wareIds.contains(w.getWareId())).collect(Collectors.toList()),
                            GoodsWareStockVO.class));
                }
                IteratorUtils.zip(goodsWareStockVOList, wareHouseList,
                        (collect1, levels1) -> collect1.getWareId().equals(levels1.getWareId()),
                        (collect2, levels2) -> {
                            collect2.setWareName(levels2.getWareName());
                        }
                );
                //过滤掉仓库名不存在的数据
                goodsWareStockVOList =
                        goodsWareStockVOList.stream().filter(goodsWareStockVO ->
                                StringUtils.isNotBlank(goodsWareStockVO.getWareName())).collect(Collectors.toList());
                goodsWareStockGroupVO.setGoodsWareStockVOList(goodsWareStockVOList);
                goodsWareStockGroupVOList.add(goodsWareStockGroupVO);
            }
            goodsWareStockInitListResponse.setGoodsWareStockGroupVOList(goodsWareStockGroupVOList);
        } else {
            List<GoodsWareStockGroupVO> goodsWareStockGroupVOList = new ArrayList<>();
            goodsInfoList.stream().forEach(goodsInfo -> {
                GoodsWareStockGroupVO goodsWareStockGroupVO = new GoodsWareStockGroupVO();
                goodsWareStockGroupVO.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                goodsWareStockGroupVO.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
                goodsWareStockGroupVO.setGoodsWareStockVOList(KsBeanUtil.convertList(wareHouseList,
                        GoodsWareStockVO.class));
                IteratorUtils.zip(goodsWareStockGroupVO.getGoodsWareStockVOList(), wareHouseList,
                        (collect1, levels1) -> collect1.getWareId().equals(levels1.getWareId()),
                        (collect2, levels2) -> {
                            collect2.setWareName(levels2.getWareName());
                        }
                );
                goodsWareStockGroupVOList.add(goodsWareStockGroupVO);
            });
            goodsWareStockInitListResponse.setGoodsWareStockGroupVOList(goodsWareStockGroupVOList);
        }
        return goodsWareStockInitListResponse;
    }

    /**
     * 保存库存变化记录
     *
     * @param goodsWareStocks
     * @param oldRecord
     */
    @Transactional
    public void saveGoodsWareStockDetail(List<GoodsWareStock> goodsWareStocks, List<GoodsWareStock> oldRecord,GoodsWareStockImportType type) {
        LocalDateTime now = LocalDateTime.now();
        List<GoodsWareStockDetail> goodsWareStockDetails = goodsWareStocks.stream().map(goodsWareStock -> {
            GoodsWareStockDetail goodsWareStockDetail = KsBeanUtil.convert(goodsWareStock, GoodsWareStockDetail.class);
            goodsWareStockDetail.setDelFlag(DeleteFlag.NO);
            goodsWareStockDetail.setCreateTime(now);
            goodsWareStockDetail.setImportType(type);
            Optional<GoodsWareStock> exist =
                    oldRecord.stream().filter(gs -> gs.getWareId().equals(goodsWareStock.getWareId())
                            && gs.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).findFirst();
            if (exist.isPresent()) {
                goodsWareStockDetail.setOperateStock(goodsWareStock.getStock().subtract(exist.get().getStock()).setScale(0,BigDecimal.ROUND_DOWN).longValue());
                goodsWareStockDetail.setGoodsWareStockId(goodsWareStock.getId());
            } else {
                goodsWareStockDetail.setOperateStock(0L);
            }
            return goodsWareStockDetail;
        }).collect(Collectors.toList());
        List<GoodsWareStockDetail> goodsWareStockDetailList =
                goodsWareStockDetails.stream().filter(goodsWareStockDetail ->
                        !goodsWareStockDetail.getOperateStock().equals(0L)).collect(Collectors.toList());
        goodsWareStockDetailList.forEach(param->{
            param.setStockImportNo(new StringBuilder("IM").append(RandomStringUtils.randomNumeric(6)).toString());
        });
        if (CollectionUtils.isNotEmpty(goodsWareStockDetailList)) {
            goodsWareStockDetailService.addList(goodsWareStockDetailList);
        }
    }

    public void deleteByGoodsIds(List<String> goodsIds) {
        goodsWareStockRepository.deleteByGoodsIds(goodsIds);
    }

    /**
     * 批量删除sku的库存
     * @param goodsIds
     */
    public void deleteByGoodsInfoIds(List<String> goodsIds) {
        goodsWareStockRepository.deleteByGoodsIds(goodsIds);
    }

    /**
     * 导出模板
     * 加载xls已有模板，填充商品分类、品牌数据，实现excel下拉列表
     *
     * @return base64位文件字符串
     */
    public String exportTemplate(Long storeId) {
//		Resource file = type == NumberUtils.INTEGER_ZERO ? templateFile : templateFileIEP;
        Resource file = templateFile;
        if (file == null || !file.exists()) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }

        //根据店铺获取品牌
        ContractBrandQueryRequest contractBrandQueryRequest = new ContractBrandQueryRequest();
        contractBrandQueryRequest.setStoreId(storeId);
        List<GoodsBrand> brands = contractBrandRepository.findAll(contractBrandQueryRequest.getWhereCriteria()).stream()
                .filter(contractBrand -> contractBrand.getGoodsBrand() != null && StringUtils.isNotBlank(contractBrand.getGoodsBrand().getBrandName()))
                .map(ContractBrand::getGoodsBrand).collect(Collectors.toList());

        //根据店铺获取店铺分类
        StoreCateQueryRequest queryRequest = new StoreCateQueryRequest();
        queryRequest.setStoreId(storeId);
        queryRequest.setDelFlag(DeleteFlag.NO);
        List<StoreCate> storeCates = storeCateRepository.findAll(queryRequest.getWhereCriteria())
                .stream().filter(storeCate -> storeCate.getCateGrade() == 3).collect(Collectors.toList());

        //获取仓库信息
        List<WareHouse> wareHouseList =
                wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(WareHouseQueryRequest.builder().delFlag(DeleteFlag.NO).storeId(storeId).build()));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InputStream is = file.getInputStream();
             Workbook wk = WorkbookFactory.create(is)) {

            //填放分类数据
            Sheet storeCateSheet = wk.getSheetAt(3);
            int storeCateSize = storeCates.size();
            for (int i = 0; i < storeCateSize; i++) {
                StoreCate cate = storeCates.get(i);
                // 查询店铺分类所有父分类名称
//                String storeCateName = queryStoreCate(cate.getStoreCateId());
                storeCateSheet.createRow(i).createCell(0).setCellValue(String.valueOf(cate.getStoreCateId()).concat(
                        "_").concat(cate.getCateName()));
            }
            //品牌
            Sheet brandSheet = wk.getSheetAt(2);
            int brandSize = brands.size();
            for (int i = 0; i < brandSize; i++) {
                GoodsBrand brand = brands.get(i);
                brandSheet.createRow(i).createCell(0).setCellValue(String.valueOf(brand.getBrandId()).concat("_").concat(brand.getBrandName()));
            }

//			仓库信息
            Sheet wareHouseSheet = wk.getSheetAt(1);
            int wareHouseSize = wareHouseList.size();
            for (int i = 0; i < wareHouseSize; i++) {
                WareHouse wareHouse = wareHouseList.get(i);
                wareHouseSheet.createRow(i).createCell(0).setCellValue(String.valueOf(wareHouse.getWareId()).concat(
                        "_").concat(wareHouse.getWareName()));
            }

            wk.write(baos);
            return new BASE64Encoder().encode(baos.toByteArray());
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }

    /**
     * 递归查询所有店铺分类所有父分类名称
     *
     * @param storeCateId
     * @return
     */
    private String queryStoreCate(Long storeCateId) {
        StoreCate storeCate = storeCateRepository.findById(storeCateId).orElse(new StoreCate());
        if (storeCate.getCateParentId() != 0) {
            String queryParentCate = queryStoreCate(storeCate.getCateParentId());
            return queryParentCate + "-" + storeCate.getCateName();
        }
        return storeCate.getCateName();
    }


    /**
     * 将实体包装成VO
     *
     * @author zhangwenchang
     */
    public GoodsWareStockVO wrapperVo(GoodsWareStock goodsWareStock) {
        if (goodsWareStock != null) {
            GoodsWareStockVO goodsWareStockVO = KsBeanUtil.convert(goodsWareStock, GoodsWareStockVO.class);
            goodsWareStockVO.setLockStock(Objects.isNull(goodsWareStock.getLockStock()) ? BigDecimal.ZERO : goodsWareStock.getLockStock());
            goodsWareStockVO.setSelfStock(Objects.isNull(goodsWareStock.getSelfStock()) ? BigDecimal.ZERO : goodsWareStock.getSelfStock());
            return goodsWareStockVO;
        }
        return null;
    }

    /**
     * 根据仓库的Id和skuId获取库存信息
     * @param goodsInfoId
     * @param wareId
     * @return
     */
    public GoodsWareStockVO queryWareStockByWareIdAndGoodsInfoId(String goodsInfoId, Long wareId){
        if(Objects.nonNull(wareId)){
            GoodsWareStock wareStock = getGoodsWareStockDealed(goodsInfoId, wareId);
            return this.wrapperVo(wareStock);
        }
        return null;
    }

    private GoodsWareStock getGoodsWareStockDealed(String goodsInfoId, Long wareId) {
        GoodsWareStock wareStock = getWareStockByWareIdAndInfoId(goodsInfoId, wareId);
        dealStockByParentId(wareStock,null);
        return wareStock;
    }

    public GoodsWareStock getWareStockByWareIdAndInfoId(String goodsInfoId, Long wareId) {
        GoodsWareStock wareStock = goodsWareStockRepository.findTopByGoodsInfoIdAndWareId(goodsInfoId, wareId);
        return wareStock;
    }

    /**
     * 批量扣减库存
     * @param skuList
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void batchSubStock(List<GoodsInfoMinusStockDTO> skuList, Long wareId){
        skuList.stream().forEach(s->{
            goodsWareStockRepository.subStockByWareIdAndGoodsInfoId(s.getStock(),s.getGoodsInfoId(),wareId);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void subStock(GoodsInfoMinusStockDTO dto) {
        goodsWareStockRepository.subStockByWareIdAndGoodsInfoId(dto.getStock(), dto.getGoodsInfoId(), dto.getWareId());
    }

    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void addStock(GoodsInfoPlusStockDTO dto) {
        goodsWareStockRepository.addStockByWareIdAndGoodsInfoId(dto.getStock(), dto.getGoodsInfoId(), dto.getWareId());
    }

    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public int lockStock(GoodsInfoLockStockDTO dto) {
        return goodsWareStockRepository.lockStockByWareIdAndGoodsInfoId(dto.getStock(), dto.getGoodsInfoId(), dto.getWareId());
    }

    @Transactional(rollbackFor = Exception.class)
    public int unlockStock(GoodsInfoUnlockStockDTO dto) {
        return goodsWareStockRepository.unlockStockByWareIdAndGoodsInfoId(dto.getStock(), dto.getGoodsInfoId(), dto.getWareId());
    }

    /**
     * 批量扣减库存
     *
     * @param skuList
     * @param needUpdateLockStock
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    @Retryable(value = { DeadlockLoserDataAccessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 200))
    public void batchSubStockNew(List<GoodsInfoMinusStockDTO> skuList, Long wareId, boolean needUpdateLockStock){
        //        update goods_ware_stock set stock =
        //case   goods_info_id  when  '2c9afcf275acc0580175c41da69b0088'   THEN stock-2
        //                      WHEN  '2c9afcf275acc0580175c41da69b0088'   THEN stock-3 END ,update_time=now()
        //WHERE goods_info_id in ('2c9afcf275acc0580175c14e53b8007e','2c9afcf275acc0580175c41da69b0088') and ware_id = 1
        StopWatch stopWatch = new StopWatch("批量修改sql总时间");
        stopWatch.start("拼装sql");
        StringBuilder stringBuilder = new StringBuilder("update goods_ware_stock  set stock = case goods_info_id");
        StringBuilder stringBuilder1 = new StringBuilder("WHERE goods_info_id in (");
        for (int i = 0 ;i<skuList.size();i++){
            stringBuilder.append(" when ");
            stringBuilder.append("'"+skuList.get(i).getGoodsInfoId()+"'");
            stringBuilder.append(" then ");
            stringBuilder.append("stock-"+skuList.get(i).getStock()+" ");
            if (i+1==skuList.size()){
                stringBuilder.append("end ,");
            }
            stringBuilder1.append("'"+skuList.get(i).getGoodsInfoId()+"'");
            if (i+1!=skuList.size()){
                stringBuilder1.append(",");
            }
        }

        if(needUpdateLockStock){
            StringBuilder stringBuilderLockStock = new StringBuilder(" lock_stock = case goods_info_id");
            for (int i = 0 ;i<skuList.size();i++){
                stringBuilderLockStock.append(" when ");
                stringBuilderLockStock.append("'"+skuList.get(i).getGoodsInfoId()+"'");
                stringBuilderLockStock.append(" then ");
                stringBuilderLockStock.append("lock_stock-"+skuList.get(i).getStock()+" ");
                if (i+1==skuList.size()){
                    stringBuilderLockStock.append("end ,");
                }
            }
            stringBuilder.append(stringBuilderLockStock);
        }

        stringBuilder.append("update_time=now()");
        stringBuilder1.append(") and ware_id = " + wareId);
        stringBuilder.append(stringBuilder1);
        stringBuilder.append(";");

        log.info("最终输入sql"+stringBuilder.toString());
        stopWatch.stop();
        stopWatch.start("去mysql执行");
        int update = jdbcTemplate.update(stringBuilder.toString());
        log.info("要修改结果"+skuList.size());
        log.info("修改结果"+update);
        stopWatch.stop();
        log.info("修改后的执行"+stopWatch.prettyPrint());
//        skuList.stream().forEach(s->{
//            goodsWareStockRepository.subStockByWareIdAndGoodsInfoId(s.getStock(),s.getGoodsInfoId(),wareId);
//        });
    }

    /**
     * 批量增加库存
     * @param stockList
     * @param wareId
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void batchPlusStock(List<GoodsInfoPlusStockDTO> stockList,Long wareId){
        stockList.stream().forEach(s->goodsWareStockRepository.addStockByWareIdAndGoodsInfoId(s.getStock(),s.getGoodsInfoId(),wareId));
    }

    @Transactional
    public void  updateByGoodsInfoId(String goodsInfoId, BigDecimal stock, Long wareId){
        goodsWareStockRepository.updateByGoodsInfoId(goodsInfoId, stock, wareId);
    }

    @Transactional
    public void  updateStockMutilSpeci(String goodsInfoId, Long wareId, BigDecimal addStep, BigDecimal mainAddStep, Integer saleType, String mainSkuId,Long mainSkuWareId,Long parentGoodsWareStockId){
        goodsWareStockRepository.updateStockMutilSpeci(goodsInfoId,  wareId,addStep,mainAddStep,saleType,mainSkuId,mainSkuWareId,parentGoodsWareStockId);
    }

    @Transactional
    public void  updateStockMutilSpeci(GoodsWareStock updateStock){
        goodsWareStockRepository.updateStockMutilSpeci(updateStock.getGoodsInfoId(), updateStock.getWareId(),updateStock.getAddStep(),updateStock.getMainAddStep(),updateStock.getSaleType(),updateStock.getMainSkuId(),updateStock.getMainSkuWareId(),updateStock.getParentGoodsWareStockId());
    }

    public List<GoodsWareStock> findByGoodsIdIn(List<String> goodsIds) {
        List<Object> result = goodsWareStockRepository.findByGoodsIdsIn(goodsIds);
        return result.stream().map(item -> {
            GoodsWareStock goodsWareStock = new GoodsWareStock();
            goodsWareStock.setGoodsId((String) ((Object[]) item)[0]);
            goodsWareStock.setStock(((BigDecimal) ((Object[]) item)[1]));
            return goodsWareStock;
        }).collect(Collectors.toList());
    }

    /**
     * @description  根据Id查值
     * @author  shiy
     * @date    2023/4/6 9:29
     * @params  [java.lang.Long]
     * @return  com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock
    */
    public GoodsWareStock findById(Long id) {
       return goodsWareStockRepository.findById(id).orElse(null);
    }

    /**
     * @description  根据父库存处理
     * @author  shiy
     * @date    2023/4/6 9:56
     * @params  [java.util.List<com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock>]
     * @return  void
    */
    public void dealStockListByParentId(List<GoodsWareStock> goodsWareStockVOList) {
        if(CollectionUtils.isEmpty(goodsWareStockVOList)){
            return;
        }
        LocalDateTime startTime = null;
        GoodsWareStock existSubGoodsWareStock =goodsWareStockVOList.stream().filter(g->null!=g.getParentGoodsWareStockId()).findFirst().orElse(null);
        if(null!=existSubGoodsWareStock) {
            startTime = getMultiSpeciStartTime();
        }
        for(GoodsWareStock goodsWareStock:goodsWareStockVOList) {
            dealStockByParentId(goodsWareStock,startTime);
        }
    }

    private void dealStockByParentId(GoodsWareStock goodsWareStock,LocalDateTime startTime) {
        goodsWareStock.setSelfStock(goodsWareStock.getStock());
        if (null== goodsWareStock.getParentGoodsWareStockId()) {
            return;
        }
        if(null==startTime) {
            startTime = getMultiSpeciStartTime();
        }
        if(startTime.isAfter(DateUtil.parse(DateUtil.format(new Date(),DateUtil.FMT_TIME_1),DateUtil.FMT_TIME_1))){
            return;
        }
        GoodsWareStock parentGoodsWareStock = findById(goodsWareStock.getParentGoodsWareStockId());
        if(null==parentGoodsWareStock){
            return;
        }
        goodsWareStock.setParentStock(parentGoodsWareStock.getStock());
        BigDecimal newStock = goodsWareStock.getStock().add(goodsWareStock.getMainAddStep().multiply(parentGoodsWareStock.getStock()));
        goodsWareStock.setStock(newStock);
    }

    /**
     * @description  多规格启用日期查询
     * @author  shiy
     * @date    2023/5/12 9:27
     * @params  []
     * @return  java.time.LocalDateTime
     */
    private LocalDateTime getMultiSpeciStartTime(){
        LocalDateTime start_dt = LocalDateTime.of(2099,5,17,0,0,0);
        try {
            String contextValue = systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey("order_setting")
                    .configType("order_setting_multi_speci_start_time").delFlag(DeleteFlag.NO).build()).getContext().getSystemConfigVOList().get(0).getContext();
            start_dt = DateUtil.parse(contextValue,DateUtil.FMT_TIME_1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return start_dt;
    }

    public List<Object[]> getInventory() {
        return goodsWareStockRepository.getInventory();
    }

//    private List<GoodsWareStock> resultToGoodsWareStockList(List<Object> results) {
//        return results.stream().map(item -> {
//            GoodsWareStock goodsWareStock = new GoodsWareStock();
//            goodsWareStock.setGoodsInfoNo((String) ((Object[]) item)[0]);
//            goodsWareStock.setGoodsInfoId((String) ((Object[]) item)[1]);
//            goodsWareStock.setStock(((BigInteger) ((Object[]) item)[2]).longValue());
//            return goodsWareStock;
//        }).collect(Collectors.toList());
//    }

    public List<ShortagesGoodsInfoVO> getShortagesGoodsInfos() {

        List<Object[]> shortagesGoodsInfos = goodsWareStockRepository.getShortagesGoodsInfos();

        if(CollectionUtils.isEmpty(shortagesGoodsInfos)){
            return Lists.newArrayList();
        }

        List<ShortagesGoodsInfoVO> result = Lists.newArrayList();

        shortagesGoodsInfos.forEach(g->{
            ShortagesGoodsInfoVO shortagesGoodsInfo = new ShortagesGoodsInfoVO();
            shortagesGoodsInfo.setGoodsInfoId(g[0].toString());
            shortagesGoodsInfo.setGoodsInfoNo(g[1].toString());
            shortagesGoodsInfo.setErpGoodsInfoNo(g[2].toString());
            shortagesGoodsInfo.setGoodsInfoName(g[3].toString());
            shortagesGoodsInfo.setBrandId(Long.parseLong(g[4].toString()));
            shortagesGoodsInfo.setCateId(Long.parseLong(g[5].toString()));
            shortagesGoodsInfo.setCheckTime(DateUtil.parseDayTime(DateUtil.getStartToday()));
            result.add(shortagesGoodsInfo);
        });

        return result;
    }

    public Map<String,BigDecimal> getskusstock(List<String> skuids){
        HashMap<String,BigDecimal> result = new HashMap<>();
        if (CollectionUtils.isEmpty(skuids)){
            return result;
        }

        List<Object> getskusstock = goodsWareStockRepository.getskusstock(skuids);

        getskusstock.forEach(v->{
            result.put((String) ((Object[]) v)[1],(BigDecimal) ((Object[]) v)[0]);
        });
        return result;
    }



    public Map<String,BigDecimal> getskusstockbylingshou(List<String> skuids){
        HashMap<String,BigDecimal> result = new HashMap<>();
        if (CollectionUtils.isEmpty(skuids)){
            return result;
        }
        List<Object> getskusstock = goodsWareStockRepository.getskusstockbylingshou(skuids);
        getskusstock.forEach(v->{
            result.put((String) ((Object[]) v)[1],(BigDecimal) ((Object[]) v)[0]);
        });
        return result;
    }






    public Map<String,BigDecimal> getskusJiYastock(List<String> skuids){
        HashMap<String,BigDecimal> result = new HashMap<>();
        if (CollectionUtils.isEmpty(skuids)){
            return result;
        }

        List<Object> getskusstock = goodsWareStockRepository.getskusJiYastock(skuids);
        //key ： goodsInfoId
        //value ： stock
        getskusstock.forEach(v->{
            result.put((String) ((Object[]) v)[1],(BigDecimal) ((Object[]) v)[0]);
        });
        return result;
    }

}

