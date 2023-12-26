package com.wanmi.sbc.sign;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityDetailByActivityTypeRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetByActivityIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityModifyRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivitySignGiveRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailByActivityIdResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailByActivityTypeResponse;
import com.wanmi.sbc.marketing.api.response.coupon.SendCouponResponse;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityConfigVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.marketing.bean.vo.CouponSignDaysVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.sign.request.SignRequest;
import com.wanmi.sbc.sign.response.SignInfoDetailsVO;
import com.wanmi.sbc.sign.response.SignInfoResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "SignInController", description = "签到控制器")
@RestController
@RequestMapping("/sign")
public class SignInController {
    private static final String SIGNED = "SIGNED#";

    @Autowired
    RedisService redisService;

    @Autowired
    CouponActivityProvider couponActivityProvider;

    @Autowired
    CouponActivityQueryProvider couponActivityQueryProvider;

    @Autowired
    CommonUtil commonUtil;



    @ApiOperation(value = "签到列表")
    @RequestMapping(value = "/signList",method = RequestMethod.POST)
    public BaseResponse<SignInfoResponse> signList(@RequestBody SignRequest request){
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        CouponActivityDetailByActivityTypeRequest typeRequest = new CouponActivityDetailByActivityTypeRequest();
        typeRequest.setActivityType(CouponActivityType.SIGN_GIVE);
        //根据签到天数赠送优惠券
        BaseResponse<CouponActivityDetailByActivityTypeResponse> activityType = couponActivityQueryProvider.getByActivitySign();
        CouponActivityDetailByActivityTypeResponse response = activityType.getContext();
        CouponActivityVO activity = response.getCouponActivity();
        if(Objects.isNull(activity)){
            return BaseResponse.success(null);
        }
        //签到天数信息
        List<CouponSignDaysVO> couponSignDaysList = response.getCouponSignDaysList();
        //优惠券配置信息
        List<CouponActivityConfigVO> couponActivityConfigList = response.getCouponActivityConfigList();
        //优惠券信息
        List<CouponInfoVO> couponInfoList = response.getCouponInfoList();

        SignInfoResponse signInfoResponse = new SignInfoResponse();
        matchingCoupon(request,signInfoResponse,couponSignDaysList,couponActivityConfigList,couponInfoList);
        signInfoResponse.setActivityId(activity.getActivityId());
        //签到返回值
        return BaseResponse.success(signInfoResponse);
    }

    @ApiOperation(value = "签到")
    @RequestMapping(value = "/doSign", method = RequestMethod.POST)
    public BaseResponse<SendCouponResponse> doSign(@RequestBody SignRequest request) {
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        //customerId + 年月 = key
        String key = SIGNED+customerId+"#"+getYears();
        LocalDate localDate = LocalDate.parse(specificDate(request.getDate()));
        Boolean b = redisService.doSign(key,localDate);
        //已经签到了
        if(b){
            throw new SbcRuntimeException("k-240001","今天已经签到过了");
        }
        //签到成功,获本周以今天为准的签到次数
        //获取本周签到信息
        List<String> thisWeekInSignfo = redisService.getThisWeekInSignfo(key, localDate);
        //本周连续签到数
        Long continuousSignCountInfo = getContinuousSignCountInfoCopy(thisWeekInSignfo);
        //签到几天了,获得优惠券配置,送券
        Integer signDays = continuousSignCountInfo.intValue();
        CouponActivitySignGiveRequest giveRequest = new CouponActivitySignGiveRequest();
        //签到天数
        giveRequest.setSignDays(signDays);
        //领取用户
        giveRequest.setCustomerId(customerId);
        //活动id
        giveRequest.setActivityId(request.getActivityId());
        BaseResponse<SendCouponResponse> sendCouponResponseBaseResponse = couponActivityProvider.giveCouponDays(giveRequest);
        return sendCouponResponseBaseResponse;
    }

