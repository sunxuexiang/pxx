package com.wanmi.sbc.goods.info.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.goods.bean.enums.AuditStatus;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.distributor.goods.repository.DistributiorGoodsInfoRepository;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsCheckRequest;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.log.GoodsCheckLog;
import com.wanmi.sbc.goods.log.service.GoodsCheckLogService;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import com.wanmi.sbc.goods.standard.model.root.StandardSku;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRepository;
import com.wanmi.sbc.goods.standard.repository.StandardSkuRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * S2b商品服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Transactional(readOnly = true)
public class S2bGoodsService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private StandardGoodsRepository standardGoodsRepository;

    @Autowired
    private StandardGoodsRelRepository standardGoodsRelRepository;

    @Autowired
    private StandardSkuRepository standardSkuRepository;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private GoodsCheckLogService goodsCheckLogService;

    @Autowired
    private DistributiorGoodsInfoRepository distributiorGoodsInfoRepository;

    /**
     * 商品审核
     * @param checkRequest
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void check(GoodsCheckRequest checkRequest) throws SbcRuntimeException {
        goodsRepository.updateAuditDetail(checkRequest.getAuditStatus(), checkRequest.getAuditReason(), checkRequest.getGoodsIds());
        goodsInfoRepository.updateAuditDetail(checkRequest.getAuditStatus(), checkRequest.getGoodsIds());

        //如果商品库里有此商品    禁售状态 同步到商品库
        List<StandardGoodsRel> standardGoodsRels = standardGoodsRelRepository.findByGoodsIds(checkRequest.getGoodsIds());
        if(CollectionUtils.isNotEmpty(standardGoodsRels)){
            List<String> standardGoodsIds = standardGoodsRels.stream().map(StandardGoodsRel::getStandardId).collect(Collectors.toList());
            standardGoodsRepository.deleteByGoodsIds(standardGoodsIds);
            standardSkuRepository.deleteByGoodsIds(standardGoodsIds);

            //如果已经商家已经把 商品库里的供应商商品 加入到商品,同步状态
            List<Goods> goods = goodsRepository.findAllByProviderGoodsIdIn(checkRequest.getGoodsIds());
            if(CollectionUtils.isNotEmpty(goods)){
                List<String> goodsIds = goods.stream().map(Goods::getGoodsId).collect(Collectors.toList());
                goodsRepository.updateAuditDetail(checkRequest.getAuditStatus(), checkRequest.getAuditReason(), goodsIds);
                goodsInfoRepository.updateAuditDetail(checkRequest.getAuditStatus(), goodsIds);
            }
        }

        //审核通过，已经加入到商品库的商品还原
        if(checkRequest.getAuditStatus().equals(AuditStatus.CHECKED)){

            List<StandardGoodsRel> standardGoodsRelList = standardGoodsRelRepository.findByGoodsIds(checkRequest.getGoodsIds());
            for (StandardGoodsRel standardGoodsRel : standardGoodsRelList){
                String standardId = standardGoodsRel.getStandardId();
                standardGoodsRepository.updateDelFlag(standardId);
                standardSkuRepository.updateDelFlag(standardId);
            }
        }


        //商品禁售删除分销员分销商品
        checkRequest.getGoodsIds().forEach(goodsID->{
            distributiorGoodsInfoRepository.deleteByGoodsId(goodsID);
        });

        //新增审核记录
        checkRequest.getGoodsIds().forEach(goodsId -> {
            GoodsCheckLog checkLog = new GoodsCheckLog();
            checkLog.setId(UUIDUtil.getUUID());
            checkLog.setGoodsId(goodsId);
            checkLog.setChecker(checkRequest.getChecker());
            checkLog.setAuditReason(checkRequest.getAuditReason());
            checkLog.setAuditStatus(checkRequest.getAuditStatus());
            goodsCheckLogService.addGoodsCheckLog(checkLog);
        });

        //重新索引
//        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsIds(checkRequest.getGoodsIds()).build());
    }

    /**
     * 待审核统计
     * @param request
     * @return
     */
    public Long countByTodo(GoodsQueryRequest request){
        request.setAuditStatus(CheckStatus.WAIT_CHECK);
        request.setDelFlag(DeleteFlag.NO.toValue());
        return goodsRepository.count(request.getWhereCriteria());
    }
}
