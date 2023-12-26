package com.wanmi.sbc.customer.contract.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.request.fadada.WordParamsRequest;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import com.wanmi.sbc.customer.contract.model.root.Contract;
import com.wanmi.sbc.customer.contract.model.root.CustomerContract;
import com.wanmi.sbc.customer.contract.repository.ContractRepository;
import com.wanmi.sbc.customer.contract.repository.CustomerContractRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerContractService {
    @Autowired
    private CustomerContractRepository customerContractRepository;
    // 读取省、市、区JSON文件并解析为JSON对象
    private static JSONArray provinceArray = loadJsonArrayFromResources("provinces.json");
    private static JSONArray cityData = loadJsonArrayFromResources("cities.json");
    private static JSONArray districtData = loadJsonArrayFromResources("areas.json");

    public void save(WordParamsRequest wordParamsRequest) {
        CustomerContract customerContract = new CustomerContract();
        KsBeanUtil.copyProperties(wordParamsRequest,customerContract);
        if (StringUtils.isNotEmpty(wordParamsRequest.getApplicationSigningDate())) {
            customerContract.setApplicationSigningDate(convertDate(wordParamsRequest.getApplicationSigningDate()));
        }
        if (StringUtils.isNotEmpty(wordParamsRequest.getPeriodEnd())) {
            customerContract.setPeriodEnd(convertDate(wordParamsRequest.getPeriodEnd()));
        }
        if (StringUtils.isNotEmpty(wordParamsRequest.getPeriodStart())) {
            customerContract.setPeriodStart(convertDate(wordParamsRequest.getPeriodStart()));
        }
        CustomerContract byEmployeeId = null;
        if (StringUtils.isNotEmpty(wordParamsRequest.getEmployeeId())){
            byEmployeeId = customerContractRepository.findByEmployeeId(wordParamsRequest.getEmployeeId());
        }else if (StringUtils.isNotEmpty(wordParamsRequest.getContractPhone())) {
            byEmployeeId = customerContractRepository.findByContractPhone(wordParamsRequest.getContractPhone());
        } else if(StringUtils.isNotEmpty(wordParamsRequest.getAppCustomerId())) {
            byEmployeeId = customerContractRepository.findByAppCustomerId(wordParamsRequest.getAppCustomerId());
        }

        if (null != byEmployeeId) {
            customerContract.setCustomerContractId(byEmployeeId.getCustomerContractId());
            customerContract.setSignImage(StringUtils.isNotEmpty(wordParamsRequest.getSignImage())?wordParamsRequest.getSignImage():byEmployeeId.getSignImage());
        }
//        // 添加省市区，详情地址
//        StringBuilder sb = new StringBuilder();
//        customerContract.setProvinceId(paserCode(provinceArray,customerContract.getBusinessAddress(),sb));
//        customerContract.setCityId(paserCode(cityData,customerContract.getBusinessAddress(),sb));
//        customerContract.setAreaId(paserCode(districtData,customerContract.getBusinessAddress(),sb));
//        customerContract.setDetailAddress(getAddressDetail(customerContract.getBusinessAddress(), sb.toString()));
        customerContractRepository.save(customerContract);
    }

    private String getAddressDetail(String address,String removeAddress) {
        // 查找前缀的起始位置
        int startIndex = address.indexOf(removeAddress.toString());
        if (startIndex != -1) {
            // 使用substring获取前缀之后的部分
            return address.substring(startIndex + removeAddress.length());
        } else {
            // 如果找不到前缀，则保持原始地址不变
            return address;
        }
    }
    public void saveAndUpdate(WordParamsRequest wordParamsRequest) {
        CustomerContract byEmployeeId = null;
        if (StringUtils.isNotEmpty(wordParamsRequest.getAppCustomerId())) {
            byEmployeeId = customerContractRepository.findByAppCustomerId(wordParamsRequest.getAppCustomerId());
        } else {
            byEmployeeId = customerContractRepository.findByContractPhone(wordParamsRequest.getContractPhone());
        }
        if (null != byEmployeeId) {
            byEmployeeId.setEmployeeId(wordParamsRequest.getEmployeeId());
            customerContractRepository.save(byEmployeeId);
        }

    }
    public Date convertDate (String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
        return null;
    }
    public  WordParamsRequest findByEmployeeId(WordParamsRequest wordParamsRequest) {
        CustomerContract byEmployeeId = null;
        if (!StringUtils.isEmpty(wordParamsRequest.getEmployeeId())) {
            byEmployeeId = customerContractRepository.findByEmployeeId(wordParamsRequest.getEmployeeId());
        }else if (StringUtils.isNotEmpty(wordParamsRequest.getAppCustomerId())) {
            byEmployeeId = customerContractRepository.findByAppCustomerId(wordParamsRequest.getAppCustomerId());
        } else if (StringUtils.isNotEmpty(wordParamsRequest.getContractPhone())) {
            byEmployeeId = customerContractRepository.findByContractPhone(wordParamsRequest.getContractPhone());
        }
        if (null != byEmployeeId) {
            WordParamsRequest request = new WordParamsRequest();
            KsBeanUtil.copyProperties(byEmployeeId,request);
            return request;
        }
        return null;
    }

    private static JSONArray loadJsonArrayFromResources(String fileName) {
        try (InputStream inputStream = Objects.requireNonNull(
                CustomerContractService.class.getClassLoader().getResourceAsStream(fileName));
             Scanner scanner = new Scanner(inputStream, String.valueOf(StandardCharsets.UTF_8))) {

            StringBuilder jsonContent = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonContent.append(scanner.nextLine());
            }

            return new JSONArray(jsonContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static Long paserCode(JSONArray arrays,String address,StringBuilder sb) {
        String[] addressArray = address.split(" ");
        for (int i = 0; i < arrays.length(); i++) {
            JSONObject array = arrays.getJSONObject(i);
            for (String addressPart : addressArray) {
                if (array.has("name") && addressPart.contains(array.getString("name"))) {
                    sb.append(array.getString("name"));
                    return array.getLong("code");
                }
            }
        }
        return null;
    }

    public List<CustomerContract> findByTabRelationIds(List<String> relationIds,List<String>phoneIds) {
        return customerContractRepository.findByTabRelationIds(relationIds,phoneIds);
    }

    public BaseResponse delContractInfo(WordParamsRequest request) {
        customerContractRepository.delContractInfo(request.getEmployeeId());
        return BaseResponse.SUCCESSFUL();
    }
}
