package com.wanmi.sbc.goods.freight.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.NoDeleteStoreByIdRequest;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.constant.FreightTemplateErrorCode;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsModifyRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsSaveRequest;
import com.wanmi.sbc.goods.bean.dto.FreightTemplateGoodsExpressSaveDTO;
import com.wanmi.sbc.goods.bean.dto.FreightTemplateGoodsFreeSaveDTO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.ValuationType;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoods;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoodsExpress;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoodsFree;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateStore;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateGoodsExpressRepository;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateGoodsFreeRepository;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateGoodsRepository;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateStoreRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 单品运费模板服务
 * Created by sunkun on 2018/5/2.
 */
@Service
@Slf4j
public class FreightTemplateGoodsService {

    @Resource
    private FreightTemplateGoodsRepository freightTemplateGoodsRepository;

    @Resource
    private FreightTemplateGoodsExpressRepository freightTemplateGoodsExpressRepository;

    @Resource
    private FreightTemplateGoodsFreeRepository freightTemplateGoodsFreeRepository;

    @Resource
    private FreightTemplateStoreRepository freightTemplateStoreRepository;

    @Resource
    private GoodsRepository goodsRepository;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Resource
//    private StoreRepository storeRepository;
    private StoreQueryProvider storeQueryProvider;


    /**
     * 保存单品运费模板
     *
     * @param request 单品运费模板信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void renewalFreightTemplateGoods(FreightTemplateGoodsSaveRequest request) {
        FreightTemplateGoods freightTemplateGoods = null;
        Integer deliverWay = DeliverWay.EXPRESS.toValue();
        if(request!=null && request.getDeliverWay()!=null){
            deliverWay = request.getDeliverWay().toValue();
        }
        if (request.getFreightTempId() == null) {
            int count = freightTemplateGoodsRepository.countByStoreIdAndDelFlagAndDeliverWay(request.getStoreId(), DeleteFlag.NO,request.getDeliverWay());
            if (count >= Constants.FREIGHT_GOODS_MAX_SIZE) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            //校验模板名称是否重复
            this.freightTemplateNameIsRepetition(request.getStoreId(), request.getFreightTempName(),deliverWay,false);
            freightTemplateGoods = new FreightTemplateGoods();
            BeanUtils.copyProperties(request, freightTemplateGoods);
            freightTemplateGoods.setCreateTime(LocalDateTime.now());
            freightTemplateGoods.setDelFlag(DeleteFlag.NO);
        } else {
            freightTemplateGoods = freightTemplateGoodsRepository.findById(request.getFreightTempId()).orElse(null);
            if (freightTemplateGoods == null) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            //修改名称，校验模板名称是否重复
            if (!StringUtils.equals(request.getFreightTempName(), freightTemplateGoods.getFreightTempName())) {
                this.freightTemplateNameIsRepetition(freightTemplateGoods.getStoreId(), request.getFreightTempName(),deliverWay,false);
            }
            //组装并保存单品运费模板
            freightTemplateGoods.setAreaId(request.getAreaId());
            freightTemplateGoods.setCityId(request.getCityId());
            freightTemplateGoods.setProvinceId(request.getProvinceId());
            if (Objects.equals(freightTemplateGoods.getDefaultFlag(), DefaultFlag.NO)) {
                freightTemplateGoods.setFreightTempName(request.getFreightTempName());
            }
            freightTemplateGoods.setFreightFreeFlag(request.getFreightFreeFlag());
            freightTemplateGoods.setValuationType(request.getValuationType());
            freightTemplateGoods.setSpecifyTermFlag(request.getSpecifyTermFlag());
        }
        FreightTemplateGoods rFreightTemplateGoods = freightTemplateGoodsRepository.save(freightTemplateGoods);

        //过滤出单品运费模板快递运送下所有区域
        List<String> expressAreas = request.getFreightTemplateGoodsExpressSaveRequests().stream()
                .filter(info->Objects.equals(DeleteFlag.NO,info.getDelFlag())).map(info -> {
            if (info.getDestinationArea() == null || info.getDestinationArea().length == 0) {
                return null;
            }
            return StringUtils.join(info.getDestinationArea(), ",");
        }).filter(Objects::nonNull).collect(Collectors.toList());
        //过滤出单品运费模板指定包邮条件下所有区域
        List<String> freeAreas = request.getFreightTemplateGoodsFreeSaveRequests().stream()
                .filter(info->Objects.equals(DeleteFlag.NO,info.getDelFlag())).map(info -> {
            if (info.getDestinationArea() == null || info.getDestinationArea().length == 0) {
                return null;
            }
            return StringUtils.join(info.getDestinationArea(), ",");
        }).filter(Objects::nonNull).collect(Collectors.toList());
        //校验是否有重复区域
        if (this.verifyAreaRepetition(expressAreas) || this.verifyAreaRepetition(freeAreas)) {
            throw new SbcRuntimeException(FreightTemplateErrorCode.AREA_REPETITION_SETTING);
        }
        //保存单品运费模板
        this.batchRenewalFreightTemplateGoodsExpress(rFreightTemplateGoods, request.getFreightTemplateGoodsExpressSaveRequests());
        //保存单品运费模板指定包邮条件
        this.batchRenewalFreightTemplateGoodsFree(rFreightTemplateGoods, request.getFreightTemplateGoodsFreeSaveRequests());
    }

    /**
     * 校验名称重复
     *
     * @param storeId 店铺id
     * @param freightTempName 单品运费模板名称
     * @param isCopy 是否复制
     */
    public void freightTemplateNameIsRepetition(Long storeId, String freightTempName,Integer deliverWay,boolean isCopy) {
        FreightTemplateGoods freightTemplateGoods = freightTemplateGoodsRepository.queryByFreighttemplateName(storeId, freightTempName,DeliverWay.fromValue(deliverWay));
        if (freightTemplateGoods != null) {
            String errorCode = FreightTemplateErrorCode.STORE_NAME_EXIST;
            if(isCopy){
                errorCode = FreightTemplateErrorCode.NAME_EXIST;
            }
            throw new SbcRuntimeException(errorCode);
        }
    }

