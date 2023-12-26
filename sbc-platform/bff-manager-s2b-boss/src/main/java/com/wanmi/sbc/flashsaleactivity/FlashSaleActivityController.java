package com.wanmi.sbc.flashsaleactivity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.goods.api.provider.flashsaleactivity.FlashSaleActivityQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.flashsaleactivity.FlashSaleActivityListRequest;
import com.wanmi.sbc.goods.api.request.flashsaleactivity.FlashSaleActivityPageRequest;
import com.wanmi.sbc.goods.api.request.flashsalegoods.FlashSaleGoodsPageRequest;
import com.wanmi.sbc.goods.api.response.flashsaleactivity.FlashSaleActivityListResponse;
import com.wanmi.sbc.goods.api.response.flashsaleactivity.FlashSaleActivityPageResponse;
import com.wanmi.sbc.goods.api.response.flashsalegoods.FlashSaleGoodsPageResponse;
import com.wanmi.sbc.goods.bean.vo.FlashSaleActivityVO;
import com.wanmi.sbc.goods.bean.vo.FlashSaleGoodsVO;
import com.wanmi.sbc.setting.api.provider.flashsalesetting.FlashSaleSettingQueryProvider;
import com.wanmi.sbc.setting.api.request.flashsalesetting.FlashSaleSettingListRequest;
import com.wanmi.sbc.setting.bean.vo.FlashSaleSettingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Api(description = "秒杀活动列表管理API", tags = "FlashSaleActivityController")
@RestController
@RequestMapping(value = "/flashsaleactivity")
public class FlashSaleActivityController {

    @Autowired
    private FlashSaleActivityQueryProvider flashSaleActivityQueryProvider;

    @Autowired
    private FlashSaleSettingQueryProvider flashSaleSettingQueryProvider;

    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @ApiOperation(value = "列表查询即将开场活动列表")
    @PostMapping("/soonlist")
    public BaseResponse<FlashSaleActivityListResponse> getSoonList(@RequestBody @Valid FlashSaleActivityListRequest listReq) {
        //查询最近一个月秒杀活动列表
        if (listReq.getFullTimeBegin() == null) {
            listReq.setFullTimeBegin(LocalDateTime.now());
            listReq.setFullTimeEnd(LocalDateTime.now().plusDays(30));
        } else {
            // 如果是今天
            if(listReq.getFullTimeBegin().toLocalDate().isEqual(LocalDate.now())) {
                listReq.setFullTimeEnd(listReq.getFullTimeBegin().plusDays(1).minusHours(1));
                listReq.setFullTimeBegin(LocalDateTime.now());
            }else{
                listReq.setFullTimeEnd(listReq.getFullTimeBegin().plusDays(1).minusHours(1));
            }

        }

        List<FlashSaleActivityVO> flashSaleActivityVOList = flashSaleActivityQueryProvider.list(listReq).getContext()
                .getFlashSaleActivityVOList();

        //查询秒杀场次设置
        FlashSaleSettingListRequest request = new FlashSaleSettingListRequest();
        request.setDelFlag(DeleteFlag.NO);
        request.setStatus(EnableStatus.ENABLE);
        request.putSort("time", "asc");
        List<FlashSaleSettingVO> flashSaleSettingVOList = flashSaleSettingQueryProvider.list(request).getContext()
                .getFlashSaleSettingVOList();

        List<FlashSaleActivityVO> result = new ArrayList<>();
        FlashSaleActivityVO activityVO;
        for (int day = 0; day < 30; day++) {
            String date = DateUtil.getDate(LocalDateTime.now().plusDays(day));
            for (FlashSaleSettingVO flashSaleSettingVO : flashSaleSettingVOList) {
                LocalDateTime fullTime = LocalDateTime.parse(date + " " + flashSaleSettingVO.getTime(),
                        DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_2));
                if (fullTime.isBefore(listReq.getFullTimeBegin()) || fullTime.isAfter(listReq.getFullTimeEnd())) {
                    continue;
                }
                activityVO = new FlashSaleActivityVO();
                activityVO.setActivityDate(date);
                activityVO.setActivityTime(flashSaleSettingVO.getTime());
                activityVO.setActivityFullTime(fullTime);
                activityVO.setActivityEndTime(fullTime.plusHours(Constants.FLASH_SALE_LAST_HOUR));
                activityVO.setStoreNum(0);
                activityVO.setGoodsNum(0);
                Optional<FlashSaleActivityVO> flashSaleActivityVO = flashSaleActivityVOList.stream().filter(f -> f
                        .getActivityDate().equals(date) &&
                        f.getActivityTime()
                                .equals(flashSaleSettingVO.getTime())).findFirst();
                if (flashSaleActivityVO.isPresent()) {
                    activityVO.setGoodsNum(flashSaleActivityVO.get().getGoodsNum());
                    activityVO.setStoreNum(flashSaleActivityVO.get().getStoreNum());
                }
                result.add(activityVO);
            }
        }

