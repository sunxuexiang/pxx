package com.wanmi.sbc.logisticscompany.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyProvider;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyImportExcelRequest;
import com.wanmi.sbc.setting.api.request.yunservice.YunGetResourceRequest;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyImportResponse;
import com.wanmi.sbc.setting.api.response.yunservice.YunGetResourceResponse;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import static com.wanmi.sbc.common.util.IteratorUtils.distinctByKey;


/**
 * @author fcq
 */
@Slf4j
@Service
public class LogisticsCompanyService {
	
	public static final String EXCEL_PREFIX = "logisticscompany/excel/";
    
	public static final String ERR_EXCEL_PREFIX = "logisticscompany/err_excel/";
	
    @Autowired
    private LogisticsCompanyProvider logisticsCompanyProvider;

    @Resource
    private YunServiceProvider yunServiceProvider;


    @Transactional
    public LogisticsCompanyImportResponse importLogisticsCompany(LogisticsCompanyImportExcelRequest excelRequest) {

        String ext = excelRequest.getExt();
        String userId = excelRequest.getUserId();
//        String filePath =
//                HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/").concat(excelRequest.getUserId()).concat(".").concat(ext);
//        File file = new File(filePath);
        
        String fileKey = EXCEL_PREFIX + userId.concat(".").concat(ext);
        BaseResponse<YunGetResourceResponse> fileResp = yunServiceProvider.getFile(YunGetResourceRequest.builder().resourceKey(fileKey).build());
        ByteArrayInputStream in = new ByteArrayInputStream(fileResp.getContext().getContent());
        
        if (in.available() == 0) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (in.available() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(SettingErrorCode.FILE_MAX_SIZE,
                    new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }
        try (Workbook workbook = WorkbookFactory.create(in)) {
            return this.executeMethod(workbook, excelRequest, ext);
        } catch (SbcRuntimeException e) {
            log.error("批量设置物流公司异常", e);
            throw e;
        } catch (Exception e) {
            log.error("批量设置物流公司异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }


    /**
     * 执行主程序，获取导入文件里数据进行批量导入物流公司
     * @param workbook
     * @param excelRequest
     * @param ext
     * @return
     */
    public LogisticsCompanyImportResponse executeMethod(Workbook workbook, LogisticsCompanyImportExcelRequest excelRequest,
                                                        String ext) {
        //创建Workbook工作薄对象，表示整个excel
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new SbcRuntimeException(SettingErrorCode.EMPTY_ERROR);
        }

        //检测文档正确性
        this.checkExcel(workbook);

        //获得当前sheet的开始行
        int firstRowNum = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 1) {
            throw new SbcRuntimeException(SettingErrorCode.EMPTY_ERROR);
        }
        int maxCell = 4;
        boolean isError = false;

        //循环除了第一行的所有行
        List<LogisticsCompanyVO> logisticsCompanies = Lists.newArrayList();
        List<String> logisticsCompanyNames =
                logisticsCompanyProvider.selectLogisticsCompanyNumbersByMarketId(excelRequest.getMarketId(),excelRequest.getLogisticsType()).getContext().getLogisticsCompanyNumber();

        //去除所欲编号数据
        List<String> companyNumbers = Lists.newArrayList();
        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            Cell[] cells = new Cell[maxCell];
            boolean isNotEmpty = false;
            for (int i = 0; i < 1; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    cell = row.createCell(i);
                }
                cells[i] = cell;
                if (StringUtils.isNotBlank(ExcelHelper.getValue(cell))) {
                    isNotEmpty = true;
                }
            }
            //数据都为空，则跳过去
            if (!isNotEmpty) {
                continue;
            }
            //物流编号
            String logisticsCompanyNumber = ExcelHelper.getValue(cells[0]);
            companyNumbers.add(logisticsCompanyNumber);
        }
        List<String> repetitionData = null;
        if (CollectionUtils.isNotEmpty(companyNumbers)) {
            repetitionData = getDuplicateElements(companyNumbers);
        }
        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            Cell[] cells = new Cell[maxCell];
            boolean isNotEmpty = false;
            for (int i = 0; i < maxCell; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    cell = row.createCell(i);
                }
                cells[i] = cell;
                if (StringUtils.isNotBlank(ExcelHelper.getValue(cell))) {
                    isNotEmpty = true;
                }
            }
            //数据都为空，则跳过去
            if (!isNotEmpty) {
                continue;
            }
            Cell cell_logisticsName = cells[0];
            Cell cell_logisticsPhone = cells[1];
            Cell cell_logisticsAddress = cells[2];
            //物流编号
            /*String logisticsCompanyNumber = ExcelHelper.getValue(cells[0]);
            if (StringUtils.isBlank(logisticsCompanyNumber)) {
                ExcelHelper.setError(workbook, cells[0], "此项必填");
                isError = true;
            }else {
                if (CollectionUtils.isNotEmpty(logisticsCompanyNames)) {
                    if (logisticsCompanyNames.contains(logisticsCompanyNumber)) {
                        ExcelHelper.setError(workbook, cells[0], "编号已存在");
                        isError = true;
                    }
                    if (logisticsCompanyNumber.length()<5 || logisticsCompanyNumber.length()>20){
                        ExcelHelper.setError(workbook, cells[0], "物流公司编号，5-20位数字、字母或组合");
                        isError = true;
                    }
                    if (CollectionUtils.isNotEmpty(repetitionData)) {
                        if (repetitionData.contains(logisticsCompanyNumber)) {
                            ExcelHelper.setError(workbook, cells[0], "编号重复");
                            isError = true;
                        }
                    }
                }
            }*/

