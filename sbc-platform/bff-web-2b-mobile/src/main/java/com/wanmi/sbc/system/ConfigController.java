package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.WechatAuthProvider;
import com.wanmi.sbc.setting.api.provider.viewversionconfig.ViewVersionConfigProvider;
import com.wanmi.sbc.setting.api.request.MiniProgramQrCodeRequest;
import com.wanmi.sbc.setting.api.response.GoodsDisplayConfigGetResponse;
import com.wanmi.sbc.setting.bean.vo.ViewVersionConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 获取后台配置项
 * Created by xmn on 2018/10/10.
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Resource
    private AuditQueryProvider auditQueryProvider;

    @Resource
    private WechatAuthProvider wechatAuthProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ViewVersionConfigProvider viewVersionConfigProvider;


    /**
     * 前台商品列表默认展示维度（大小图、spu |sku）
     *
     * @return
     */
    @RequestMapping(value = "/goodsDisplayDefault", method = RequestMethod.GET)
    public BaseResponse<GoodsDisplayConfigGetResponse> listConfigs() {
        return auditQueryProvider.getGoodsDisplayConfigForMobile();
    }


    /**
     * 获取某个商品的小程序码
     * @return
     */
    @RequestMapping(value = "/getSkuQrCode/{skuId}", method = RequestMethod.GET)
    public BaseResponse<String> getSkuQrCode(@PathVariable String skuId) {
        MiniProgramQrCodeRequest request = new MiniProgramQrCodeRequest();
        request.setPage("pages/sharepage/sharepage");
        request.setScene(skuId);
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainInfo)){
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        return wechatAuthProvider.getWxaCodeUnlimit(request);
    }


    /**
     * 获取数据看板最新版本号
     *
     * @return
     */
    @ApiOperation(value = "获取数据看板最新版本号")
    @GetMapping(value = "/view/version")
    public BaseResponse<ViewVersionConfigVO> getViewVersion(@RequestParam("systemType") String systemType, @RequestParam(value = "currentVersion",required = false) String currentVersion) {
        return viewVersionConfigProvider.getViewVersion(systemType, currentVersion);
    }
}
