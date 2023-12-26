package com.wanmi.sbc.setting.provider.impl.storeresourcecate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.storeresourcecate.StoreResourceCateQueryProvider;
import com.wanmi.sbc.setting.api.request.storeresourcecate.*;
import com.wanmi.sbc.setting.api.request.systemresourcecate.SystemResourceCateQueryRequest;
import com.wanmi.sbc.setting.api.response.storeresourcecate.StoreResourceCateByIdResponse;
import com.wanmi.sbc.setting.api.response.storeresourcecate.StoreResourceCateListResponse;
import com.wanmi.sbc.setting.api.response.storeresourcecate.StoreResourceCatePageResponse;
import com.wanmi.sbc.setting.bean.vo.StoreResourceCateVO;
import com.wanmi.sbc.setting.storeresourcecate.model.root.StoreResourceCate;
import com.wanmi.sbc.setting.storeresourcecate.service.StoreResourceCateService;
import com.wanmi.sbc.setting.systemresourcecate.model.root.SystemResourceCate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>店铺资源资源分类表查询服务接口实现</p>
 * @author lq
 * @date 2019-11-05 16:13:19
 */
@RestController
@Validated
@Slf4j
public class StoreResourceCateQueryController implements StoreResourceCateQueryProvider {
	@Autowired
	private StoreResourceCateService storeResourceCateService;

	@Override
	public BaseResponse<StoreResourceCatePageResponse> page(@RequestBody @Valid StoreResourceCatePageRequest storeResourceCatePageReq) {
		StoreResourceCateQueryRequest queryReq = new StoreResourceCateQueryRequest();
		KsBeanUtil.copyPropertiesThird(storeResourceCatePageReq, queryReq);
		Page<StoreResourceCate> storeResourceCatePage = storeResourceCateService.page(queryReq);
		Page<StoreResourceCateVO> newPage = storeResourceCatePage.map(entity -> storeResourceCateService.wrapperVo(entity));
		MicroServicePage<StoreResourceCateVO> microPage = new MicroServicePage<>(newPage, storeResourceCatePageReq.getPageable());
		StoreResourceCatePageResponse finalRes = new StoreResourceCatePageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<StoreResourceCateListResponse> list(@RequestBody @Valid StoreResourceCateListRequest storeResourceCateListReq) {
		StoreResourceCateQueryRequest queryReq = new StoreResourceCateQueryRequest();
		KsBeanUtil.copyPropertiesThird(storeResourceCateListReq, queryReq);
		queryReq.putSort("cateId", SortType.ASC.toValue());
		queryReq.putSort("createTime", SortType.DESC.toValue());
		queryReq.putSort("sort", SortType.ASC.toValue());
		queryReq.setDelFlag( DeleteFlag.NO);
		List<StoreResourceCate> storeResourceCateList = storeResourceCateService.list(queryReq);
		List<StoreResourceCateVO> newList = storeResourceCateList.stream().map(entity -> storeResourceCateService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new StoreResourceCateListResponse(newList));
	}

	public BaseResponse<Map<Long,String>> resourceReport (@RequestBody @Valid StoreResourceCateListRequest storeResourceCateListRequest) {
		log.info("查询商家图片素材库============={}",storeResourceCateListRequest.toString());
		List<StoreResourceCateVO> analysisResource = new ArrayList<>();
		List<StoreResourceCateVO> storeResourceCateVOList = list(storeResourceCateListRequest).getContext().getStoreResourceCateVOList();
		Map<Long,String> nodeMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(storeResourceCateVOList)) {
			// 查找是否有子类产品
			analysisResource.addAll(storeResourceCateVOList);
			// 如果是根节点递归出所有节点的所有类目
			List<Long> collect = storeResourceCateVOList.stream().map(StoreResourceCateVO::getCateId).collect(Collectors.toList());
			analysisPath(collect,analysisResource,storeResourceCateListRequest.getStoreId());
			// 解析所有节点
			analysisResource.sort(Comparator.comparing(StoreResourceCateVO::getCatePath));
			// 循环所有的节点数据
			nodeMap = downLoadZIP(analysisResource);
		}
		return BaseResponse.success(nodeMap);
	}

