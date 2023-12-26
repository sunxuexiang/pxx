package com.wanmi.sbc.setting.api.provider.logisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.*;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyAddResponse;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyImportResponse;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * <p>物流公司保存服务Provider</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "LogisticsCompanyProvider")
public interface LogisticsCompanyProvider {

	/**
	 * 新增物流公司API
	 *
	 * @author fcq
	 * @param logisticsCompanyAddRequest 物流公司新增参数结构 {@link LogisticsCompanyAddRequest}
	 * @return 新增的物流公司信息 {@link LogisticsCompanyAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/add")
	BaseResponse<LogisticsCompanyAddResponse> add(@RequestBody @Valid LogisticsCompanyAddRequest logisticsCompanyAddRequest);

	@PostMapping("/setting/${application.setting.version}/logisticscompany/addByApp")
	BaseResponse<LogisticsCompanyAddResponse> addByApp(@RequestBody @Valid LogisticsCompanyAddRequest logisticsCompanyAddRequest);

	/**
	 * 修改物流公司API
	 *
	 * @author fcq
	 * @param logisticsCompanyModifyRequest 物流公司修改参数结构 {@link LogisticsCompanyModifyRequest}
	 * @return 修改的物流公司信息 {@link LogisticsCompanyModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/modify")
	BaseResponse<LogisticsCompanyModifyResponse> modify(@RequestBody @Valid LogisticsCompanyModifyRequest logisticsCompanyModifyRequest);

	/**
	 * 单个删除物流公司API
	 *
	 * @author fcq
	 * @param logisticsCompanyDelByIdRequest 单个删除参数结构 {@link LogisticsCompanyDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid LogisticsCompanyDelByIdRequest logisticsCompanyDelByIdRequest);

	/**
	 * 批量删除物流公司API
	 *
	 * @author fcq
	 * @param logisticsCompanyDelByIdListRequest 批量删除参数结构 {@link LogisticsCompanyDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid LogisticsCompanyDelByIdListRequest logisticsCompanyDelByIdListRequest);


	/**
	 *
	 * 批量保存物流公司
	 * @param logisticsCompanyImportExcelRequest
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/import-logistics-save")
	BaseResponse saveAll(@RequestBody @Valid LogisticsCompanyImportExcelRequest logisticsCompanyImportExcelRequest);

	/**
	 * 查询物流公司名称
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/import-logistics-numbers")
	BaseResponse<LogisticsCompanyImportResponse> selectLogisticsCompanyNumbers();

	/**
	 * 查询物流公司名称
	 * @return
	 */
	@GetMapping("/setting/${application.setting.version}/logisticscompany/import-logistics-numbers-by-storeId")
	BaseResponse<LogisticsCompanyImportResponse> selectLogisticsCompanyNumbersByStoreId(@RequestParam(value="storeId")Long storeId,@RequestParam(value="logisticsType")Integer logisticsType);

	/**
	 * 查询物流公司名称
	 * @return
	 */
	@GetMapping("/setting/${application.setting.version}/logisticscompany/import-logistics-numbers-by-market-Id")
	BaseResponse<LogisticsCompanyImportResponse> selectLogisticsCompanyNumbersByMarketId(@RequestParam(value="marketId")Long marketId, @RequestParam(value="logisticsType")Integer logisticsType);


	/**
	 * 查询数据库最大id
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/import-logistics-maxId")
	BaseResponse<LogisticsCompanyImportResponse> selectMaxId();

	/**
	 * @desc  同步物流信息
	 * @author shiy  2023/8/17 17:47
	*/
	@PostMapping("/setting/${application.setting.version}/logisticscompany/sync-logistics-company")
	BaseResponse<LogisticsCompanyAddResponse> syncLogisticsCompany(@RequestBody @Valid LogisticsCompanySyncRequest addReq);

	@PostMapping("/setting/${application.setting.version}/logisticscompany/getCountByLogisticsName")
	BaseResponse<Long> getCountByLogisticsName(@RequestBody LogisticsCompanyAddRequest logisticsCompanyAddRequest);
}

