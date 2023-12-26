package com.wanmi.sbc.customer.provider.impl.store;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.response.store.*;
import com.wanmi.sbc.customer.bean.dto.StoreShippingAddressEditDTO;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.KingdeePushStoreInfo;
import com.wanmi.sbc.customer.bean.vo.StoreShippingAddressVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.model.root.CompanyMallContractRelation;
import com.wanmi.sbc.customer.company.model.root.StoreShippingAddress;
import com.wanmi.sbc.customer.company.request.CompanyMallContractRelationRequest;
import com.wanmi.sbc.customer.company.service.CompanyMallContractRelationService;
import com.wanmi.sbc.customer.company.service.StoreShippingAddressService;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.service.EmployeeService;
import com.wanmi.sbc.customer.service.model.CustomerKingdeeModel;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.service.StoreService;
import com.wanmi.sbc.customer.util.CustomerKingdeeLoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>店铺操作接口实现</p>
 * Created by of628-wenzhi on 2018-09-25-下午3:18.
 */
@RestController
@Validated
@Slf4j
public class StoreController implements StoreProvider {

    @Autowired
    private StoreService storeService;

    @Value("${kingdee.supplier.save.url}")
    private String kingDeeSupplierSaveUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    @Value("${kingdee.Customer.url}")
    private String customerUrl;

    @Autowired
    private CustomerKingdeeLoginUtils customerKingdeeLoginUtils;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyMallContractRelationService companyMallContractRelationService;

    @Autowired
    private StoreShippingAddressService storeShippingAddressService;