            // 物流公司名称
            String logisticsName = ExcelHelper.getValue(cell_logisticsName);
            if (StringUtils.isBlank(logisticsName)) {
                ExcelHelper.setError(workbook, cell_logisticsName, "此项必填");
                isError = true;
            }else {
                    if (logisticsName.length()>100){
                        ExcelHelper.setError(workbook, cell_logisticsName, "物流公司名称，不超过100字符");
                        isError = true;
                    }
                if (CollectionUtils.isNotEmpty(logisticsCompanyNames)) {
                    if (logisticsCompanyNames.contains(logisticsName)) {
                        ExcelHelper.setError(workbook, cell_logisticsName, "物流公司名称已存在");
                        isError = true;
                    }
                    if (CollectionUtils.isNotEmpty(repetitionData)) {
                        if (repetitionData.contains(logisticsName)) {
                            ExcelHelper.setError(workbook, cell_logisticsName, "物流公司名称重复");
                            isError = true;
                        }
                    }
                }
            }
            // 物流公司电话
            // 改变表格的数据类型，防止手机号码 变成 科学计数
            cell_logisticsPhone.setCellType(CellType.STRING);
            String logisticsPhone = ExcelHelper.getValue(cell_logisticsPhone);
            if (StringUtils.isBlank(logisticsPhone)) {
                ExcelHelper.setError(workbook, cell_logisticsPhone, "此项必填");
                isError = true;
            }else {
                if (logisticsPhone.length()>15){
                    ExcelHelper.setError(workbook, cell_logisticsPhone, "物流公司电话，不超过15位数字");
                    isError = true;
                }
                
                // 手机或座机正则
        		String regex = "^1\\d{10}$|^(0\\d{2,3}[-+]*|\\(0\\d{2,3}\\))?[2-9]\\d{4,7}([-+]*\\d{1,8})?$";
        		boolean matches = logisticsPhone.matches(regex);
        		if (!matches) {
                    ExcelHelper.setError(workbook, cell_logisticsPhone, "物流公司电话格式错误");
                    isError = true;
				}
            }
            //物流地址
            String logisticsAddress = ExcelHelper.getValue(cell_logisticsAddress);
            if (StringUtils.isBlank(logisticsAddress)) {
                ExcelHelper.setError(workbook, cell_logisticsAddress, "此项必填");
                isError = true;
            }else {
                if (logisticsAddress.length()>200){
                    ExcelHelper.setError(workbook, cell_logisticsAddress, "物流公司地址，不超过200字符");
                    isError = true;
                }
            }

