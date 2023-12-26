package com.wanmi.sbc.customer.service;

import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.customer.api.provider.department.DepartmentQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.RoleInfoQueryProvider;
import com.wanmi.sbc.customer.api.request.department.DepartmentListRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeImportRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeJobNoExistsRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeMobileExistsRequest;
import com.wanmi.sbc.customer.api.request.employee.RoleInfoListRequest;
import com.wanmi.sbc.customer.api.response.employee.RoleInfoListResponse;
import com.wanmi.sbc.customer.bean.dto.EmployeeDTO;
import com.wanmi.sbc.customer.bean.enums.GenderType;
import com.wanmi.sbc.customer.bean.vo.DepartmentVO;
import com.wanmi.sbc.customer.bean.vo.RoleInfoVO;
import com.wanmi.sbc.customer.request.EmployeeExcelImportRequest;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.wanmi.sbc.pay.PayCallbackController.LOGGER;

/**
 * 员工Excel处理服务
 */
@Slf4j
@Service
public class EmployeeExcelService {

    @Autowired
    private DepartmentQueryProvider departmentQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private RoleInfoQueryProvider roleInfoQueryProvider;

    @Autowired
    private EmployeeProvider employeeProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;


    /**
     * 导入模版
     * @param employeeExcelImportRequest
     */
    public void importEmployee(EmployeeExcelImportRequest employeeExcelImportRequest){
        final String ext = employeeExcelImportRequest.getExt();
        final String filePath = HttpUtil.getProjectRealPath().concat("/")
                .concat(Constants.EXCEL_DIR).concat("/")
                .concat(employeeExcelImportRequest.getUserId())
                .concat(".").concat(ext);

        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE,
                    new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }
        Map<String, String> departments = this.getDepartments();
        List<RoleInfoVO> roleInfos = this.getRoles();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }
            //检测文档正确性
            this.checkExcel(workbook);
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 3) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            int maxCell = 8;
            boolean isError = false;
            List<EmployeeDTO> employeeDTOS = new ArrayList<>();
            //循环除了第一行的所有行
            for (int rowNum = 3; rowNum <= lastRowNum; rowNum++) {
                //获得当前行
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                boolean isNotEmpty = false;
                Cell[] cells = new Cell[maxCell];
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
                EmployeeDTO employeeDTO = new EmployeeDTO();
                //用户名
                String employeeName =  ExcelHelper.getValue(cells[0]);
                if (StringUtils.isBlank(employeeName)) {
                    ExcelHelper.setError(workbook, cells[0], "此项必填");
                    isError = true;
                }  else if (employeeName.length() > 20) {
                    ExcelHelper.setError(workbook, cells[0], "用户名长度过长");
                    isError = true;
                }else{
                    employeeDTO.setEmployeeName(employeeName);
                }

                //手机号
                String employeeMobile = ExcelHelper.getValue(cells[1]);
                if (StringUtils.isBlank(employeeMobile)) {
                    ExcelHelper.setError(workbook, cells[1], "此项必填");
                    isError = true;
                }  else if (!ValidateUtil.isPhone(employeeMobile)) {
                    ExcelHelper.setError(workbook, cells[1], "请输入正确的手机号");
                    isError = true;
                }  else if (mobileNoIsExists(employeeMobile,employeeExcelImportRequest.getAccountType(),
                        employeeExcelImportRequest.getCompanyInfoId())) {
                    ExcelHelper.setError(workbook, cells[1], "手机号已被使用");
                    isError = true;
                }else{
                    employeeDTO.setEmployeeMobile(employeeMobile);
                }

                //工号
                String jobNo = ExcelHelper.getValue(cells[2]);
                if (jobNo.length() > 20) {
                    ExcelHelper.setError(workbook, cells[2], "工号长度过长");
                    isError = true;
                }else if(jobNoIsExists(jobNo, employeeExcelImportRequest.getAccountType(),
                        employeeExcelImportRequest.getCompanyInfoId())){
                    ExcelHelper.setError(workbook, cells[2], "工号已被占用");
                    isError = true;
                }else if(StringUtils.isNotBlank(jobNo)){
                    employeeDTO.setJobNo(jobNo);
                }

                //邮箱
                String email = ExcelHelper.getValue(cells[3]);
                if(StringUtils.isNotBlank(email)){
                    if (!SensitiveUtils.isEmail(email)) {
                        ExcelHelper.setError(workbook, cells[3], "请输入正确的邮箱");
                        isError = true;
                    }else {
                        employeeDTO.setEmail(email);
                    }
                }


                //部门
                String nameStr = ExcelHelper.getValue(cells[4]);
                if(StringUtils.isNotEmpty(nameStr)){
                    if(!checkDepartmentIsExist(Arrays.asList(nameStr.split(",")), departments)){
                        ExcelHelper.setError(workbook, cells[4], "部门不存在");
                        isError = true;
                    }else{
                        String ids = dealDepartmentName(Arrays.asList(nameStr.split(",")), departments)
                                .stream().collect(Collectors.joining(","));
                        employeeDTO.setDepartmentIds(ids);
                    }
                }

                //岗位
                String position = ExcelHelper.getValue(cells[5]);
                if (position.length() > 20) {
                    ExcelHelper.setError(workbook, cells[5], "岗位长度过长");
                    isError = true;
                }else if(StringUtils.isNotBlank(position)){
                    employeeDTO.setPosition(position);
                }

                //角色
                String roles = ExcelHelper.getValue(cells[6]);
                if(StringUtils.isNotBlank(roles)){
                    List<String> roleNameList = Arrays.asList(roles.split(","));
                    if(!checkRoleIsExist(roleNameList, roleInfos)){
                        ExcelHelper.setError(workbook, cells[6], "角色不存在");
                        isError = true;
                    }else{
                        String ids = this.dealRoleName(roleNameList, roleInfos)
                                .stream().map(id -> id.toString())
                                .collect(Collectors.joining(","));
                        employeeDTO.setRoleIds(ids);
                    }
                }
                employeeDTO.setIsEmployee(1);
                employeeDTO.setSex(GenderType.SECRET);
                employeeDTO.setCompanyInfoId(employeeExcelImportRequest.getCompanyInfoId());
                employeeDTO.setAccountType(employeeExcelImportRequest.getAccountType());
                employeeDTOS.add(employeeDTO);
            }

            if (isError) {
                errorExcel(employeeExcelImportRequest.getUserId().concat(".").concat(ext), workbook);
                throw new SbcRuntimeException("K-030404", new Object[]{ext});
            }

            employeeProvider.importEmployee(new EmployeeImportRequest(employeeDTOS));


        }catch (SbcRuntimeException e) {
            log.error("部门导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("部门导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }

    /**
     * 验证EXCEL
     * @param workbook
     */
    private void checkExcel(Workbook workbook){
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row firstRow = sheet1.getRow(0);
            if(!(firstRow.getCell(0).getStringCellValue().contains("填写须知：\n" +
                    "<1>红色字段为必填字段，黑色字段为选填字段；\n" +
                    "<2>部门：请先到组织架构-部门管理添加好部门，再到本excel里填入，多部门用英文的“,”隔开，部门未填写时，归于全部部门；\n" +
                    "<3>角色：请先到组织架构-角色权限添加好角色，再到本excel里填入，多角色用英文的“,”隔开；"))){
                throw new SbcRuntimeException(GoodsImportErrorCode.DATA_FILE_FAILD);
            }
        }catch (Exception e){
            throw new SbcRuntimeException(GoodsImportErrorCode.DATA_FILE_FAILD);
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
     * @param userId 用户Id
     * @param ext 文件扩展名
     */
    public void downErrExcel(String userId, String ext){
        //图片存储地址
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_EXCEL_DIR).concat("/").concat(userId).concat(".").concat(ext);
        File picSaveFile = new File(filePath);
        FileInputStream is = null;
        ServletOutputStream os = null;
        try {
            if(picSaveFile.exists()){
                is = new FileInputStream(picSaveFile);
                os = HttpUtil.getResponse().getOutputStream();
                String fileName = URLEncoder.encode("错误表格.".concat(ext), "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));

                byte b[]=new byte[1024];
                //读取文件，存入字节数组b，返回读取到的字符数，存入read,默认每次将b数组装满
                int read = is.read(b);
                while(read!=-1) {
                    os.write(b,0,read);
                    read=is.read(b);
                }
                HttpUtil.getResponse().flushBuffer();
            }
        } catch (Exception e) {
            log.error("下载EXCEL文件异常->", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("下载EXCEL文件关闭IO失败->", e);
                }
            }

            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("下载EXCEL文件关闭IO失败->", e);
                }
            }
        }

    }

    /**
     * 上传文件
     * @param file 文件
     * @param userId 操作员id
     * @return 文件格式
     */
    public String upload(MultipartFile file, String userId){
        if (file == null || file.isEmpty()) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if(!(fileExt.equalsIgnoreCase("xls") || fileExt.equalsIgnoreCase("xlsx"))){
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_EXT_ERROR);
        }

        if (file.getSize() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        //上传存储地址
        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/");
        File picSaveFile = new File(t_realPath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception e) {
                log.error("创建文件路径失败->".concat(t_realPath), e);
                throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
            }
        }

        String newFileName = userId.concat(".").concat(fileExt);
        File newFile = new File(t_realPath.concat(newFileName));
        try {
            newFile.deleteOnExit();
            file.transferTo(newFile);
        } catch (IOException e) {
            log.error("上传Excel文件失败->".concat(newFile.getPath()), e);
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        return fileExt;
    }

    /**
     * 获取部门列表
     * @return
     */
    public Map<String, String> getDepartments(){
        Map<String, String> map = new HashMap<>();
        DepartmentListRequest request = new DepartmentListRequest();
        request.setCompanyInfoId(Objects.nonNull(commonUtil.getCompanyInfoId()) ? commonUtil.getCompanyInfoId() : 0L);
        request.setDelFlag(DeleteFlag.NO.toValue());
        request.putSort("departmentGrade", SortType.ASC.toValue());
        request.putSort("departmentSort", SortType.ASC.toValue());
        request.putSort("createTime", SortType.ASC.toValue());
        List<DepartmentVO> departmentVOS = departmentQueryProvider.listDepartmentTree(request).getContext().getDepartmentVOS();
        if(CollectionUtils.isNotEmpty(departmentVOS)){
            departmentVOS.stream().forEach(departmentVO -> {
                String[] parentIds = departmentVO.getParentDepartmentIds().split("\\|");

                String name = Arrays.asList(parentIds).stream().filter(id -> !id.equals("0")).map(id -> {
                    String departmentName = departmentVOS.stream()
                            .filter(d -> d.getDepartmentId().equals(id))
                            .findFirst().get().getDepartmentName();
                    return departmentName;
                }).collect(Collectors.joining("-"));
                if(StringUtils.isNotEmpty(name)){
                    name = name.concat("-").concat(departmentVO.getDepartmentName());
                }else{
                    name = departmentVO.getDepartmentName();
                }
                map.put(departmentVO.getDepartmentId(), name);
            });
        }
        return map;
    }

    /**
     * 获取角色列表
     * @return
     */
    public List<RoleInfoVO> getRoles(){
        List<RoleInfoVO> list = new ArrayList<>();
        RoleInfoListRequest request = new RoleInfoListRequest();
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        RoleInfoListResponse response = roleInfoQueryProvider.listByCompanyInfoId(request).getContext();
        if(Objects.nonNull(response)){
            list = response.getRoleInfoVOList();
        }
        return list;
    }

    /**
     * 校验部门是否存在
     * @param departmentNames
     * @param departments
     * @return
     */
    private boolean checkDepartmentIsExist(List<String> departmentNames, Map<String, String> departments){
        Map<String, String> map = new HashMap<>();
        if(departments.size() > 0){
            map = departmentNames.stream().filter(name -> departments.containsValue(name))
                    .collect(Collectors.toMap(String::toString, String::toString));
        }
        if(map.size() != departmentNames.size()){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 校验角色是否存在
     * @param roleNames
     * @param roleInfoVOS
     * @return
     */
    private boolean checkRoleIsExist(List<String> roleNames, List<RoleInfoVO> roleInfoVOS){
        List<Long> ids = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(roleInfoVOS)){
             ids = this.dealRoleName(roleNames, roleInfoVOS);
        }
        if(ids.size() != roleNames.size()){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 查找所属部门
     * @param departmentNames
     * @param departments
     * @return
     */
    private List<String> dealDepartmentName(List<String> departmentNames, Map<String, String> departments){
        List<String> ids = departmentNames.stream().map(name -> {
            return departments.entrySet().stream().filter(entry -> entry.getValue().equals(name)).findFirst().get().getKey();
        }).collect(Collectors.toList());

        return ids;
    }

    /**
     *获取指定名称的角色id
     * @param roleNames
     * @param roleInfoVOS
     * @return
     */
    private List<Long> dealRoleName(List<String> roleNames, List<RoleInfoVO> roleInfoVOS){
        List<Long> roleIds = roleInfoVOS.stream()
                .filter(roleInfoVO -> roleNames.contains(roleInfoVO.getRoleName()))
                .map(RoleInfoVO::getRoleInfoId).collect(Collectors.toList());
        return roleIds;
    }

    /**
     * 校验工号是否存在
     * @param jobNo
     * @param accountType
     * @return
     */
    private boolean jobNoIsExists(String jobNo, AccountType accountType, Long companyInfoId){
        EmployeeJobNoExistsRequest request = EmployeeJobNoExistsRequest.builder()
                .jobNo(jobNo)
                .accountType(accountType)
                .companyInfoId(companyInfoId)
                .build();
        return employeeQueryProvider.jobNoIsExist(request).getContext().getExists();
    }

    /**
     * 校验手机号是否存在
     * @param phone
     * @param accountType
     * @return
     */
    private boolean mobileNoIsExists(String phone, AccountType accountType, Long companyInfoId){
        EmployeeMobileExistsRequest request = EmployeeMobileExistsRequest.builder()
                .mobile(phone)
                .accountType(accountType)
                .companyInfoId(companyInfoId)
                .build();
        return employeeQueryProvider.mobileIsExists(request).getContext().isExists();
    }

}
