package com.wanmi.sbc.live.host.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.live.api.request.host.LiveHostCustomerAccount;
import com.wanmi.sbc.live.api.request.host.LiveHostAddRequest;
import com.wanmi.sbc.live.api.request.host.LiveHostModifyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LiveHost extends BaseEntity {

 	private static final long serialVersionUID = 4861312497938594874L;

	/**主播ID**/
	private Integer hostId;

	/**主播姓名**/
	private String hostName;

	/**联系方式**/
	private String contactPhone;

	/**主播类型 0 官方 1入驻**/
	private Long hostType;

	/**在职状态 0 离职 1 在职**/
	private Long workingState;

	/**直播账号id**/
	private String customerId;

	/**直播账号**/
	private String customerAccount;

	/**运营账号**/
	private String accountName;

	/**运营账号id**/
	private String employeeId;

	/**删除标识,0:未删除1:已删除 **/
	private Long delFlag;


	public LiveHost(){}


	public LiveHost(LiveHostAddRequest request){
		List<LiveHostCustomerAccount> accounts = request.getAccounts();
		StringBuffer customerIds=new StringBuffer();
		StringBuffer customerAccounts=new StringBuffer();
		if(accounts!=null){
			for (int i = 0; i < accounts.size(); i++) {
				LiveHostCustomerAccount customerAccount1 = accounts.get(i);
				if(i==0){
					customerIds.append(customerAccount1.getCustomerId());
					customerAccounts.append(customerAccount1.getCustomerAccount());
				}else{
					customerIds.append(",").append(customerAccount1.getCustomerId());
					customerAccounts.append(",").append(customerAccount1.getCustomerAccount());
				}
			}
		}
		this.customerId=customerIds.toString();
		this.customerAccount=customerAccounts.toString();

		this.hostName=request.getHostName();
		this.contactPhone=request.getContactPhone();
		this.hostType=request.getHostType();
		this.workingState=request.getWorkingState();
		this.accountName=request.getAccountName();
		this.employeeId=request.getEmployeeId();
	}

	public LiveHost(LiveHostModifyRequest request){
		List<LiveHostCustomerAccount> accounts = request.getAccounts();
		StringBuffer customerIds=new StringBuffer();
		StringBuffer customerAccounts=new StringBuffer();
		if(accounts!=null){
			for (int i = 0; i < accounts.size(); i++) {
				LiveHostCustomerAccount customerAccount1 = accounts.get(i);
				if(i==0){
					customerIds.append(customerAccount1.getCustomerId());
					customerAccounts.append(customerAccount1.getCustomerAccount());
				}else{
					customerIds.append(",").append(customerAccount1.getCustomerId());
					customerAccounts.append(",").append(customerAccount1.getCustomerAccount());
				}
			}
		}
		this.customerId=customerIds.toString();
		this.customerAccount=customerAccounts.toString();
		this.hostId=request.getHostId();
		this.hostName=request.getHostName();
		this.contactPhone=request.getContactPhone();
		this.hostType=request.getHostType();
		this.workingState=request.getWorkingState();
		this.accountName=request.getAccountName();
		this.employeeId=request.getEmployeeId();
	}

}