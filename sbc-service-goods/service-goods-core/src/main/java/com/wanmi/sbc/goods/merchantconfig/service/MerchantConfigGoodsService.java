package com.wanmi.sbc.goods.merchantconfig.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantRecommendGoodsSortRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.MerchantConfigGoodsQueryRequest;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendGoodsVO;
import com.wanmi.sbc.goods.goodstypeconfig.root.MerchantRecommendType;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.merchantconfig.repository.MerchantConfigGoodsInfoRepository;
import com.wanmi.sbc.goods.merchantconfig.repository.MerchantConfigGoodsRepository;
import com.wanmi.sbc.goods.merchantconfig.root.MerchantRecommendGoods;
import com.wanmi.sbc.goods.merchantconfig.root.RecommendGoodsInfo;
import com.wanmi.sbc.goods.redis.RedisService;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.service.StoreCateGoodsRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

/**
 * <p>商品推荐商品业务逻辑</p>
 *
 * @author sgy
 * @date 2023-06-09 10:53:36
 */
@Service("merchantConfigGoodsService")
public class MerchantConfigGoodsService {

    @Autowired
    private MerchantConfigGoodsRepository goodsRecommendGoodsRepository;
    @Autowired
    private MerchantConfigGoodsInfoRepository merchantConfigGoodsInfoRepository;
    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    private StoreCateGoodsRelaService storeCateGoodsRelaService;
    
	@Autowired
	private MerchantConfigGoodsService goodsRecommendGoodsService;
	
	@Autowired
	private RedisService redisService;
	
    /**
     * 新增商品推荐商品
     *
     * @author sgy
     */
    @Transactional
    public MerchantRecommendGoods add(MerchantRecommendGoods entity) {
        goodsRecommendGoodsRepository.save(entity);
        return entity;
    }

