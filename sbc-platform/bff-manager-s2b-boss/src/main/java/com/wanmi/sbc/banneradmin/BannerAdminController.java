package com.wanmi.sbc.banneradmin;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.setting.api.provider.banneradmin.BannerAdminProvider;
import com.wanmi.sbc.setting.api.provider.banneradmin.BannerAdminQueryProvider;
import com.wanmi.sbc.setting.api.request.banneradmin.*;
import com.wanmi.sbc.setting.api.response.banneradmin.*;
import com.wanmi.sbc.setting.bean.vo.BannerAdminVO;
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


@Api(description = "轮播管理管理API", tags = "BannerAdminController")
@RestController
@RequestMapping(value = "/banneradmin")
public class BannerAdminController {

    @Autowired
    private BannerAdminQueryProvider bannerAdminQueryProvider;

    @Autowired
    private BannerAdminProvider bannerAdminProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "分页查询轮播管理")
    @PostMapping("/page")
    public BaseResponse<BannerAdminPageResponse> getPage(@RequestBody @Valid BannerAdminPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("bannerSort", "asc");
        pageReq.putSort("oneCateId", "asc");
        return bannerAdminQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询轮播管理")
    @PostMapping("/list")
    public BaseResponse<BannerAdminListResponse> getList(@RequestBody @Valid BannerAdminListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("bannerSort", "asc");
        listReq.putSort("oneCateId", "asc");
        return bannerAdminQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询轮播管理")
    @GetMapping("/{id}")
    public BaseResponse<BannerAdminByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        BannerAdminByIdRequest idReq = new BannerAdminByIdRequest();
        idReq.setId(id);
        return bannerAdminQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增轮播管理")
    @PostMapping("/add")
    public BaseResponse<BannerAdminAddResponse> add(@RequestBody @Valid BannerAdminAddRequest addReq) {
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setIsShow(0);
        addReq.setCreatePerson(commonUtil.getOperatorId());
        addReq.setCreateTime(LocalDateTime.now());
        operateLogMQUtil.convertAndSend("设置", "轮播管理", "新增轮播管理");
        return bannerAdminProvider.add(addReq);
    }

    @ApiOperation(value = "修改轮播管理")
    @PutMapping("/modify")
    public BaseResponse<BannerAdminModifyResponse> modify(@RequestBody @Valid BannerAdminModifyRequest modifyReq) {
        modifyReq.setDelFlag(DeleteFlag.NO);
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        modifyReq.setUpdateTime(LocalDateTime.now());
        operateLogMQUtil.convertAndSend("设置", "轮播管理", "修改轮播管理");
        return bannerAdminProvider.modify(modifyReq);
    }


    @ApiOperation(value = "隐藏/显示")
    @PutMapping("/modifyStatus")
    public BaseResponse<BannerAdminModifyResponse> modifyStatus(@RequestBody @Valid BannerAdminModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("设置", "轮播管理", "隐藏/显示");
        return bannerAdminProvider.modifyStatus(modifyReq);
    }

    @ApiOperation(value = "根据id删除轮播管理")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        BannerAdminDelByIdRequest delByIdReq = new BannerAdminDelByIdRequest();
        delByIdReq.setId(id);
        operateLogMQUtil.convertAndSend("设置", "轮播管理", "根据id删除轮播管理");
        return bannerAdminProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除轮播管理")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid BannerAdminDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("设置", "轮播管理", "根据idList批量删除轮播管理");
        return bannerAdminProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出轮播管理列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        BannerAdminListRequest listReq = JSON.parseObject(decrypted, BannerAdminListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        List<BannerAdminVO> dataRecords = bannerAdminQueryProvider.list(listReq).getContext().getBannerAdminVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("轮播管理列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("设置", "轮播管理", "导出轮播管理列表");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<BannerAdminVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
            new Column("名称", new SpelColumnRender<BannerAdminVO>("bannerName")),
            new Column("一级类ID", new SpelColumnRender<BannerAdminVO>("oneCateId")),
            new Column("一级分类名称", new SpelColumnRender<BannerAdminVO>("oneCateName")),
            new Column("排序号", new SpelColumnRender<BannerAdminVO>("bannerSort")),
            new Column("添加链接", new SpelColumnRender<BannerAdminVO>("link")),
            new Column("banner图片", new SpelColumnRender<BannerAdminVO>("bannerImg")),
            new Column("状态(0.显示 1.隐藏)", new SpelColumnRender<BannerAdminVO>("isShow")),
            new Column("删除时间", new SpelColumnRender<BannerAdminVO>("deleteTime"))
        };
        excelHelper.addSheet("轮播管理列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

}
