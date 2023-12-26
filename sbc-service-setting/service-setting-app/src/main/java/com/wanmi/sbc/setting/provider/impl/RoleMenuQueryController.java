package com.wanmi.sbc.setting.provider.impl;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.constant.SettingConstant;
import com.wanmi.sbc.setting.api.provider.RoleMenuQueryProvider;
import com.wanmi.sbc.setting.api.request.*;
import com.wanmi.sbc.setting.api.response.*;
import com.wanmi.sbc.setting.authority.service.RoleMenuService;
import com.wanmi.sbc.setting.bean.vo.MenuInfoVO;
import com.wanmi.sbc.setting.bean.vo.NewMenuInfoVO;
import com.wanmi.sbc.setting.bean.vo.RoleInfoAndMenuInfoVO;
import com.wanmi.sbc.setting.videoresource.model.root.VideoResource;
import com.wanmi.sbc.setting.videoresource.service.VideoResourceService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RoleMenuQueryController implements RoleMenuQueryProvider {
    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private VideoResourceService videoResourceService;

    @Override
    public BaseResponse<RoleMenuFuncIdsQueryResponse> queryRoleMenuFuncIds(@RequestBody @Valid RoleMenuFuncIdsQueryRequest request) {

        return BaseResponse.success(roleMenuService.queryRoleMenuFuncIds(request.getRoleInfoId()));
    }

    @Override
    public BaseResponse<RoleMenuInfoListResponse> listRoleMenuInfo(@RequestBody @Valid RoleMenuInfoListRequest request) {
        RoleMenuInfoListResponse response = new RoleMenuInfoListResponse();
        response.setMenuInfoVOList(roleMenuService.queryRoleMenuInfoList(request.getRoleInfoId()));

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<AllRoleMenuInfoListResponse> listAllRoleMenuInfo(@RequestBody AllRoleMenuInfoListRequest request) {
        AllRoleMenuInfoListResponse response = new AllRoleMenuInfoListResponse();
        response.setMenuInfoVOList(roleMenuService.queryAllRoleMenuInfoList(request.getSystemTypeCd()));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<AuthorityListResponse> listAuthority(@RequestBody @Valid AuthorityListRequest request) {
        AuthorityListResponse response = new AuthorityListResponse();
        response.setAuthorityVOList(roleMenuService.hasAuthorityList(request.getRoleInfoId()));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<FunctionListResponse> listFunction(@RequestBody FunctionListRequest request) {
        FunctionListResponse response = new FunctionListResponse();
        response.setFunctionList(roleMenuService.hasFunctionList(request.getRoleInfoId(), request.getAuthorityNames()));

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<FunctionsByRoleIdListResponse> listFunctionsByRoleId(@RequestBody FunctionListByRoleIdRequest request) {
        FunctionsByRoleIdListResponse response = new FunctionsByRoleIdListResponse();
        response.setFunctionList(roleMenuService.queryFunctionsByRoleId(request.getRoleId(), request.getSystemTypeCd()));

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<RoleInfoAndMenuInfoListResponse> listRoleWithMenuNames(@RequestBody @Valid RoleInfoAndMenuInfoListRequest request){
        List<RoleInfoAndMenuInfoVO> list = roleMenuService.listRoleWithMenuNames(request.getRoleInfoIdList());
        return BaseResponse.success(new RoleInfoAndMenuInfoListResponse(list));
    }

    @Override
    public BaseResponse<TwoAndThreeMenuInfoListResponse> listTwoAndThreeMenuInfo(AllRoleMenuInfoListRequest request) {
        //定义返回对象
        TwoAndThreeMenuInfoListResponse response = new TwoAndThreeMenuInfoListResponse();
        //设置菜单层级为2，3的过滤集合
        List<Integer> gradeList = Arrays.asList(2,3);
        //查询商家端菜单所有目录
        List<MenuInfoVO> menuInfoVOList = roleMenuService.queryAllRoleMenuInfoList(request.getSystemTypeCd());
        //新增特殊菜单记录
        menuInfoVOList.add(MenuInfoVO.initSpecialMenu(SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getKey(),SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getDesc(),SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getGrade(),SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getParentKey()));
        //过滤层级为2，3级的菜单集合
        List<MenuInfoVO> collectList = menuInfoVOList.stream().filter(m -> gradeList.contains(m.getGrade())).collect(Collectors.toList());
        //定义父节点新目录集合
        List<NewMenuInfoVO> parentList = Lists.newArrayList();
        //MenuInfoVO 转换为 NewMenuInfoVO
        collectList.forEach(menuInfoVO -> {
             parentList.add(KsBeanUtil.convert(menuInfoVO, NewMenuInfoVO.class));
        } );
        //定义新目录返回集合
        List<NewMenuInfoVO> result =Lists.newArrayList();
        //通过层级为3的子节点目录Ids 查询视频教程资源分类表中是否存在记录
        List<VideoResource> videoResourceList = videoResourceService.findByIdList(parentList);
        //定义视频教程资源分类Ids
        List<String> cateIds = Lists.newArrayList();
        //不为空收集Ids
        if(CollectionUtils.isNotEmpty(videoResourceList)) {
            cateIds = videoResourceList.stream().map(VideoResource::getCateId).collect(Collectors.toList());
        }
        //定义父节点的Maps
        Map<String, NewMenuInfoVO> parentNodes = collectList.stream().filter(newMenuInfoVO -> newMenuInfoVO.getGrade() == 2).collect(Collectors.toMap(menuInfoVO -> menuInfoVO.getId(), menuInfoVO -> KsBeanUtil.convert(menuInfoVO,NewMenuInfoVO.class)));
        //定义子新目录集合
        List<NewMenuInfoVO> childList = Lists.newArrayList();
        //定义子节点的Maps
        Map<String, NewMenuInfoVO> childNodes = collectList.stream().filter(newMenuInfoVO -> newMenuInfoVO.getGrade() == 3).collect(Collectors.toMap(menuInfoVO -> menuInfoVO.getId(), menuInfoVO -> KsBeanUtil.convert(menuInfoVO,NewMenuInfoVO.class)));
        //遍历子节点 并赋值是否上传字段的值
        for (String key : childNodes.keySet()) {
            if(CollectionUtils.isEmpty(cateIds)){
                childNodes.get(key).setUploadFlag(0);
            }
            if(cateIds.contains(key)){
                childNodes.get(key).setUploadFlag(1);
            }
            childList.add(childNodes.get(key));
        }
        //收集结果
        parentNodes.keySet().forEach(p -> {
                    parentNodes.get(p).setChildren(childList.stream().filter(m ->  p.equals(m.getPid())).collect(Collectors.toList()));
                    parentNodes.get(p).setUploadFlag(0);
            result.add(parentNodes.get(p));
        } );
        response.setChildren(result);
        return BaseResponse.success(response);
    }

}