    @ApiOperation(value = "查询今日是否签到")
    @RequestMapping(value = "/checkSign",method = RequestMethod.POST)
    public BaseResponse<Boolean> checkSign(@RequestBody SignRequest request){
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        //customerId + 年月 = key
        String key = SIGNED+customerId+"#"+getYears(request.getGetYears());
        return BaseResponse.success(redisService.checkSign(key,LocalDate.parse(specificDate(request.getDate()))));
    }

    @ApiOperation(value = "获取用户签到次数")
    @RequestMapping(value = "/getSignCount",method = RequestMethod.POST)
    public BaseResponse<Long> getSignCount(@RequestBody SignRequest request){
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        //customerId + 年月 = key
        String key = SIGNED+customerId+"#"+getYears();
        return BaseResponse.success(redisService.getSignCount(key));
    }


    @ApiOperation(value = "获取本月签到信息")
    @RequestMapping(value = "/getSignInfo",method = RequestMethod.POST)
    public BaseResponse<List<String>> getSignInfo(@RequestBody SignRequest request){
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        //customerId + 年月 = key
        String key = SIGNED+customerId+"#"+getYears();
        LocalDate parse = LocalDate.parse(specificDate(request.getDate()));
        List<String> signInfo1 = redisService.getSignInfo(key, parse);
        return BaseResponse.success(signInfo1);
    }

//    @ApiOperation(value = "根据时间段获取本月签到信息")
//    @RequestMapping(value = "/getSignInfo2",method = RequestMethod.POST)
//    public BaseResponse<List<String>> getSignInfo2(@RequestBody SignRequest request){
//        String customerId = request.getCustomerId();
//        if (StringUtil.isEmpty(customerId)) {
//            throw new SbcRuntimeException("K-000009");
//        }
//        //customerId + 年月 = key
//        String key = SIGNED+customerId+getYears();
//        LocalDate parse = LocalDate.parse(specificDate(request.getDate()));
//        LocalDate parse2 = LocalDate.parse(specificDate(request.getDate2()));
//        List<String> signInfo1 = redisService.getSignInfo(key, parse,parse2);
//        return BaseResponse.success(signInfo1);
//    }

    @ApiOperation(value = "获取本周签到信息")
    @RequestMapping(value = "/getThisWeekInSignfo",method = RequestMethod.POST)
    public BaseResponse<List<String>> getThisWeekInSignfo(@RequestBody SignRequest request){
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        //customerId + 年月 = key
        String key = SIGNED+customerId+"#"+getYears();
        LocalDate parse = LocalDate.parse(specificDate(request.getDate()));
        List<String> signInfo1 = redisService.getThisWeekInSignfo(key, parse);
        return BaseResponse.success(signInfo1);
    }


    @ApiOperation(value = "获取本月连续签到信息")
    @RequestMapping(value = "/getContinuousSignCount",method = RequestMethod.POST)
    public BaseResponse<Long> getContinuousSignCount(@RequestBody SignRequest request){
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        //customerId + 年月 = key
        String key = SIGNED+customerId+"#"+getYears();
        LocalDate parse = LocalDate.parse(specificDate(request.getDate()));
        //先获取本周的签到信息
        long continuousSignCount = redisService.getContinuousSignCount(key, parse);
        return BaseResponse.success(continuousSignCount);
    }


    @ApiOperation(value = "获取本周的连续签到天数")
    @RequestMapping(value = "/getWeekContinuousSignCount",method = RequestMethod.POST)
    public BaseResponse<Long> getWeekContinuousSignCount(@RequestBody SignRequest request){
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtil.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setCustomerId(customerId);
        //customerId + 年月 = key
        String key = SIGNED+customerId+"#"+getYears();
        LocalDate parse = LocalDate.parse(specificDate(request.getDate()));
        //获取本周签到信息
        List<String> thisWeekInSignfo = redisService.getThisWeekInSignfo(key, parse);
        Long continuousSignCountInfo = getContinuousSignCountInfoCopy(thisWeekInSignfo);
        return BaseResponse.success(continuousSignCountInfo);
    }

    /**
     * 获取当前年月份
     * @return
     */
    private static String getYears(){
        return new SimpleDateFormat("yyyyMM").format(new Date());
    }

