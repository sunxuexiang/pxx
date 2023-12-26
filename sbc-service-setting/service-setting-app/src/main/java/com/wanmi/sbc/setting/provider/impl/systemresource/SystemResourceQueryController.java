package com.wanmi.sbc.setting.provider.impl.systemresource;

import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.systemresource.SystemResourceQueryProvider;
import com.wanmi.sbc.setting.api.request.systemresource.SystemResourceByIdRequest;
import com.wanmi.sbc.setting.api.request.systemresource.SystemResourceListRequest;
import com.wanmi.sbc.setting.api.request.systemresource.SystemResourcePageRequest;
import com.wanmi.sbc.setting.api.request.systemresource.SystemResourceQueryRequest;
import com.wanmi.sbc.setting.api.request.systemresourcecate.SystemResourceCateQueryRequest;
import com.wanmi.sbc.setting.api.response.systemresource.SystemResourceByIdResponse;
import com.wanmi.sbc.setting.api.response.systemresource.SystemResourceListResponse;
import com.wanmi.sbc.setting.api.response.systemresource.SystemResourcePageResponse;
import com.wanmi.sbc.setting.bean.vo.SystemResourceCateVO;
import com.wanmi.sbc.setting.bean.vo.SystemResourceVO;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import com.wanmi.sbc.setting.systemresource.model.root.SystemResource;
import com.wanmi.sbc.setting.systemresource.service.SystemResourceService;
import com.wanmi.sbc.setting.systemresourcecate.model.root.SystemResourceCate;
import com.wanmi.sbc.setting.systemresourcecate.service.SystemResourceCateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>平台素材资源查询服务接口实现</p>
 *
 * @author lq
 * @date 2019-11-05 16:14:27
 */
@RestController
@Validated
@Slf4j
public class SystemResourceQueryController implements SystemResourceQueryProvider {
    @Autowired
    private SystemResourceService systemResourceService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemResourceCateService systemResourceCateService;

    @Override
    public BaseResponse<SystemResourcePageResponse> page(@RequestBody @Valid SystemResourcePageRequest systemResourcePageReq) {
        SystemResourceQueryRequest queryReq = new SystemResourceQueryRequest();
        KsBeanUtil.copyPropertiesThird(systemResourcePageReq, queryReq);
        queryReq.setDelFlag(DeleteFlag.NO);
        queryReq.putSort("createTime", SortType.DESC.toValue());
        queryReq.putSort("resourceId", SortType.ASC.toValue());
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        Page<SystemResource> systemResourcePage = systemResourceService.page(queryReq);
        Page<SystemResourceVO> newPage = systemResourcePage.map(entity -> {
                    //获取url
                    String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, entity.getArtworkUrl());
                    entity.setArtworkUrl(resourceUrl);
                    return systemResourceService.wrapperVo(entity);
                }
        );
        MicroServicePage<SystemResourceVO> microPage = new MicroServicePage<>(newPage, systemResourcePageReq.getPageable());
        SystemResourcePageResponse finalRes = new SystemResourcePageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<SystemResourceListResponse> list(@RequestBody @Valid SystemResourceListRequest systemResourceListReq) {
        SystemResourceQueryRequest queryReq = new SystemResourceQueryRequest();
        KsBeanUtil.copyPropertiesThird(systemResourceListReq, queryReq);
        List<SystemResource> systemResourceList = systemResourceService.list(queryReq);
        List<SystemResourceVO> newList = systemResourceList.stream().map(entity -> systemResourceService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new SystemResourceListResponse(newList));
    }

