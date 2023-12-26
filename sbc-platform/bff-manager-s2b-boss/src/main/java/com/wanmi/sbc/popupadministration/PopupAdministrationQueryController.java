package com.wanmi.sbc.popupadministration;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.setting.api.provider.popupadministration.PopupAdministrationQueryProvider;
import com.wanmi.sbc.setting.api.request.popupadministration.PageManagementGetIdRequest;
import com.wanmi.sbc.setting.api.request.popupadministration.PageManagementRequest;
import com.wanmi.sbc.setting.api.request.popupadministration.PopupAdministrationPageRequest;
import com.wanmi.sbc.setting.api.response.popupadministration.PageManagementResponse;
import com.wanmi.sbc.setting.api.response.popupadministration.PopupAdministrationPageResponse;
import com.wanmi.sbc.setting.api.response.popupadministration.PopupAdministrationResponse;
import com.wanmi.sbc.setting.bean.vo.PopupAdministrationVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>弹窗管理服务API</p>
 *
 * @author weiwenhao
 * @date 2020-04-23
 */
@RestController
@Api(description = "弹窗管理查询服务API", tags = "PopupAdministrationQueryController")
@RequestMapping("/popup_administration")
public class PopupAdministrationQueryController {


    @Autowired
    private PopupAdministrationQueryProvider popupAdministrationQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CommonUtil commonUtil;


    /**
     * 弹窗列表查询
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "弹窗列表查询")
    @PostMapping("/page")
    public BaseResponse<PopupAdministrationPageResponse> page(@RequestBody @Valid PopupAdministrationPageRequest request) {
        operateLogMQUtil.convertAndSend("弹窗列表查询", "查询弹窗管理列表", "查询弹窗管理列表");
        BaseResponse<PopupAdministrationPageResponse> page = popupAdministrationQueryProvider.page(request);
        fillWareName(page);
        return page;
    }

    /**
     *  设置发货仓值
     *
     * @param page
     */
    public void fillWareName(BaseResponse<PopupAdministrationPageResponse>  page){
        //获取仓库信息
        List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList();
        List<PopupAdministrationVO> content = page.getContext().getPopupAdministrationVOS().getContent();
        content.forEach(popupAdministrationVO -> {
            WareHouseVO wareHouseVO = wareHouseVOList.stream().filter(wareHouseVOOne -> wareHouseVOOne.getWareId().equals(popupAdministrationVO.getWareId())).findFirst().get();
            popupAdministrationVO.setWareName(wareHouseVO.getWareName());
        });
    }

    /**
     * 弹窗详情查询
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "弹窗详情查询")
    @PostMapping("/popupAdministration_id")
    public BaseResponse<PopupAdministrationResponse> getPopupAdministrationById(@RequestBody @Valid PageManagementGetIdRequest request) {
        operateLogMQUtil.convertAndSend("弹窗详情查询", "弹窗详情查询", "弹窗详情查询：" + request.getPopupId());
        return popupAdministrationQueryProvider.getPopupAdministrationById(request);
    }

    /**
     * 弹窗管理&页面管理列表查询
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "弹窗管理&页面管理列表查询")
    @PostMapping("/page_management_popup_administration")
    public BaseResponse<PageManagementResponse> pageManagementAndPopupAdministrationList(@RequestBody @Valid PageManagementRequest request) {
        operateLogMQUtil.convertAndSend("弹窗管理&页面管理列表查询", " 弹窗管理&页面管理列表查询", " 弹窗管理&页面管理列表查询：" + request.getApplicationPageName());
        return popupAdministrationQueryProvider.pageManagementAndPopupAdministrationList(request);
    }

}
