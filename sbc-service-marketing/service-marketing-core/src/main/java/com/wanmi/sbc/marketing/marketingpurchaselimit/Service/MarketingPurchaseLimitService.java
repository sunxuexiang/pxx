package com.wanmi.sbc.marketing.marketingpurchaselimit.Service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityConfigSaveRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityModifyRequest;
import com.wanmi.sbc.marketing.api.request.pointscoupon.PointsCouponFetchRequest;
import com.wanmi.sbc.marketing.api.request.pointscoupon.PointsCouponQueryRequest;
import com.wanmi.sbc.marketing.api.response.pointscoupon.PointsCouponSendCodeResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.PointsCouponStatus;
import com.wanmi.sbc.marketing.bean.enums.RangeDayType;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.marketing.bean.vo.PointsCouponVO;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivity;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityConfig;
import com.wanmi.sbc.marketing.coupon.model.root.CouponCode;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import com.wanmi.sbc.marketing.coupon.repository.CouponActivityConfigRepository;
import com.wanmi.sbc.marketing.coupon.repository.CouponActivityRepository;
import com.wanmi.sbc.marketing.coupon.repository.CouponCodeRepository;
import com.wanmi.sbc.marketing.coupon.service.CouponActivityService;
import com.wanmi.sbc.marketing.coupon.service.CouponInfoService;
import com.wanmi.sbc.marketing.marketingpurchaselimit.model.root.MarketingPurchaseLimit;
import com.wanmi.sbc.marketing.marketingpurchaselimit.repository.MarketingPurchaseLimitRepository;
import com.wanmi.sbc.marketing.pointscoupon.model.root.PointsCoupon;
import com.wanmi.sbc.marketing.pointscoupon.repository.PointsCouponRepository;
import com.wanmi.sbc.marketing.pointscoupon.service.PointsCouponWhereCriteriaBuilder;
import com.wanmi.sbc.marketing.util.common.CodeGenUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("MarketingPurchaseLimitService")
public class MarketingPurchaseLimitService {

    @Autowired
    private MarketingPurchaseLimitRepository marketingPurchaseLimitRepository;

    public List<MarketingPurchaseLimit> getbyCoutomerIdAndMarketingIdAndGoodsInfoId (String customerId,Long marketingId,String goodsInfoId){
        List<MarketingPurchaseLimit> marketingPurchaseLimits =
                marketingPurchaseLimitRepository.getbyCoutomerIdAndMarketingIdAndGoodsInfoId(customerId, marketingId, goodsInfoId);
        return marketingPurchaseLimits;
    }

    public List<MarketingPurchaseLimit> getMarketingIdAndGoodsInfoId (Long marketingId,String goodsInfoId){
        List<MarketingPurchaseLimit> marketingPurchaseLimits =
                marketingPurchaseLimitRepository.getbyMarketingIdAndGoodsInfoId(marketingId, goodsInfoId);
        return marketingPurchaseLimits;
    }

    public List<MarketingPurchaseLimit> getbyMarketingIdAndGoodsInfosId (Long marketingId, List<String> goodsInfoIds){
        return marketingPurchaseLimitRepository.findAll((Specification<MarketingPurchaseLimit>) (root, cQuery, cBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cBuilder.equal(root.get("marketingId"), marketingId));
            predicates.add(cBuilder.in(root.get("goodsInfoId")).value(goodsInfoIds));
            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        });
    }


}
