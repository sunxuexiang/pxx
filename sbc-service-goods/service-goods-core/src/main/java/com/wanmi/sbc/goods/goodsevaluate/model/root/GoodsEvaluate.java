package com.wanmi.sbc.goods.goodsevaluate.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.goodsevaluateimage.model.root.GoodsEvaluateImage;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>商品评价实体类</p>
 * @author liutao
 * @date 2019-02-25 15:14:16
 */
@Data
@Entity
@Table(name = "goods_evaluate")
public class GoodsEvaluate implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 评价id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "evaluate_id")
	private String evaluateId;

	/**
	 * 店铺Id
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 店铺名称
	 */
	@Column(name = "store_name")
	private String storeName;

	/**
	 * 商品id(spuId)
	 */
	@Column(name = "goods_id")
	private String goodsId;

	/**
	 * 货品id(skuId)
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * 商品名称
	 */
	@Column(name = "goods_info_name")
	private String goodsInfoName;

	/**
	 * 订单号
	 */
	@Column(name = "order_no")
	private String orderNo;

	/**
	 * 商品规格信息
	 */
	@Column(name = "spec_details")
	private String specDetails;

	/**
	 * 购买时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "buy_time")
	private LocalDateTime buyTime;

	/**
	 * 商品图片
	 */
	@Column(name = "goods_img")
	private String goodsImg;

	/**
	 * 会员Id
	 */
	@Column(name = "customer_id")
	private String customerId;

	/**
	 * 会员名称
	 */
	@Column(name = "customer_name")
	private String customerName;

	/**
	 * 会员登录账号|手机号
	 */
	@Column(name = "customer_account")
	private String customerAccount;

	/**
	 * 商品评分
	 */
	@Column(name = "evaluate_score")
	private Integer evaluateScore;

	/**
	 * 商品评价内容
	 */
	@Column(name = "evaluate_content")
	private String evaluateContent;

	/**
	 * 发表评价时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "evaluate_time")
	private LocalDateTime evaluateTime;

	/**
	 * 评论回复
	 */
	@Column(name = "evaluate_answer")
	private String evaluateAnswer;

	/**
	 * 回复时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "evaluate_answer_time")
	private LocalDateTime evaluateAnswerTime;

	/**
	 * 回复人账号
	 */
	@Column(name = "evaluate_answer_account_name")
	private String evaluateAnswerAccountName;

	/**
	 * 回复员工Id
	 */
	@Column(name = "evaluate_answer_employee_id")
	private String evaluateAnswerEmployeeId;

	/**
	 * 历史商品评分
	 */
	@Column(name = "history_evaluate_score")
	private Integer historyEvaluateScore;

	/**
	 * 历史商品评价内容
	 */
	@Column(name = "history_evaluate_content")
	private String historyEvaluateContent;

	/**
	 * 历史发表评价时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "history_evaluate_time")
	private LocalDateTime historyEvaluateTime;

	/**
	 * 历史评论回复
	 */
	@Column(name = "history_evaluate_answer")
	private String historyEvaluateAnswer;

	/**
	 * 历史回复时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "history_evaluate_answer_time")
	private LocalDateTime historyEvaluateAnswerTime;

	/**
	 * 历史回复人账号
	 */
	@Column(name = "history_evaluate_answer_account_name")
	private String historyEvaluateAnswerAccountName;

	/**
	 * 历史回复员工Id
	 */
	@Column(name = "history_evaluate_answer_employee_id")
	private String historyEvaluateAnswerEmployeeId;

	/**
	 * 点赞数
	 */
	@Column(name = "good_num")
	private Integer goodNum;

	/**
	 * 是否匿名 0：否，1：是
	 */
	@Column(name = "is_anonymous")
	private Integer isAnonymous;

	/**
	 * 是否已回复 0：否，1：是
	 */
	@Column(name = "is_answer")
	private Integer isAnswer;

	/**
	 * 是否已经修改 0：否，1：是
	 */
	@Column(name = "is_edit")
	private Integer isEdit;

	/**
	 * 是否展示 0：否，1：是
	 */
	@Column(name = "is_show")
	private Integer isShow;

	/**
	 * 是否晒单 0：否，1：是
	 */
	@Column(name = "is_upload")
	private Integer isUpload;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@Column(name = "del_flag")
	private Integer delFlag;

	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 创建人
	 */
	@Column(name = "create_person")
	private String createPerson;

	/**
	 * 修改时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	/**
	 * 修改人
	 */
	@Column(name = "update_person")
	private String updatePerson;

	/**
	 * 删除时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "del_time")
	private LocalDateTime delTime;

	/**
	 * 删除人
	 */
	@Column(name = "del_person")
	private String delPerson;

	@OneToMany
	@Where(clause = "del_flag = 0")
	@org.hibernate.annotations.OrderBy(clause = "create_time")
	@JoinColumn(name = "evaluate_id")
	private List<GoodsEvaluateImage> goodsEvaluateImages;

}