    /**
     * 修改商品推荐商品
     *
     * @author sgy
     */
    @Transactional
    public MerchantRecommendGoods modify(MerchantRecommendGoods entity) {
        goodsRecommendGoodsRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除商品推荐商品
     *
     * @author sgy
     */
    @Transactional
    public void deleteById(String id) {
        goodsRecommendGoodsRepository.deleteById(id);
    }

    /**
     * 批量删除商品推荐商品
     *
     * @author sgy
     */
    @Transactional
    public void deleteByIdList(List<String> ids) {
        ids.forEach(id -> goodsRecommendGoodsRepository.deleteById(id));
    }

    /**
     * 批量删除商品推荐商品
     *
     * @author sgy
     */
    @Transactional
    public void deleteAll() {
        goodsRecommendGoodsRepository.findAll().forEach(goodsRecommendGoods -> goodsRecommendGoodsRepository.delete(goodsRecommendGoods));
    }

    /**
     * 批量删除推荐商品信息
     * @param storeId
     */
    @Transactional
    public void delByStoreId(Long storeId) {
        goodsRecommendGoodsRepository.deleteByCompanyInfoId(storeId);
    }

    /**
     * 单个查询商品推荐商品
     *
     * @author sgy
     */
    public MerchantRecommendGoods getById(String id) {
        return goodsRecommendGoodsRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询商品推荐商品
     *
     * @author sgy
     */
    public Page<MerchantRecommendGoods> page(MerchantConfigGoodsQueryRequest queryReq) {
        return goodsRecommendGoodsRepository.findAll(
                MerchantConfigGoodsWhereCriteriaBuilder.merchantBuild(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询商品推荐商品
     *
     * @author sgy
     */
    public List<MerchantRecommendGoods> list(MerchantConfigGoodsQueryRequest queryReq) {
        return goodsRecommendGoodsRepository.findAll(
                MerchantConfigGoodsWhereCriteriaBuilder.merchantBuild(queryReq),
                queryReq.getSort());
    }


    /**
     * 列表查询商品推荐商品
     *
     * @author sgy
     */
    public List<MerchantRecommendGoods> list() {
        return goodsRecommendGoodsRepository.findAll();
    }

    public List<MerchantRecommendGoods> findByCompanyInfoId(Long companyInfoId) {
        return goodsRecommendGoodsRepository.findByCompanyInfoId(companyInfoId);
    }

    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public MerchantRecommendGoodsVO wrapperVo(MerchantRecommendGoods goodsRecommendGoods) {
        if (goodsRecommendGoods != null) {
            MerchantRecommendGoodsVO goodsRecommendGoodsVO = new MerchantRecommendGoodsVO();
            KsBeanUtil.copyPropertiesThird(goodsRecommendGoods, goodsRecommendGoodsVO);
            return goodsRecommendGoodsVO;
        }
        return null;
    }
    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public MerchantRecommendGoodsVO wrapperGoodsInfoVo(MerchantRecommendGoods goodsRecommendGoods) {
        if (goodsRecommendGoods != null) {
            MerchantRecommendGoodsVO goodsRecommendGoodsVO = new MerchantRecommendGoodsVO();
            KsBeanUtil.copyPropertiesThird(goodsRecommendGoods, goodsRecommendGoodsVO);
            GoodsInfo goodsInfo = goodsInfoService.findOne(goodsRecommendGoods.getGoodsInfoId());
            if (null!=goodsInfo){
                GoodsInfoVO goodsInfoVO = new GoodsInfoVO();
                KsBeanUtil.copyPropertiesThird(goodsInfo, goodsInfoVO);
                goodsRecommendGoodsVO.setGoodsInfo(goodsInfoVO);
                List<String> goodsIds =new ArrayList<>();
                goodsIds.add(goodsInfo.getGoodsId());
                List<StoreCateGoodsRela> storeCateGoodsRelas = storeCateGoodsRelaService.selectByGoodsId(goodsIds);
                List<Long>  longs = storeCateGoodsRelas.stream().map(StoreCateGoodsRela::getStoreCateId).collect(Collectors.toList());
                goodsRecommendGoodsVO.getGoodsInfo().setStoreCateIds(longs);
                goodsRecommendGoodsVO.setMerchantRecommendId(goodsRecommendGoods.getRecommendId());
            }
            return goodsRecommendGoodsVO;
        }
        return null;
    }

    public List<MerchantRecommendGoods> notList(MerchantConfigGoodsQueryRequest queryReq) {
        return goodsRecommendGoodsRepository.notIdList(queryReq.getCompanyInfoId(),queryReq.getGoodsInfoIds());
    }
    @Transactional
    public int  deleteByGoodsInfoId(MerchantRecommendGoods type) {
      return  merchantConfigGoodsInfoRepository.deleteByGoodsInfoId(type.getGoodsInfoId(),type.getCompanyInfoId().toString());
    }

    @Transactional
    public List<MerchantRecommendGoods> sortMerchantRecommendGoods(MerchantRecommendGoodsSortRequest request) {
        final String id = request.getMerchantRecommendId();
        RecommendGoodsInfo recommendGoodsInfo = merchantConfigGoodsInfoRepository.findById(id).orElse(null);
        if (null == recommendGoodsInfo || null == recommendGoodsInfo.getStoreId()) {
            throw new SbcRuntimeException("参数异常");
        }
        final List<MerchantRecommendGoods> merchantRecommendGoodsList = list(MerchantConfigGoodsQueryRequest.builder().storeId(recommendGoodsInfo.getStoreId()).build());
        merchantRecommendGoodsList.sort(Comparator.comparing(MerchantRecommendGoods::getSort));
        if (request.getSort() > merchantRecommendGoodsList.size()) {
            throw new SbcRuntimeException("参数异常,排序超出范围");
        }
        List<String> ids = new ArrayList<>();
        merchantRecommendGoodsList.forEach(o -> {
            if (o.getRecommendId().equals(id))

                return ;
            ids.add(o.getRecommendId());
        });
        ids.add(request.getSort() - 1, id);
        for (int i = 0; i < ids.size(); i++) {
            merchantConfigGoodsInfoRepository.updateSortById(ids.get(i), i + 1);
        }

        return merchantRecommendGoodsList;
    }
    
    
	public BaseResponse<MerchantConfigGoodsListResponse> recommendRedis(@RequestBody @Valid MerchantConfigGoodsQueryRequest goodsRecommendGoodsListReq) {
		String key = CacheKeyConstant.SCREEN_ORDER_ADD_LAST_COMPANY+goodsRecommendGoodsListReq.getStoreId()+ SpecialSymbols.UNDERLINE.toValue();
		MerchantConfigGoodsQueryRequest listReq=new MerchantConfigGoodsQueryRequest();
		listReq.putSort("sort", "asc");
		listReq.setCompanyInfoId(goodsRecommendGoodsListReq.getCompanyInfoId());
		listReq.setStoreId(goodsRecommendGoodsListReq.getStoreId());
		List<MerchantRecommendGoods> list = goodsRecommendGoodsService.list(listReq);
		redisService.setString(key, JSONObject.toJSONString(list));
		return BaseResponse.success(new MerchantConfigGoodsListResponse());
	}
    

}
