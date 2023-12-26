package com.wanmi.sbc.logisticscompany;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBulkMarketQueryRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallBulkMarketResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.logisticscompany.service.LogisticsCompanyService;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeGetByLogisticsCompanyIdRequest;
import com.wanmi.sbc.order.api.request.trade.updateTradeLogisticCompanyRequest;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.*;
import com.wanmi.sbc.setting.api.request.yunservice.YunGetResourceRequest;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.*;
import com.wanmi.sbc.setting.api.response.yunservice.YunGetResourceResponse;
import com.wanmi.sbc.setting.bean.enums.LogisticsType;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * @author fcq
 */
@Api(description = "指定物流公司管理API", tags = "SpecifyLogisticsController")
@RestController
@RequestMapping(value = "/specifyLogistics")
@Slf4j
public class SpecifyLogisticsController {

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;
    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private LogisticsCompanyProvider logisticsCompanyProvider;

    @Value("classpath:logistics_company_import_template.xls")
    private org.springframework.core.io.Resource templateFile;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private LogisticsCompanyService logisticsCompanyService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Resource
    private YunServiceProvider yunServiceProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @ApiOperation(value = "列表查询物流公司")
    @PostMapping("/list")
    public BaseResponse<LogisticsCompanyListResponse> getList(@RequestBody @Valid LogisticsCompanyListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        listReq.setStoreId(commonUtil.getStoreIdWithDefault());
        BaseResponse<LogisticsCompanyListResponse> response = logisticsCompanyQueryProvider.list(listReq);
        List<LogisticsCompanyVO> logisticsCompanyVOList = response.getContext().getLogisticsCompanyVOList();
        setLogisticsCompanyMarketName(logisticsCompanyVOList);
        return response;
    }

    private void setLogisticsCompanyMarketName(List<LogisticsCompanyVO> logisticsCompanyVOList) {
        if(CollectionUtils.isNotEmpty(logisticsCompanyVOList)){
            HashMap<Long,String> marketVOHashMap = new HashMap<>(10);
            for(LogisticsCompanyVO logisticsCompanyVO: logisticsCompanyVOList){
                String marketName =  marketVOHashMap.get(logisticsCompanyVO.getMarketId());
                if(null==marketName && logisticsCompanyVO.getMarketId()!=null && logisticsCompanyVO.getMarketId()>0) {
                    marketName = getMarketByMarketId(logisticsCompanyVO.getMarketId());
                    marketVOHashMap.put(logisticsCompanyVO.getMarketId(), marketName);
                }
                if(marketName!=null) {
                    logisticsCompanyVO.setMarketName(marketName);
                }
            }
        }
    }

    private CompanyMallBulkMarketVO getMarketByStoreId(Long storeId){
        return companyIntoPlatformQueryProvider.getMarketByStoreId(storeId).getContext();
    }

    private String getMarketByMarketId(Long marketId){
        if(marketId!=null && marketId>0) {
            CompanyMallBulkMarketResponse mallBulkMarketResponse = companyIntoPlatformQueryProvider.getByIdForMarket(CompanyMallBulkMarketQueryRequest.builder().marketId(marketId).build()).getContext();
            if(null!= mallBulkMarketResponse){
                return mallBulkMarketResponse.getMarketName();
            }
        }
        return Constants.EMPTY_STR;
    }

    private Long getMarketIdByStoreId(Long storeId){
        if(storeId!=null && storeId>0) {
            return getMarketByStoreId(storeId).getMarketId();
        }
        return Constants.BOSS_DEFAULT_MARKET_ID;
    }


    @ApiOperation(value = "修改物流公司")
    @PutMapping("/updateTradeCompanyInfo")
    public BaseResponse updateTradeLogisticsCompany(@RequestBody @Valid updateTradeLogisticCompanyRequest request) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "修改物流公司", "修改物流公司:物流公司id" + (Objects.nonNull(request) ? request.getCompanyId() : ""));
        Operator operator = commonUtil.getOperator();
        //if (Objects.nonNull(operator.getCompanyType()) && operator.getCompanyType() == 1) {
          //不区分供应商类型,都可以修改
