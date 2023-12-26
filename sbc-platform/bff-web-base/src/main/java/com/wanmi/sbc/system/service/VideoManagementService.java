package com.wanmi.sbc.system.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdsRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeListByAccountTypeResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeListByAccountTypeVO;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 小视频逻辑处理器
 */
@Slf4j
@Service
public class VideoManagementService {

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    public void processUserInfo(BaseResponse<VideoManagementPageResponse> page){
        List<String> ids = new ArrayList<>();
        MicroServicePage<VideoManagementVO> videoManagementVOPage = page.getContext().getVideoManagementVOPage();
        videoManagementVOPage.stream().forEach(videoManagementVO -> {
            String coverFollowCustomerId = videoManagementVO.getCoverFollowCustomerId();
            if(!StringUtils.isEmpty(coverFollowCustomerId)){
                ids.add(coverFollowCustomerId);
            }
        });
        //有用户,查询用户信息,给用户设置信息
        if(ids.size()>0){
            EmployeeByIdsRequest employeeByIdsRequest = new EmployeeByIdsRequest();
            employeeByIdsRequest.setEmployeeIds(ids);
            BaseResponse<EmployeeListByAccountTypeResponse> byIds = employeeQueryProvider.getByIds(employeeByIdsRequest);
            List<EmployeeListByAccountTypeVO> employeeList = byIds.getContext().getEmployeeList();
            //进行数据比对
            videoManagementVOPage.stream().forEach(videoManagementVO -> {
                for (EmployeeListByAccountTypeVO employeeListByAccountTypeVO: employeeList) {
                    if(videoManagementVO.getCoverFollowCustomerId().equals(employeeListByAccountTypeVO.getEmployeeId())){
                        videoManagementVO.setEmployeeName(employeeListByAccountTypeVO.getEmployeeName());
                        //退出循环
                        break;
                    }
                }
            });
        }
    }
}
