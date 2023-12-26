package com.wanmi.sbc.setting.presetsearch.service;


import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.setting.api.response.presetsearch.PresetSearchTermsQueryResponse;
import com.wanmi.sbc.setting.bean.vo.PresetSearchTermsVO;
import com.wanmi.sbc.setting.presetsearch.model.PresetSearchTerms;
import com.wanmi.sbc.setting.presetsearch.repositoy.PresetSearchTermsRepositoy;
import com.wanmi.sbc.setting.redis.RedisService;
import com.wanmi.sbc.setting.util.error.SearchTermsErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PresetSearchTermsService {

    @Autowired
    PresetSearchTermsRepositoy presetSearchTermsRepositoy;

    @Autowired
    RedisService redisService;

    /**
     * 新增预置搜索词
     *
     * @param presetSearchTerms
     * @return
     */
    public PresetSearchTerms add(PresetSearchTerms presetSearchTerms) {
        List<PresetSearchTerms> list = presetSearchTermsRepositoy.findAll();
        if (list.size() >= 20) {
            throw new SbcRuntimeException(SearchTermsErrorCode.PRESET_SEARCH_TERM_RESTRICTIONS);
        }
        if (list.size() > 0) {
            //获取排序字段最大
            PresetSearchTerms presetSearchTerm =
                    list.stream().sorted((a,b)->b.getSort().compareTo(a.getSort())).collect(Collectors.toList()).get(0);
            presetSearchTerms.setSort(presetSearchTerm.getSort() + 1);
        }else {
            presetSearchTerms.setSort(1);
        }
        presetSearchTermsRepositoy.save(presetSearchTerms);
        setRedis();
        return presetSearchTerms;
    }

    /**
     * 编辑预置搜索词
     * @param presetSearchTerms
     * @return
     */
    public PresetSearchTerms modify(PresetSearchTerms presetSearchTerms){
        presetSearchTermsRepositoy.save(presetSearchTerms);
        return presetSearchTerms;
    }

    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void modifySort(List<PresetSearchTerms> presetSearchTerms){
        presetSearchTerms.stream().forEach(item->{
            presetSearchTermsRepositoy.updateSort(item.getId(), item.getSort());
        });
        setRedis();
    }

    public void modifyName(PresetSearchTerms entity) {
        PresetSearchTerms byId = presetSearchTermsRepositoy.findById(entity.getId());
        PresetSearchTerms presetSearchTerms = new PresetSearchTerms();
        presetSearchTerms = KsBeanUtil.convert(byId, PresetSearchTerms.class);
        KsBeanUtil.copyPropertiesIgnoreNull(presetSearchTerms, entity);
        presetSearchTermsRepositoy.save(presetSearchTerms);
        setRedis();
    }
    /**
     * 查询预置搜索词
     * @return
     */
    public PresetSearchTermsQueryResponse listPresetSearchTerms() {
        List<PresetSearchTerms> list = presetSearchTermsRepositoy.findAll();
        List<PresetSearchTermsVO> listSearch = KsBeanUtil.convert(list, PresetSearchTermsVO.class);
        return new PresetSearchTermsQueryResponse(listSearch);
    }

    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void deleteById(Long id) {
        PresetSearchTerms byId = presetSearchTermsRepositoy.findById(id);
        if (Objects.isNull(byId)) {
            throw new SbcRuntimeException("K-180008");
        }
        //获取比当前排序序号大的数据，然后全部-1，并删除当前的数据
        List<PresetSearchTerms> bySortGreaterThan = presetSearchTermsRepositoy.findBySortGreaterThan(byId.getSort());
        bySortGreaterThan.stream().forEach(item -> item.setSort(item.getSort() - 1));
        presetSearchTermsRepositoy.deleteById(id);
        presetSearchTermsRepositoy.saveAll(bySortGreaterThan);
        setRedis();
    }

    /**
     * 将预置词存储到Redis
     */
    public void setRedis() {
        List<PresetSearchTerms> all = presetSearchTermsRepositoy.findAll();
        all = all.stream().sorted((a, b) -> a.getSort().compareTo(b.getSort())).collect(Collectors.toList());
        redisService.setObj(RedisKeyConstant.PRESET_SEARCH_TERMS_DATA, all, Duration.ofDays(7).getSeconds());
    }

}
