package com.wanmi.sbc.goodslabel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.goodslabel.GoodsLabelProvider;
import com.wanmi.sbc.goods.api.provider.goodslabel.GoodsLabelQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodslabelrela.GoodsLabelRelaProvider;
import com.wanmi.sbc.goods.api.provider.goodslabelrela.GoodsLabelRelaQueryProvider;
import com.wanmi.sbc.goods.api.request.goodslabel.*;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaByLabelIdRequest;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaInGoodsIdsRequest;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelAddResponse;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelByIdResponse;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelListResponse;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelModifyResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelRelaVO;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Api(description = "导航配置管理API", tags = "GoodsLabelController")
@RestController
@RequestMapping(value = "/goodslabel")
public class GoodsLabelController {

    @Autowired
    private GoodsLabelQueryProvider goodsLabelQueryProvider;

    @Autowired
    private GoodsLabelProvider goodsLabelProvider;


    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private GoodsLabelRelaQueryProvider goodsLabelRelaQueryProvider;

    @Autowired
    private GoodsLabelRelaProvider goodsLabelRelaProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;




    @ApiOperation(value = "列表查询导航配置")
    @PostMapping("/list")
    public BaseResponse<GoodsLabelListResponse> getList(@RequestBody @Valid GoodsLabelListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        return goodsLabelQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询导航配置")
    @GetMapping("/{id}")
    public BaseResponse<GoodsLabelByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsLabelByIdRequest idReq = new GoodsLabelByIdRequest();
        idReq.setId(id);
        return goodsLabelQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增导航配置")
    @PostMapping("/add")
    public BaseResponse<GoodsLabelAddResponse> add(@RequestBody @Valid GoodsLabelAddRequest addReq) {
        operateLogMQUtil.convertAndSend("导航配置管理", "新增导航配置","新增导航配置：标签名称" + (Objects.nonNull(addReq) ? addReq.getName() : ""));
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setCreatePerson(commonUtil.getOperatorId());
        addReq.setVisible(DefaultFlag.NO);
        return goodsLabelProvider.add(addReq);
    }

    @ApiOperation(value = "修改导航配置")
    @PutMapping("/modify")
    public BaseResponse<GoodsLabelModifyResponse> modify(@RequestBody @Valid GoodsLabelModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("导航配置管理", "修改导航配置","修改导航配置:标签名称" + (Objects.nonNull(modifyReq) ? modifyReq.getName() : ""));
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        GoodsLabelByIdRequest goodsLabelByIdRequest = new GoodsLabelByIdRequest();
        goodsLabelByIdRequest.setId(modifyReq.getId());
        GoodsLabelVO goodsLabelOld = goodsLabelQueryProvider.getById(goodsLabelByIdRequest).getContext().getGoodsLabelVO();
        //状态改变是,刷一下相关商品
        if (!goodsLabelOld.getVisible().equals(modifyReq.getVisible()) || !modifyReq.getImage().equals(goodsLabelOld.getImage()) ) {
            BaseResponse<GoodsLabelModifyResponse> modify = goodsLabelProvider.modify(modifyReq);
            List<GoodsLabelRelaVO> byLabelId = goodsLabelRelaQueryProvider
                    .findByLabelId(GoodsLabelRelaByLabelIdRequest.builder()
                            .labelId(modifyReq.getId()).build()).getContext()
                    .getGoodsLabelRelaVOList();
            //检索ID为当前标签的商品
            if (CollectionUtils.isNotEmpty(byLabelId)) {
                List<String> goodsIds = byLabelId.stream().map(item -> item.getGoodsId()).collect(Collectors.toList());
                esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsIds(goodsIds).build());
            }
            return modify;
        }


        return goodsLabelProvider.modify(modifyReq);
    }

    @ApiOperation(value = "修改导航顺序")
    @PutMapping("/modify-sort")
    public BaseResponse modifySort(@RequestBody @Valid GoodsLabelModifySortRequest request) {
        operateLogMQUtil.convertAndSend("导航配置管理", "修改导航顺序","修改导航顺序");
        return goodsLabelProvider.modifySort(request);
    }

    @ApiOperation(value = "根据id删除导航配置")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("导航配置管理", "根据id删除导航配置","根据id删除导航配置:id" + id);
        //根据标签获取绑定的商品数据
        List<GoodsLabelRelaVO> byLabelId = goodsLabelRelaQueryProvider
                .findByLabelId(GoodsLabelRelaByLabelIdRequest.builder()
                        .labelId(id).build()).getContext()
                .getGoodsLabelRelaVOList();

        //检索ID为当前标签的商品
        if (CollectionUtils.isNotEmpty(byLabelId)) {
            List<String> goodsIds = byLabelId.stream().map(item -> item.getGoodsId()).collect(Collectors.toList());
            //删除绑定的管理 然后更新商品,
            goodsLabelRelaProvider.deleteLabelInGoods(GoodsLabelRelaInGoodsIdsRequest.builder()
                    .goodsIds(goodsIds)
                    .labelId(id)
                    .build());
            //更新ES
            //获取这些商品的信息,并更新ES
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsIds(goodsIds).build());
        }

        GoodsLabelDelByIdRequest delByIdReq = new GoodsLabelDelByIdRequest();
        delByIdReq.setId(id);
        return goodsLabelProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除导航配置")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid GoodsLabelDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("导航配置管理", "根据idList批量删除导航配置","根据idList批量删除导航配置");
        return goodsLabelProvider.deleteByIdList(delByIdListReq);
    }

}
