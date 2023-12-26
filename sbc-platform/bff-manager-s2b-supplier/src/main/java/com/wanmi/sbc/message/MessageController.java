package com.wanmi.sbc.message;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdsRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeListByAccountTypeVO;
import com.wanmi.sbc.message.api.provider.messagesend.MessageSendQueryProvider;
import com.wanmi.sbc.message.api.request.messagesend.MessageSendAddRequest;
import com.wanmi.sbc.message.api.request.messagesend.MessageSendByIdRequest;
import com.wanmi.sbc.message.api.request.messagesend.MessageSendDelByIdRequest;
import com.wanmi.sbc.message.api.request.messagesend.MessageSendPageRequest;
import com.wanmi.sbc.message.api.response.messagesend.MessageSendByIdResponse;
import com.wanmi.sbc.message.api.response.messagesend.MessageSendPageResponse;
import com.wanmi.sbc.message.bean.enums.MessageSendType;
import com.wanmi.sbc.message.bean.enums.MessageType;
import com.wanmi.sbc.message.bean.enums.SendType;
import com.wanmi.sbc.message.bean.vo.MessageSendVO;
import com.wanmi.sbc.message.request.PushMessageAddRequest;
import com.wanmi.sbc.message.response.PushMessagePageResponse;
import com.wanmi.sbc.message.response.PushMessageResponse;
import com.wanmi.sbc.message.service.MessageService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商家后台发布消息
 */
@RestController("MessageController")
@RequestMapping("/pushMessage")
@Api(tags = "MessageController", description = "站内信任务相关API")
public class MessageController {

    @Autowired
    private MessageSendQueryProvider messageSendQueryProvider;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加站内信任务")
    public BaseResponse add(@Valid @RequestBody PushMessageAddRequest request){
        MessageSendAddRequest convert = KsBeanUtil.convert(request, MessageSendAddRequest.class);
        // 默认值
        convert.setMessageType(MessageType.PUBLISH);
        convert.setSendTimeType(SendType.NOW);
        convert.setSendType(MessageSendType.ALL);

        convert.setCreatePerson(commonUtil.getOperatorId());
        operateLogMQUtil.convertAndSend("商家后台发布消息", "站内信任务", "添加站内信任务");
        return messageService.addMessage(convert);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "站内信任务id", required = true)
    @ApiOperation(value = "查看站内信任务详情")
    public BaseResponse<PushMessageResponse> getMessageById(@PathVariable("id") Long id){
        MessageSendByIdRequest request = new MessageSendByIdRequest(id);
        BaseResponse<MessageSendByIdResponse> response = messageSendQueryProvider.getById(request);
        MessageSendVO vo = response.getContext().getMessageSendVO();
        PushMessageResponse convert = new PushMessageResponse();
        if (Objects.nonNull(vo)) {
            convert = KsBeanUtil.convert(vo, PushMessageResponse.class);
            fillSender(Lists.newArrayList(convert));

        }
        return BaseResponse.success(convert);
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    @ApiOperation(value = "分页查看站内信任务")
    public BaseResponse<PushMessagePageResponse> page(@Valid @RequestBody MessageSendPageRequest request){
        request.setMessageType(2);
        BaseResponse<MessageSendPageResponse> response = messageSendQueryProvider.page(request);
        PushMessagePageResponse convert = KsBeanUtil.convert(response.getContext(), PushMessagePageResponse.class);
        List<PushMessageResponse> list = KsBeanUtil.convert(response.getContext().getMessageSendVOPage().getContent(), PushMessageResponse.class);
        fillSender(list);
        MicroServicePage<PushMessageResponse> data = new MicroServicePage<>();
        data.setContent(list);
        data.setNumber(response.getContext().getMessageSendVOPage().getNumber());
        data.setPageable(response.getContext().getMessageSendVOPage().getPageable());
        data.setTotal(response.getContext().getMessageSendVOPage().getTotal());
        data.setSize(response.getContext().getMessageSendVOPage().getSize());
        convert.setPushMessagePage(data);
        return BaseResponse.success(convert);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "站内信任务id", required = true)
    @ApiOperation(value = "删除站内信任务")
    public BaseResponse deleteById(@PathVariable("id") Long id){
        MessageSendDelByIdRequest request = new MessageSendDelByIdRequest(id, null);
        messageService.deleteById(request);
        messageService.deleteMessageByJoinId(id);
        operateLogMQUtil.convertAndSend("商家后台发布消息", "站内信任务", "删除站内信任务");
       return BaseResponse.SUCCESSFUL();
    }

    /**
     * 填充发送人
     */
    public void fillSender(List<PushMessageResponse> voList){
        if(CollectionUtils.isEmpty(voList)){
            return;
        }

        List<String> idList = voList.stream().map(PushMessageResponse::getCreatePerson).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idList)) {

            Map<String, String> map = employeeQueryProvider.getByIds(EmployeeByIdsRequest.builder().employeeIds(idList).build())
                    .getContext().getEmployeeList().stream().collect(Collectors.toMap(EmployeeListByAccountTypeVO::getEmployeeId, EmployeeListByAccountTypeVO::getAccountName));

            for (PushMessageResponse pushMessageResponse : voList) {
                String account = map.get(pushMessageResponse.getCreatePerson());
                if (StringUtils.isNotEmpty(account)) {
                    pushMessageResponse.setCreateAccount(account);
                }
            }
        }

    }

}
