package com.wanmi.sbc.marketing.coupon.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.CouponStatus;
import com.wanmi.sbc.marketing.bean.enums.RangeDayType;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoGetRecordVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: jiangxin
 * @create: 2021-09-08 10:08
 */
@Data
public class CouponCodeBuildRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    @ApiModelProperty(value = "优惠券Id")
    private String couponId;

    @ApiModelProperty(value = "优惠券名称模糊查询")
    private String couponName;

    /**
     * 优惠券状态
     */
    @ApiModelProperty(value = "优惠券查询状态")
    private CouponStatus couponStatus;

    /**
     * 客户账户-手机号
     */
    private String customerAccount;

    /**
     * 活动类型
     */
    private CouponActivityType activityType;

    /**
     * 构建查询对象
     * @return
     */
    public String getQuerySql() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT customer.customer_account AS `customerAccount`,");
        sb.append("couponCode.use_status AS `useStatus`,");
        sb.append("couponCode.coupon_code_id AS `couponCodeId`,");
        sb.append("couponCode.activity_id AS `activityId`,");
        sb.append("couponInfo.coupon_id AS `couponId`,");
        sb.append("couponInfo.coupon_name AS `couponName`,");
        sb.append("couponInfo.denomination AS `denomination`,");
        sb.append("couponInfo.start_time AS `startTime`,");
        sb.append("couponInfo.end_time AS `endTime`,");
        sb.append("couponInfo.create_time AS `createTime`,");
        sb.append("couponInfo.range_day_type AS `rangeDayType`,");
        sb.append("couponInfo.effective_days AS `effectiveDays`,");
        sb.append("count(1) AS `receiveCount`");
        return sb.toString();
    }

    /**
     * 分页查询时查询领取记录总数
     * @return
     */
    public String getQueryTotalCountSql(){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(1) AS `totalCount` ");
        return sb.toString();
    }

    /**
     * 查询条件
     */
    public String getQueryConditionSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(" FROM coupon_code couponCode");
        sb.append(" LEFT JOIN v_customer customer on couponCode.customer_id = customer.customer_id");
        sb.append(" LEFT JOIN coupon_info couponInfo on couponCode.coupon_id = couponInfo.coupon_id");
        sb.append(" LEFT JOIN coupon_activity activity on couponCode.activity_id = activity.activity_id");
        sb.append(" WHERE 1=1");
        sb.append(" AND couponCode.del_flag = 0");

        if(!StringUtils.isEmpty(activityId)){
            sb.append(" AND activity.activity_id = '").append(activityId).append("'");
        }

        if(!StringUtils.isEmpty(couponId)){
            sb.append(" AND couponInfo.coupon_id = '").append(couponId).append("'");
        }

        if(!StringUtils.isEmpty(couponName)){
            sb.append(" AND couponInfo.coupon_name like '%").append(couponName).append("%'");
        }

//        if(couponStatus != null && couponStatus == CouponStatus.STARTED){
//            //参考：couponInfoservice.getWhereCriteria switch (request.getCouponStatus())
//            //优惠券领取的券码生效开始、结束时间
//            String timeNow = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
//            sb.append(" AND couponCode.start_time < '").append(timeNow).append("'");
//            sb.append(" AND couponCode.end_time >= '").append(timeNow).append("'");
////            sb.append(" AND couponInfo.range_day_type = ").append(RangeDayType.RANGE_DAY.toValue());
//        }

        //活动类型
        if (Objects.nonNull(activityType)){
            sb.append(" AND activity.activity_type = ").append(activityType.toValue());
        }

        if (!StringUtils.isEmpty(customerAccount)){
            sb.append(" AND customer.customer_account like '%").append(customerAccount).append("%'");
        }

        //分组统计数量
        sb.append(" GROUP BY couponCode.coupon_id,customer.customer_account");
        //排序
        sb.append(" ORDER BY couponInfo.create_time DESC");
        return sb.toString();
    }

    /**
     * 查询对象转换
     * @param sqlResult
     * @return
     */
    public static List<CouponInfoGetRecordVO> converter(List<Map<String, Object>> sqlResult) {
        return sqlResult.stream().map(item ->
                CouponInfoGetRecordVO.builder()
                        .couponCodeId(toStr(item,"couponCodeId"))
                        .customerAccount(toStr(item,"customerAccount"))
                        .couponId(toStr(item,"couponId"))
                        .couponName(toStr(item,"couponName"))
                        .denomination(toBigDecimal(item,"denomination"))
                        .rangeDayType(RangeDayType.fromValue(toInteger(item, "rangeDayType")))
                        .effectiveDays(toInteger(item,"effectiveDays"))
                        .startTime(toDate(item,"startTime"))
                        .endTime(toDate(item, "endTime"))
                        .receiveCount(toLong(item,"receiveCount"))
                        .useStatus(toInteger(item,"useStatus"))
                        .build()
        ).collect(Collectors.toList());
    }

    private static String toStr(Map<String, Object> map, String key) {
        return map.get(key) != null ? map.get(key).toString() : null;
    }

    private static Long toLong(Map<String, Object> map, String key) {
        return map.get(key) != null ? Long.parseLong(map.get(key).toString()) : null;
    }

    private static Integer toInteger(Map<String, Object> map, String key) {
        return map.get(key) != null ? Integer.parseInt(map.get(key).toString()) : 0;
    }

    private static LocalDateTime toDate(Map<String, Object> map, String key) {
        return map.get(key) != null ? DateUtil.parse(map.get(key).toString(), "yyyy-MM-dd HH:mm:ss.S") : null;
    }

    private static BigDecimal toBigDecimal(Map<String, Object> map, String key) {
        return map.get(key) != null ? new BigDecimal(map.get(key).toString()) : null;
    }
}