    /**
     * 获取当前年月份
     * @return
     */
    private static String getYears(Date date){
        if(Objects.isNull(date)){
            return new SimpleDateFormat("yyyyMM").format(new Date());
        }
        return new SimpleDateFormat("yyyyMM").format(date);
    }

    private static String specificDate(String date){
        if(!StringUtil.isEmpty(date)){
                return date;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }


    /**
     * 根据本月签到信息，获取最多的连续签到次数
     * @param thisWeekInSignfo
     * @return
     */
//    public Long getContinuousSignCountInfo(List<String> thisWeekInSignfo){
//        if(thisWeekInSignfo.size()>0&& thisWeekInSignfo.size()<=1){
//            return 1L;
//        }
//        //先获得这个星期的下标值
//        List<Long> collect = thisWeekInSignfo.stream().map(weelInfo -> {
//            String[] split = weelInfo.split("-");
//            return Long.valueOf(split[2]);
//        }).collect(Collectors.toList());
//        LocalDate parse = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//        Long count = 0L;
//        //今天
//        Long thisDay = Long.valueOf(parse.getDayOfMonth());
////        Long thisDay = 22L;
//        //如果当
//        boolean b = false;
//        //第一天签到
//        boolean firstDay = true;
//        //只需要算出今天的签到数
//        for (int i = 0;i < collect.size();i++) {
//            Long aLong = collect.get(i);
//            if (thisDay.equals(aLong)){
//                b =true;
//            }
//            //当今天等于签到时间时,判断是否连续签到
//            if(b){
//                //只记录一次今天的签到
//                if(firstDay){
//                    count++;
//                    firstDay = false;
//                }
//                //今天
//                Long today = collect.get(i);
//                if(collect.size()>i+1){
//                    //此行代码保证下标不会越界
//                    Long yesterday = collect.get(i+1);
//                    long l = today-yesterday;
//                    //如果等于1则说明，是连续签到
//                    if(l == 1L) {
//                        count++;
//                        //换位
//                    }else {
//                        //断了，跳出循环
//                        break;
//                    }
//                }else {
//                    //还是签到了一次的
//                    if(count.equals(0L)){
//                        count++;
//                    }
//                    break;
//                }
//            }
//        }
//        return count;
//    }



    /**
     * 根据本周签到信息，获取最多的连续签到次数
     * @param thisWeekInSignfo
     * @return
     */
    public Long getContinuousSignCountInfoCopy(List<String> thisWeekInSignfo){
        if(thisWeekInSignfo.size()>0&& thisWeekInSignfo.size()<=1){
            return 1L;
        }
        //先获得这个星期的下标值
        LocalDate parse = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        List<LocalDate> localDates = thisWeekInSignfo.stream().map(weekInfo -> {
            return LocalDate.parse(weekInfo);
        }).collect(Collectors.toList());
        Long count = 0L;
        //当本周签到列表等于今天时为true
        boolean b = false;
        //第一天签到
        boolean firstDay = true;
        //只需要算出今天的签到数
        for (int i = 0;i < localDates.size();i++) {
            LocalDate localDate = localDates.get(i);
            if(parse.isEqual(localDate)){
                b = true;
            }
            //当今天等于签到时间时,判断是否连续签到
            if(b){
                //只记录一次今天的签到
                if(firstDay){
                    //签到天数+1
                    count++;
                    //将是否为第一天改为否
                    firstDay = false;
                }
                //循环内当前时间
                LocalDate today = localDates.get(i);
                //此行代码保证下标不会越界
                if(localDates.size()>i+1){
                    //下一个下标的时间,应当是上一天
                    LocalDate yesterday = localDates.get(i+1);
                    //根据当前时间获取昨天的时间,如果下一个下标的时间等于昨天的时间,则代表是连续签到
                    LocalDate getYesterday = getYesterday(today);
                    //此行代码判断,判断下一个下标是否等于上一个下标的明天
                    boolean equal = yesterday.isEqual(getYesterday);
                    //如果等于1则说明，是连续签到
                    if(equal) {
                        count++;
                        //换位
                    }else {
                        //断了，跳出循环
                        break;
                    }
                }else {
                    //还是签到了一次的
                    if(count.equals(0L)){
                        count++;
                    }
                    break;
                }
            }
        }
        return count;
    }
    private void  matchingCoupon(SignRequest request,SignInfoResponse signInfoResponse,List<CouponSignDaysVO> couponSignDaysList,List<CouponActivityConfigVO> couponActivityConfigList,List<CouponInfoVO> couponInfoList){
        List<SignInfoDetailsVO> signInfoDetailsVOS = couponSignDaysList.stream().map(couponSignDaysVO -> {
            SignInfoDetailsVO detailsVO = new SignInfoDetailsVO();
            //根据签到天数筛选出对应的优惠券 即该天签到后送什么优惠券
            List<CouponActivityConfigVO> collect = couponActivityConfigList.stream().filter(couponActivityConfigVO -> couponActivityConfigVO.getCouponSignDaysId().equals(couponSignDaysVO.getCouponSignDaysId())).collect(Collectors.toList());
            //如果有优惠券,则代表改天设置了优惠券
            if(CollectionUtils.isNotEmpty(collect)){
                CouponActivityConfigVO couponActivityConfigVO = collect.get(0);
                //根据优惠券配置筛选出对应的优惠券
                List<CouponInfoVO> couponInfoVOS = couponInfoList.stream().filter(couponInfoVO -> couponInfoVO.getCouponId().equals(couponActivityConfigVO.getCouponId())).collect(Collectors.toList());
                CouponInfoVO couponInfoVO = couponInfoVOS.get(0);
                detailsVO.setCouponName(couponInfoVO.getCouponName());
                detailsVO.setTotalCount(couponActivityConfigVO.getTotalCount());
                detailsVO.setSignDays(couponSignDaysVO.getSignDays());
                detailsVO.setDenomination(couponInfoVO.getDenomination());
                //优惠券金额
            }else {
                //没有设置优惠券,则代表这天没有优惠券
                detailsVO.setSignDays(couponSignDaysVO.getSignDays());
            }
            return detailsVO;
        }).collect(Collectors.toList());
        //判断是今天的则可以签到
        Integer weekByChooseDay = redisService.getWeekByChooseDay();
        signInfoDetailsVOS.stream().forEach(signInfoDetailsVO -> {
            Integer signDays1 = signInfoDetailsVO.getSignDays();
            //根据签到天数判断是周几
            String weekByDay = redisService.getWeekByDay(signDays1);
            signInfoDetailsVO.setSignDay(weekByDay);
            //判断今天是否签到
            request.setDate(weekByDay);
            //签到天数
            Integer signDays = signInfoDetailsVO.getSignDays();
            try {
                //签到时间
                Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(signInfoDetailsVO.getSignDay());
                request.setGetYears(parse);
            }catch (ParseException e){
                e.printStackTrace();
            }
            if(signDays.equals(weekByChooseDay)){
                //今天没有签到就可以签到
                if(!checkSign(request).getContext()){
                    signInfoDetailsVO.setIsThatOK(1);
                }else {
                    signInfoDetailsVO.setIsThatOK(0);
                }
            }else {
                signInfoDetailsVO.setIsThatOK(0);
            }
            //判断是否签到
            Boolean b = checkSign(request).getContext();
            if(b){
                signInfoDetailsVO.setIsSign(1);
            }else {
                signInfoDetailsVO.setIsSign(0);
            }
        });
        signInfoResponse.setSignInfoDetailsVOS(signInfoDetailsVOS);
    }

    /**
     * 根据传入的时间获取明天的时间
     * @param localDate
     * @return
     */
    public LocalDate getTomorrow(LocalDate localDate){
        Date from = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.add(Calendar.DATE,1);
        //昨天的日期
        Date time = calendar.getTime();
        return time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 根据传入的时间获取明天的时间
     * @param localDate
     * @return
     */
    public LocalDate getYesterday(LocalDate localDate){
        Date from = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.add(Calendar.DATE,-1);
        //昨天的日期
        Date time = calendar.getTime();
        return time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


}