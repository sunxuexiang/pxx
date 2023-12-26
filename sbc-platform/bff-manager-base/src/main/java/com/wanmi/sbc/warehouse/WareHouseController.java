package com.wanmi.sbc.warehouse;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyTypeRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.*;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Api(description = "仓库表管理API", tags = "WareHouseController")
@RestController
@RequestMapping(value = "/ware/house")
public class WareHouseController {

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private WareHouseProvider wareHouseProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询仓库表")
    @PostMapping("/page")
    public BaseResponse<WareHousePageResponse> getPage(@RequestBody @Valid WareHousePageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        if(Objects.isNull(pageReq.getDefaultFlag())) {
            pageReq.setDefaultFlag(DefaultFlag.YES);
        }
        pageReq.setStoreId(commonUtil.getStoreId());
        Map<String, String> map = new LinkedHashMap<>();
        map.put("defaultFlag", "desc");
        map.put("wareId", "desc");
        pageReq.setSortMap(map);
        BaseResponse<WareHousePageResponse> response = wareHouseQueryProvider.page(pageReq);
        List<WareHouseVO> wareHouseVOList = response.getContext().getWareHouseVOPage().getContent();
        WareHouseVO wareHouseVO = wareHouseVOList.stream().filter(s->s.getWareId().equals(1L)).findAny().orElse(null);
        //如果找不到仓库1，找默认仓库
        if(wareHouseVO==null){
            List<WareHouseVO> wareHouseVOS = wareHouseQueryProvider.list(WareHouseListRequest.builder().wareId(1L).build()).getContext().getWareHouseVOList();
            if(CollectionUtils.isNotEmpty( wareHouseVOS)) {
                wareHouseVO = wareHouseVOS.stream().findFirst().orElse(null);
                if(wareHouseVO!=null){
                    List<WareHouseVO> newList = new ArrayList<>(wareHouseVOList.size()+1);
                    wareHouseVO.setWareId(1L);
                    wareHouseVO.setStoreId(commonUtil.getStoreId());
                    wareHouseVO.setWareName("默认仓库");
                    newList.add(wareHouseVO);
                    newList.addAll(wareHouseVOList);
                    response.getContext().getWareHouseVOPage().setContent(newList);
                }
            }
        }
        return response;
    }


    @ApiOperation(value = "分页查询仓库表")
    @PostMapping("/new/page")
    public BaseResponse<WareHousePageResponse> getNewPage(@RequestBody @Valid WareHousePageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        //pageReq.setStoreId(commonUtil.getStoreIdWithDefault());
        Map<String, String> map = new LinkedHashMap<>();
        map.put("defaultFlag", "desc");
        map.put("wareId", "desc");
        pageReq.setSortMap(map);
        return wareHouseQueryProvider.page(pageReq);
    }



