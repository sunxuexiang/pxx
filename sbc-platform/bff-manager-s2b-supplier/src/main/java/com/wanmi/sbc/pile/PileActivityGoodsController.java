package com.wanmi.sbc.pile;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityGoodsProvider;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsAddRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsDeleteRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsModifyRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsPageRequest;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsPageVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(tags = "PileActivityGoodsController", description = "囤货活动商品管理API")
@RequestMapping("/pileActivity/goods")
@Validated
public class PileActivityGoodsController {

    @Autowired
    private PileActivityGoodsProvider pileActivityGoodsProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 新增囤货活动商品
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增囤货活动商品")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse add(@Valid @RequestBody PileActivityGoodsAddRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());
        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "创建囤货活动", "新增囤货活动商品");
        return pileActivityGoodsProvider.add(request);
    }

    /**
     * 修改囤货活动
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改囤货活动商品")
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody PileActivityGoodsModifyRequest request) {
        request.setUpdatePerson(commonUtil.getOperatorId());
        operateLogMQUtil.convertAndSend("营销", "编辑囤货活动商品", "修改囤货活动商品");
        return pileActivityGoodsProvider.modify(request);
    }

    /**
     * 删除囤货活动商品
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "删除囤货活动商品")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public BaseResponse delete(@Valid @RequestBody PileActivityGoodsDeleteRequest request) {
        request.setDeletePerson(commonUtil.getOperatorId());
        operateLogMQUtil.convertAndSend("营销", "删除囤货活动商品", "删除囤货活动商品");
        return pileActivityGoodsProvider.delete(request);
    }


    /**
     * 囤货活动商品列表分页
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "囤货活动商品列表分页")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<PileActivityGoodsPageVO>> page(@RequestBody @Valid PileActivityGoodsPageRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        return pileActivityGoodsProvider.page(request);
    }

}
