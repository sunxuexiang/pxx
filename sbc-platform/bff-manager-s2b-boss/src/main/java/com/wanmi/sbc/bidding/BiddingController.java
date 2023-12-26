package com.wanmi.sbc.bidding;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.bidding.BiddingQueryProvider;
import com.wanmi.sbc.goods.api.provider.bidding.BiddingProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.request.bidding.*;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandByIdsRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdsRequest;
import com.wanmi.sbc.goods.api.response.bidding.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.wanmi.sbc.goods.api.response.brand.GoodsBrandByIdsResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.BiddingGoodsVO;
import com.wanmi.sbc.goods.bean.vo.BiddingVO;
import com.wanmi.sbc.mq.GoodsBiddingMqConsumerService;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(description = "竞价配Hi管理API", tags = "BiddingController")
@RestController
@RequestMapping(value = "/bidding")
public class BiddingController {

    @Autowired
    private BiddingQueryProvider biddingQueryProvider;

    @Autowired
    private BiddingProvider biddingProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private GoodsBiddingMqConsumerService goodsBiddingMqConsumerService;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询竞价配Hi")
    @PostMapping("/page")
    public BaseResponse<BiddingPageResponse> getPage(@RequestBody @Valid BiddingPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("biddingId", "desc");
        return biddingQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询竞价配Hi")
    @PostMapping("/list")
    public BaseResponse<BiddingListResponse> getList(@RequestBody @Valid BiddingListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("biddingId", "desc");
        return biddingQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询竞价配Hi")
    @GetMapping("/{biddingId}")
    public BaseResponse<BiddingByIdResponse> getById(@PathVariable String biddingId) {
        if (biddingId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        BiddingByIdRequest idReq = new BiddingByIdRequest();
        idReq.setBiddingId(biddingId);
        BiddingByIdResponse response = biddingQueryProvider.getById(idReq).getContext();
        if(Objects.nonNull(response)
                && Objects.nonNull(response.getBiddingVO())
                && CollectionUtils.isNotEmpty(response.getBiddingVO().getBiddingGoodsVOS())){
            List<BiddingGoodsVO> biddingGoodsVOS = response.getBiddingVO().getBiddingGoodsVOS();
            Set<Long> storeIds = new HashSet();
            Set<Long> cateIds = new HashSet();
            Set<Long> brandIds = new HashSet<>();
            biddingGoodsVOS.stream().forEach(b -> {
                storeIds.add(b.getGoodsInfo().getStoreId());
                cateIds.add(b.getGoodsInfo().getCateId());
                brandIds.add(b.getGoodsInfo().getBrandId());
            });
            //商家名称
            if(CollectionUtils.isNotEmpty(storeIds)){
                ListStoreByIdsResponse storeByIdsResponse = storeQueryProvider
                        .listByIds(ListStoreByIdsRequest.builder().storeIds(new ArrayList<>(storeIds)).build()).getContext();
                Map<Long, StoreVO> storeVOMap = storeByIdsResponse.getStoreVOList().stream().collect(Collectors.toMap(StoreVO::getStoreId,g->g));
                response.getBiddingVO().getBiddingGoodsVOS().stream()
                        .forEach(g->g.getGoodsInfo().setStoreName(storeVOMap.get(g.getGoodsInfo().getStoreId()).getStoreName()));
            }
            //分类名称
            if(CollectionUtils.isNotEmpty(cateIds)){
                GoodsCateByIdsResponse goodsCateByIdsResponse = goodsCateQueryProvider
                        .getByIds(new GoodsCateByIdsRequest(new ArrayList<>(cateIds))).getContext();
                response.setGoodsCateVOS(goodsCateByIdsResponse.getGoodsCateVOList());
            }
            //品牌名称
            if(CollectionUtils.isNotEmpty(brandIds)){
                GoodsBrandByIdsResponse goodsBrandByIdsResponse = goodsBrandQueryProvider.listByIds(GoodsBrandByIdsRequest.builder()
                        .brandIds(new ArrayList<>(brandIds)).build()).getContext();
                response.setGoodsBrandVOS(goodsBrandByIdsResponse.getGoodsBrandVOList());
            }
        }
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "新增竞价配Hi")
    @PostMapping("/add")
    @LcnTransaction
    public BaseResponse<BiddingAddResponse> add(@RequestBody @Valid BiddingAddRequest addReq) {
        addReq.setDelFlag(DeleteFlag.NO);
        BiddingAddResponse addResponse = biddingProvider.add(addReq).getContext();
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "新增竞价配Hi", "操作成功");
        return BaseResponse.success(addResponse);
    }

    @ApiOperation(value = "修改竞价配Hi")
    @PutMapping("/modify")
    @LcnTransaction
    public BaseResponse<BiddingModifyResponse> modify(@RequestBody @Valid BiddingModifyRequest modifyReq) {
        BiddingModifyResponse modifyResponse = biddingProvider.modify(modifyReq).getContext();
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "修改竞价配Hi", "操作成功");
        return BaseResponse.success(modifyResponse);
    }

    @ApiOperation(value = "根据id删除竞价配Hi")
    @DeleteMapping("/{biddingId}")
    public BaseResponse deleteById(@PathVariable String biddingId) {
        if (biddingId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        BiddingDelByIdRequest delByIdReq = new BiddingDelByIdRequest();
        delByIdReq.setBiddingId(biddingId);
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "根据id删除竞价配Hi", "删除竞价配Hi");
        return biddingProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除竞价配Hi")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid BiddingDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "根据idList批量删除竞价配Hi", "批量删除竞价配Hi");
        return biddingProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出竞价配Hi列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        BiddingListRequest listReq = JSON.parseObject(decrypted, BiddingListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("biddingId", "desc");
        List<BiddingVO> dataRecords = biddingQueryProvider.list(listReq).getContext().getBiddingVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("竞价配Hi列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "导出竞价配Hi列表", "导出竞价配Hi列表");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<BiddingVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
            new Column("关键字,分类", new SpelColumnRender<BiddingVO>("keywords")),
            new Column("竞价类型0:关键字，1:分类", new SpelColumnRender<BiddingVO>("biddingType")),
            new Column("竞价的状态：0:未开始，1:进行中，2:已结束", new SpelColumnRender<BiddingVO>("biddingStatus")),
            new Column("开始时间", new SpelColumnRender<BiddingVO>("startTime")),
            new Column("修改时间", new SpelColumnRender<BiddingVO>("modifyTime")),
            new Column("结束时间", new SpelColumnRender<BiddingVO>("endTime"))
        };
        excelHelper.addSheet("竞价配Hi列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

    /**
     * 校验关键字重复
     * @return
     */
    @ApiOperation(value = "校验关键字重复")
    @PostMapping("/validate/keywords")
    public BaseResponse<BiddingValidateResponse> validateBiddingKeywords(@RequestBody @Valid BiddingValidateKeywordsRequest request){
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "校验关键字重复", "校验关键字重复");
        return biddingQueryProvider.validatekeywords(request);
    }

    /**
     * 校验竞价商品是否重复
     * @return
     */
    @ApiOperation(value = "校验竞价商品是否重复")
    @PostMapping("/validate/sku")
    public BaseResponse<BiddingValidateResponse> validateBiddingGoods(@RequestBody @Valid BiddingValidateGoodsRequest request){
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "校验竞价商品是否重复", "校验竞价商品是否重复");
        return biddingQueryProvider.validateBiddingGoods(request);
    }

    /**
     * 校验竞价商品是否重复
     * @return
     */
    @ApiOperation(value = "校验竞价商品是否重复")
    @GetMapping("/start/activity/{id}")
    public BaseResponse validateBiddingGoods(@PathVariable String id){
        goodsBiddingMqConsumerService.startBiddingActivity(id);
        operateLogMQUtil.convertAndSend("竞价配Hi管理", "校验竞价商品是否重复", "校验成功");
        return BaseResponse.SUCCESSFUL();
    }

}
