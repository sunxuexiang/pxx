package com.wanmi.sbc.warehousecity;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.goods.api.provider.warehousecity.WareHouseCityProvider;
import com.wanmi.sbc.goods.api.provider.warehousecity.WareHouseCityQueryProvider;
import com.wanmi.sbc.goods.api.request.warehousecity.*;
import com.wanmi.sbc.goods.api.response.warehousecity.*;
import com.wanmi.sbc.goods.bean.vo.WareHouseCityVO;
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


@Api(description = " 仓库地区表管理API", tags = "WareHouseCityController")
@RestController
@RequestMapping(value = "/ware/house/city")
public class WareHouseCityController {

    @Autowired
    private WareHouseCityQueryProvider wareHouseCityQueryProvider;

    @Autowired
    private WareHouseCityProvider wareHouseCityProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询 仓库地区表")
    @PostMapping("/page")
    public BaseResponse<WareHouseCityPageResponse> getPage(@RequestBody @Valid WareHouseCityPageRequest pageReq) {
        pageReq.putSort("id", "desc");
        return wareHouseCityQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询 仓库地区表")
    @PostMapping("/list")
    public BaseResponse<WareHouseCityListResponse> getList(@RequestBody @Valid WareHouseCityListRequest listReq) {
        listReq.putSort("id", "desc");
        return wareHouseCityQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询 仓库地区表")
    @GetMapping("/{id}")
    public BaseResponse<WareHouseCityByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        WareHouseCityByIdRequest idReq = new WareHouseCityByIdRequest();
        idReq.setId(id);
        return wareHouseCityQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增 仓库地区表")
    @PostMapping("/add")
    public BaseResponse<WareHouseCityAddResponse> add(@RequestBody @Valid WareHouseCityAddRequest addReq) {
        operateLogMQUtil.convertAndSend("仓库地区表管理", "新增仓库地区表", "新增仓库地区表：仓库id" + (Objects.nonNull(addReq) ? addReq.getWareId() : ""));
        return wareHouseCityProvider.add(addReq);
    }

    @ApiOperation(value = "修改 仓库地区表")
    @PutMapping("/modify")
    public BaseResponse<WareHouseCityModifyResponse> modify(@RequestBody @Valid WareHouseCityModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("仓库地区表管理", "修改仓库地区表", "修改仓库地区表：仓库id" + (Objects.nonNull(modifyReq) ? modifyReq.getWareId() : ""));
        return wareHouseCityProvider.modify(modifyReq);
    }

    @ApiOperation(value = "根据id删除 仓库地区表")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("仓库地区表管理", "根据id删除仓库地区表", "根据id删除仓库地区表：仓库id" + id);
        WareHouseCityDelByIdRequest delByIdReq = new WareHouseCityDelByIdRequest();
        delByIdReq.setId(id);
        return wareHouseCityProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除 仓库地区表")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid WareHouseCityDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("仓库地区表管理", "根据idList批量删除仓库地区表", "根据idList批量删除仓库地区表");
        return wareHouseCityProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出 仓库地区表列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        WareHouseCityListRequest listReq = JSON.parseObject(decrypted, WareHouseCityListRequest.class);
        listReq.putSort("id", "desc");
        List<WareHouseCityVO> dataRecords = wareHouseCityQueryProvider.list(listReq).getContext().getWareHouseCityVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format(" 仓库地区表列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("仓库地区表管理", "导出仓库地区表列表", "操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<WareHouseCityVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
            new Column("仓库iD", new SpelColumnRender<WareHouseCityVO>("wareId")),
            new Column("省份", new SpelColumnRender<WareHouseCityVO>("provinceId")),
            new Column("市", new SpelColumnRender<WareHouseCityVO>("cityId")),
            new Column("区县ID", new SpelColumnRender<WareHouseCityVO>("areaId"))
        };
        excelHelper.addSheet(" 仓库地区表列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

}