        return BaseResponse.success(FlashSaleActivityListResponse.builder().flashSaleActivityVOList(result).build());
    }

    @ApiOperation(value = "列表查询进行中活动列表")
    @PostMapping("/salelist")
    public BaseResponse<FlashSaleActivityPageResponse> getSaleList(@RequestBody @Valid FlashSaleActivityPageRequest pageRequest) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(Constants.FLASH_SALE_LAST_HOUR);
        LocalDateTime endTime = LocalDateTime.now();
        pageRequest.setFullTimeBegin(startTime);
        pageRequest.setFullTimeEnd(endTime);

        return flashSaleActivityQueryProvider.page(pageRequest);
    }

    @ApiOperation(value = "列表查询已结束活动列表")
    @PostMapping("/endlist")
    public BaseResponse<FlashSaleActivityPageResponse> getEndList(@RequestBody @Valid FlashSaleActivityPageRequest pageRequest) {
        if (pageRequest.getFullTimeBegin() != null) {
            // 如果是今天
            if(pageRequest.getFullTimeBegin().toLocalDate().isEqual(LocalDate.now())) {
                LocalDateTime endTime = LocalDateTime.now().minusHours(Constants.FLASH_SALE_LAST_HOUR);
                pageRequest.setFullTimeEnd(endTime);
            }else {
                pageRequest.setFullTimeEnd(pageRequest.getFullTimeBegin().plusDays(1).minusHours(1));
            }
        } else {
            LocalDateTime endTime = LocalDateTime.now().minusHours(Constants.FLASH_SALE_LAST_HOUR);
            pageRequest.setFullTimeEnd(endTime);
        }
        pageRequest.putSort("activity_full_time","desc");

        return flashSaleActivityQueryProvider.page(pageRequest);
    }

    @ApiOperation(value = "列表查询已结束活动列表")
    @PostMapping("/detail")
    public BaseResponse<FlashSaleGoodsPageResponse> getDetail(@RequestBody @Valid FlashSaleGoodsPageRequest req) {

        req.putSort("activityFullTime", "asc");
        req.putSort("id", "asc");
        FlashSaleGoodsPageResponse response = flashSaleGoodsQueryProvider.page(req).getContext();
        List<Long> storeIds = response.getFlashSaleGoodsVOPage().getContent().stream().map(FlashSaleGoodsVO::getStoreId)
                .collect(Collectors.toList());
        ListStoreByIdsResponse storeResponse =
                storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(storeIds).build()).getContext();

        response.getFlashSaleGoodsVOPage().getContent().forEach(flashSaleGoodsVO -> {
            storeResponse.getStoreVOList().forEach(storeVO -> {
                if (flashSaleGoodsVO.getStoreId().equals(storeVO.getStoreId())) {
                    flashSaleGoodsVO.setStoreName(storeVO.getStoreName());
                }
            });
        });
        return BaseResponse.success(response);
    }
}