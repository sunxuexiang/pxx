package com.wanmi.sbc.customer.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.SensitiveUtils;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressAddRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerByAccountRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerRegisterRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerSendMobileCodeRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.dto.CustomerDetailDTO;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.enums.PointsServiceType;
import com.wanmi.sbc.customer.bean.enums.SmsTemplate;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.request.CustomerImportExcelRequest;
import com.wanmi.sbc.customer.response.CustomerImportExcelResponse;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 会员导入Excel处理服务
 *
 * @author minchen
 */
@Slf4j
@Service
public class CustomerImportExcelService {

    @Autowired
    private CustomerSiteQueryProvider customerSiteQueryProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Autowired
    private CustomerDeliveryAddressProvider customerDeliveryAddressProvider;
    /**
     * 导入模板
     *
     * @author minchen
     */
    @Transactional
    public CustomerImportExcelResponse importCustomer(CustomerImportExcelRequest excelRequest) {
        String ext = excelRequest.getExt();
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/").concat(excelRequest.getUserId()).concat(".").concat(ext);
        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        try (Workbook workbook = WorkbookFactory.create(file)) {
            //创建Workbook工作薄对象，表示整个excel
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            //检测文档正确性
            this.checkExcel(workbook);

            //获得当前sheet的开始行
            int firstRowNum = sheet.getFirstRowNum();
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 1) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }
            int maxCell = 4;
            boolean isError = false;

            List<CustomerRegisterRequest> registerRequests = new ArrayList<>();
            //循环除了第一行的所有行
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

                CustomerDTO customerDTO = new CustomerDTO();
                // 用户名
                String customerName = ExcelHelper.getValue(cells[0]);
                if (StringUtils.isBlank(customerName)) {
                    ExcelHelper.setError(workbook, cells[0], "此项必填");
                    isError = true;
                } else if (customerName.length() > 16) {
                    ExcelHelper.setError(workbook, cells[0], "用户名长度过长");
                    isError = true;
                }
                CustomerDetailDTO detailDTO = new CustomerDetailDTO();
                detailDTO.setCustomerName(customerName);
                detailDTO.setIsLive(1);
                detailDTO.setProvinceId(430000l);
                detailDTO.setCityId(430100l);
                detailDTO.setAreaId(430111l);
                detailDTO.setCustomerAddress("主播地址");
                customerDTO.setCustomerDetail(detailDTO);

                // 用户账号
                DecimalFormat df = new DecimalFormat("#");
                String customerAccount = df.format(cells[1].getNumericCellValue());

                if (StringUtils.isBlank(customerAccount)) {
                    ExcelHelper.setError(workbook, cells[1], "此项必填");
                    isError = true;
                } else if (!SensitiveUtils.isMobilePhone(customerAccount)) {
                    ExcelHelper.setError(workbook, cells[1], "请输入正确的手机号");
                    isError = true;
                }else{
                    customerDTO.setCustomerAccount(customerAccount);
                    customerDTO.setCustomerPassword(customerAccount.substring(5, 11));

                }

                // 用户积分
                String points = ExcelHelper.getValue(cells[2]);
                if (StringUtils.isNotBlank(points)) {
                    Long pointsAvailable = new BigDecimal(points).longValue();
                    if (pointsAvailable > 999999 || pointsAvailable < 0) {
                        ExcelHelper.setError(workbook, cells[2], "必须在0-999999整数范围内,可为0");
                        isError = true;
                    }
                    customerDTO.setPointsAvailable(pointsAvailable);
                } else {
                    customerDTO.setPointsAvailable(0L);
                }

                CustomerRegisterRequest registerRequest = new CustomerRegisterRequest();

