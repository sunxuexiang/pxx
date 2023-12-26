package com.wanmi.sbc.customer.contract.service;

import com.fadada.sdk.base.model.req.*;
import com.fadada.sdk.extra.model.req.PushShortUrlSmsParams;
import com.fadada.sdk.verify.model.req.ApplyCertParams;
import com.fadada.sdk.verify.model.req.CompanyVerifyUrlParams;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.wanmi.sbc.common.base.BaseQueryResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.contract.ManagerContractProviderTo;
import com.wanmi.sbc.customer.api.request.contract.BelugaMallContractFindRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.BelugaInfoContractFindRequest;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractRequest;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractSaveRequest;
import com.wanmi.sbc.customer.api.response.fadada.BelugaMallContractPageResponese;
import com.wanmi.sbc.customer.api.response.fadada.BelugaMallContractResponese;
import com.wanmi.sbc.customer.api.response.fadada.FadadaParamsResponese;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import com.wanmi.sbc.customer.contract.model.root.BelugaContractInfo;
import com.wanmi.sbc.customer.contract.model.root.BelugaMallContract;
import com.wanmi.sbc.customer.contract.repository.BelugaMallContractRepository;
import com.wanmi.sbc.customer.contract.repository.BelugaMallInfoRepository;
import com.wanmi.sbc.customer.util.FadadaUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BelugaMallContractService {
    @Autowired
    private BelugaMallContractRepository belugaMallContractRepository;
    @Autowired
    private ManagerContractProviderTo managerContractProviderTo;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private BelugaMallInfoRepository belugaMallInfoRepository;

    @Value("${fadada.version}")
    private String v;
    @Value("${fadada.app.id}")
    private String appId;
    @Value("${fadada.app.secret}")
    private String appSecret;
    @Value("${fadada.url}")
    private String fadadaUrl;
    @Value("${auto.sign}")
    private String autoSign;
    @Value("${beluga.mall.register}")
    private String returnRegister;
    @Value("${beluga.mall.sign.url}")
    private  String return_url ;

    private static Integer NO_START = 0;
    private static Integer WALLET_SIGN = 9999;
    private static Integer OVER_SIGN = 3000;
    private static List<String> centerList = Arrays.asList("大众物流","配送服务合同","（2023版）","中国建设银行“惠市宝—对公专业结算综合服务平台”加入","甲方（委托方）","乙方 (承运商)","市场方/参与分账方授权书","结算账户使用授权书");

    public BaseResponse saveFristBelugaMallContract (BelugaMallContractSaveRequest request) {
        log.info("保存承运商基本信息========={}",request.toString());
        BelugaContractInfo belugaContractInfo = new BelugaContractInfo();
        KsBeanUtil.copyProperties(request,belugaContractInfo);
        belugaContractInfo.setStatus(NO_START);
        belugaContractInfo.setInfoId(generateMerchantNumber());
        //保存承运商基本信息
        return BaseResponse.success(belugaMallInfoRepository.saveAndFlush(belugaContractInfo));
    }

    public BaseResponse findBelugaInfo(BelugaInfoContractFindRequest request) {

        return BaseResponse.success(belugaMallInfoRepository.findByCreditCode(request.getCreditCode()));
    }

    // 添加承运商物流
    public BaseResponse<FadadaParamsResponese> addBelugaMallContract(BelugaMallContractRequest belugaMallContractRequest) {
        log.info("添加合同开始=========={}",belugaMallContractRequest.toString());
        BelugaMallContract belugaMallContract = new BelugaMallContract();
        KsBeanUtil.copyProperties(belugaMallContractRequest,belugaMallContract);
        // 查询当前合同模版
        // 查询现在开启的承运商模版
        ContractUpdateRequest contractUpdateRequest = new ContractUpdateRequest();
        contractUpdateRequest.setIsPerson(3);
        String url = null;
        List<UploadContractResponese> context1 = managerContractProviderTo.seachIsPersonContract(contractUpdateRequest).getContext();
        if (CollectionUtils.isNotEmpty(context1)) {
            if (StringUtils.isNotEmpty(context1.get(0).getContractUrl())) {
                url = context1.get(0).getContractUrl();
            }
        }
        // 查询当前用户合同信息
        BelugaMallContract byBelugaUser = belugaMallContractRepository.findByUnifiedCreditCodeOfSettlement(belugaMallContract.getPhoneNumber());
        // 查询当前用户的基本信息
        BelugaContractInfo byCreditCode = belugaMallInfoRepository.findByCreditCode(belugaMallContract.getUnifiedCreditCodeOfSettlement());
        if (StringUtils.isEmpty(url) || byBelugaUser==null || byCreditCode == null) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "未找到可使用合同");
        }

        String tempFilePath = "temp_template.docx";
        String uuid = generatorService.generate("UUID");
        String pdfFilePath = uuid+".pdf";
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream out = new FileOutputStream(tempFilePath)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }

            // 使用Apache POI处理Word文档
            String outputFilePath = processWordDocument(belugaMallContractRequest, tempFilePath);

            // word文档转成PDF
            convertDocxToPdf(outputFilePath,pdfFilePath,belugaMallContractRequest);

            File file = new File(pdfFilePath);