	private void analysisPath (List<Long> parentId,List<StoreResourceCateVO> analysisResource,Long storeId) {
		// 节点属性，是1级目录，还是2级目录，还是3级目录
		StoreResourceCateVO node = analysisResource.get(0);
		int l = analysisResource.get(0).getCatePath().split("\\|").length;
		log.info("获取子节点数量======{}",l);
		// 1级目录
		if(l == 1) {
			getFirstFlodPath(parentId,analysisResource,node.getStoreId());
		}// 二级目录
		else if (l == 2) {
			// 2级节点的父节点
			log.info("获取二级节点======{}",node.getCateParentId());
			analysisResource.addAll(list(StoreResourceCateListRequest.builder().cateId(node.getCateParentId()).storeId(node.getStoreId()).build()).getContext().getStoreResourceCateVOList());
			// 2级节点的子节点
			getFirstFlodPath(parentId,analysisResource,node.getStoreId());
		} // 获取三级目录
		else if (l == 3) {
			getThirdFlodPath(analysisResource);
		}
	}

	private void getThirdFlodPath (List<StoreResourceCateVO> nodesToExport) {
		if (CollectionUtils.isNotEmpty(nodesToExport)) {
			String[] sp = nodesToExport.get(0).getCatePath().split("\\|");
			for (int i=1;i<sp.length;i++) {
				nodesToExport.addAll(list(StoreResourceCateListRequest.builder().cateId(Long.valueOf(sp[i])).build()).getContext().getStoreResourceCateVOList());
			}
		}
	}

	private void getFirstFlodPath (List<Long> parentIds,List<StoreResourceCateVO> nodesToExport,Long storeId) {
		if (CollectionUtils.isNotEmpty(parentIds)) {
			List<StoreResourceCateVO> list = list(StoreResourceCateListRequest.builder().cateParentIds(parentIds).storeId(storeId).build()).getContext().getStoreResourceCateVOList();
			log.info("获取节点数量========={}",list.size());
			if (CollectionUtils.isNotEmpty(list)) {
				nodesToExport.addAll(list);
				List<Long> idLists = list.stream().map(StoreResourceCateVO::getCateId).collect(Collectors.toList());
				getFirstFlodPath(idLists,nodesToExport,storeId);
			}
		}
	}

	private Map<Long,String> downLoadZIP(List<StoreResourceCateVO> nodesToExport){
		log.info("获取节点NodeToExport====={}",nodesToExport.size());
		Map<Long,String> nodeMap = new HashMap<>();
		// 获取根节点
		StoreResourceCateVO rootNode = nodesToExport.stream().filter(item -> item.getCateParentId() == 0).findFirst()
				.orElse(null);
		log.info("根节点========={}",rootNode.getCateName());
		// 二级节点
		List<StoreResourceCateVO> secondNode = nodesToExport.stream().filter(item -> item.getCateParentId().equals(rootNode.getCateId())).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(secondNode)) {
			// 没有二级节点就是主节点
			nodeMap.put(rootNode.getCateId(),rootNode.getCateName()+"-"+rootNode.getCateId());
		}
		// 三级节点
		for (StoreResourceCateVO systemResourceCate : secondNode) {
			List<StoreResourceCateVO> thirdNode = nodesToExport.stream().filter(node -> node.getCateParentId().equals(systemResourceCate.getCateId())).collect(Collectors.toList());
			// 没有三级节点那就只有2级节点
			if (CollectionUtils.isEmpty(thirdNode)) {
				nodeMap.put(systemResourceCate.getCateId(),rootNode.getCateName()+"-"+rootNode.getCateId()+"/"+systemResourceCate.getCateName()+"-"+systemResourceCate.getCateId());
			} else {
				for (StoreResourceCateVO resourceCate : thirdNode) {
					nodeMap.put(resourceCate.getCateId(),rootNode.getCateName()+"-"+rootNode.getCateId()+"/"+systemResourceCate.getCateName()+"-"+systemResourceCate.getCateId()+"/"+resourceCate.getCateName()+"-"+resourceCate.getCateId());
				}
			}
		}
		return nodeMap;
	}

	@Override
	public BaseResponse<StoreResourceCateByIdResponse> getById(@RequestBody @Valid StoreResourceCateByIdRequest storeResourceCateByIdRequest) {
		StoreResourceCate storeResourceCate = storeResourceCateService.getById(storeResourceCateByIdRequest.getCateId());
		return BaseResponse.success(new StoreResourceCateByIdResponse(storeResourceCateService.wrapperVo(storeResourceCate)));
	}

	@Override
	public BaseResponse<Integer> checkChild(@RequestBody @Valid StoreResourceCateCheckChildRequest
													request) {
		return BaseResponse.success(storeResourceCateService.checkChild(request.getCateId(),request.getStoreId()));
	}


	@Override
	public BaseResponse<Integer> checkResource(@RequestBody @Valid  StoreResourceCateCheckResourceRequest
													   request) {
		return BaseResponse.success(storeResourceCateService.checkResource(request.getCateId(),request.getStoreId()));
	}
}