    @Override
    public BaseResponse<SystemResourceListResponse> reportList(@RequestBody @Valid SystemResourceListRequest systemResourceListReq) {
        SystemResourceQueryRequest queryReq = new SystemResourceQueryRequest();
        KsBeanUtil.copyPropertiesThird(systemResourceListReq, queryReq);
        List<SystemResource> systemResourceList = systemResourceService.list(queryReq);
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        systemResourceList.forEach(item->{
            //获取url
            String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, item.getArtworkUrl());
            item.setArtworkUrl(resourceUrl);
        });
        List<SystemResourceVO> newList = systemResourceList.stream().map(entity -> systemResourceService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new SystemResourceListResponse(newList));
    }

    @Override
    public BaseResponse<SystemResourceByIdResponse> getById(@RequestBody @Valid SystemResourceByIdRequest systemResourceByIdRequest) {
        SystemResource systemResource = systemResourceService.getById(systemResourceByIdRequest.getResourceId());
        return BaseResponse.success(new SystemResourceByIdResponse(systemResourceService.wrapperVo(systemResource)));
    }

    @Override
    public BaseResponse<Map<Long,String>> resourceReport (@RequestBody @Valid SystemResourceCateQueryRequest systemResourceByIdRequest) {
        List<SystemResourceCate> analysisResource = new ArrayList<>();
        Map<Long,String> nodeMap = new HashMap<>();
            List<SystemResourceCate> list = systemResourceCateService.list(SystemResourceCateQueryRequest.builder().cateId(systemResourceByIdRequest.getCateId()).build());
            if (CollectionUtils.isNotEmpty(list)) {
                // 查找是否有子类产品
                analysisResource.addAll(list);
                // 如果是根节点递归出所有节点的所有类目
                List<Long> collect = list.stream().map(SystemResourceCate::getCateId).collect(Collectors.toList());
                analysisPath(collect,analysisResource);
                // 解析所有节点
                analysisResource.sort(Comparator.comparing(SystemResourceCate::getCatePath));
                // 循环所有的节点数据
                nodeMap = downLoadZIP(analysisResource);
            }
        return BaseResponse.success(nodeMap);
    }

    private void analysisPath (List<Long> parentId,List<SystemResourceCate> analysisResource) {
        // 节点属性，是1级目录，还是2级目录，还是3级目录
        SystemResourceCate node = analysisResource.get(0);
        int l = analysisResource.get(0).getCatePath().split("\\|").length;
        log.info("获取子节点数量======{}",l);
        // 1级目录
        if(l == 1) {
            getFirstFlodPath(parentId,analysisResource);
        }// 二级目录
        else if (l == 2) {
            // 2级节点的父节点
            log.info("获取二级节点======{}",node.getCateParentId());
            analysisResource.addAll(systemResourceCateService.list(SystemResourceCateQueryRequest.builder().cateId(node.getCateParentId()).build()));
            // 2级节点的子节点
            getFirstFlodPath(parentId,analysisResource);
        } // 获取三级目录
        else if (l == 3) {
            getThirdFlodPath(analysisResource);
        }
    }

    private void getFirstFlodPath (List<Long> parentIds,List<SystemResourceCate> nodesToExport) {
        if (CollectionUtils.isNotEmpty(parentIds)) {
            List<SystemResourceCate> list = systemResourceCateService.list(SystemResourceCateQueryRequest.builder().cateParentIds(parentIds).build());
            log.info("获取节点数量========={}",list.size());
            if (CollectionUtils.isNotEmpty(list)) {
                nodesToExport.addAll(list);
                List<Long> idLists = list.stream().map(SystemResourceCate::getCateId).collect(Collectors.toList());
                getFirstFlodPath(idLists,nodesToExport);
            }
        }
    }

    private void getThirdFlodPath (List<SystemResourceCate> nodesToExport) {
        if (CollectionUtils.isNotEmpty(nodesToExport)) {
            String[] sp = nodesToExport.get(0).getCatePath().split("\\|");
            for (int i=1;i<sp.length;i++) {
                nodesToExport.addAll(systemResourceCateService.list(SystemResourceCateQueryRequest.builder().cateId(Long.valueOf(sp[i])).build()));
            }
        }
    }

   private Map<Long,String> downLoadZIP(List<SystemResourceCate> nodesToExport){
        log.info("获取节点NodeToExport====={}",nodesToExport.size());
        Map<Long,String> nodeMap = new HashMap<>();
        // 获取根节点
            SystemResourceCate rootNode = nodesToExport.stream().filter(item -> item.getCateParentId() == 0).findFirst()
                .orElse(null);
            log.info("根节点========={}",rootNode.getCateName());
        // 二级节点
        List<SystemResourceCate> secondNode = nodesToExport.stream().filter(item -> item.getCateParentId().equals(rootNode.getCateId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(secondNode)) {
            // 没有二级节点就是主节点
            nodeMap.put(rootNode.getCateId(),rootNode.getCateName()+"-"+rootNode.getCateId());
        }
        // 三级节点
        for (SystemResourceCate systemResourceCate : secondNode) {
            List<SystemResourceCate> thirdNode = nodesToExport.stream().filter(node -> node.getCateParentId().equals(systemResourceCate.getCateId())).collect(Collectors.toList());
            // 没有三级节点那就只有2级节点
            if (CollectionUtils.isEmpty(thirdNode)) {
                nodeMap.put(systemResourceCate.getCateId(),rootNode.getCateName()+"-"+rootNode.getCateId()+"/"+systemResourceCate.getCateName()+"-"+systemResourceCate.getCateId());
            } else {
                for (SystemResourceCate resourceCate : thirdNode) {
                    nodeMap.put(resourceCate.getCateId(),rootNode.getCateName()+"-"+rootNode.getCateId()+"/"+systemResourceCate.getCateName()+"-"+systemResourceCate.getCateId()+"/"+resourceCate.getCateName()+"-"+resourceCate.getCateId());
                }
            }
        }
        return nodeMap;
   }

}