            if (!isError) {
                LogisticsCompanyVO logisticsCompany = new LogisticsCompanyVO();
                logisticsCompany.setLogisticsName(logisticsName);
                logisticsCompany.setLogisticsPhone(logisticsPhone);
                logisticsCompany.setLogisticsAddress(logisticsAddress);
                logisticsCompany.setCreateTime(LocalDateTime.now());
                logisticsCompany.setDelFlag(DeleteFlag.NO);
				logisticsCompany.setStoreId(excelRequest.getStoreId());
                logisticsCompany.setLogisticsType(excelRequest.getLogisticsType());
                logisticsCompany.setMarketId(excelRequest.getMarketId());
                logisticsCompanies.add(logisticsCompany);
            }
        }
        if (isError) {
            errorExcel(excelRequest.getUserId().concat(".").concat(ext), workbook);
            throw new SbcRuntimeException("K-030404", new Object[]{ext});
        }

        //批量保存物流公司且自身重复的数据进行去重
        List<LogisticsCompanyVO> collect = logisticsCompanies.stream()
                .filter(distinctByKey(o -> o.getLogisticsName()))
                .collect(Collectors.toList());
        logisticsCompanyProvider.saveAll(LogisticsCompanyImportExcelRequest.builder().logisticsCompanyVOS(collect).build());
        return LogisticsCompanyImportResponse.builder().successFlag(DefaultFlag.YES).build();
    }

    /**
     * 重复数据工具类抽取
     * @param list
     * @return
     */
    public  List<String> getDuplicateElements(List<String> list){
        return list.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, (a, b) -> a + b))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    /**
     * EXCEL错误文件-本地生成
     *
     * @param newFileName 新文件名
     * @param wk          Excel对象
     * @return 新文件名
     * @throws SbcRuntimeException
     */
    private String errorExcel(String newFileName, Workbook wk) throws SbcRuntimeException {
//        //图片存储地址
//        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_EXCEL_DIR).concat("/");
//
//        // 根据真实路径创建目录文件
//        File picSaveFile = new File(t_realPath);
//        if (!picSaveFile.exists()) {
//            try {
//                picSaveFile.mkdirs();
//            } catch (Exception e) {
//                log.error("创建文件路径失败->".concat(t_realPath), e);
//                throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
//            }
//        }
//        //获取扩展名
//        File newFile = new File(t_realPath.concat(newFileName));
//        if (newFile.exists()) {
//            newFile.delete();
//        }
//        FileOutputStream fos = null;
//        try {
//            newFile.createNewFile();
//            fos = new FileOutputStream(newFile);
//            wk.write(fos);
//            return newFileName;
//        } catch (IOException e) {
//            log.error("生成文件失败->".concat(t_realPath.concat(newFileName)), e);
//            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    log.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), e);
//                }
//            }
//        }
        
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
			wk.write(baos);
		} catch (IOException e) {
          log.error("Workbook转ByteArrayOutputStream时发生异常");
          throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
		}
    	byte[] bytes = baos.toByteArray();
    	String fileKey = LogisticsCompanyService.ERR_EXCEL_PREFIX + newFileName;
		BaseResponse<String> justUploadFile = yunServiceProvider.justUploadFile(YunUploadResourceRequest.builder()
		        .resourceKey(fileKey)
		        .content(bytes)
		        .build());
		log.info(justUploadFile.getContext());
	    return newFileName;
    }

    /**
     * 验证EXCEL
     *
     * @param workbook
     */
    private void checkExcel(Workbook workbook) {
        try {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            if (!(row.getCell(0).getStringCellValue().contains("物流公司") && sheet.getSheetName().contains("物流公司导入模板"))) {
                throw new SbcRuntimeException("K-030406");
            }
        } catch (Exception e) {
            throw new SbcRuntimeException("K-030406");
        }
    }
}