//            // 上传合同到法大大
            uploadContract(file,uuid);

            //保存到自己的数据库
            // 返回法大大链接
            //注册法大大平台账户
            RegisterAccountParams params = new RegisterAccountParams();
            params.setAccountType("2");
            params.setOpenId(belugaMallContractRequest.getPhoneNumber());

            CompanyVerifyUrlParams companyVerifyUrlParams = new CompanyVerifyUrlParams();
            // 个人信息
//            CompanyVerifyUrlParams.LegalInfo legalInfo = new CompanyVerifyUrlParams.LegalInfo();
//            legalInfo.setLegalName(belugaMallContractRequest.getPartyBLegalRepresentative());
//            legalInfo.setLegalId(belugaMallContractRequest.getIdNumber());
//            legalInfo.setLegalMobile(belugaMallContractRequest.getPartyBPhone());
//            legalInfo.setLegalIdFrontPath(belugaMallContractRequest.getIdCradUrl());
//            companyVerifyUrlParams.setLegalInfo(legalInfo);
            // 企业信息
//            CompanyVerifyUrlParams.CompanyInfo companyInfo = new CompanyVerifyUrlParams.CompanyInfo();
//            companyInfo.setCompanyName(belugaMallContractRequest.getCompanyName());
//            companyInfo.setCreditNo(belugaMallContractRequest.getCreditCode());
//            companyInfo.setCreditImagePath(belugaMallContractRequest.getBusinessUrl());
//            companyVerifyUrlParams.setCompanyInfo(companyInfo);
            // TODO 缺少一个回调地址
            companyVerifyUrlParams.setReturnUrl(returnRegister);
            companyVerifyUrlParams.setMVerifiedWay("4");
            // 企业身份法人
//            companyVerifyUrlParams.setCompanyPrincipalType("1");
//            companyVerifyUrlParams.setPageModify("1");
//            companyVerifyUrlParams.setOption("modify");
            FadadaUtil bulider = new FadadaUtil.Bulider().initBaseClient(appId,appSecret,v,fadadaUrl)
                    .setParam(params)
                    .initCustomer()
                    .setParam(companyVerifyUrlParams)
                    .initVerifyClient(appId,appSecret,v,fadadaUrl)
                    .initCompanyVerifyUrl()
                    .bulider();
            String customerId = bulider.getCustomerId();
            String transactionNo = bulider.getTransactionNo();
            BelugaMallContract belugaMallContract1 = new BelugaMallContract();
            KsBeanUtil.copyProperties(belugaMallContractRequest,belugaMallContract1);
            belugaMallContract1.setBelugaMallId(generateMerchantNumber());
            if (byBelugaUser != null ) {
                belugaMallContract1.setBelugaMallId(byBelugaUser.getBelugaMallId());
                belugaMallContract1.setInfoId(byCreditCode.getInfoId());
            }
            log.info("保存协议信息========={}",belugaMallContract1.toString());
            belugaMallContractRepository.saveAndFlush(belugaMallContract1);
            BelugaContractInfo belugaContractInfo = belugaMallInfoRepository.findById(byCreditCode.getInfoId()).orElse(null);
            belugaContractInfo.setStatus(WALLET_SIGN);
            belugaContractInfo.setCustomerId(customerId);
            belugaContractInfo.setContractId(uuid);
            belugaContractInfo.setTransactionNo(transactionNo);
            log.info("保存承运商基本信息========{}",belugaContractInfo.toString());
            belugaMallInfoRepository.saveAndFlush(belugaContractInfo);
