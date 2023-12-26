package com.wanmi.sbc.account.wallet.service;

import com.wanmi.sbc.account.api.provider.wallet.WalletConfigProvider;
import com.wanmi.sbc.account.api.request.wallet.BalaceSettingRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigAddRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletSettingRequest;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigAddResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletSettingResPonse;
import com.wanmi.sbc.account.bean.dto.WalletConfigDTO;
import com.wanmi.sbc.account.bean.vo.WalletConfigVO;
import com.wanmi.sbc.account.bean.vo.WalletSettingVO;
import com.wanmi.sbc.account.wallet.model.root.BlanceSetting;
import com.wanmi.sbc.account.wallet.model.root.WalletConfig;
import com.wanmi.sbc.account.wallet.repository.BlanceSettingReposiyory;
import com.wanmi.sbc.account.wallet.repository.WalletConfigRepository;
import com.wanmi.sbc.account.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WalletConfigService {
    
    @Autowired
    private WalletConfigRepository walletConfigRepository;

    @Autowired
    private BlanceSettingReposiyory blanceSettingReposiyory;

    /**
     * 编辑品牌、分类、商品等不可消费限制
     * @param request
     * @return
     */
    @Transactional
    public WalletConfigAddResponse editConfig(WalletConfigAddRequest request){
        WalletConfigDTO walletConfig = request.getWalletConfig();
        if(Objects.nonNull(walletConfig)){
            StringBuilder goodsBrandIds = new StringBuilder();
            StringBuilder goodsCateIds = new StringBuilder();
            StringBuilder goodsInfoIds = new StringBuilder();
            //品牌id集合
            List<Long> goodsBrandIdList = walletConfig.getGoodsBrandIds();
            goodsBrandIdList.stream().forEach(goodsBrandId ->{
                goodsBrandIds.append(goodsBrandId).append(",");
            });
            goodsBrandIds.substring(0,goodsBrandIds.length()-1);
            //分类id集合
            List<Long> goodsCateIdList = walletConfig.getGoodsCateIds();
            goodsCateIdList.stream().forEach(goodsCateId ->{
                goodsCateIds.append(goodsCateId).append(",");
            });
            goodsCateIds.substring(0,goodsCateIds.length()-1);
            //商品id集合
            List<String> goodsInfoIdList = walletConfig.getGoodsInfoIds();
            goodsInfoIdList.stream().forEach(goodsInfoId ->{
                goodsInfoIds.append(goodsInfoId).append(",");
            });
            goodsInfoIds.substring(0,goodsInfoIds.length()-1);
            WalletConfig config = walletConfigRepository.getWalletConfigById(walletConfig.getId());
            config.setGoodsBrandIds(goodsBrandIds.toString());
            config.setGoodsCateIds(goodsCateIds.toString());
            config.setGoodsInfoIds(goodsInfoIds.toString());
            walletConfigRepository.save(config);
            return WalletConfigAddResponse.builder().walletConfigVO(KsBeanUtil.convert(walletConfig, WalletConfigVO.class)).build();
        }
        return WalletConfigAddResponse.builder().walletConfigVO(new WalletConfigVO()).build();
    }


    public WalletConfigResponse getWalletConfig(WalletConfigRequest request){
        List<WalletConfig> walletConfigs = walletConfigRepository.getWalletConfigAll();
        List<WalletConfigVO> walletConfigVOS = walletConfigs.stream().map(walletConfig -> {
            WalletConfigVO vo = new WalletConfigVO();
            List<Long> goodsBrandIdList = new ArrayList<>();
            List<Long> goodsCateIdList = new ArrayList<>();
            List<String> goodsInfoIdList = new ArrayList<>();
            String goodsBrandIds = walletConfig.getGoodsBrandIds();
            if(StringUtils.isNotEmpty(goodsBrandIds)){
                String[] goodsBrandIdArray = goodsBrandIds.split(",");
                for (String s : goodsBrandIdArray) {
                    if (StringUtils.isNotEmpty(s)) {
                        goodsBrandIdList.add(Long.valueOf(s));
                    }
                }
                vo.setGoodsBrandIds(goodsBrandIdList);
            }
            String goodsCateIds = walletConfig.getGoodsCateIds();
            if(StringUtils.isNotEmpty(goodsCateIds)){
                String[] goodsCateIdArray = goodsCateIds.split(",");
                for (String s : goodsCateIdArray) {
                    if (StringUtils.isNotEmpty(s)) {
                        goodsCateIdList.add(Long.valueOf(s));
                    }
                }
                vo.setGoodsCateIds(goodsCateIdList);
            }
            String goodsInfoIds = walletConfig.getGoodsInfoIds();
            if(StringUtils.isNotEmpty(goodsInfoIds)){
                String[] goodsInfoIdArray = goodsInfoIds.split(",");
                for (String s : goodsInfoIdArray) {
                    if (StringUtils.isNotEmpty(s)) {
                        goodsInfoIdList.add(s);
                    }
                }
                vo.setGoodsInfoIds(goodsInfoIdList);
            }
            vo.setId(walletConfig.getId());
            return vo;
        }).collect(Collectors.toList());
        return WalletConfigResponse.builder().walletConfigVO(walletConfigVOS).build();
    }

    /**
     * 获取余额说明
     * @return
     */
    public BaseResponse<WalletSettingResPonse> getWalletSettingByKey(BalaceSettingRequest request){
        //WalletSettingVO
        List<WalletSettingVO> list = new ArrayList<>();
        WalletSettingResPonse resPonse = new WalletSettingResPonse();

        BlanceSetting byTypeBALANCEAGREEMENT = blanceSettingReposiyory.findBySettingKey(request.getSettingKey());
        WalletSettingVO BALANCEAGREEMENT = new WalletSettingVO();
        BeanUtils.copyProperties(byTypeBALANCEAGREEMENT,BALANCEAGREEMENT);
        list.add(BALANCEAGREEMENT);

        resPonse.setResponse(list);
        return BaseResponse.success(resPonse);
    }

    public BaseResponse saveWalletSetting(WalletSettingRequest request) {

        request.getResponse().forEach(item->{
            BlanceSetting bySettingKey = blanceSettingReposiyory.findBySettingKey(item.getSettingKey());
            bySettingKey.setSettingValue(item.getSettingValue());
            blanceSettingReposiyory.save(bySettingKey);
        });

        return BaseResponse.SUCCESSFUL();
    }

}