//          if (Objects.nonNull(operator.getCompanyType())) {
//            return BaseResponse.error("非法越权操作");
//        }
        request.setOperator(commonUtil.getOperator());
        return tradeProvider.updateTradeLogisticsCompany(request);
    }

    @ApiOperation(value = "分页查询物流公司")
    @PostMapping("/page")
    public BaseResponse<LogisticsCompanyPageResponse> getPage(@RequestBody @Valid LogisticsCompanyPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("id", "desc");
        pageReq.setStoreId(commonUtil.getStoreIdWithDefault());
        pageReq.setCompanyType(commonUtil.getCompanyType());
        CompanyMallBulkMarketVO companyMallBulkMarketVO = getMarketByStoreId(pageReq.getStoreId());
        if(companyMallBulkMarketVO!=null){
            pageReq.setMarketId(companyMallBulkMarketVO.getMarketId());
        }
        pageReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        BaseResponse<LogisticsCompanyPageResponse> r = logisticsCompanyQueryProvider.page(pageReq);
        MicroServicePage<LogisticsCompanyVO> logisticsCompanyVOPage = r.getContext().getLogisticsCompanyVOPage();
        List<LogisticsCompanyVO> logisticsCompanyVOList = logisticsCompanyVOPage.getContent();
        setLogisticsCompanyMarketName(logisticsCompanyVOList);
        return r;
    }


    @ApiOperation(value = "根据id查询物流公司")
    @GetMapping("/{id}")
    public BaseResponse<LogisticsCompanyByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LogisticsCompanyByIdRequest idReq = new LogisticsCompanyByIdRequest();
        idReq.setId(id);
        return logisticsCompanyQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增物流公司")
    @PostMapping("/add")
    public BaseResponse<LogisticsCompanyAddResponse> add(@RequestBody  @Valid LogisticsCompanyAddRequest addReq) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "新增物流公司", "新增物流公司:公司名称" + (Objects.nonNull(addReq) ? addReq.getLogisticsName() : ""));
        addReq.setCreateTime(LocalDateTime.now());
        addReq.setStoreId(commonUtil.getStoreIdWithDefault());
        addReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        if(Constants.BOSS_DEFAULT_STORE_ID.compareTo(addReq.getStoreId())!=0){
            addReq.setMarketId(getMarketIdByStoreId(addReq.getStoreId()));
        }else{
            addReq.setMarketId(Constants.BOSS_DEFAULT_MARKET_ID);
        }
        return logisticsCompanyProvider.add(addReq);
    }

    @ApiOperation(value = "同步物流公司")
    @PostMapping("/syncLogisticsCompany")
    public BaseResponse<LogisticsCompanyAddResponse> syncLogisticsCompany(@RequestBody  @Valid LogisticsCompanySyncRequest syncRequest) {
        if(syncRequest==null || Objects.isNull(syncRequest.getSourceStoreId()) || CollectionUtils.isEmpty(syncRequest.getTargetStoreIdList())){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "同步物流公司", "同步物流公司");
        syncRequest.getTargetStoreIdList().remove(syncRequest.getSourceStoreId());
        syncRequest.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        return logisticsCompanyProvider.syncLogisticsCompany(syncRequest);
    }

    @ApiOperation(value = "修改物流公司")
    @PutMapping("/modify")
    public BaseResponse<LogisticsCompanyModifyResponse> modify(@RequestBody @Valid LogisticsCompanyModifyRequest modifyReq) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "修改物流公司", "修改物流公司:公司名称" + (Objects.nonNull(modifyReq) ? modifyReq.getLogisticsName() : ""));
        modifyReq.setUpdateTime(LocalDateTime.now());
        modifyReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        modifyReq.setMarketId(Constants.BOSS_DEFAULT_MARKET_ID);
        return logisticsCompanyProvider.modify(modifyReq);
    }

    @ApiOperation(value = "根据id删除物流公司")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LogisticsCompanyDelByIdRequest delByIdReq = new LogisticsCompanyDelByIdRequest();
        delByIdReq.setId(id);
        //根据物流id查询订单 1.存在且订单状态，已完成/已作废，可删除  2.不存在直接可以删除
        List<TradeVO> tradeVOList =
                tradeQueryProvider.getListByLogisticsCompanyId(TradeGetByLogisticsCompanyIdRequest.builder().id(id).build()).getContext().getTradeVOList();
        if (CollectionUtils.isNotEmpty(tradeVOList)) {
            throw new SbcRuntimeException(CommonErrorCode.ORDER_EXIT_LOGISTICS_COMPANY);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "根据id删除物流公司", "根据id删除物流公司:id" + id);
        return logisticsCompanyProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除物流公司")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid LogisticsCompanyDelByIdListRequest delByIdListReq) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "根据idList批量删除物流公司", "根据idList批量删除物流公司");
        return logisticsCompanyProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出物流公司列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        LogisticsCompanyListRequest listReq = JSON.parseObject(decrypted, LogisticsCompanyListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());

        List<LogisticsCompanyVO> dataRecords = logisticsCompanyQueryProvider.list(listReq).getContext().getLogisticsCompanyVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("物流公司列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "导出物流公司列表", "操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<LogisticsCompanyVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("公司编号", new SpelColumnRender<LogisticsCompanyVO>("companyNumber")),
                new Column("公司名称", new SpelColumnRender<LogisticsCompanyVO>("logisticsName")),
                new Column("公司电话", new SpelColumnRender<LogisticsCompanyVO>("logisticsPhone")),
                new Column("物流公司地址", new SpelColumnRender<LogisticsCompanyVO>("logisticsAddress")),
                new Column("删除时间", new SpelColumnRender<LogisticsCompanyVO>("deleteTime"))
        };
        excelHelper.addSheet("物流公司列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }


    /**
     * 物流公司导入模板下载
     *
     * @param encrypted
     */
    @ApiOperation(value = "物流公司模板下载")
    @RequestMapping(value = "/downloadTemplate/{encrypted}", method = RequestMethod.GET)
    public void export(@PathVariable String encrypted) {
        if (templateFile == null || !templateFile.exists()) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }
        InputStream is = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            is = templateFile.getInputStream();
            Workbook wk = WorkbookFactory.create(is);
            wk.write(byteArrayOutputStream);
            String file = new BASE64Encoder().encode(byteArrayOutputStream.toByteArray());
            if (StringUtils.isNotBlank(file)) {
                String fileName = URLEncoder.encode("物流公司模板.xls", "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                        "filename*=\"utf-8''%s\"", fileName, fileName));
                HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
            }
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                log.error("物流公司模板转Base64位异常", e);
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("读取物流公司模板异常", e);
                }
            }
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "物流公司模板下载", "操作成功");
    }

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "上传文件", "上传文件");
        return BaseResponse.success(this.upload(uploadFile, commonUtil.getOperatorId()));
    }

    /**
     * 确认导入物流公司
     *
     * @param ext 文件格式 {@link String}
     * @return
     */
    @ApiOperation(value = "确认导入物流公司")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "ext",
            value = "文件名后缀", required = true)
    @RequestMapping(value = "/import/{ext}", method = RequestMethod.GET)
    public BaseResponse<LogisticsCompanyImportResponse> importCustomer(@PathVariable String ext) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        LogisticsCompanyImportExcelRequest excelRequest = new LogisticsCompanyImportExcelRequest();
        excelRequest.setExt(ext);
        excelRequest.setUserId(commonUtil.getOperatorId());
        excelRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        excelRequest.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        excelRequest.setMarketId(Constants.BOSS_DEFAULT_MARKET_ID);
        LogisticsCompanyImportResponse response = logisticsCompanyService.importLogisticsCompany(excelRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("物流公司导入", "批量导入", "批量导入");
        return BaseResponse.success(response);
    }

    /**
     * 下载错误文档
     */
    @ApiOperation(value = "下载错误文档")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "ext", value = "后缀", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "decrypted", value = "解密", required = true)
    })
    @RequestMapping(value = "/excel/err/{ext}/{decrypted}", method = RequestMethod.GET)
    public void downErrExcel(@PathVariable String ext, @PathVariable String decrypted) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        this.downloadErrExcel(commonUtil.getOperatorId(), ext);
        //记录操作日志
        operateLogMQUtil.convertAndSend("物流公司管理", "下载错误文档", "操作成功");
    }

    /**
     * 下载Excel错误文档
     *
     * @param userId 用户Id
     * @param ext    文件扩展名
     */
    private void downloadErrExcel(String userId, String ext) {
        //图片存储地址
//        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_EXCEL_DIR).concat("/").concat(userId).concat(".").concat(ext);
//        File picSaveFile = new File(filePath);
//        FileInputStream is = null;
//        ServletOutputStream os = null;
//        try {
//            if (picSaveFile.exists()) {
//                is = new FileInputStream(picSaveFile);
//                os = HttpUtil.getResponse().getOutputStream();
//                String fileName = URLEncoder.encode("错误表格.".concat(ext), "UTF-8");
//                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
//
//                byte b[] = new byte[1024];
//                //读取文件，存入字节数组b，返回读取到的字符数，存入read,默认每次将b数组装满
//                int read = is.read(b);
//                while (read != -1) {
//                    os.write(b, 0, read);
//                    read = is.read(b);
//                }
//                HttpUtil.getResponse().flushBuffer();
//            }
//        } catch (Exception e) {
//            log.error("下载EXCEL文件异常->", e);
//            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    log.error("下载EXCEL文件关闭IO失败->", e);
//                }
//            }
//
//            if (os != null) {
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    log.error("下载EXCEL文件关闭IO失败->", e);
//                }
//            }
//        }


		String fileKey = LogisticsCompanyService.ERR_EXCEL_PREFIX + userId.concat(".").concat(ext);
		BaseResponse<YunGetResourceResponse> fileResp = yunServiceProvider
				.getFile(YunGetResourceRequest.builder().resourceKey(fileKey).build());
		ServletOutputStream os = null;
		try {
			os = HttpUtil.getResponse().getOutputStream();
			String fileName = URLEncoder.encode("错误表格.".concat(ext), "UTF-8");
			HttpUtil.getResponse().setHeader("Content-Disposition",
					String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
			os.write(fileResp.getContext().getContent());
			HttpUtil.getResponse().flushBuffer();
		} catch (Exception e) {
			log.error("下载物流公司模板错误EXCEL文件异常->", e);
			throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					log.error("下载物流公司模板错误EXCEL文件关闭IO失败->", e);
				}
			}
		}

    }
    /**
     * 上传文件
     *
     * @param file   文件
     * @param userId 操作员id
     * @return 文件格式
     */
    private String upload(MultipartFile file, String userId) {
        if (file == null || file.isEmpty()) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if (!(fileExt.equalsIgnoreCase("xls") || fileExt.equalsIgnoreCase("xlsx"))) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_EXT_ERROR);
        }

        if (file.getSize() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

//        //上传存储地址
//        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/");
//        File picSaveFile = new File(t_realPath);
//        if (!picSaveFile.exists()) {
//            try {
//                picSaveFile.mkdirs();
//            } catch (Exception e) {
//                log.error("创建文件路径失败->".concat(t_realPath), e);
//                throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
//            }
//        }
//        
//        String newFileName = userId.concat(".").concat(fileExt);
//        File newFile = new File(t_realPath.concat(newFileName));
//        try {
//            newFile.deleteOnExit();
//            file.transferTo(newFile);
//        } catch (IOException e) {
//            log.error("上传Excel文件失败->".concat(newFile.getPath()), e);
//            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
//        }
        String fileKey = LogisticsCompanyService.EXCEL_PREFIX + userId.concat(".").concat(fileExt);
        try {
			BaseResponse<String> justUploadFile = yunServiceProvider.justUploadFile(YunUploadResourceRequest.builder()
			        .resourceKey(fileKey)
			        .content(file.getBytes())
			        .build());
			log.info(justUploadFile.getContext());
		} catch (IOException e) {
          log.error("上传物流公司模板Excel文件失败->".concat(fileKey), e);
          throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
		}
        return fileExt;
    }

}