    @ApiOperation(value = "列表查询仓库表")
    @PostMapping("/list")
    public BaseResponse<WareHouseListResponse> getList(@RequestBody @Valid WareHouseListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setDefaultFlag(DefaultFlag.YES);
        listReq.setStoreId(commonUtil.getStoreIdWithDefault());
        listReq.putSort("wareId", "desc");
        return wareHouseQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询仓库表")
    @GetMapping("/{wareId}")
    public BaseResponse<WareHouseByIdResponse> getById(@PathVariable Long wareId) {
        if (wareId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        WareHouseByIdRequest idReq = new WareHouseByIdRequest();
        idReq.setWareId(wareId);
        idReq.setStoreId(commonUtil.getStoreIdWithDefault());
        return wareHouseQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增仓库表")
    @PostMapping("/add")
    public BaseResponse<WareHouseAddResponse> add(@RequestBody @Valid WareHouseAddRequest addReq) {
        operateLogMQUtil.convertAndSend("仓库表管理", "新增仓库表", "新增仓库表：仓库名称" + (Objects.nonNull(addReq) ? addReq.getWareName() : ""));
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setCreatePerson(commonUtil.getOperatorId());
        addReq.setStoreId(commonUtil.getStoreIdWithDefault());
        addReq.setDefaultFlag(DefaultFlag.NO);
        return wareHouseProvider.add(addReq);
    }

    @ApiOperation(value = "修改仓库表")
    @PutMapping("/modify")
    public BaseResponse<WareHouseModifyResponse> modify(@RequestBody @Valid WareHouseModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("仓库表管理", "修改仓库表", "修改仓库表：仓库名称" + (Objects.nonNull(modifyReq) ? modifyReq.getWareName() : ""));
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        modifyReq.setStoreId(commonUtil.getStoreIdWithDefault());
        return wareHouseProvider.modify(modifyReq);
    }

    @ApiOperation(value = "修改默认仓仓库表")
    @PutMapping("/modify-default-flag")
    public BaseResponse<WareHouseModifyResponse> modifyDefaultFlag(@RequestBody @Valid WareHouseModifyDefaultFlagRequest modifyReq) {
        operateLogMQUtil.convertAndSend("仓库表管理", "修改默认仓仓库表", "修改默认仓仓库表：仓库ID" + (Objects.nonNull(modifyReq) ? modifyReq.getWareId() : ""));
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        modifyReq.setStoreId(commonUtil.getStoreIdWithDefault());
        return wareHouseProvider.modifyDefaultFlag(modifyReq);
    }

    @ApiOperation(value = "根据id删除仓库表")
    @PostMapping("/delete-by-id")
    public BaseResponse deleteById(@RequestBody @Valid WareHouseDelByIdRequest delByIdReq) {
        operateLogMQUtil.convertAndSend("仓库表管理", "根据id删除仓库表", "根据id删除仓库表：仓库ID" + (Objects.nonNull(delByIdReq) ? delByIdReq.getWareId() : ""));
        delByIdReq.setUpdatePerson(commonUtil.getOperatorId());
        delByIdReq.setStoreId(commonUtil.getStoreId());
        return wareHouseProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除仓库表")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid WareHouseDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("仓库表管理", "根据idList批量删除仓库表", "根据idList批量删除仓库表");
        return wareHouseProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出仓库表列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        WareHouseListRequest listReq = JSON.parseObject(decrypted, WareHouseListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("wareId", "desc");
        List<WareHouseVO> dataRecords = wareHouseQueryProvider.list(listReq).getContext().getWareHouseVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("仓库表列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                    "filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("仓库表管理", "导出仓库表列表", "操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<WareHouseVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("店铺标识", new SpelColumnRender<WareHouseVO>("storeId")),
                new Column("仓库名称", new SpelColumnRender<WareHouseVO>("wareName")),
                new Column("仓库编号", new SpelColumnRender<WareHouseVO>("wareCode")),
                new Column("省份", new SpelColumnRender<WareHouseVO>("provinceId")),
                new Column("市", new SpelColumnRender<WareHouseVO>("cityId")),
                new Column("区", new SpelColumnRender<WareHouseVO>("areaId")),
                new Column("详细地址", new SpelColumnRender<WareHouseVO>("addressDetail")),
                new Column("是否默认仓 0：否，1：是", new SpelColumnRender<WareHouseVO>("defaultFlag"))
        };
        excelHelper.addSheet("仓库表列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

    @ApiOperation(value = "根据id设为默认")
    @PostMapping("/default/{wareId}")
    public BaseResponse defaultById(@PathVariable Long wareId) {
        if (wareId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return wareHouseProvider.setDefault(WareHouseModifyRequest.builder().wareId(wareId).build());
    }

    @ApiOperation(value = "查询已选区域,根据仓库类型")
    @RequestMapping(value = "/selected/area", method = RequestMethod.POST)
    public BaseResponse<List<Long>> getSelectedArea(@RequestBody @Valid WareHouseQueryTypeRequest request) {
        if (commonUtil.getStoreIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return BaseResponse.success(wareHouseQueryProvider.queryAreaIdsByIdAndStoreId(
                WareHouseAreaIdByStoreIdRequest.builder()
                        .wareId(0L).storeId(commonUtil.getStoreIdWithDefault()).wareHouseType(request.getWareHouseType()).build()
        ).getContext().getAreaIds());
    }

    /**
     * 获取自营商家的分仓信息以及同仓统配的仓库Id
     * @return
     */
    @ApiOperation(value = "获取自营商家的分仓信息以及同仓统配的仓库Id")
    @GetMapping("/list-all-self")
    public BaseResponse<WareHouseListResponse> getSelfList() {
        StoreByIdResponse response = storeQueryProvider.findByCompanyType(StoreByCompanyTypeRequest.builder()
                .companyType(CompanyType.PLATFORM)
                .build()).getContext();
        StoreVO storeVO = response.getStoreVO();
        Long storeId = commonUtil.getStoreId();
        WareHouseListResponse returnResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .storeId(storeVO.getStoreId()).build()).getContext();
        WareHouseListResponse chooseWareHouses = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .storeId(storeId).build()).getContext();
        if(Objects.nonNull(chooseWareHouses) && CollectionUtils.isNotEmpty(chooseWareHouses.getWareHouseVOList())){
            List<WareHouseVO> sameWareHouses = returnResponse.getWareHouseVOList().stream().filter(w->
                    chooseWareHouses.getWareHouseVOList().stream()
                            .filter(c->c.getWareCode().equals(w.getWareCode())).findFirst().isPresent()).collect(Collectors.toList());
            returnResponse.setChooseWareIds(sameWareHouses.stream().map(WareHouseVO::getWareId).collect(Collectors.toList()));
        }
        return BaseResponse.success(returnResponse);
    }

    @ApiOperation(value = "商家加载仓库")
    @PostMapping("/loadWare")
    public BaseResponse<WareHousePageResponse> loadWare(@RequestBody @Valid WareHousePageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        if(Objects.isNull(pageReq.getDefaultFlag())) {
            pageReq.setDefaultFlag(DefaultFlag.YES);
        }
        //现有逻辑大量依据仓库，商家模式依据APP端固定仓库1
        pageReq.setWareId(1L);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("defaultFlag", "desc");
        map.put("wareId", "desc");
        pageReq.setSortMap(map);
        return wareHouseQueryProvider.page(pageReq);
    }


}
