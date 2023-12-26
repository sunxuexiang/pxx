package com.wanmi.sbc.message.provider.impl.appmessage;

import com.wanmi.sbc.message.api.request.appmessage.*;
import com.wanmi.sbc.message.api.response.appmessage.AppMessageUnreadResponse;
import com.wanmi.sbc.message.bean.enums.MessageType;
import com.wanmi.sbc.message.bean.vo.AppMessageUnreadVo;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.message.api.provider.appmessage.AppMessageQueryProvider;
import com.wanmi.sbc.message.api.response.appmessage.AppMessagePageResponse;
import com.wanmi.sbc.message.api.response.appmessage.AppMessageListResponse;
import com.wanmi.sbc.message.bean.vo.AppMessageVO;
import com.wanmi.sbc.message.appmessage.service.AppMessageService;
import com.wanmi.sbc.message.appmessage.model.root.AppMessage;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>App站内信消息发送表查询服务接口实现</p>
 * @author xuyunpeng
 * @date 2020-01-06 10:53:00
 */
@RestController
@Validated
public class AppMessageQueryController implements AppMessageQueryProvider {
	@Autowired
	private AppMessageService appMessageService;

	@Override
	public BaseResponse<AppMessagePageResponse> page(@RequestBody @Valid AppMessagePageRequest appMessagePageReq) {
		AppMessageQueryRequest queryReq = KsBeanUtil.convert(appMessagePageReq, AppMessageQueryRequest.class);
		Page<AppMessage> appMessagePage = appMessageService.page(queryReq);
		Page<AppMessageVO> newPage = appMessagePage.map(entity -> appMessageService.wrapperVo(entity));
		MicroServicePage<AppMessageVO> microPage = new MicroServicePage<>(newPage, appMessagePageReq.getPageable());
		int preferentialNum = appMessageService.getMessageCount(queryReq.getCustomerId(), MessageType.Preferential);
		int noticeNum = appMessageService.getMessageCount(queryReq.getCustomerId(), MessageType.Notice);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd");
		if (!ObjectUtils.isEmpty(microPage.getContent())) {
			microPage.getContent().forEach(msg -> {
				if (msg.getSendTime() != null) {
					try {
						msg.setSendTimeSecond(msg.getSendTime().toEpochSecond(ZoneOffset.of("+8")));
						String timeStr = dateTimeFormatter.format(msg.getSendTime());
						int year = msg.getSendTime().getYear() % 100;
						msg.setSendTimeStr(year + "/" + timeStr);
					}
					catch (Exception e) {}
				}
			});
		}
		AppMessagePageResponse finalRes = new AppMessagePageResponse(microPage, noticeNum, preferentialNum);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<AppMessageListResponse> list(@RequestBody @Valid AppMessageListRequest appMessageListReq) {
		AppMessageQueryRequest queryReq = KsBeanUtil.convert(appMessageListReq, AppMessageQueryRequest.class);
		List<AppMessage> appMessageList = appMessageService.list(queryReq);
		List<AppMessageVO> newList = appMessageList.stream().map(entity -> appMessageService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new AppMessageListResponse(newList));
	}

	@Override
	public BaseResponse<AppMessagePageResponse> getUnreadMessageCount(AppMessageUnreadRequest unreadRequest) {
		int preferentialNum = appMessageService.getMessageCount(unreadRequest.getCustomerId(), MessageType.Preferential);
		int noticeNum = appMessageService.getMessageCount(unreadRequest.getCustomerId(), MessageType.Notice);
		return BaseResponse.success(AppMessagePageResponse.builder().NoticeNum(noticeNum).PreferentialNum(preferentialNum).build());
	}

	/**
	 * 查询App站内信未读消息数以及最新一条消息体数据（不查询消息列表）
	 */
	@Override
	public BaseResponse<AppMessageUnreadResponse> getUnreadNumData(@RequestBody AppMessagePageRequest request) {
		// 未读消息数以及未读消息内容封装对象
		AppMessageUnreadResponse response = new AppMessageUnreadResponse();
		response.setPreferentialMessage(new AppMessageUnreadVo());
		response.setNoticeMessage(new AppMessageUnreadVo());
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		// 查询通知、促销消息未读数量
		Integer preferentialNum = appMessageService.getMessageCount(request.getCustomerId(), MessageType.Preferential);
		Integer noticeNum = appMessageService.getMessageCount(request.getCustomerId(), MessageType.Notice);

		// 查询服务通知消息的最后一条消息
		AppMessage noticeAppMessage = appMessageService.getNewMessageByGroup(request.getCustomerId(), 1);
		if (noticeAppMessage == null) {
			noticeAppMessage = new AppMessage();
		}
		AppMessageUnreadVo noticeMessageUnreadVo = KsBeanUtil.convert(noticeAppMessage, AppMessageUnreadVo.class);
		noticeMessageUnreadVo.setUnreadNum(noticeNum);
		if (noticeAppMessage.getSendTime() != null) {
			try {
				noticeMessageUnreadVo.setSendTime(dateTimeFormatter.format(noticeAppMessage.getSendTime()));

				DateTimeFormatter childFormatter = DateTimeFormatter.ofPattern("MM/dd");
				noticeMessageUnreadVo.setSendTimeSecond(noticeAppMessage.getSendTime().toEpochSecond(ZoneOffset.of("+8")));
				String timeStr = childFormatter.format(noticeAppMessage.getSendTime());
				int year = noticeAppMessage.getSendTime().getYear() % 100;
				noticeMessageUnreadVo.setSendTimeStr(year + "/" + timeStr);
			}
			catch (Exception e) {}
		}
		response.setNoticeMessage(noticeMessageUnreadVo);

		// 查询优惠促销消息的最后一条消息
		AppMessage preferentialAppMessage = appMessageService.getNewMessageByGroup(request.getCustomerId(), 0);
		if (preferentialAppMessage == null) {
			preferentialAppMessage = new AppMessage();
		}
		AppMessageUnreadVo preferentialMessageUnreadVo = KsBeanUtil.convert(preferentialAppMessage, AppMessageUnreadVo.class);
		preferentialMessageUnreadVo.setUnreadNum(preferentialNum);
		if (preferentialAppMessage.getSendTime() != null) {
			try {
				preferentialMessageUnreadVo.setSendTime(dateTimeFormatter.format(preferentialAppMessage.getSendTime()));

				DateTimeFormatter childFormatter = DateTimeFormatter.ofPattern("MM/dd");
				preferentialMessageUnreadVo.setSendTimeSecond(preferentialAppMessage.getSendTime().toEpochSecond(ZoneOffset.of("+8")));
				String timeStr = childFormatter.format(preferentialAppMessage.getSendTime());
				int year = preferentialAppMessage.getSendTime().getYear() % 100;
				preferentialMessageUnreadVo.setSendTimeStr(year + "/" + timeStr);
			}
			catch (Exception e) {}
		}
		response.setPreferentialMessage(preferentialMessageUnreadVo);

		return BaseResponse.success(response);
	}


}

