package com.wanmi.sbc.setting.provider.impl.wechatauth;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.MD5Util;
import com.wanmi.sbc.setting.api.provider.WechatAuthProvider;
import com.wanmi.sbc.setting.api.request.DistributionMiniProgramRequest;
import com.wanmi.sbc.setting.api.request.MiniProgramQrCodeRequest;
import com.wanmi.sbc.setting.api.request.MiniProgramSetRequest;
import com.wanmi.sbc.setting.api.request.ShareMiniProgramRequest;
import com.wanmi.sbc.setting.api.response.MiniProgramSetGetResponse;
import com.wanmi.sbc.setting.config.Config;
import com.wanmi.sbc.setting.redis.RedisService;
import com.wanmi.sbc.setting.wechat.WechatApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by feitingting on 2018/12/28.
 */

@Slf4j
@RestController
public class WechatAuthController implements WechatAuthProvider {

    @Autowired
    private WechatApiUtil wechatApiUtil;

    @Autowired
    private RedisService redisService;


    /**
     * 生成小程序二维码
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<String> getWxaCodeUnlimit(@RequestBody MiniProgramQrCodeRequest request) {
        String imageURL = "";
        BaseResponse<MiniProgramSetGetResponse> baseResponse = getMiniProgramSet();
        if(baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)){
            //开关启用的时候，采取生成小程序码
            if(baseResponse.getContext().getStatus().equals(1)){
                imageURL = wechatApiUtil.getWxaCodeUnlimit(request, "PUBLIC");
            }
        }
        return BaseResponse.success(imageURL);
    }


    /**
     * 获取小程序基础配置信息
     *
     * @return
     */
    @Override
    public BaseResponse<MiniProgramSetGetResponse> getMiniProgramSet() {
        MiniProgramSetGetResponse response = new MiniProgramSetGetResponse();
        try {

            Config config = wechatApiUtil.getMiniProgramSet();
            KsBeanUtil.copyPropertiesThird(config, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResponse.success(response);
    }


    @Override
    public BaseResponse updateMiniProgramSet(@RequestBody MiniProgramSetRequest request) {
        if (StringUtils.isBlank(request.getAppId()) || request.getAppId().length() > 50) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (StringUtils.isBlank(request.getAppSecret()) || request.getAppSecret().length() > 50) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return wechatApiUtil.updateMiniProgramSet(request);
    }


    /**
     * 社交分校里面生成小程序码
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<String> distributionMiniProgram(@RequestBody DistributionMiniProgramRequest request) {
        String url = null;
        //渠道不为空，分享赚的场景，为店内分享或店外分享
        if (StringUtils.isNotBlank(request.getChannel())) {
            //店外分享
            if (request.getChannel().equals("mall")) {
                url = String.format("/pages/package-B/goods/goods-details/index?skuId=%s&channel=%s&inviteeId=%s",
                        request.getSkuId(), request.getChannel(), request.getInviteeId());
            } else {
                //店内分享,要有spuId
                url = String.format("/shop-index/goods-detail/%s/%s/%s?channel=%s",
                        request.getInviteeId(), request.getSpuId(), request.getSkuId(), request.getChannel());
            }
        }

        //tag标识不为空，说明是邀新或是分享店铺
        if (StringUtils.isNotBlank(request.getTag())) {
            //分享店铺
            if (request.getTag().equals("shop")) {
                url = String.format("/shop-index-c/%s", request.getInviteeId());
            } else if(request.getTag().equals("register")) {
                //分销员邀新
                url = String.format("/pages/package-A/login/register/index?inviteeId=%s", request.getInviteeId());
            }else if(request.getTag().equals("jobNo")) {
                //业务员邀新
                url = String.format("/pages/package-A/login/register/index?jobNo=%s", request.getJobNo());
            } else if(request.getTag().equals("video")){
                //视频分享
                url = String.format("/pages/package-D/video-play/index?videoId=%s",request.getVideoId());
            }else {
                //分销员邀新
                url = String.format("/pages/package-A/login/register/index?inviteeId=%s", request.getInviteeId());
            }
        }

        log.info("url==============: {}", url);

        //把链接加密，生成redisKey，作为参数传递，用来生成小程序码
        String redisKey = (MD5Util.md5Hex(url, "utf-8")).toUpperCase().substring(16);

        log.info("redisKey================: {}", redisKey);

        if (StringUtils.isNotBlank(redisKey)) {
            redisService.setString(redisKey, url, 15000000L);
        }

        MiniProgramQrCodeRequest miniProgramQrCodeRequest = new MiniProgramQrCodeRequest();
        miniProgramQrCodeRequest.setIs_hyaline(true);
        miniProgramQrCodeRequest.setPage("pages/sharepage/sharepage");
        //添加分销标识，方便小程序解析
        miniProgramQrCodeRequest.setScene("FX" + redisKey);
        if(request.getSaasStatus()!=null
                && request.getSaasStatus()){
            miniProgramQrCodeRequest.setSaasStatus(Boolean.TRUE);
            miniProgramQrCodeRequest.setStoreId(request.getStoreId());
        }
        return getWxaCodeUnlimit(miniProgramQrCodeRequest);
    }

    /**
     * 商品详情页分享生成小程序码
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<String> getMiniProgramQrCodeWithShareUserId(@RequestBody ShareMiniProgramRequest request) {
        String url = String.format("/goods-detail/%s?shareUserId=%s", request.getSkuId(), request.getShareUserId());

        //把链接加密，生成redisKey，作为参数传递，用来生成小程序码
        String redisKey = (MD5Util.md5Hex(url, "utf-8")).toUpperCase().substring(16);

        if (StringUtils.isNotBlank(redisKey)) {
            redisService.setString(redisKey, url, 15000000L);
        }

        MiniProgramQrCodeRequest miniProgramQrCodeRequest = new MiniProgramQrCodeRequest();
        miniProgramQrCodeRequest.setIs_hyaline(true);
        miniProgramQrCodeRequest.setPage("pages/sharepage/sharepage");
        //添加分享标识，方便小程序解析
        miniProgramQrCodeRequest.setScene("SHARE:" + redisKey);
        if(request.getSaasStatus()!=null
                && request.getSaasStatus()){
            miniProgramQrCodeRequest.setSaasStatus(Boolean.TRUE);
            miniProgramQrCodeRequest.setStoreId(request.getStoreId());
        }
        return getWxaCodeUnlimit(miniProgramQrCodeRequest);
    }
}

