package com.wanmi.sbc.hotstylemoments;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.hotstylemoments.response.HotStyleMomentsDetailResponse;
import com.wanmi.sbc.setting.api.provider.hotstylemoments.HotStyleMomentsProvider;
import com.wanmi.sbc.setting.api.provider.hotstylemoments.HotStyleMomentsQueryProvider;
import com.wanmi.sbc.setting.api.request.hotstylemoments.*;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsListResponse;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsPageResponse;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsConfigVO;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 爆款时刻API
 * @author: XinJiang
 * @time: 2022/5/10 0:01
 */
@Api(description = "爆款时刻API",tags = "HotStyleMomentsController")
@RestController
@RequestMapping(value = "/hot/style/moments")
public class HotStyleMomentsController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private HotStyleMomentsProvider hotStyleMomentsProvider;

    @Autowired
    private HotStyleMomentsQueryProvider hotStyleMomentsQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "校验活动时间是否重复（新增、修改都需要校验）")
    @PostMapping("/check-time")
    public BaseResponse checkTime(@RequestBody @Valid HotStyleMomentsCheckTimeRequest request){
        HotStyleMomentsListResponse response = hotStyleMomentsQueryProvider.checkTime(request).getContext();
        if (CollectionUtils.isNotEmpty(response.getHotStyleMomentsVOS())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "爆款时刻开始时间、结束时间与其他活动时间冲突重复！请重新选择日期");
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "校验活动时间是否重复（新增、修改都需要校验）", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "新增爆款时刻")
    @PostMapping
    public BaseResponse add(@RequestBody @Valid HotStyleMomentsAddRequest request){
        Operator operator = commonUtil.getOperator();
        request.setCreatePerson(operator.getAccount());
        request.setCreateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        request.setIsPause(DefaultFlag.NO);
        request.setTerminationFlag(DefaultFlag.NO);
        BaseResponse add = hotStyleMomentsProvider.add(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "新增爆款时刻", "操作成功");
        return add;
    }

    @ApiOperation(value = "修改爆款时刻")
    @PutMapping
    public BaseResponse modify(@RequestBody @Valid HotStyleMomentsModifyRequest request) {
        Operator operator = commonUtil.getOperator();
        request.setUpdatePerson(operator.getAccount());
        request.setUpdateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        BaseResponse modify = hotStyleMomentsProvider.modify(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "修改爆款时刻", "操作成功");
        return modify;
    }

    @ApiOperation(value = "删除爆款时刻（进行中、暂停不能删除）")
    @DeleteMapping
    public BaseResponse delById(@RequestBody @Valid HotStyleMomentsDelByIdRequest request){
        if (Objects.isNull(request.getHotId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR, "主键id不能为空！");
        }
        Operator operator = commonUtil.getOperator();
        request.setDeleteTime(LocalDateTime.now());
        request.setDeletePerson(operator.getAccount());
        BaseResponse baseResponse = hotStyleMomentsProvider.delById(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "删除爆款时刻（进行中、暂停不能删除）", "操作成功");
        return baseResponse;
    }

    @ApiOperation(value = "批量删除爆款时刻")
    @DeleteMapping("/del-by-ids")
    public BaseResponse delByIds(@RequestBody @Valid HotStyleMomentsDelByIdRequest request){
        if (CollectionUtils.isEmpty(request.getHotIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR, "主键id集合不能为空！");
        }
        Operator operator = commonUtil.getOperator();
        request.setDeleteTime(LocalDateTime.now());
        request.setDeletePerson(operator.getAccount());
        BaseResponse baseResponse = hotStyleMomentsProvider.delById(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "批量删除爆款时刻", "操作成功");
        return baseResponse;
    }

    @ApiOperation(value = "终止爆款时刻活动（只有进行中的活动才有终止按钮）")
    @PostMapping("/termination-by-id")
    public BaseResponse terminationById(@RequestBody @Valid HotStyleMomentsTerminationRequest request) {
        hotStyleMomentsProvider.terminationById(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "终止爆款时刻活动（只有进行中的活动才有终止按钮）", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "启动或暂停爆款时刻活动（只有进行中的才有启动/暂停按钮）")
    @PostMapping("/pause-by-id")
    public BaseResponse pauseById(@RequestBody @Valid HotStyleMomentsPauseRequest request) {
        hotStyleMomentsProvider.pauseById(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "启动或暂停爆款时刻活动（只有进行中的才有启动/暂停按钮）", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "提前开始爆款时刻")
    @PostMapping("/early/start/{hotId}")
    public BaseResponse earlyStart(@PathVariable("hotId") Long hotId) {
        hotStyleMomentsProvider.earlyStart(hotId);
        //操作日志记录
        operateLogMQUtil.convertAndSend("爆款时刻", "提前开始爆款时刻", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "通过id获取爆款时刻信息")
    @PostMapping("/get-by-id")
    public BaseResponse<HotStyleMomentsDetailResponse> getById(@RequestBody @Valid HotStyleMomentsGetByIdRequest request) {
        HotStyleMomentsDetailResponse response = new HotStyleMomentsDetailResponse();
        //获取爆款信息
        response.setHotStyleMomentsVO(KsBeanUtil.convert(hotStyleMomentsQueryProvider.getById(request).getContext(), HotStyleMomentsVO.class));
        //爆款信息sku集合
        List<String> skuIds = response.getHotStyleMomentsVO().getHotStyleMomentsConfigs()
                .stream().map(HotStyleMomentsConfigVO::getGoodsInfoId).collect(Collectors.toList());
        //爆款信息sku商品排序map
        Map<String,Integer> sortMap = response.getHotStyleMomentsVO().getHotStyleMomentsConfigs().stream()
                .collect(Collectors.toMap(HotStyleMomentsConfigVO::getGoodsInfoId, HotStyleMomentsConfigVO::getSortNum, (k1,k2) -> k1));
        //散批商品信息
        GoodsInfoViewByIdsResponse goodsResponse = retailGoodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(skuIds).build()).getContext();
        //排序赋值
        goodsResponse.getGoodsInfos().forEach(goodsInfoVO -> {
            goodsInfoVO.setSortNumKey(sortMap.getOrDefault(goodsInfoVO.getGoodsInfoId(),null));
        });
        //升序
        goodsResponse.getGoodsInfos().sort(Comparator.comparing(GoodsInfoVO::getSortNumKey));
        response.setGoods(goodsResponse.getGoodses());
        response.setGoodsInfos(goodsResponse.getGoodsInfos());
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "根据条件获取爆款时刻列表信息")
    @PostMapping("/get-list")
    public BaseResponse<HotStyleMomentsListResponse> getList(@RequestBody @Valid HotStyleMomentsQueryRequest request) {
        request.setDelFlag(DeleteFlag.NO);
        request.putSort("createTime", SortType.DESC.toValue());
        HotStyleMomentsListResponse response = hotStyleMomentsQueryProvider.getList(request).getContext();
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "分页获取爆款时刻信息")
    @PostMapping("/get-page")
    public BaseResponse<HotStyleMomentsPageResponse> getPage(@RequestBody @Valid HotStyleMomentsQueryRequest request) {
        request.setDelFlag(DeleteFlag.NO);
        request.putSort("createTime", SortType.DESC.toValue());
        return  BaseResponse.success(hotStyleMomentsQueryProvider.getPage(request).getContext());
    }
}