    /**
     * 修改单品运费模板
     *
     * @param request 单品运费模板信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFreightTemplateGoods(FreightTemplateGoodsSaveRequest request) {
        FreightTemplateGoods freightTemplateGoods = freightTemplateGoodsRepository.findById(request.getFreightTempId()).orElse(null);
        if (freightTemplateGoods == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //组装并保存单品运费模板
        freightTemplateGoods.setAreaId(request.getAreaId());
        freightTemplateGoods.setCityId(request.getCityId());
        freightTemplateGoods.setProvinceId(request.getProvinceId());
        freightTemplateGoods.setFreightTempName(request.getFreightTempName());
        freightTemplateGoods.setFreightFreeFlag(request.getFreightFreeFlag());
        freightTemplateGoods.setValuationType(request.getValuationType());
        freightTemplateGoods.setSpecifyTermFlag(request.getSpecifyTermFlag());
        freightTemplateGoodsRepository.save(freightTemplateGoods);
        //批量更新单品运费模板快递运送
        this.batchRenewalFreightTemplateGoodsExpress(freightTemplateGoods, request.getFreightTemplateGoodsExpressSaveRequests());
        //批量更新单品运费模板指定包邮条件
        this.batchRenewalFreightTemplateGoodsFree(freightTemplateGoods, request.getFreightTemplateGoodsFreeSaveRequests());
    }

    /**
     * 查询所有单品运费模板列表
     *
     * @param storeId 店铺id
     * @return 单品运费模板列表
     */
    public List<FreightTemplateGoods> queryAll(Long storeId,Integer deliverWay) {
        List<FreightTemplateGoods> list = freightTemplateGoodsRepository.queryAll(storeId, DeleteFlag.NO,DeliverWay.fromValue(deliverWay));
        List<Long> ids = list.stream().map(FreightTemplateGoods::getFreightTempId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            List<FreightTemplateGoodsExpress> freightTemplateGoodsExpresses = freightTemplateGoodsExpressRepository.findByFreightTempIds(ids);
            if (CollectionUtils.isNotEmpty(freightTemplateGoodsExpresses)) {
                list.forEach(info -> {
                    info.setFreightTemplateGoodsExpresses(freightTemplateGoodsExpresses.stream().filter(express ->
                            info.getFreightTempId().equals(express.getFreightTempId())).collect(Collectors.toList()));
                });
            }
        }
        return list;
    }


