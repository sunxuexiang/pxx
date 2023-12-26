package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.EmailConfigVO;
import com.wanmi.sbc.common.base.GenerateExcelSendEmailVo;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SendEmailUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoCountByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoStockByGoodsInfoIdsRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoCountByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.setting.api.provider.EmailConfigProvider;
import com.wanmi.sbc.setting.api.response.EmailConfigQueryResponse;
import com.wanmi.sbc.setting.bean.enums.EmailStatus;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.core.Local;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 库存不足预警邮件发送定时任务 每天8点00  手动推送时，如果参数不为空，则拼接邮件内容
 * @author: XinJiang
 * @time: 2021/12/20 8:59
 */
@JobHandler(value = "sendStockWarningEmailHandler")
@Component
@Slf4j
public class SendStockWarningEmailHandler extends IJobHandler {

    private static final int PAGE_SIZE = 100;

    @Value("${send.goods.stock.warning.emails}")
    private String emailList;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private EmailConfigProvider emailConfigProvider;

    @Autowired
    private SendEmailUtil sendEmailUtil;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("发送商品库存不足预警邮件任务执行开始：" + LocalDateTime.now());
        log.info("发送商品库存不足预警邮件任务执行开始：{}",LocalDateTime.now());
        //需要发送邮件的商品信息
        List<GoodsInfoVO> sendGoodsEmails = new ArrayList<>();
        //所有子分类信息
        Map<Long, GoodsCateVO> goodsCateMap = goodsCateQueryProvider.listLeaf().getContext().getGoodsCateList().stream()
                .collect(Collectors.toMap(GoodsCateVO::getCateId, c -> c));
        //查询商品总数
        GoodsInfoCountByConditionResponse response = goodsInfoQueryProvider.countByCondition(GoodsInfoCountByConditionRequest.builder()
                .goodsInfoType(0).delFlag(0).addedFlag(1).build()).getContext();
        Long total = response.getCount();
        //封装查询商品条件
        GoodsInfoPageRequest request = GoodsInfoPageRequest.builder().goodsInfoType(0).delFlag(0).addedFlag(1).build();
        int pageNum = total.intValue() / PAGE_SIZE + 1;
        request.setPageSize(PAGE_SIZE);
        int pageNo = 0;
        XxlJobLogger.log("预警商品库存总数："+total+"，分页数："+pageNum);
        while (pageNo < pageNum) {
            request.setPageNum(pageNo);
            //分页查询商品信息
            GoodsInfoPageResponse infoPageResponse = goodsInfoQueryProvider.page(request).getContext();
            List<GoodsInfoVO> goodsInfoVOS = infoPageResponse.getGoodsInfoPage().getContent();
            Map<String,GoodsInfoVO> goodsInfoVOMap = goodsInfoVOS.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g->g));
            List<String> skuIds = goodsInfoVOS.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            //获取商品库存信息
            Map<String, BigDecimal> goodsInfoStockMap = goodsInfoQueryProvider.findGoodsInfoStockByIds(GoodsInfoStockByGoodsInfoIdsRequest.builder().goodsInfoIds(skuIds).build()).getContext().getGoodsInfoStockMap();
            goodsInfoStockMap.forEach((k,v) -> {
                if (v.compareTo(BigDecimal.valueOf(10)) <= 0) {
                    GoodsInfoVO goodsInfoVO = goodsInfoVOMap.getOrDefault(k,null);
                    if (Objects.nonNull(goodsInfoVO)) {
                        //设置商品库存
                        goodsInfoVO.setStock(v);
                        GoodsCateVO goodsCateVO = goodsCateMap.getOrDefault(goodsInfoVO.getCateId(),null);
                        if (Objects.nonNull(goodsCateVO)) {
                            //设置分类名称
                            goodsInfoVO.setCateName(goodsCateVO.getCateName());
                        }
                        sendGoodsEmails.add(goodsInfoVO);
                    }
                }
            });
            pageNo++;
        }

        if (CollectionUtils.isNotEmpty(sendGoodsEmails)) {
            XxlJobLogger.log("需要发送邮件的商品库存预警数："+sendGoodsEmails.size());
            //查询邮箱信息
            EmailConfigQueryResponse config = emailConfigProvider.queryEmailConfig().getContext();
            //邮箱停用状态下直接返回
            if (config.getStatus() == EmailStatus.DISABLE) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "发件人邮箱禁用，请在运营后台设置邮箱信息！");
            }
            //收件人
            List<String> acceptAddressList = new ArrayList<>();
            if (StringUtils.isNotBlank(emailList)) {
                acceptAddressList = Arrays.asList(emailList.split(","));
            } else {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请配置收件人邮箱！");
            }
            //邮件发送内容封装
            GenerateExcelSendEmailVo generateExcelSendEmailVo = new GenerateExcelSendEmailVo();
            generateExcelSendEmailVo.setEmailTitle("商品库存不足预警");
            //如果参数不为空 则填充邮件内容
            if (StringUtils.isNotBlank(s)) {
                generateExcelSendEmailVo.setEmailContent("（"+s+"）邮件导出为上架且未删除的正常商品库存预警信息，详情见附件！");
            }else {
                generateExcelSendEmailVo.setEmailContent("邮件导出为上架且未删除的正常商品库存预警信息，详情见附件！");
            }
            generateExcelSendEmailVo.setFileName("商品库存-" + DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_5) +".xls");
            if (CollectionUtils.isNotEmpty(acceptAddressList)) {
                generateExcelSendEmailVo.setAcceptAddressList(acceptAddressList);
            } else {
                throw  new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请配置收件人邮箱");
            }
            //输出文件
            ByteArrayOutputStream byteArrayOutputStream = exportToByteArrayOutputStream(sendGoodsEmails);
            generateExcelSendEmailVo.setOut(byteArrayOutputStream);
            //发送邮件
            sendEmailUtil.sendEmail(KsBeanUtil.convert(config,EmailConfigVO.class),generateExcelSendEmailVo);
            //延迟1分钟结束任务；防止异步发送邮件未完成，定时任务已结束导致邮件投递不成功。
            Thread.sleep(60*1000);
        }

        log.info("发送商品库存不足预警邮件任务执行结束：{}",LocalDateTime.now());
        XxlJobLogger.log("发送商品库存不足预警邮件任务执行结束：" + LocalDateTime.now());
        return SUCCESS;
    }


    /**
     * 文件输出流
     * @param sendGoodsEmails
     * @return
     * @throws Exception
     */
    public ByteArrayOutputStream exportToByteArrayOutputStream(List<GoodsInfoVO> sendGoodsEmails) throws Exception {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("erp编码", new SpelColumnRender<GoodsInfoVO>("erpGoodsInfoNo")),
                new Column("sku编码", new SpelColumnRender<GoodsInfoVO>("goodsInfoNo")),
                new Column("商品名称", new SpelColumnRender<GoodsInfoVO>("goodsInfoName")),
                new Column("分类名称", new SpelColumnRender<GoodsInfoVO>("cateName")),
                new Column("库存数量", new SpelColumnRender<GoodsInfoVO>("stock"))
        };
        List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
        columns = columnList.toArray(new Column[0]);
        excelHelper.addSheet("商品库存预警", columns, sendGoodsEmails);

        HSSFWorkbook hssfWorkbook = excelHelper.getWork();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        hssfWorkbook.write(byteArrayOutputStream);
        return byteArrayOutputStream;
    }
}