//             删除xml,pdf,docx文件
            deleteFile(tempFilePath);
            deleteFile(pdfFilePath);
            return BaseResponse.success(new FadadaParamsResponese(customerId,transactionNo,bulider.getCompanyUrl()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateMerchantNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("BJ-");

        for (int i = 0; i < 7; i++) {
            int digit;
            do {
                digit = random.nextInt(10);
            } while (digit == 4); // Exclude digit 4
            sb.append(digit);
        }
        return sb.toString();
    }
    public BaseResponse returnRegister (String companyName,
                                        String transactionNo,
                                        String authenticationType,
                                        String status,String sign) throws SbcRuntimeException {

        try{
            log.info("法大大验证完成同步回调接口========={},{},{},{},{}",companyName,transactionNo,authenticationType,status,sign);
            BelugaContractInfo byTransactionNo = belugaMallInfoRepository.findByTransactionNo(transactionNo);
            if (byTransactionNo == null) {
                return BaseResponse.FAILED();
            }
            // 解密公司名称
            companyName = URLDecoder.decode(companyName, "UTF-8");
            if (status.equals("4")) {
                // 绑定实名信息
                ApplyCertParams params = new ApplyCertParams();
                params.setCustomerId(byTransactionNo.getCustomerId());
                params.setVerifiedSerialNo(transactionNo);
                // 印章上传
                CustomSignatureParams customSignatureParams = new CustomSignatureParams();
                customSignatureParams.setContent(companyName);
                customSignatureParams.setCustomerId(byTransactionNo.getCustomerId());
                // 手动签署
                // 手动签署重新生成交易号
                String uuid = generatorService.generate("UUID");
                ExtSignParams extSignParams = new ExtSignParams();
                log.info("获取到的合同ID===={}",byTransactionNo.getContractId());
                extSignParams.setTransactionId(transactionNo);//平台自定义唯一交易号
                extSignParams.setContractId(byTransactionNo.getContractId());//此处传入调用上传或填充合同接口成功时定义的合同编号
                extSignParams.setCustomerId(byTransactionNo.getCustomerId());//此处传入认证成功后成功绑定实名信息的客户编号
                extSignParams.setDocTitle(companyName);
                extSignParams.setPositionType("0");//0-关键字（默认）1-坐标
                extSignParams.setSignKeyword("授权单位盖章");
                extSignParams.setReturnUrl(return_url);
                extSignParams.setKeywordStrategy("0");//0-所有关键字签章 （默认） 1-第一个关键字签章 2-最后一个关键字签章

                // 发送短信
                PushShortUrlSmsParams pushShortUrlSmsParams = new PushShortUrlSmsParams();
                log.info("查询用户员工编号======={}",byTransactionNo.getLegalName());
                String employeeMobile = byTransactionNo.getLegalPhone();
                pushShortUrlSmsParams.setMobile(employeeMobile);

                ExtSignAutoParams signAutoParams = new ExtSignAutoParams();
                signAutoParams.setContractId(byTransactionNo.getContractId());
//                signAutoParams.setCustomerId(autoSign);
                signAutoParams.setCustomerId("0EB574226E5B623D32EB7A48E76193E3");
                signAutoParams.setTransactionId(uuid);
                signAutoParams.setPositionType("0");
                signAutoParams.setDocTitle("入驻商家签约条款");
                signAutoParams.setSignKeyword("甲方（公章）");
                extSignParams.setKeywordStrategy("0");
                FadadaUtil bulider = new FadadaUtil.Bulider()
                        .initVerifyClient(appId,appSecret,v,fadadaUrl)
                        .setParam(params)
                        // 绑定实名信息
                        .initApplyCert()
                        .initBaseClient(appId,appSecret,v,fadadaUrl)
                        // 印章上传
                        .setParam(customSignatureParams)
                        .initBaseClient(appId,appSecret,v,fadadaUrl)
                        .initSignature()
                        //自动签署
                        .setParam(signAutoParams)
                        .autoExtSign()
                        // 手动签署
                        .setParam(extSignParams)
                        .initExtSign()
                        //生成短链
                        .initExtraClient(appId,appSecret,v,fadadaUrl)
                        .shortUrl()
                        //发送信息
                        .setParam(pushShortUrlSmsParams)
                        .pushShortUrlSms(appSecret)
                        .bulider();
                //发送短信
                byTransactionNo.setStatus(WALLET_SIGN);
                belugaMallInfoRepository.saveAndFlush(byTransactionNo);
                log.info("用户签署合同====={},合同ID=========={}",byTransactionNo.getLegalName(),byTransactionNo.getTransactionNo());
            }
            return BaseResponse.SUCCESSFUL();
        }catch (SbcRuntimeException  e) {
            throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "签署后异步通知接口")
    public BaseResponse astcExtSign (String transaction_id,String result_code,String result_desc
            ,String download_url,String viewpdf_url) {
        log.info("进入签署后接口异步回调通知====={},{},{},{},{}",transaction_id,result_code,result_desc,download_url,viewpdf_url);
        BelugaContractInfo belugaContractInfo = belugaMallInfoRepository.findByTransactionNo(transaction_id);
        if (null == belugaContractInfo) {
            return BaseResponse.FAILED();
        }

        download_url = download_url.replace("&amp;", "&").replace("%26", "&");
        viewpdf_url = viewpdf_url.replace("&amp;", "&").replace("%26", "&");

        belugaContractInfo.setStatus(Integer.valueOf(result_code));
        belugaContractInfo.setContractUrl(download_url+","+viewpdf_url);
        belugaMallInfoRepository.saveAndFlush(belugaContractInfo);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "查询签约合同列表")
    public BaseResponse<BelugaMallContractPageResponese> findBelugaMallContract(BelugaMallContractFindRequest request) {
        log.info("查询合同列表开始========={}",request.toString());
        BelugaMallContractPageResponese belugaMallContractPageResponese = new BelugaMallContractPageResponese();
        Page<BelugaMallContract> all = belugaMallContractRepository.findAll(findRequest(request), request.getPageable());
        BaseQueryResponse<BelugaMallContract> baseQueryResponse = new BaseQueryResponse<>(all);
        List<BelugaMallContractResponese> collect = baseQueryResponse.getData().stream().map(beluga -> {
            BelugaMallContractResponese contractResponese = KsBeanUtil.convert(beluga, BelugaMallContractResponese.class);
            return contractResponese;
        }).collect(Collectors.toList());
        belugaMallContractPageResponese.setPageVo(new MicroServicePage<>(collect,request.getPageable(),all.getTotalElements()));
        return BaseResponse.success(belugaMallContractPageResponese);
    }

    @ApiOperation(value = "根据交易号查询当个承运商")
    public BaseResponse<BelugaMallContractResponese> findBylugaMallContractByTra(BelugaMallContractFindRequest belugaMallContractFindRequest) {
        BelugaMallContract byTransactionNo = belugaMallContractRepository.findByTransactionNo(belugaMallContractFindRequest.getTransactionId());
        BelugaMallContractResponese belugaMallContractResponese = new BelugaMallContractResponese();
        KsBeanUtil.copyProperties(byTransactionNo,belugaMallContractResponese);
        return BaseResponse.success(belugaMallContractResponese);
    }

    private static Specification<BelugaMallContract> findRequest(final BelugaMallContractFindRequest request){
        return (root,query,criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (StringUtils.isNotEmpty(request.getSupplierName()) ) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.like(root.get("supplierName"),"%" + request.getSupplierName() + "%"));
            }
            if (StringUtils.isNotEmpty(request.getContractNo())) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("userContractId"),request.getContractNo()));
            }
            if (request.getStatus() == 0) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.or(root.get("status").in("0","9999")));
            } else if (request.getStatus() == 1){
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("status"),"3000"));
            }
            if (request.getBeginTime() != null && request.getEndTime() == null) {
                LocalDateTime startOfDay = request.getBeginTime().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createTime"), startOfDay, endOfDay));
            }
            if (request.getBeginTime() != null && request.getEndTime() != null) {
                LocalDateTime startOfDay = request.getBeginTime().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = request.getEndTime().toLocalDate().atStartOfDay();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createTime"), startOfDay, endOfDay));
            }
            if (request.getEndTime() == null && request.getEndTime() != null) {
                LocalDateTime endOfDay = request.getEndTime().toLocalDate().atStartOfDay();
                LocalDateTime startOfDay = endOfDay.plusDays(1).minusNanos(1);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createTime"), startOfDay, endOfDay));
            }
            query.orderBy(criteriaBuilder.desc(root.get("createTime")));
            return predicate;
        };
    }


    private void uploadContract(File file,String uuid) {

        UploadDocsParams uploadDocsParams = new UploadDocsParams();
        uploadDocsParams.setContractId(uuid);
        uploadDocsParams.setDocTitle(file.getName());
        uploadDocsParams.setFile(file);
        uploadDocsParams.setDocType(".pdf");

        new FadadaUtil.Bulider()
                .initBaseClient(appId,appSecret,v,fadadaUrl)
                .setParam(uploadDocsParams)
                .initUploadDocs()
                .bulider();
    }

    public static void convertDocxToPdf(String docxFilePath, String pdfFilePath,BelugaMallContractRequest belugaMallContractRequest) throws Exception {
        try (FileInputStream fis = new FileInputStream(docxFilePath)) {
            XWPFDocument wordDoc = new XWPFDocument(fis);

            Document pdfDoc = new Document(PageSize.A4, 15, 15, 15, 15);
            PdfWriter.getInstance(pdfDoc, new FileOutputStream(pdfFilePath));
            pdfDoc.open();

            // 设置字体为中文字体
            BaseFont baseFont = BaseFont.createFont("SimHei.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font chineseFont = new Font(baseFont);

            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font underlineFont = new Font(baseFont, 12, Font.UNDERLINE);

            for (XWPFParagraph paragraph : wordDoc.getParagraphs()) {
                // 将Word文档内容逐段复制到PDF中，使用中文字体
                String text = paragraph.getText();
                Paragraph elements = new Paragraph();
                int startIndex = 0;

                int index = text.indexOf("分割线");
                if (index != -1) {
                    // 添加"分割线"之前的文本
                    elements.add(new Chunk(text.substring(0, index), chineseFont));
                    // 添加分割线
                    LineSeparator separator = new LineSeparator();
                    separator.setLineColor(BaseColor.BLACK);
                    separator.setPercentage(100);
                    separator.setAlignment(Element.ALIGN_CENTER);
                    elements.add(separator);
                    // 更新索引
                    startIndex = index + 3;
                    // 添加剩余的文本，跳过"分割线"之后的部分
                    elements.add(new Chunk(text.substring(startIndex), chineseFont));
                }
                int braceStartIndex = text.indexOf('{', startIndex);
                int braceEndIndex = text.indexOf('}', braceStartIndex + 1);
                while (braceStartIndex != -1 && braceEndIndex != -1) {
                    // 添加花括号之前的文本
                    elements.add(new Chunk(text.substring(startIndex, braceStartIndex), chineseFont));

                    // 添加花括号内的文本，并设置下划线
                    String textWithinBraces = text.substring(braceStartIndex + 1, braceEndIndex);
                    elements.add(new Chunk(textWithinBraces, underlineFont));

                    // 更新索引
                    startIndex = braceEndIndex + 1;
                    braceStartIndex = text.indexOf('{', startIndex);
                    braceEndIndex = text.indexOf('}', braceStartIndex + 1);
                }

                if (centerList.contains(text)) {
                    elements.setAlignment(Element.ALIGN_CENTER); // 设置对齐方式
                } else if (text.equals("乙方（授权单位盖章）：") || text.equals("传真：")) {
                    elements.add(new Chunk("\n\n\n\n\n\n\n", chineseFont));
                } else if (text.equals("附件5-2：") || text.equals("附件7：")) {
                    pdfDoc.add(Chunk.NEXTPAGE);
                } else if (text.equals("合同编号：")){
                    elements.setAlignment(Element.ALIGN_RIGHT);
                }else {
                    elements.setAlignment(Element.ALIGN_LEFT); // 设置对齐方式
                }
                // 添加剩余的文本
                elements.add(new Chunk(text.substring(startIndex), chineseFont));
                elements.setSpacingAfter(1); // 设置段后间距
                elements.setIndentationLeft(1); // 设置左缩进
                elements.setIndentationRight(1); // 设置右缩进
                pdfDoc.add(elements);
                // 处理段落中的图片
                for (XWPFRun run : paragraph.getRuns()) {
                    for (XWPFPicture picture : run.getEmbeddedPictures()) {
                        // 获取图片数据
                        byte[] imageData = picture.getPictureData().getData();

                        // 将图片插入到 PDF 文档
                        Image pdfImage = Image.getInstance(imageData);
                        pdfImage.setAlignment(Image.ALIGN_LEFT); // 设置图片对齐方式为右对齐
                        pdfImage.scaleToFit(120, 80); // 调整图片大小适合页面
                        pdfDoc.add(pdfImage);
                    }
                }
            }
            pdfDoc.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
    private static void deleteFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("文件 " + filePath + " 已成功删除！");
            } else {
                System.out.println("无法删除文件 " + filePath + "，请检查文件权限或文件是否正在使用。");
            }
        } else {
            System.out.println("文件 " + filePath + " 不存在，无法执行删除操作。");
        }
    }
    private static String processWordDocument(BelugaMallContractRequest belugaMallContractRequest, String filePath) throws IOException {
        // 下载图片并读取为字节数组
        byte[] imageData = downloadImageFromURL(belugaMallContractRequest.getSignImage());
        try (FileInputStream fis = new FileInputStream(filePath)) {
            XWPFDocument doc = new XWPFDocument(fis);

            Map<String,String> fieldsMap = objectToMap(belugaMallContractRequest);

            // 替换必填字段的值
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        if (text.equals("{{signImage}}")) {
                            text = text.replace("{{signImage}}", "");
                            run.setText(text,0);
                            if (imageData != null) {
                                // 添加图片到 Word 文档中
                                int width = 80; // 设置图片宽度
                                int height = 60; // 设置图片高度
                                // 创建图片
                                run.addPicture(new ByteArrayInputStream(imageData), XWPFDocument.PICTURE_TYPE_PNG, "image.png", Units.toEMU(width), Units.toEMU(height));
                            }
                            paragraph.setAlignment(ParagraphAlignment.RIGHT);
                        } else {
                            for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
                                text = text.replace(entry.getKey(), entry.getValue());
                            }
                            run.setText(text, 0);
                        }
                    }
                }
            }

            // 保存填写后的文档
            String outputFilePath = "filled_document.docx";
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                doc.write(fos);
            }
            return outputFilePath;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public static Map<String, String> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = field.get(obj);
            String value = String.valueOf(fieldValue);
            if (fieldName.equals("signTime") || fieldName.equals("periodStart") || fieldName.equals("periodEnd")) {
                value = formatDate(value);
            }

            map.put("{{"+fieldName+"}}", value);
        }

        return map;
    }
    public static String formatDate(String dateStr) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy年MM月dd日");
            Date date = sdfInput.parse(dateStr);
            return sdfOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    // 下载图片并转换为字节数组
    private static byte[] downloadImageFromURL(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = conn.getInputStream()) {
                return IOUtils.toByteArray(inputStream);
            }
        }

        return null;
    }
}
