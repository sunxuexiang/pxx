package com.wanmi.sbc.goods.warehousecity.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityQueryRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseCityVO;
import com.wanmi.sbc.goods.warehousecity.model.root.WareHouseCity;
import com.wanmi.sbc.goods.warehousecity.repository.WareHouseCityRepository;
import com.wanmi.sbc.setting.api.provider.platformaddress.PlatformAddressQueryProvider;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressListRequest;
import com.wanmi.sbc.setting.api.response.platformaddress.PlatformAddressListResponse;
import com.wanmi.sbc.setting.bean.enums.AddrLevel;
import com.wanmi.sbc.setting.bean.vo.PlatformAddressVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p> 仓库地区表业务逻辑</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@Service("WareHouseCityService")
public class WareHouseCityService {
    @Autowired
    private WareHouseCityRepository wareHouseCityRepository;

    @Autowired
    private PlatformAddressQueryProvider platformAddressQueryProvider;

    @Autowired
    private WareHouseCityService wareHouseCityService;

    /**
     * 新增 仓库地区表
     *
     * @author zhangwenchang
     */
    @Transactional
    public WareHouseCity add(WareHouseCity entity) {
        wareHouseCityRepository.save(entity);
        return entity;
    }

    /**
     * 新增 仓库地区表
     *
     * @author zhangwenchang
     */
    @Transactional
    public List<WareHouseCity> add(List<WareHouseCity> entity) {
        wareHouseCityRepository.saveAll(entity);
        return entity;
    }

    /**
     * 修改 仓库地区表
     *
     * @author zhangwenchang
     */
    @Transactional
    public WareHouseCity modify(WareHouseCity entity) {
        wareHouseCityRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除 仓库地区表
     *
     * @author zhangwenchang
     */
    @Transactional
    public void deleteById(Long id) {
        wareHouseCityRepository.deleteById(id);
    }

    /**
     * 根据仓库表id批量删除 仓库地区表
     *
     * @author huapeiliang
     */
    @Transactional
    public void deleteByWareHouseId(Long id) {
        wareHouseCityRepository.deleteByWareHouseId(id);
    }

    /**
     * 批量删除 仓库地区表
     *
     * @author zhangwenchang
     */
    @Transactional
    public void deleteByIdList(List<Long> ids) {
        wareHouseCityRepository.deleteByIdList(ids);
    }

    /**
     * 单个查询 仓库地区表
     *
     * @author zhangwenchang
     */
    public WareHouseCity getOne(Long id) {
        return wareHouseCityRepository.findById(id)
                .orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, " 仓库地区表不存在"));
    }

    /**
     * 分页查询 仓库地区表
     *
     * @author zhangwenchang
     */
    public Page<WareHouseCity> page(WareHouseCityQueryRequest queryReq) {
        return wareHouseCityRepository.findAll(
                WareHouseCityWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询 仓库地区表
     *
     * @author zhangwenchang
     */
    public List<WareHouseCity> list(WareHouseCityQueryRequest queryReq) {
        return wareHouseCityRepository.findAll(WareHouseCityWhereCriteriaBuilder.build(queryReq));
    }

    /**
     * 将实体包装成VO
     *
     * @author zhangwenchang
     */
    public WareHouseCityVO wrapperVo(WareHouseCity wareHouseCity) {
        if (wareHouseCity != null) {
            WareHouseCityVO wareHouseCityVO = KsBeanUtil.convert(wareHouseCity, WareHouseCityVO.class);
            return wareHouseCityVO;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(String[] destinationArea, Long wareId) {
//        //保存仓库关联省市
//        if (ArrayUtils.isNotEmpty(destinationArea)) {
//            //删除老数据
//            wareHouseCityService.deleteByWareHouseId(wareId);
//            //保存新数据
//            PlatformAddressListResponse platformAddressListResponse =
//                    platformAddressQueryProvider.provinceOrCitylist(PlatformAddressListRequest.builder().addrIdList(Arrays.asList(destinationArea)).build()).getContext();
//            List<PlatformAddressVO> platformAddressVOList = platformAddressListResponse.getPlatformAddressVOList();
//            if (CollectionUtils.isNotEmpty(platformAddressVOList)) {
//                List<WareHouseCity> all = new ArrayList<>();
//                platformAddressVOList.stream().filter(c -> c.getAddrLevel().equals(AddrLevel.CITY)).collect(Collectors.toList()).forEach(c -> {
//                    WareHouseCity wareHouseCity = new WareHouseCity();
//                    wareHouseCity.setWareId(wareId);
//                    wareHouseCity.setProvinceId(Long.valueOf(c.getAddrParentId()));
//                    wareHouseCity.setCityId(Long.valueOf(c.getAddrId()));
//                    all.add(wareHouseCity);
//                });
//                wareHouseCityService.add(all);
//            }
//        }
    }
}

