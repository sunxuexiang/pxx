package com.wanmi.sbc.setting.hotstylemoments.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsQueryRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 爆款时刻查询条件封装类
 * @author: XinJiang
 * @time: 2022/5/9 18:39
 */
public class HotStyleMomentsWhereCriteriaBuilder {

    public static Specification<HotStyleMoments> build(HotStyleMomentsQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询
            if (StringUtils.isNotBlank(queryRequest.getHotName())) {
                predicates.add(cbuild.like(root.get("hotName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getHotName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //查询条件开始时间、结束时间不能和查询状态条件重叠使用
            if (Objects.nonNull(queryRequest.getStatus())) {
                if (Objects.isNull(queryRequest.getNow())) {
                    queryRequest.setNow(LocalDateTime.now());
                }
                switch (queryRequest.getStatus()) {
                    case STARTED://进行中
                        //开始时间小于等于当前时间
                        predicates.add(cbuild.lessThanOrEqualTo(root.get("beginTime"), queryRequest.getNow()));
                        //结束时间大于等于当前时间
                        predicates.add(cbuild.greaterThanOrEqualTo(root.get("endTime"), queryRequest.getNow()));
                        //未暂停
                        predicates.add(cbuild.equal(root.get("isPause"), DefaultFlag.NO));
                        //未终止
                        predicates.add(cbuild.equal(root.get("terminationFlag"), DefaultFlag.NO));
                        break;
                    case PAUSED://暂停中
                        //开始时间小于等于当前时间
                        predicates.add(cbuild.lessThanOrEqualTo(root.get("beginTime"), queryRequest.getNow()));
                        //结束时间大于等于当前时间
                        predicates.add(cbuild.greaterThanOrEqualTo(root.get("endTime"), queryRequest.getNow()));
                        //暂停中
                        predicates.add(cbuild.equal(root.get("isPause"), DefaultFlag.YES));
                        break;
                    case NOT_START://未开始
                        //开始时间大于当前时间
                        predicates.add(cbuild.greaterThan(root.get("beginTime"), queryRequest.getNow()));
                        break;
                    case ENDED://已结束
                        //结束时间小于当前时间
                        predicates.add(cbuild.lessThan(root.get("endTime"), queryRequest.getNow()));
                        break;
                    case S_NS: // 进行中&未开始
                        //结束时间大于等于当前时间
                        predicates.add(cbuild.lessThanOrEqualTo(root.get("endTime"), queryRequest.getNow()));
                        //未暂停
                        predicates.add(cbuild.equal(root.get("isPause"), DefaultFlag.NO));
                        break;
                    case TERMINATION: //终止
                        predicates.add(cbuild.equal(root.get("terminationFlag"), DefaultFlag.YES));
                        break;
                    default:
                        break;
                }
            } else {
                //开始时间 大于等于
                if (Objects.nonNull(queryRequest.getBeginTime())) {
                    predicates.add(cbuild.greaterThanOrEqualTo(root.get("beginTime"), queryRequest.getBeginTime()));
                }

                //结束时间 小于等于
                if (Objects.nonNull(queryRequest.getEndTime())) {
                    predicates.add(cbuild.lessThanOrEqualTo(root.get("endTime"), queryRequest.getEndTime()));
                }
            }

            //是否暂停（1：暂停，0：正常）
            if (Objects.nonNull(queryRequest.getIsPause())) {
                predicates.add(cbuild.equal(root.get("isPause"), queryRequest.getIsPause()));
            }

            //是否终止（1：是，0：否）
            if (Objects.nonNull(queryRequest.getTerminationFlag())) {
                predicates.add(cbuild.equal(root.get("terminationFlag"), queryRequest.getTerminationFlag()));
            }

            //是否删除（1：是，0：否）
            if (Objects.nonNull(queryRequest.getDelFlag())) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
