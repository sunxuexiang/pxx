package com.wanmi.sbc.customer;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerModifyRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerRelationBatchRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListByAccountTypeRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeePageRequest;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeListByAccountTypeVO;
import com.wanmi.sbc.customer.bean.vo.EmployeePageVO;
import com.wanmi.sbc.customer.dto.CustomerRelationImportDto;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/19 17:07
 */
@Api(description = "平台会员API", tags = "CustomerRelationImportController")
@RestController
@RequestMapping(value = "/customer/relation")
@Slf4j
public class CustomerRelationImportController {

    @Value("classpath:customer_manager_import_template.xls")
    private Resource templateFile;

    @Autowired
    private CustomerProvider customerProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 客户与业务关系导入模板
     */
    @ApiOperation(value = "客户与业务关系导入模板下载")
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
            if (org.apache.commons.lang.StringUtils.isNotBlank(file)) {
                String fileName = URLEncoder.encode("客户与业务关系导入模板.xls", "UTF-8");
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
                log.error("客户与业务关系导入模板转Base64位异常", e);
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("读取客户与业务关系导入模板异常", e);
                }
            }
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("平台会员", "客户与业务关系导入模板下载","操作成功");
    }

    @ApiOperation(value = "客户与业务关系导入")
    @PostMapping(value = "/import")
    public BaseResponse relationImport(@RequestParam(value = "file") MultipartFile file) {
        // 校验文件格式
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        if (!("xls".equalsIgnoreCase(fileSuffix) || "xlsx".equalsIgnoreCase(fileSuffix))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        File files = transferToFile(file);
        try (Workbook workbook = WorkbookFactory.create(files)) {
            // 创建Workbook工作薄对象，表示整个excel
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }
            // 获得当前sheet的开始行
            int firstRowNum = sheet.getFirstRowNum();
            // 获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 1) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }
            /*if (lastRowNum > 3000) {
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{"一次最多导入3000条数据，请重新导入"});
            }*/

            int maxCell = 3;
            List<CustomerRelationImportDto> importDtoList = new ArrayList<>();
            // 循环除了第一行的所有行
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                // 获得当前行
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                Cell[] cells = new Cell[maxCell];
                boolean isNotEmpty = false;
                for (int i = 0; i < 3; i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null) {
                        cell = row.createCell(i);
                    }
                    cells[i] = cell;
                    if (StringUtils.isNotBlank(ExcelHelper.getValue(cell))) {
                        isNotEmpty = true;
                    }
                }
                // 数据都为空，则跳过去
                if (!isNotEmpty) {
                    continue;
                }

                // 客户账号
                String customerAccount = ExcelHelper.getValue(cells[0]);
                // 业务代表名称
                String representative = ExcelHelper.getValue(cells[1]);
                // 白鲸管家名称
                String managerName = ExcelHelper.getValue(cells[2]);

                CustomerRelationImportDto importDto = CustomerRelationImportDto
                        .builder()
                        .customerAccount(customerAccount)
                        .employee(representative)
                        .managerName(managerName)
                        .build();

                importDtoList.add(importDto);

            }

            if (importDtoList.size() > 3000) {
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{"一次最多导入3000条数据，当前条数" + importDtoList.size() + ",请重新导入"});
            }

            List<Map<String, Object>> reasonList = new ArrayList<>();
            // 校验数据
            this.checkData(importDtoList, reasonList);

            if (CollectionUtils.isEmpty(reasonList)) {
                // 处理数据
                // log.info("客户与业务导入修改数据开始：{}", JSON.toJSONString(importDtoList));
                this.updateData(importDtoList);
            }

            Map<String, Object> data = new HashMap<>();
            // 1导入成功  0 导入失败
            int status = CollectionUtils.isEmpty(reasonList) ? 1 : 0;
            data.put("status", status);
            data.put("reason", reasonList);
            //操作日志记录
            operateLogMQUtil.convertAndSend("平台会员", "客户与业务关系导入","操作成功");
            return BaseResponse.success(data);
        } catch (SbcRuntimeException e) {
            log.error("促销活动商品导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("促销活动商品导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }


    }

    public void updateData(List<CustomerRelationImportDto> importDtoList) {
        CustomerRelationBatchRequest batchRequest = CustomerRelationBatchRequest.builder().relationList(KsBeanUtil.convertList(importDtoList, CustomerModifyRequest.class)).build();
        customerProvider.customerRelationBatch(batchRequest);
    }

    /**
     * 检查excel中的数据
     */
    public void checkData(List<CustomerRelationImportDto> importDtoList, List<Map<String, Object>> reasonList) {

        HashMap<String, Integer> mapExist = new HashMap<>();
        for (CustomerRelationImportDto importDto : importDtoList) {
            String customerAccount = importDto.getCustomerAccount();
            String employee = importDto.getEmployee();
            String managerName = importDto.getManagerName();
            String reason = "";
            if (StringUtils.isBlank(customerAccount)) {
                reason = reason + "客户账号为空;";
            }else {
                importDto.setCustomerAccount(customerAccount.trim());
            }

            if (StringUtils.isBlank(employee)) {
                reason = reason + "业务代表名称为空;";
            }else {
                importDto.setEmployee(employee.trim());
            }

            if (StringUtils.isBlank(managerName)) {
                reason = reason + "白鲸管家名称为空;";
            }else {
                importDto.setManagerName(managerName.trim());
            }

            if (mapExist.containsKey(customerAccount)) {
                reason = reason + "客户账号在表格中存在重复项;";
            }

            if (StringUtils.isNotBlank(reason)) {
                Map<String, Object> map = new HashMap<>();
                map.put("customerAccount", customerAccount);
                map.put("employeeName", employee);
                map.put("managerName", managerName);
                map.put("reason", reason);
                reasonList.add(map);
            }
            mapExist.put(customerAccount, 1);
        }

        if (CollectionUtils.isEmpty(reasonList)) {
            // 表中数据没问题 校验数据库

            List<String> accountList = importDtoList.stream().map(CustomerRelationImportDto::getCustomerAccount).collect(Collectors.toList());

            List<CustomerDetailVO> voList = customerQueryProvider.listCustomerDetailByCondition(CustomerDetailListByConditionRequest.builder().customerAccountList(accountList).build())
                    .getContext().getDetailResponseList();

            List<EmployeeListByAccountTypeVO> employeeList = employeeQueryProvider.listByAccountType(EmployeeListByAccountTypeRequest.builder().accountType(AccountType.s2bBoss).build())
                    .getContext().getEmployeeList();

            EmployeePageRequest request = new EmployeePageRequest();
            request.setAccountType(AccountType.s2bBoss);
            request.putSort("isMasterAccount", SortType.DESC.toValue());
            request.putSort("manageDepartmentIds", SortType.DESC.toValue());
            request.putSort("createTime", SortType.DESC.toValue());
            request.setPageNum(0);
            request.setPageSize(10000);
            // request.setIsHiddenDimission(1);
            request.setAccountState(AccountState.ENABLE);
            request.setDepartmentIds(Collections.singletonList("7ffffe8beb0ae7de2746167928a48af6"));
            List<EmployeePageVO> repList = employeeQueryProvider.page(request)
                    .getContext().getEmployeePageVOPage().getContent();
            List<EmployeePageVO> manageList = new ArrayList<>(repList);
            EmployeePageVO vo1 = new EmployeePageVO();
            vo1.setEmployeeId("2c8080815cd3a74a015cd3ae86850001");
            vo1.setEmployeeName("system");
            manageList.add(vo1);

            for (CustomerRelationImportDto importDto : importDtoList) {
                String customerAccount = importDto.getCustomerAccount();
                String employee = importDto.getEmployee();
                String managerName = importDto.getManagerName();

                String reason = "";

                CustomerDetailVO detailVO = voList.stream().filter(vo -> StringUtils.equals(customerAccount, vo.getCustomerVO().getCustomerAccount())).findFirst().orElse(null);
                if (Objects.isNull(detailVO)) {
                    reason = reason + "客户账号系统不存在;";
                }else {
                    importDto.setCustomerDetailId(detailVO.getCustomerDetailId());
                }

                List<EmployeeListByAccountTypeVO> employeeVoList = employeeList.stream().filter(vo -> StringUtils.equals(employee, vo.getEmployeeName())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(employeeVoList)) {
                    reason = reason + "业务代表系统不存在;";
                } else if (employeeVoList.size() > 1) {
                    reason = reason + "业务代表姓名在系统中重名;";
                }else {
                    importDto.setEmployeeId(employeeVoList.get(0).getEmployeeId());
                }

                List<EmployeePageVO> managerVoList = manageList.stream().filter(vo -> StringUtils.equals(managerName, vo.getEmployeeName())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(managerVoList)) {
                    reason = reason + "白鲸管家系统不存在;";
                } else if (managerVoList.size() > 1) {
                    reason = reason + "白鲸管家姓名在系统中重名;";
                }else {
                    importDto.setManagerId(managerVoList.get(0).getEmployeeId());
                }

                if (StringUtils.isNotBlank(reason)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("customerAccount", customerAccount);
                    map.put("employeeName", employee);
                    map.put("managerName", managerName);
                    map.put("reason", reason);
                    reasonList.add(map);
                }

            }

        }
    }

    /**
     * MultipartFile 转换为 File 文件
     *
     * @param multipartFile
     * @return
     */
    public static File transferToFile(MultipartFile multipartFile) {
        // 选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            // 获取文件后缀
            String prefix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 创建临时文件
            file = File.createTempFile(originalFilename, prefix);
            multipartFile.transferTo(file);
            // 删除
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
