package com.wanmi.sbc.account.bean.enums;

import com.wanmi.sbc.common.annotation.ApiEnum;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;

/**
 * 鲸贴来源/鲸贴明细
 */
@ApiEnum
public enum WalletDetailsType {

    //明细的获得（明细中为增加的数据） 其中1、2、3点， 点击明细支持跳转相关订单详情   其中售后理赔、订单返运费、物流赔偿、补差价赔偿放在2期开发

    //1、订单退款 （线上、线下支付的订单都退至资产，好友代付的部分金额需要原路返回代付账户（如果用户已经使用资产抵扣，则该部分退回用户资产）
    @ApiEnumProperty("1：订单退款")
    INCREASE_ORDER_REFUND("订单退款"),

    //2、订单奖励 （省外返现，长沙市内 、湖南省内返现规则,提交订单时满足条件才返，且去需要订单为已完成状态）
    @ApiEnumProperty("2：订单奖励")
    INCREASE_ORDER_AWARD("订单奖励"),

    //3、取消订单 （订单未支付，后台或者用户操作取消订单）
    @ApiEnumProperty("3：取消订单")
    INCREASE_ORDER_CANCEL("取消订单"),

    //4、取消申请 （提现进度在客服审核完成前用户操作取消）
    @ApiEnumProperty("4：取消提现")
    INCREASE_WITHDRAW_CANCEL("取消提现"),

    //5、申请失败 （客服、财务审核未通过或财务操作提现失败）
    @ApiEnumProperty("5：提现失败")
    INCREASE_WITHDRAW_FAIL("提现失败"),

    @ApiEnumProperty("6：手动充值")
    INCREASE_RECHARGE("手动充值"),


    //鲸贴的扣除（明细中为减去的数据） 其中1、2、3点，点击明细支持跳转相关订单详情

    //1、订单抵扣（提交订单时抵扣商品金额）
    @ApiEnumProperty("1：订单抵扣")
    DEDUCTION_ORDER_DEDUCTION("订单抵扣"),

    //2、订单奖励退还（有下单奖励的订单发生退款，退款商品数量低于奖励条件，则全部退还；不低于奖励条件的，退还退款商品部分）
    @ApiEnumProperty("2：订单奖励退还")
    DEDUCTION_ORDER_REFUND_DEDUCTED("订单奖励退还"),

    //3、订单等待支付（提交订单后，未完成支付状态）-- 此状态废除,不需要
    @ApiEnumProperty("3：订单等待支付")
    DEDUCTION_ORDER_WAITING_PAYMENT("订单等待支付"),

    //4、申请审核（提现申请在待审核、待打款状态）
    @ApiEnumProperty("4：申请审核")
    DEDUCTION_WITHDRAW_AUDIT("申请审核"),

    //5、申请成功（财务审核完成）
    @ApiEnumProperty("5：申请成功")
    DEDUCTION_WITHDRAW_SUCCEED("提现成功"),

    @ApiEnumProperty("6：手动扣除")
    DEDUCTION_RECHARGE("手动扣除"),

    @ApiEnumProperty("7：购买商品赠送")
    BUY_GOODS_SEND("指定商品返鲸币"),

    @ApiEnumProperty("8：取消购买商品扣除")
    CANCEL_GOODS_RECHARGE("指定商品返鲸币退回"),
    @ApiEnumProperty("10: 鲸币赠送")
    GIVE_JINGBI("鲸币赠送"),
    @ApiEnumProperty("11: 鲸币收回")
    BACK_JINGBI("鲸币收回"),
    ;

    private String desc;

    public String getDesc() {
        return desc;
    }

    WalletDetailsType(String desc) {
        this.desc = desc;
    }

}
