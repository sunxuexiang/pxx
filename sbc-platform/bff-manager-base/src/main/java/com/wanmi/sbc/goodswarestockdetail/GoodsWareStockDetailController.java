package com.wanmi.sbc.goodswarestockdetail;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.goods.api.provider.goodswarestockdetail.GoodsWareStockDetailProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestockdetail.GoodsWareStockDetailQueryProvider;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.*;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.*;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockDetailVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;


@Api(description = " 库存明细表管理API", tags = "GoodsWareStockDetailController")
@RestController
@RequestMapping(value = "/goods/ware/stock/detail")
public class GoodsWareStockDetailController {

    @Autowired
    private GoodsWareStockDetailQueryProvider goodsWareStockDetailQueryProvider;

    @Autowired
    private GoodsWareStockDetailProvider goodsWareStockDetailProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询 库存明细表")
    @PostMapping("/page")
    public BaseResponse<GoodsWareStockDetailPageResponse> getPage(@RequestBody @Valid GoodsWareStockDetailPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("id", "desc");
        return goodsWareStockDetailQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询 库存明细表")
    @PostMapping("/list")
    public BaseResponse<GoodsWareStockDetailListResponse> getList(@RequestBody @Valid GoodsWareStockDetailListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        return goodsWareStockDetailQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询 库存明细表")
    @GetMapping("/{id}")
    public BaseResponse<GoodsWareStockDetailByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsWareStockDetailByIdRequest idReq = new GoodsWareStockDetailByIdRequest();
        idReq.setId(id);
        return goodsWareStockDetailQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增 库存明细表")
    @PostMapping("/add")
    public BaseResponse<GoodsWareStockDetailAddResponse> add(@RequestBody @Valid GoodsWareStockDetailAddRequest addReq) {
        operateLogMQUtil.convertAndSend("库存明细表管理", "新增库存明细表","新增库存明细表：sku编码" + (Objects.nonNull(addReq) ? addReq.getGoodsInfoNo() : ""));
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setCreatePerson(commonUtil.getOperatorId());
        return goodsWareStockDetailProvider.add(addReq);
    }

    @ApiOperation(value = "修改 库存明细表")
    @PutMapping("/modify")
    public BaseResponse<GoodsWareStockDetailModifyResponse> modify(@RequestBody @Valid GoodsWareStockDetailModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("库存明细表管理", "修改库存明细表","修改库存明细表：sku编码" + (Objects.nonNull(modifyReq) ? modifyReq.getGoodsInfoNo() : ""));
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        return goodsWareStockDetailProvider.modify(modifyReq);
    }

    @ApiOperation(value = "根据id删除 库存明细表")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("库存明细表管理", "根据id删除库存明细表","根据id删除库存明细表：id" + id);
        GoodsWareStockDetailDelByIdRequest delByIdReq = new GoodsWareStockDetailDelByIdRequest();
        delByIdReq.setId(id);
        return goodsWareStockDetailProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除 库存明细表")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid GoodsWareStockDetailDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("库存明细表管理", "根据idList批量删除库存明细表","根据idList批量删除库存明细表");
        return goodsWareStockDetailProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出 库存明细表列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        GoodsWareStockDetailListRequest listReq = JSON.parseObject(decrypted, GoodsWareStockDetailListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        List<GoodsWareStockDetailVO> dataRecords = goodsWareStockDetailQueryProvider.list(listReq).getContext().getGoodsWareStockDetailVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format(" 库存明细表列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("库存明细表管理", "导出库存明细表列表","操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<GoodsWareStockDetailVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
            new Column("商品库存关联表ID", new SpelColumnRender<GoodsWareStockDetailVO>("goodsWareStockId")),
            new Column("导入编码", new SpelColumnRender<GoodsWareStockDetailVO>("goodsInfoNo")),
            new Column("导入类型 0：导入，1：编辑，2：返还，3：下单扣减", new SpelColumnRender<GoodsWareStockDetailVO>("importType")),
            new Column("操作库存", new SpelColumnRender<GoodsWareStockDetailVO>("operateStock")),
            new Column("库存数量", new SpelColumnRender<GoodsWareStockDetailVO>("stock"))
        };
        excelHelper.addSheet(" 库存明细表列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

}