                //平台导入，则会员默认已审核
                customerDTO.setCheckState(CheckState.CHECKED);
                registerRequest.setCustomerDTO(customerDTO);
                registerRequests.add(registerRequest);
            }

            if (isError) {
                errorExcel(excelRequest.getUserId().concat(".").concat(ext), workbook);
                throw new SbcRuntimeException("K-030404", new Object[]{ext});
            }

            /**
             * 注册用户并发送验证码
             */
            int sendMsgSuccessCount = 0;
            int sendMsgFailedCount = 0;

            for (CustomerRegisterRequest registerRequest : registerRequests) {
                CustomerByAccountRequest customerRequest = new CustomerByAccountRequest();
                customerRequest.setCustomerAccount(registerRequest.getCustomerDTO().getCustomerAccount());
                CustomerVO res = customerSiteQueryProvider.getCustomerByCustomerAccount(customerRequest).getContext();
                // 会员已存在，添加积分明细
                if (Objects.nonNull(res)) {
                    if (!registerRequest.getCustomerDTO().getPointsAvailable().equals(0L)) {
                        customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                                .customerId(res.getCustomerId())
                                .type(OperateType.GROWTH)
                                .serviceType(PointsServiceType.CUSTOMER_IMPORT)
                                .points(registerRequest.getCustomerDTO().getPointsAvailable())
                                .build());
                    }
                } else {
                    // 会员不存在则注册,添加积分明细
                    CustomerVO registerCustomer = customerSiteProvider.register(registerRequest).getContext();
                    if(Objects.nonNull(registerCustomer)){
                        CustomerDeliveryAddressAddRequest customerDeliveryAddressAddRequest=new CustomerDeliveryAddressAddRequest();
                        customerDeliveryAddressAddRequest.setCustomerId(registerCustomer.getCustomerId());
                        customerDeliveryAddressAddRequest.setConsigneeName("主播");
                        customerDeliveryAddressAddRequest.setConsigneeNumber(registerRequest.getCustomerDTO().getCustomerAccount());
                        customerDeliveryAddressAddRequest.setProvinceId(430000l);
                        customerDeliveryAddressAddRequest.setCityId(430100l);
                        customerDeliveryAddressAddRequest.setAreaId(430111l);
                        customerDeliveryAddressAddRequest.setCityName("长沙市");
                        customerDeliveryAddressAddRequest.setProvinceName("湖南省");
                        customerDeliveryAddressAddRequest.setAreaName("雨花区");
                        customerDeliveryAddressAddRequest.setDeliveryAddress("高桥大市场酒水食品城农副区喜吖吖大楼(喜吖吖大楼)");
                        customerDeliveryAddressAddRequest.setTwonId(430111007l);
                        customerDeliveryAddressAddRequest.setTwonName("高桥街道");
                        customerDeliveryAddressProvider.add(customerDeliveryAddressAddRequest);
                    }
                    if (!registerRequest.getCustomerDTO().getPointsAvailable().equals(0L)) {
                        customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                                .customerId(registerCustomer.getCustomerId())
                                .type(OperateType.GROWTH)
                                .serviceType(PointsServiceType.CUSTOMER_IMPORT)
                                .points(registerRequest.getCustomerDTO().getPointsAvailable())
                                .build());
                    }
                    // 是否需要发送短信
                    if (excelRequest.getSendMsgFlag()) {
                        CustomerSendMobileCodeRequest customerSendMobileCodeRequest = new CustomerSendMobileCodeRequest();
                        customerSendMobileCodeRequest.setMobile(registerRequest.getCustomerDTO().getCustomerAccount());
                        customerSendMobileCodeRequest.setRedisKey(CacheKeyConstant.IMPORT_CUSTOMER);
                        customerSendMobileCodeRequest.setSmsTemplate(SmsTemplate.CUSTOMER_IMPORT_SUCCESS);
                        if (Constants.yes.equals(customerSiteProvider.sendMobileCode(customerSendMobileCodeRequest).getContext().getResult())) {
                            sendMsgSuccessCount++;
                        } else {
                            // 验证码发送失败
                            sendMsgFailedCount++;
                        }
                    }
                }
            }

            CustomerImportExcelResponse response = new CustomerImportExcelResponse();
            response.setSendMsgSuccessCount(sendMsgSuccessCount);
            response.setSendMsgFailedCount(sendMsgFailedCount);
            return response;
        } catch (SbcRuntimeException e) {
            log.error("会员导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("会员导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }

    }


    /**
     * 生成主播账号
     * @param size
     */
    @Transactional
    public void importCustomer(int size) {
        for (int rowNum = 1; rowNum <= size; rowNum++) {
            CustomerDTO customerDTO = new CustomerDTO();
            CustomerDetailDTO detailDTO = new CustomerDetailDTO();
            String customerName="主播"+RandomUtils.nextInt(1,99999999);
            detailDTO.setCustomerName(customerName);
            detailDTO.setIsLive(1);
            detailDTO.setProvinceId(430000l);
            detailDTO.setCityId(430100l);
            detailDTO.setAreaId(430111l);
            detailDTO.setCustomerAddress("主播地址");
            customerDTO.setCustomerDetail(detailDTO);
            String customerAccount="199"+ RandomUtils.nextInt(1,99999999);
            customerDTO.setCustomerAccount(customerAccount);
            customerDTO.setCustomerPassword(customerAccount.substring(5, 11));
            customerDTO.setPointsAvailable(0L);
            CustomerRegisterRequest registerRequest = new CustomerRegisterRequest();
            //平台导入，则会员默认已审核
            customerDTO.setCheckState(CheckState.CHECKED);
            registerRequest.setCustomerDTO(customerDTO);

            CustomerByAccountRequest customerRequest = new CustomerByAccountRequest();
            customerRequest.setCustomerAccount(registerRequest.getCustomerDTO().getCustomerAccount());
            CustomerVO res = customerSiteQueryProvider.getCustomerByCustomerAccount(customerRequest).getContext();
            if (Objects.isNull(res)) {
                CustomerVO customerVO =  customerSiteProvider.register(registerRequest).getContext();
                CustomerDeliveryAddressAddRequest customerDeliveryAddressAddRequest=new CustomerDeliveryAddressAddRequest();
                customerDeliveryAddressAddRequest.setCustomerId(customerVO.getCustomerId());
                customerDeliveryAddressAddRequest.setConsigneeName(customerName);
                customerDeliveryAddressAddRequest.setConsigneeNumber(customerDTO.getCustomerAccount());
                customerDeliveryAddressAddRequest.setProvinceId(430000l);
                customerDeliveryAddressAddRequest.setCityId(430100l);
                customerDeliveryAddressAddRequest.setAreaId(430111l);
                customerDeliveryAddressAddRequest.setCityName("长沙市");
                customerDeliveryAddressAddRequest.setProvinceName("湖南省");
                customerDeliveryAddressAddRequest.setAreaName("雨花区");
                customerDeliveryAddressAddRequest.setDeliveryAddress("高桥大市场酒水食品城农副区喜吖吖大楼(喜吖吖大楼)");
                customerDeliveryAddressAddRequest.setTwonId(430111007l);
                customerDeliveryAddressAddRequest.setTwonName("高桥街道");
                customerDeliveryAddressProvider.add(customerDeliveryAddressAddRequest);
            }
        }
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
        if (newFile.exists()) {
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
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), e);
                }
            }
        }
    }

    /**
     * 验证EXCEL
     *
     * @param workbook
     */
    private void checkExcel(Workbook workbook) {
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row row = sheet1.getRow(0);
            Sheet sheet2 = workbook.getSheetAt(1);
            if (!(row.getCell(0).getStringCellValue().contains("客户名称") && sheet2.getSheetName().contains("数据"))) {
                throw new SbcRuntimeException("K-030406");
            }
        } catch (Exception e) {
            throw new SbcRuntimeException("K-030406");
        }
    }
}