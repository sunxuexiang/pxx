package com.wanmi.sbc.department.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.customer.api.constant.DepartmentErrorCode;
import com.wanmi.sbc.customer.api.provider.department.DepartmentProvider;
import com.wanmi.sbc.customer.api.request.department.DepartmentImportRequest;
import com.wanmi.sbc.customer.bean.dto.DepartmentImportDTO;
import com.wanmi.sbc.department.request.DepartmentExcelImportRequest;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 商品EXCEL处理服务
 * Created by dyt on 2017/8/17.
 */
@Slf4j
@Service
public class DepartmentExcelService {

    @Autowired
    private DepartmentProvider departmentProvider;

    /**
     * 导入模板
     *
     * @return
     */
    public void importDepartment(DepartmentExcelImportRequest departmentExcelImportRequest) {
        final String ext = departmentExcelImportRequest.getExt();
        final String filePath =
                HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/").concat(departmentExcelImportRequest.getUserId()).concat(".").concat(ext);
        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE,
                    new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }
        Set<String> importDepartmentString = new HashSet<>();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            //检测文档正确性
            this.checkExcel(workbook);


            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 2) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }


            boolean isError = false;

            Boolean isCheckDepartment = Boolean.FALSE;

            String[] departmentNames;
            //循环除了第一行的所有行
            for (int rowNum = 2; rowNum <= lastRowNum; rowNum++) {
                //获得当前行
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                boolean isNotEmpty = false;
                Cell cell = row.getCell(0);
                if(cell == null) {
                    cell = row.createCell(0);
                }
                if(StringUtils.isNotBlank(ExcelHelper.getValue(cell))){
                    isNotEmpty = true;
                }
                //数据都为空，则跳过去
                if (!isNotEmpty) {
                    continue;
                }
                String departmentName =  ExcelHelper.getValue(cell);
                if (StringUtils.isBlank(departmentName)) {
                    ExcelHelper.setError(workbook, cell, "此项必填");
                    isError = true;
                }  else if (ValidateUtil.containsEmoji(departmentName)) {
                    ExcelHelper.setError(workbook, cell, "含有非法字符");
                    isError = true;
                }
                departmentNames = departmentName.split("-");
                isCheckDepartment =  Arrays.stream(departmentNames).anyMatch(s -> (StringUtils.isBlank(s) || s.length() > 20));
                if (isCheckDepartment.equals(Boolean.TRUE)) {
                    ExcelHelper.setError(workbook, cell, "格式不正确");
                    isError = true;
                }else{
                    isCheckDepartment = Boolean.FALSE;
                }
                importDepartmentString.add(departmentName);

            }

            if (isError) {
                this.errorExcel(departmentExcelImportRequest.getUserId().concat(".").concat(ext), workbook);
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{ext});
            }

            if (CollectionUtils.isEmpty(importDepartmentString)){
                throw new SbcRuntimeException(DepartmentErrorCode.DATA_FILE_FAILD);
            }
            List<DepartmentImportDTO> addRequestList = new ArrayList<>();
            String departmentId = UUIDUtil.getUUID();
            for (String node : importDepartmentString) {
                String[] nodeData = node.split("-");
                if (nodeData.length < 1){
                    continue;
                }
                //部门-根节点
                String parentDepartmentName = nodeData[0];
                //
                String preParentDepartmentName = parentDepartmentName;
                //
                String checkPreParentDepartmentName = parentDepartmentName;
                //上一级部门名称
                String preDepartmentName="";
                int allNodeLength = nodeData.length ;
                for (int i = 0; i < allNodeLength; i++) {

                    if (checkDepartmentIsExit(addRequestList,nodeData[i],parentDepartmentName,i)) {
                        checkPreParentDepartmentName = parentDepartmentName;
                        parentDepartmentName = parentDepartmentName.concat("-").concat(nodeData[i]);
                        preDepartmentName = nodeData[i];
                        continue;
                    }else{
                        preParentDepartmentName =  checkPreParentDepartmentName;
                    }
                    String parentDepartmentId = getParentDepartmentId(addRequestList,preParentDepartmentName,preDepartmentName,i);
                    String parentDepartmentIds = getParentDepartmentIds(addRequestList,preParentDepartmentName,preDepartmentName,i);
                    int  sort = getDepartmentSort(addRequestList,parentDepartmentName,i).intValue();
                    DepartmentImportDTO nodeAddRequest = DepartmentImportDTO.builder().departmentId(departmentId)
                            .departmentName(nodeData[i]).companyInfoId(departmentExcelImportRequest.getCompanyInfoId())
                            .departmentGrade(i+1).departmentSort(sort+1).parentDepartmentId(parentDepartmentId)
                            .employeeNum(0).parentDepartmentIds(parentDepartmentIds).delFlag(DeleteFlag.NO).createTime(LocalDateTime.now()).createPerson(departmentExcelImportRequest.getUserId()).parentDepartmentName(parentDepartmentName).build();
                    addRequestList.add(nodeAddRequest);
                    preDepartmentName = nodeData[i];
                    checkPreParentDepartmentName = parentDepartmentName;
                    parentDepartmentName = parentDepartmentName.concat("-").concat(nodeData[i]);

                    departmentId = UUIDUtil.getUUID();
                }

            }
            if (CollectionUtils.isNotEmpty(addRequestList)) {
                departmentProvider.addfromImport(new DepartmentImportRequest(addRequestList));
            }

        } catch (SbcRuntimeException e) {
            log.error("部门导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("部门导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }

    /**
     * 根据当前部门名称、父部门层级名称 验证部门是否已存在
     * @param addRequestList
     * @param departmentName
     * @param parentDepartmentName
     * @param departmentGrade
     * @return
     */
    private Boolean checkDepartmentIsExit (List<DepartmentImportDTO> addRequestList ,final String departmentName,final String parentDepartmentName,final int departmentGrade){
      return  addRequestList.stream().anyMatch(v -> v.getDepartmentName().equals(departmentName) && v.getParentDepartmentName().equals(parentDepartmentName) && v.getDepartmentGrade().equals(departmentGrade+1));
    }

    /**
     * 根据前一级的部门层级名称、前一个部门名称、层级 查询出当前部门的父部门ID
     * @param addRequestList
     * @param parentDepartmentName
     * @param preDepartmentName
     * @param departmentGrade
     * @return
     */
    private String getParentDepartmentId( List<DepartmentImportDTO> addRequestList ,final String parentDepartmentName,final String preDepartmentName,final int departmentGrade){
       return departmentGrade == 0 ? "0" : addRequestList.stream().filter(v -> v.getParentDepartmentName().equals(parentDepartmentName) && v.getDepartmentName().equals(preDepartmentName)  && v.getDepartmentGrade().equals(departmentGrade))
                .map(DepartmentImportDTO::getDepartmentId).findFirst().orElse("0");
    }

    /**
     * 根据前一级部门的部门层级名称、前一个部门名称、层级 拼接出当前部门的层级数据结构（ 1-10-15 ）
     * @param addRequestList
     * @param parentDepartmentName
     * @param preDepartmentName
     * @param departmentGrade
     * @return
     */
    private String getParentDepartmentIds( List<DepartmentImportDTO> addRequestList ,final String parentDepartmentName,final String preDepartmentName,final int departmentGrade){
        return departmentGrade == 0 ? "0|" : addRequestList.stream().filter(v -> v.getParentDepartmentName().equals(parentDepartmentName) && v.getDepartmentName().equals(preDepartmentName) && v.getDepartmentGrade().equals(departmentGrade))
                .map(v ->
                    v.getParentDepartmentIds().concat(v.getDepartmentId()).concat("|")
                ).findFirst().orElse("");
    }

    /**
     * 根据当前部门的部门层级名称，当前层级汇总出同父层级的总数
     * @param addRequestList
     * @param parentDepartmentName
     * @param departmentGrade
     * @return
     */
    private Long getDepartmentSort( List<DepartmentImportDTO> addRequestList ,final String parentDepartmentName,final int departmentGrade){
        return  addRequestList.stream().filter(v -> v.getParentDepartmentName().equals(parentDepartmentName) && v.getDepartmentGrade().equals(departmentGrade+1))
                .count();
    }

    /**
     * 验证EXCEL
     * @param workbook
     */
    private void checkExcel(Workbook workbook){
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row firstRow = sheet1.getRow(0);
            Row secondRow = sheet1.getRow(1);
            if(!(firstRow.getCell(0).getStringCellValue().contains("填写须知：\n" +
                    "<1>上下级部门间用\"-\"隔开，且从最上级部门开始，例如\"市场部-杭州分部\"；\n" +
                    "<2>每级部门名称不可超过")) || !(secondRow.getCell(0).getStringCellValue().equals("部门名称"))){
                throw new SbcRuntimeException(DepartmentErrorCode.DATA_FILE_FAILD);
            }
        }catch (Exception e){
            throw new SbcRuntimeException(DepartmentErrorCode.DATA_FILE_FAILD);
        }
    }

    /**
     * EXCEL错误文件-本地生成
     * @param newFileName 新文件名
     * @param wk Excel对象
     * @return 新文件名
     * @throws SbcRuntimeException
     */
    public String errorExcel(String newFileName, Workbook wk) throws SbcRuntimeException {
        //图片存储地址
        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_EXCEL_DIR).concat("/");

        // 根据真实路径创建目录文件
        File picSaveFile = new File(t_realPath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception e) {
                log.error("创建文件路径失败->".concat(t_realPath), e);
                throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
            }
        }

        //获取扩展名
        File newFile = new File(t_realPath.concat(newFileName));
        if(newFile.exists()){
            newFile.delete();
        }
        FileOutputStream fos = null;
        try {
            newFile.createNewFile();
            fos = new FileOutputStream(newFile);
            wk.write(fos);
            return newFileName;
        } catch (IOException e) {
            log.error("生成文件失败->".concat(t_realPath.concat(newFileName)), e);
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        } finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), e);
                }
            }
        }
    }

    /**
     * 下载Excel错误文档
     *
     * @param userId 用户Id
     * @param ext    文件扩展名
     */
    public void downErrExcel(String userId, String ext) {
        //图片存储地址
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_EXCEL_DIR).concat("/").concat(userId).concat(".").concat(ext);
        File picSaveFile = new File(filePath);
        FileInputStream is = null;
        ServletOutputStream os = null;
        try {
            if (picSaveFile.exists()) {
                is = new FileInputStream(picSaveFile);
                os = HttpUtil.getResponse().getOutputStream();
                String fileName = URLEncoder.encode("错误表格.".concat(ext), "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                        "filename*=\"utf-8''%s\"", fileName, fileName));

                byte b[] = new byte[1024];
                //读取文件，存入字节数组b，返回读取到的字符数，存入read,默认每次将b数组装满
                int read = is.read(b);
                while (read != -1) {
                    os.write(b, 0, read);
                    read = is.read(b);
                }
                HttpUtil.getResponse().flushBuffer();
            }
        } catch (Exception e) {
            log.error("下载EXCEL文件异常->", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("下载EXCEL文件关闭IO失败->", e);
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("下载EXCEL文件关闭IO失败->", e);
                }
            }
        }

    }
}