    /**
     * 根据主键列表查询单品运费模板列表
     *
     * @param ids 单品运费模板ids
     * @return 单品运费模板列表
     */
    public List<FreightTemplateGoods> queryAllByIds(List<Long> ids) {
        List<FreightTemplateGoods> list = freightTemplateGoodsRepository.queryByFreightTempIds(ids);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        List<FreightTemplateGoodsExpress> freightTemplateGoodsExpresses = freightTemplateGoodsExpressRepository.findByFreightTempIds(ids);
        List<FreightTemplateGoodsFree> freightTemplateGoodsFrees = freightTemplateGoodsFreeRepository.findByFreightTempIds(ids);
        list.forEach(info -> {
            info.setFreightTemplateGoodsExpresses(freightTemplateGoodsExpresses.stream().filter(express ->
                    info.getFreightTempId().equals(express.getFreightTempId())).collect(Collectors.toList()));
            info.setFreightTemplateGoodsFrees(freightTemplateGoodsFrees.stream().filter(frees ->
                    info.getFreightTempId().equals(frees.getFreightTempId())).collect(Collectors.toList()));
        });
        return list;
    }

    /**
     * 查询单品运费模板
     *
     * @param freightTempId 单品运费模板id
     * @return 单品运费模板
     */
    public FreightTemplateGoods queryById(Long freightTempId) {
        FreightTemplateGoods freightTemplateGoods = freightTemplateGoodsRepository.queryById(freightTempId);
        if (freightTemplateGoods == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        freightTemplateGoods.setFreightTemplateGoodsExpresses(freightTemplateGoodsExpressRepository.findByFreightTempIdAndDelFlag(freightTempId, DeleteFlag.NO));
        freightTemplateGoods.setFreightTemplateGoodsFrees(freightTemplateGoodsFreeRepository.findByFreightTempIdAndDelFlag(freightTempId, DeleteFlag.NO));
        return freightTemplateGoods;
    }

    /**
     * 根据运费模板ID判断模板是否存在
     * @param freightTempId 单品运费模板id
     */
    public void hasFreightTemp(Long freightTempId) {
        FreightTemplateGoods freightTemplateGoods = freightTemplateGoodsRepository.queryById(freightTempId);
        if (Objects.isNull(freightTemplateGoods)) {
            //运费模板不存在异常，待common拆分后再改异常编号
            throw new SbcRuntimeException(FreightTemplateErrorCode.NOT_EXIST);
        }
    }

    /**
     * 根据主键和店铺id删除单品运费模板
     *
     * @param id 运费模板ID
     * @param storeId 店铺id
     */
    @Transactional(rollbackFor = Exception.class)
    public void delById(Long id, Long storeId,Integer deliverWay) {
        updateDelFlagById(id, storeId);
        List<FreightTemplateGoods> defaultFreightTemplateGodos = freightTemplateGoodsRepository.queryByDefault(storeId,DeliverWay.fromValue(deliverWay));
        if (CollectionUtils.isEmpty(defaultFreightTemplateGodos)) {
            //店铺下没有默认模板,系统数据错误
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //切换默认仓库 多仓库均需数据变更
        wareHouseRepository.findAll().forEach(wareHouse -> {
            defaultFreightTemplateGodos.forEach(var ->{
                if(wareHouse.getWareId().equals(var.getWareId())){
                    goodsRepository.updateFreightTempId(id, var.getFreightTempId());
                }
            });
        });
    }

    public void updateDelFlagById(Long id, Long storeId) {
        FreightTemplateGoods freightTemplateGoods = this.queryById(id);
        if (!Objects.equals(freightTemplateGoods.getStoreId(), storeId) ||
                Objects.equals(freightTemplateGoods.getDefaultFlag(), DefaultFlag.YES)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        freightTemplateGoods.setDelFlag(DeleteFlag.YES);
        freightTemplateGoodsRepository.save(freightTemplateGoods);
    }

    /**
     * 复制单品运费模板
     *
     * @param freightTempId 运费模板ID
     * @param storeId 店铺id
     */
    @Transactional(rollbackFor = Exception.class)
    public void copyFreightTemplateGoods(Long freightTempId, Long storeId,DeliverWay deliverWay) {
        //单品运费模板上限20
        int count = freightTemplateGoodsRepository.countByStoreIdAndDelFlagAndDeliverWay(storeId, DeleteFlag.NO,deliverWay);
        if (count >= Constants.FREIGHT_GOODS_MAX_SIZE) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //查询要复制单品运费模板
        FreightTemplateGoods freightTemplateGoods = this.queryById(freightTempId);
        if (!Objects.equals(freightTemplateGoods.getStoreId(), storeId)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateGoods newFreightTemplateGoods = new FreightTemplateGoods();
        BeanUtils.copyProperties(freightTemplateGoods, newFreightTemplateGoods);
        //拼接新模板名称
        newFreightTemplateGoods.setFreightTempName(freightTemplateGoods.getFreightTempName() + "的副本");
        if (newFreightTemplateGoods.getFreightTempName().length() > Constants.FREIGHT_GOODS_MAX_SIZE) {
            //名称长度超出限制
            throw new SbcRuntimeException(FreightTemplateErrorCode.NAME_OVER_LIMIT);
        }
        this.freightTemplateNameIsRepetition(storeId, newFreightTemplateGoods.getFreightTempName(),deliverWay.toValue(),true);
        LocalDateTime date = LocalDateTime.now();
        newFreightTemplateGoods.setFreightTempId(null);
        newFreightTemplateGoods.setCreateTime(date);
        newFreightTemplateGoods.setDefaultFlag(DefaultFlag.NO);
        newFreightTemplateGoods.setSpecifyTermFlag(CollectionUtils.isEmpty(freightTemplateGoods.getFreightTemplateGoodsFrees()) ? DefaultFlag.NO : DefaultFlag.YES);
        //复制单品模板
        FreightTemplateGoods resultFreightTemplateGoods = freightTemplateGoodsRepository.save(newFreightTemplateGoods);
        //复制单品运费模板快递运送
        freightTemplateGoods.getFreightTemplateGoodsExpresses().forEach(info -> {
            FreightTemplateGoodsExpress newFreightTemplateGoodsExpress = new FreightTemplateGoodsExpress();
            BeanUtils.copyProperties(info, newFreightTemplateGoodsExpress);
            newFreightTemplateGoodsExpress.setId(null);
            newFreightTemplateGoodsExpress.setFreightTempId(resultFreightTemplateGoods.getFreightTempId());
            newFreightTemplateGoodsExpress.setCreateTime(date);
            freightTemplateGoodsExpressRepository.save(newFreightTemplateGoodsExpress);
        });
        //复制单品运费模板指定包邮条件
        freightTemplateGoods.getFreightTemplateGoodsFrees().forEach(info -> {
            FreightTemplateGoodsFree newFreightTemplateGoodsFree = new FreightTemplateGoodsFree();
            BeanUtils.copyProperties(info, newFreightTemplateGoodsFree);
            newFreightTemplateGoodsFree.setId(null);
            newFreightTemplateGoodsFree.setFreightTempId(resultFreightTemplateGoods.getFreightTempId());
            newFreightTemplateGoodsFree.setCreateTime(date);
            freightTemplateGoodsFreeRepository.save(newFreightTemplateGoodsFree);
        });
    }

    /**
     * 创建店铺后初始化运费模板
     *
     * @param storeId 店铺id
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void initFreightTemplate(Long storeId,Integer deliverWay) {
//        Store store = storeRepository.findByStoreIdAndDelFlag(storeId, DeleteFlag.NO);
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(storeId)).getContext().getStoreVO();
        if (store == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        final FreightTemplateGoods freightTemplateGoods = freightTemplateGoodsRepository.queryByFreighttemplateName(storeId,"默认模板",DeliverWay.fromValue(deliverWay));
        if (null != freightTemplateGoods){
            log.info("已经存在默认模板，不需要初始化，storeId:{}", storeId);
            return;
        }
        //组装单品运费模板数据
        FreightTemplateGoodsSaveRequest freightTemplateGoodsSaveRequest = new FreightTemplateGoodsSaveRequest();
        freightTemplateGoodsSaveRequest.setFreightTempName("默认模板");
        freightTemplateGoodsSaveRequest.setStoreId(storeId);
        freightTemplateGoodsSaveRequest.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
        freightTemplateGoodsSaveRequest.setDefaultFlag(DefaultFlag.YES);
        freightTemplateGoodsSaveRequest.setFreightFreeFlag(DefaultFlag.NO);
        freightTemplateGoodsSaveRequest.setSpecifyTermFlag(DefaultFlag.NO);
        freightTemplateGoodsSaveRequest.setValuationType(ValuationType.NUMBER);
        freightTemplateGoodsSaveRequest.setWareId(1L);
        // 组装单品运费模板快递运送数据
        List<FreightTemplateGoodsExpressSaveDTO> list = new ArrayList<>();
        FreightTemplateGoodsExpressSaveDTO freightTemplateGoodsExpressSaveRequest = new FreightTemplateGoodsExpressSaveDTO();
        freightTemplateGoodsExpressSaveRequest.setDefaultFlag(DefaultFlag.YES);
        freightTemplateGoodsExpressSaveRequest.setDelFlag(DeleteFlag.NO);
        freightTemplateGoodsExpressSaveRequest.setDestinationArea(new String[]{});
        freightTemplateGoodsExpressSaveRequest.setDestinationAreaName(new String[]{"未被划分的配送地区自动归于默认运费"});
        BigDecimal defaultNum = BigDecimal.ZERO;
        freightTemplateGoodsExpressSaveRequest.setFreightStartNum(new BigDecimal(1));
        freightTemplateGoodsExpressSaveRequest.setFreightStartPrice(defaultNum);
        freightTemplateGoodsExpressSaveRequest.setFreightPlusNum(new BigDecimal(1));
        freightTemplateGoodsExpressSaveRequest.setFreightPlusPrice(defaultNum);
        list.add(freightTemplateGoodsExpressSaveRequest);
        freightTemplateGoodsSaveRequest.setFreightTemplateGoodsExpressSaveRequests(list);
        //保存单品运费模板
        this.renewalFreightTemplateGoods(freightTemplateGoodsSaveRequest);
        //组装店铺运费模板数据
        FreightTemplateStore freightTemplateStore = new FreightTemplateStore();
        freightTemplateStore.setFreightTempName("默认模板");
        freightTemplateStore.setDestinationAreaName("未被划分的配送地区自动归于默认运费");
        freightTemplateStore.setFixedFreight(defaultNum);
        freightTemplateStore.setSatisfyFreight(defaultNum);
        freightTemplateStore.setSatisfyPrice(defaultNum);
        freightTemplateStore.setStoreId(storeId);
        freightTemplateStore.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
        freightTemplateStore.setCreateTime(LocalDateTime.now());
        freightTemplateStore.setDefaultFlag(DefaultFlag.YES);
        freightTemplateStore.setDelFlag(DeleteFlag.NO);
        freightTemplateStore.setFreightType(DefaultFlag.YES);
        freightTemplateStore.setDeliverWay(DeliverWay.EXPRESS);
        freightTemplateStore.setDestinationArea("");
        freightTemplateStoreRepository.save(freightTemplateStore);
    }

    /**
     * 根据店铺id获取默认的单品运费模板
     * @param storeId 店铺id
     * @return 单品运费模板
     */
    public List<FreightTemplateGoods> queryByDefaultByStoreId(Long storeId,Integer deliverWay){
        return freightTemplateGoodsRepository.queryByDefault(storeId,DeliverWay.fromValue(deliverWay));
    }

    /**
     * 批量更新单品运费模板快递运送
     *
     * @param freightTemplateGoods  单品运费模板
     * @param list 单品运费模板费用
     */
    private void batchRenewalFreightTemplateGoodsExpress(FreightTemplateGoods freightTemplateGoods,
                                                         List<FreightTemplateGoodsExpressSaveDTO> list) {
        list.forEach(info -> {
            FreightTemplateGoodsExpress freightTemplateGoodsExpress = null;
            if (info.getId() != null) {
                //编辑单品运费模板快递运送
                freightTemplateGoodsExpress = freightTemplateGoodsExpressRepository.findById(info.getId()).orElse(null);
                if (freightTemplateGoodsExpress != null) {
                    //默认模板不支持删除，非法输入
                    if (Objects.equals(info.getDefaultFlag(), DefaultFlag.YES) && Objects.equals(info.getDelFlag(), DeleteFlag.YES)) {
                        throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                    }
                    freightTemplateGoodsExpress.setDelFlag(info.getDelFlag());
                    freightTemplateGoodsExpress.setFreightPlusNum(info.getFreightPlusNum());
                    freightTemplateGoodsExpress.setFreightPlusPrice(info.getFreightPlusPrice());
                    freightTemplateGoodsExpress.setFreightStartNum(info.getFreightStartNum());
                    freightTemplateGoodsExpress.setFreightStartPrice(info.getFreightStartPrice());
                }
            } else {
                //新增单品运费模板快递运送
                freightTemplateGoodsExpress = new FreightTemplateGoodsExpress();
                BeanUtils.copyProperties(info, freightTemplateGoodsExpress);
                freightTemplateGoodsExpress.setCreateTime(LocalDateTime.now());
                freightTemplateGoodsExpress.setDelFlag(DeleteFlag.NO);
                freightTemplateGoodsExpress.setFreightTempId(freightTemplateGoods.getFreightTempId());
            }
            if (freightTemplateGoodsExpress != null) {
                freightTemplateGoodsExpress.setValuationType(freightTemplateGoods.getValuationType());
                freightTemplateGoodsExpress.setDestinationArea(StringUtils.join(info.getDestinationArea(), ","));
                freightTemplateGoodsExpress.setDestinationAreaName(StringUtils.join(info.getDestinationAreaName(), ","));
                freightTemplateGoodsExpressRepository.save(freightTemplateGoodsExpress);
            }
        });
    }

    /**
     * 批量更新单品运费模板指定包邮条件
     *
     * @param freightTemplateGoods 单品运费模板
     * @param list 单品运费模板费用
     */
    private void batchRenewalFreightTemplateGoodsFree(FreightTemplateGoods freightTemplateGoods,
                                                      List<FreightTemplateGoodsFreeSaveDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(info -> {
            FreightTemplateGoodsFree freightTemplateGoodsFree = null;
            if (info.getId() != null) {
                //编辑单品运费模板指定包邮条件
                freightTemplateGoodsFree = freightTemplateGoodsFreeRepository.findById(info.getId()).orElse(new FreightTemplateGoodsFree());
                freightTemplateGoodsFree.setConditionType(info.getConditionType());
                freightTemplateGoodsFree.setConditionOne(info.getConditionOne());
                freightTemplateGoodsFree.setConditionTwo(info.getConditionTwo());
                freightTemplateGoodsFree.setDelFlag(info.getDelFlag());
            } else {
                //新增单品运费模板指定包邮条件
                freightTemplateGoodsFree = new FreightTemplateGoodsFree();
                BeanUtils.copyProperties(info, freightTemplateGoodsFree);
                freightTemplateGoodsFree.setCreateTime(LocalDateTime.now());
                freightTemplateGoodsFree.setFreightTempId(freightTemplateGoods.getFreightTempId());
                freightTemplateGoodsFree.setDelFlag(DeleteFlag.NO);
            }
            if (freightTemplateGoodsFree != null) {
                freightTemplateGoodsFree.setDestinationArea(StringUtils.join(info.getDestinationArea(), ","));
                freightTemplateGoodsFree.setValuationType(freightTemplateGoods.getValuationType());
                freightTemplateGoodsFree.setDestinationAreaName(StringUtils.join(info.getDestinationAreaName(), ","));
                freightTemplateGoodsFreeRepository.save(freightTemplateGoodsFree);
            }
        });
    }


    /**
     * 校验区域是否有重复
     *
     * @param list 区域
     * @return true:有重复 false:无重复
     */
    private boolean verifyAreaRepetition(List<String> list) {
        Set<String> set = new HashSet<>();
        List<String> strLists = list.stream().map(info -> {
            if (StringUtils.isBlank(info)) {
                return null;
            }
            return Arrays.asList(info.split(","));
        }).filter(Objects::nonNull).flatMap(Collection::stream).map(str -> {
            set.add(str);
            return str;
        }).collect(Collectors.toList());
        return strLists.size() != set.size();
    }

    public List<FreightTemplateGoods> queryPlatformDeliveryToStoreTempList(Boolean onlyDefault) {
        List<FreightTemplateGoods>  freightTemplateGoodsList=onlyDefault?freightTemplateGoodsRepository.queryDefaultDeliveryToStoreTempList() : freightTemplateGoodsRepository.queryPlatformDeliveryToStoreTempList();
        if(CollectionUtils.isNotEmpty(freightTemplateGoodsList)){
            freightTemplateGoodsList.forEach(freightTemplateGoods->{
               freightTemplateGoods.setFreightTemplateGoodsExpresses(freightTemplateGoodsExpressRepository.findByFreightTempIdAndDelFlag(freightTemplateGoods.getFreightTempId(), DeleteFlag.NO));
            });
        }
        return freightTemplateGoodsList;
    }

    @Transactional
    public void updateTemplateDefaultFlag(FreightTemplateGoodsModifyRequest request) {
        FreightTemplateGoods freightTemplateGoods = freightTemplateGoodsRepository.findById(request.getFreightTempId()).orElse(null);
        if (freightTemplateGoods == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if(DefaultFlag.YES.toValue()==request.getDefaultFlag()){
            //freightTemplateGoodsRepository.updateTemplateDefaultFlagByStoreId(request.getStoreId(),0,DeliverWay.DELIVERY_TO_STORE.toValue());
            freightTemplateGoodsRepository.updateTemplateDefaultFlag(request.getFreightTempId(),1);
        }else{
            freightTemplateGoodsRepository.updateTemplateDefaultFlag(request.getFreightTempId(),0);
        }
    }
}