    @Override
    public BaseResponse<InitStoreByCompanyResponse> initStoreByCompany(@RequestBody @Valid InitStoreByCompanyRequest
                                                                               initStoreByCompanyRequest) {
        Store store = storeService.getStore(initStoreByCompanyRequest.getCompanyInfoId());
        return BaseResponse.success(new InitStoreByCompanyResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override
    public BaseResponse<StoreAddResponse> add(@RequestBody @Valid StoreAddRequest storeAddRequest) {
        StoreSaveRequest saveRequest = new StoreSaveRequest();
        KsBeanUtil.copyPropertiesThird(storeAddRequest, saveRequest);
        return BaseResponse.success(new StoreAddResponse(storeService.wraper2VoFromStore(storeService.saveStore
                (saveRequest))));
    }

    @Override
    public BaseResponse modifyStoreBaseInfo(@RequestBody @Valid StoreModifyLogoRequest storeModifyLogoRequest) {
        storeService.updateStoreBaseInfo(storeModifyLogoRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<StoreModifyResponse> modifyStoreInfo(@RequestBody @Valid StoreModifyRequest request) {
        Store store = storeService.modifyStoreBaseInfo(request);

        StoreModifyResponse response = new StoreModifyResponse();

        KsBeanUtil.copyPropertiesThird(store, response);
        CompanyInfoVO companyInfoVO = new CompanyInfoVO();
        response.setCompanyInfo(companyInfoVO);
        KsBeanUtil.copyPropertiesThird(store.getCompanyInfo(),companyInfoVO);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<AccountDateModifyResponse> accountDateModify(@RequestBody @Valid AccountDateModifyRequest
                                                                                 request) {
        Store store = storeService.update(request);
        return BaseResponse.success(new AccountDateModifyResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override
    public BaseResponse<StoreSwitchResponse> switchStore(@RequestBody @Valid StoreSwitchRequest storeSwitchRequest) {
        Store store = storeService.closeOrOpen(storeSwitchRequest);
        return BaseResponse.success(new StoreSwitchResponse(KsBeanUtil.convert(store, StoreVO.class)));
    }

    @Override
    public BaseResponse<StoreAuditResponse> auditStore(@RequestBody @Valid StoreAuditRequest storeAuditRequest) {
        Store store = storeService.rejectOrPass(storeAuditRequest);
        return BaseResponse.success(new StoreAuditResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override
    public BaseResponse<StoreCheckResponse> checkStore(@RequestBody @Valid StoreCheckRequest storeCheckRequest) {
        boolean result = storeService.checkStore(storeCheckRequest.getIds());
        return BaseResponse.success(new StoreCheckResponse(result));
    }

    @Override
    public BaseResponse<StoreContractModifyResponse> modifyStoreContract(@RequestBody @Valid
                                                                                     StoreContractModifyRequest
                                                                                     storeContractModifyRequest) {
        Store store = storeService.updateStoreContract(storeContractModifyRequest);
        return BaseResponse.success(new StoreContractModifyResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override
    public BaseResponse modifyAuditState(@RequestBody @Valid StoreAuditStateModifyRequest request) {
        storeService.modifyAuditState(request);

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateStoreSmallProgram(@RequestBody @Valid StoreInfoSmallProgramRequest request){
        storeService.updateStoreSmallProgram(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse clearStoreProgramCode(){
        storeService.clearStoreProgramCode();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateJhInfo(StoreAuditJHInfoRequest request) {
        return storeService.updateJhInfo(request);
    }


    @Override
    public BaseResponse updatePileState(@RequestBody @Valid StorePlieRequest request) {
        storeService.updatePileState(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse checkPileState(@RequestBody StorePlieRequest request) {
        storeService.checkPileState(request.getStoreId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Boolean> pushErp(StoreSupplierPushErpRequest request) {
        final List<Long> storeIds = request.getStoreIds();
        final Map<Long, String> storeContactCatStrMap = request.getStoreContactCatStrMap();
        if (CollectionUtils.isEmpty(storeIds)) return BaseResponse.success(false);
        // 查找店铺信息
        final List<Store> stores = storeService.findAllList(storeIds);
        // 获取签约类目
        //登录财务系统
        Map<String, Object> requestLogMap = new HashMap<>();
        requestLogMap.put("user", kingdeeUser);
        requestLogMap.put("pwd", kingdeePwd);
        String loginToken = customerKingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
        String desSb = "账户:accountName,分账比例:rate%";
        if (StringUtils.isNotEmpty(loginToken)) {
            stores.forEach(o -> {
                final CompanyInfo companyInfo = o.getCompanyInfo();
                if (null == companyInfo) return;
                final Optional<Employee> employeeOptional = employeeService.findByComanyId(companyInfo.getCompanyInfoId());
                final Employee employee = employeeOptional.get();
                if (null == employee) return;
                Map<String, Object> requestMapRequest = new HashMap<>();
                String desSbNow = desSb.replace("accountName", employee.getAccountName());
                desSbNow = desSbNow.replace("rate", Objects.toString(o.getShareRatio()));
                final KingdeePushStoreInfo kingdeePushStoreInfo = new KingdeePushStoreInfo();
                kingdeePushStoreInfo.setFNumber(o.getCompanyInfo().getCompanyCodeNew());
                kingdeePushStoreInfo.setFName(o.getStoreName());
                kingdeePushStoreInfo.setF_ora_Remark(storeContactCatStrMap.get(o.getStoreId()));
                kingdeePushStoreInfo.setF_ora_Text(wrapTypeName(companyInfo.getCompanyType()));
                kingdeePushStoreInfo.setFDescription(desSbNow);
                requestMapRequest.put("Model", kingdeePushStoreInfo);
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(kingDeeSupplierSaveUrl, requestMapRequest, loginToken);
                log.info("推送金碟结果：{}",JSONObject.toJSON(httpCommonResult));
                pushCustomer(companyInfo, o,loginToken);
            });
        }
        return BaseResponse.success(true);
    }

    @Override
    public BaseResponse<Boolean> editPersonId(StoreSupplierPersonIdEditRequest request) {
        return BaseResponse.success(storeService.editPersonId(request));
    }

    @Override
    public BaseResponse<Boolean> editSelfManage(StoreSupplierSelfManageEditRequest request) {
        return BaseResponse.success(storeService.editSelfManage(request));
    }

    @Override
    public BaseResponse<Boolean> editAssignSort(StoreSupplierAssignSortEditRequest request) {
        if (request.getStoreId() == null) return BaseResponse.success(false);
        if (request.getAssignSort() != null) {
            editAssignSortCheck(request);
        }
        return BaseResponse.success(storeService.editAssignSort(request));
    }

    private void editAssignSortCheck(StoreSupplierAssignSortEditRequest request) {
        CompanyMallContractRelationRequest relationRequest = new CompanyMallContractRelationRequest();
        relationRequest.setRelationType(MallContractRelationType.TAB.getValue());
        relationRequest.setStoreId(request.getStoreId());
        final List<CompanyMallContractRelation> list = companyMallContractRelationService.list(relationRequest);
        if (CollectionUtils.isEmpty(list)) throw new RuntimeException("店铺未签约");
        relationRequest = new CompanyMallContractRelationRequest();
        relationRequest.setRelationType(MallContractRelationType.TAB.getValue());
        relationRequest.setRelationValue(list.get(0).getRelationValue());
        final List<CompanyMallContractRelation> list2 = companyMallContractRelationService.list(relationRequest);
        if (CollectionUtils.isNotEmpty(list2)) {
            final List<Long> collect = list2.stream().map(CompanyMallContractRelation::getStoreId).collect(Collectors.toList());
            final List<Store> checkList = storeService.findByStoreIdNotAndStoreIdInAndAssignSortAndDelFlag(request.getStoreId(), collect, request.getAssignSort(), DeleteFlag.NO);
            if (CollectionUtils.isNotEmpty(checkList)) throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"当前排序已经存在");
        }
    }

    private void pushCustomer(CompanyInfo companyInfo, Store store, String loginToken){
        CustomerKingdeeModel kingdeeModel = new CustomerKingdeeModel();
        kingdeeModel.setFNumber(companyInfo.getCompanyCodeNew());
        kingdeeModel.setFName(store.getStoreName());
        Map fCreateOrgId = new HashMap();
        fCreateOrgId.put("FNumber","102");
        kingdeeModel.setFCreateOrgId(fCreateOrgId);//组织
        Map fProvince = new HashMap();
        fProvince.put("FNumber", store.getProvinceId());
        kingdeeModel.setFProvince(fProvince);//省份
        Map fCity = new HashMap();
        fCity.put("FNumber",store.getCityId());
        kingdeeModel.setFCity(fCity);//城市
        Map area = new HashMap();
        area.put("FNumber",store.getAreaId());
        kingdeeModel.setFArea(area);//区
        kingdeeModel.setFContact(store.getContactPerson());
        kingdeeModel.setFTel(store.getContactMobile());
        kingdeeModel.setFAddress(store.getAddressDetail());
        Map fGroup = new HashMap();
        fGroup.put("FNumber","888");
        kingdeeModel.setFGroup(fGroup);
        Map fPriceListId = new HashMap();
        fPriceListId.put("FNumber","XSJMB0007");
        kingdeeModel.setFPriceListId(fPriceListId);
        //登录财务系统
        Map<String,Object> requestLogMap = new HashMap<>();
        requestLogMap.put("user",kingdeeUser);
        requestLogMap.put("pwd",kingdeePwd);
        if (StringUtils.isNotEmpty(loginToken)){
            //提交财务单
            Map<String,Object> requestMap = new HashMap<>();
            requestMap.put("Model",kingdeeModel);
            HttpCommonResult result1 = HttpCommonUtil.postHeader(customerUrl, requestMap, loginToken);
            log.info("CustomerSiteService.pushUserKingdee result1:{}", result1.getResultData());
        }else {
            log.error("CustomerSiteService.pushUserKingdee push kingdee error");
        }
    }
    @Override
    @LcnTransaction
    public BaseResponse updateAuditStateToContract(StoreAuditJHInfoRequest request){
        storeService.updateByStoreId(request.getStoreId());
        return BaseResponse.SUCCESSFUL();
    }


    private String wrapTypeName(CompanyType companyType) {
        if (null == companyType) return "";
        switch (companyType) {
            case PLATFORM:
                return "平台自营";
            case SUPPLIER:
                return "第三方商家";
            case UNIFIED:
                return "统仓统配";
            default:
                return "其他";
        }
    }

    @Override
    public BaseResponse<StoreModifyResponse> modifyH5StoreInfo(StoreContractH5ModifyRequest storeModifyRequest) {
        Store store = storeService.modifyStoreH5BaseInfo(storeModifyRequest);

        StoreModifyResponse response = new StoreModifyResponse();

        KsBeanUtil.copyPropertiesThird(store, response);
        CompanyInfoVO companyInfoVO = new CompanyInfoVO();
        response.setCompanyInfo(companyInfoVO);
        KsBeanUtil.copyPropertiesThird(store.getCompanyInfo(),companyInfoVO);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse updatePresellState(@RequestBody @Valid StorePresellRequest request) {
        storeService.updatePresellState(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<StoreShippingAddressVO> editShippingAddress(StoreShippingAddressEditDTO save) {
        return BaseResponse.success(storeShippingAddressService.editShippingAddress(save));
    }
}
