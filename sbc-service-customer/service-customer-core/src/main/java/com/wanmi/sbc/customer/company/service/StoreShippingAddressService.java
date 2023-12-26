package com.wanmi.sbc.customer.company.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.dto.StoreShippingAddressEditDTO;
import com.wanmi.sbc.customer.bean.dto.StoreShippingAddressQueryDTO;
import com.wanmi.sbc.customer.bean.vo.StoreShippingAddressVO;
import com.wanmi.sbc.customer.company.model.root.StoreShippingAddress;
import com.wanmi.sbc.customer.company.repository.StoreShippingAddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class StoreShippingAddressService {

    @Resource
    private StoreShippingAddressRepository storeShippingAddressRepository;

    public List<StoreShippingAddressVO> listByQuery(StoreShippingAddressQueryDTO queryDTO) {
        return KsBeanUtil.convertList(storeShippingAddressRepository.findAll(getWhereCriteria(queryDTO)), StoreShippingAddressVO.class);
    }

    private Specification<StoreShippingAddress> getWhereCriteria(StoreShippingAddressQueryDTO queryDTO) {
        final Long storeId = queryDTO.getStoreId();
        final Long companyInfoId = queryDTO.getCompanyInfoId();
        final Integer defaultFlag = queryDTO.getDefaultFlag();
        final Long id = queryDTO.getId();
        return (root, query, build) -> {
            List<Predicate> predicates = new ArrayList<>();
//            // in
//            if (CollectionUtils.isNotEmpty(Ids)) {
//                CriteriaBuilder.In in = build.in(root.get("id"));
//                Ids.forEach(id -> {
//                    in.value(id);
//                });
//                predicates.add(in);
//            }
            if (Objects.nonNull(id)) {
                predicates.add(build.equal(root.get("id"), id));
            }
            if (Objects.nonNull(storeId)) {
                predicates.add(build.equal(root.get("storeId"), storeId));
            }
            if (Objects.nonNull(companyInfoId)) {
                predicates.add(build.equal(root.get("companyInfoId"), companyInfoId));
            }
            if (Objects.nonNull(defaultFlag)) {
                predicates.add(build.equal(root.get("defaultFlag"), defaultFlag));
            }
            predicates.add(build.equal(root.get("delFlag"), DeleteFlag.NO));
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : build.and(p);
        };
    }

    public StoreShippingAddressVO editShippingAddress(StoreShippingAddressEditDTO editDTO) {
        log.info("editShippingAddress parameter {}", JSON.toJSONString(editDTO));
        StoreShippingAddress save;
        if (null == editDTO.getId()) {
            final StoreShippingAddress create = KsBeanUtil.convert(editDTO, StoreShippingAddress.class);
            create.setDefaultFlag(0);
            create.setCreateTime(LocalDateTime.now());
            create.setUpdateTime(LocalDateTime.now());
            create.setDelFlag(DeleteFlag.NO);
            save = storeShippingAddressRepository.save(create);
        } else {
            StoreShippingAddress shippingAddress = checkParams(editDTO);
            save = storeShippingAddressRepository.save(shippingAddress);
        }
        return KsBeanUtil.convert(save, StoreShippingAddressVO.class);
    }

    private StoreShippingAddress checkParams(StoreShippingAddressEditDTO editDTO) {
        StoreShippingAddress query = storeShippingAddressRepository.findById(editDTO.getId()).orElse(null);
        if (null == query) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前ID不存在，不支持编辑");
        }
        // 校验默认地址
        if (Objects.equals(editDTO.getDefaultFlag(), 1)) {
            if (storeShippingAddressRepository.existsByStoreIdAndDelFlagAndDefaultFlagAndIdNot(query.getStoreId(), DeleteFlag.NO, 1, query.getId())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "只能存在一个默认地址");
            }
        }
        wrapUpdate(editDTO, query);
        return query;
    }

    private void wrapUpdate(StoreShippingAddressEditDTO editDTO, StoreShippingAddress query) {
        query.setUpdateTime(LocalDateTime.now());
        Integer i;
        if (null != (i = editDTO.getProvinceCode())) {
            query.setProvinceCode(i);
        }

        if (null != (i = editDTO.getCityCode())) {
            query.setCityCode(i);
        }

        if (null != (i = editDTO.getDistrictCode())) {
            query.setDistrictCode(i);
        }

        if (null != (i = editDTO.getStreetCode())) {
            query.setStreetCode(i);
        }

        if (null != (i = editDTO.getDefaultFlag())) {
            query.setDefaultFlag(i);
        }

        String s;
        if (StringUtils.isNotBlank(s = editDTO.getProvinceName())) {
            query.setProvinceName(s);
        }

        if (StringUtils.isNotBlank(s = editDTO.getCityName())) {
            query.setCityName(s);
        }

        if (StringUtils.isNotBlank(s = editDTO.getDistrictName())) {
            query.setDistrictName(s);
        }

        if (StringUtils.isNotBlank(s = editDTO.getStreetName())) {
            query.setStreetName(s);
        }

        if (StringUtils.isNotBlank(s = editDTO.getShippingName())) {
            query.setShippingName(s);
        }

        if (StringUtils.isNotBlank(s = editDTO.getShippingPhone())) {
            query.setShippingPhone(s);
        }

        if (StringUtils.isNotBlank(s = editDTO.getDetailAddress())) {
            query.setDetailAddress(s);
        }

        if (StringUtils.isNotBlank(s = editDTO.getShippingPerson())) {
            query.setShippingPerson(s);
        }

        if (null != editDTO.getDelFlag()) {
            query.setDelFlag(editDTO.getDelFlag());
        }
    }
}
