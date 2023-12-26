package com.wanmi.sbc.goods.info.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchUpdateRequest;
import com.wanmi.sbc.goods.bean.dto.BatchGoodsUpdateDTO;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsUpdateRequest;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.repository.StoreCateGoodsRelaRepository;
import com.wanmi.sbc.goods.storecate.request.StoreCateGoodsRelaQueryRequest;
import com.wanmi.sbc.goods.storecate.request.StoreCateQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @Author: songhanlin
 * @Date: Created In 14:11 2018-12-12
 * @Description: 商品类目导入
 */
@Slf4j
@Service
public class GoodsModifyExcelService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private StoreCateGoodsRelaRepository storeCateGoodsRelaRepository;

    @Autowired
    private DevanningGoodsInfoRepository devanningGoodsInfoRepository;


    public String errorExcel(String newFileName, Workbook wk) throws SbcRuntimeException {
        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat("err_excel").concat("/").concat("goodaModify").concat("/");
        File picSaveFile = new File(t_realPath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception var18) {
                log.error("创建文件路径失败->".concat(t_realPath), var18);
                throw new SbcRuntimeException("K-000011");
            }
        }
        System.err.println("--------------------------->" + t_realPath.concat(newFileName));
        File newFile = new File(t_realPath.concat(newFileName));
        if (newFile.exists()) {
            newFile.delete();
        }

        FileOutputStream fos = null;

        String var7;
        try {
            newFile.createNewFile();
            fos = new FileOutputStream(newFile);
            wk.write(fos);
            var7 = newFileName;
        } catch (IOException var17) {
            log.error("生成文件失败->".concat(t_realPath.concat(newFileName)), var17);
            throw new SbcRuntimeException("K-000011");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException var16) {
                    log.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), var16);
                }
            }

        }
        return var7;
    }

    /**
     * 批量导入商品数据
     * @param request 商品批量信息
     * @return 批量新增的skuId
     */
    @Transactional
    public List<String> batchUpdate(GoodsCommonBatchUpdateRequest request){
        log.info("批量修改请求参数：{}",request);
        List<BatchGoodsUpdateDTO> goodsUpdateList = request.getGoodsUpdateList();
        if (CollectionUtils.isNotEmpty(goodsUpdateList)){
            List<String> erpIds = goodsUpdateList.stream().map(BatchGoodsUpdateDTO::getErpId).collect(Collectors.toList());
            Map<String, BatchGoodsUpdateDTO> collect = goodsUpdateList.stream().collect(Collectors.toMap(BatchGoodsUpdateDTO::getErpId, Function.identity()));
            List<GoodsInfo> allGoodsByErpNos = goodsInfoRepository.selectGoodsInfoByStoreIdAndErpGoodsInfoIds(request.getStoreId(),erpIds);
            if (CollectionUtils.isNotEmpty(allGoodsByErpNos)){
                for (GoodsInfo goodsInfo: allGoodsByErpNos){
                    if (Objects.nonNull(collect.get(goodsInfo.getErpGoodsInfoNo()))){
                        goodsInfo.setAddedFlag(collect.get(goodsInfo.getErpGoodsInfoNo()).getAddFlag());
                        if (Objects.nonNull(collect.get(goodsInfo.getErpGoodsInfoNo()).getBrandId())){
                            goodsInfo.setBrandId(collect.get(goodsInfo.getErpGoodsInfoNo()).getBrandId());
                        }
                        if (Objects.nonNull(collect.get(goodsInfo.getErpGoodsInfoNo()).getCateId())){
                            goodsInfo.setCateId(collect.get(goodsInfo.getErpGoodsInfoNo()).getCateId());
                        }
                        goodsInfo.setSaleType(collect.get(goodsInfo.getErpGoodsInfoNo()).getSaleType());
                    }
                }
                List<GoodsInfo> goodsInfosDistinctGoodsIds = allGoodsByErpNos.stream().
                        collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(n -> n.getGoodsId()))), ArrayList::new));
                List<String> goodsIds = goodsInfosDistinctGoodsIds.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
                //处理需要更新的字段
                Map<String, GoodsUpdateRequest> goodsUpdateRequestHashMap = new HashMap<>();
                goodsInfosDistinctGoodsIds.forEach(param -> {
                    if (Objects.nonNull(collect.get(param.getErpGoodsInfoNo()))) {
                        GoodsUpdateRequest goodsUpdateRequest = new GoodsUpdateRequest();
                        BatchGoodsUpdateDTO batchGoodsUpdateDTO = collect.get(param.getErpGoodsInfoNo());
                        goodsUpdateRequest.setAddedFlag(batchGoodsUpdateDTO.getAddFlag());
                        if (Objects.nonNull(batchGoodsUpdateDTO.getBrandId())){
                            goodsUpdateRequest.setBrandId(batchGoodsUpdateDTO.getBrandId());
                        }
                        if (Objects.nonNull(batchGoodsUpdateDTO.getCateId())){
                            goodsUpdateRequest.setCateId(batchGoodsUpdateDTO.getCateId());
                        }
                        goodsUpdateRequest.setSaleType(batchGoodsUpdateDTO.getSaleType());
                        goodsUpdateRequestHashMap.put(param.getGoodsId(), goodsUpdateRequest);
                    }
                });
                //商品信息匹配更新
                GoodsQueryRequest goodsQuery=new GoodsQueryRequest();
                goodsQuery.setGoodsIds(goodsIds);
                goodsQuery.setDelFlag(0);
                goodsQuery.setStoreId(request.getStoreId());
                List<Goods> all = goodsRepository.findAll(goodsQuery.getWhereCriteria());
                Map<String, Long> storeCateUpdateMap = new HashMap<>();
                for (Goods inner:all){
                    if (Objects.nonNull(goodsUpdateRequestHashMap.get(inner.getGoodsId()))){
                        inner.setBrandId(goodsUpdateRequestHashMap.get(inner.getGoodsId()).getBrandId());
                        inner.setSaleType(goodsUpdateRequestHashMap.get(inner.getGoodsId()).getSaleType());
                        inner.setAddedFlag(goodsUpdateRequestHashMap.get(inner.getGoodsId()).getAddedFlag());
                        if(Objects.nonNull(goodsUpdateRequestHashMap.get(inner.getGoodsId()).getCateId())){
                            storeCateUpdateMap.put(inner.getGoodsId(), goodsUpdateRequestHashMap.get(inner.getGoodsId()).getCateId());
                        }
                    }
                }
                //商品拆箱匹配更新
                DevanningGoodsInfoQueryRequest devanningGoodsInfoQueryRequest = new DevanningGoodsInfoQueryRequest();
                devanningGoodsInfoQueryRequest.setGoodsIds(goodsIds);
                devanningGoodsInfoQueryRequest.setDelFlag(0);
                devanningGoodsInfoQueryRequest.setStoreId(request.getStoreId());
                List<DevanningGoodsInfo> devanningGoodsInfoList = devanningGoodsInfoRepository.findAll(devanningGoodsInfoQueryRequest.getWhereCriteria());
                for (DevanningGoodsInfo devanningGoodsInfo:devanningGoodsInfoList){
                    if (Objects.nonNull(goodsUpdateRequestHashMap.get(devanningGoodsInfo.getGoodsId()))){
                        devanningGoodsInfo.setBrandId(goodsUpdateRequestHashMap.get(devanningGoodsInfo.getGoodsId()).getBrandId());
                        devanningGoodsInfo.setSaleType(goodsUpdateRequestHashMap.get(devanningGoodsInfo.getGoodsId()).getSaleType());
                        devanningGoodsInfo.setAddedFlag(goodsUpdateRequestHashMap.get(devanningGoodsInfo.getGoodsId()).getAddedFlag());
                    }
                }

                goodsRepository.saveAll(all);
                goodsInfoRepository.saveAll(allGoodsByErpNos);
                devanningGoodsInfoRepository.saveAll(devanningGoodsInfoList);

                if(storeCateUpdateMap.size() > 0){
                    storeCateGoodsRelaRepository.deleteByGoodsIds(goodsIds);
                    storeCateUpdateMap.forEach((key,value) ->{
                        StoreCateGoodsRela goodsRela = new StoreCateGoodsRela();
                        goodsRela.setGoodsId(key);
                        goodsRela.setStoreCateId(value);
                        storeCateGoodsRelaRepository.save(goodsRela);
                    });
                }
            }
            return allGoodsByErpNos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
