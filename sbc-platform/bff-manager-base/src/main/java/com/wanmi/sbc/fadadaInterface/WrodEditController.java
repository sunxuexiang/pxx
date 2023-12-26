package com.wanmi.sbc.fadadaInterface;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.fadada.sdk.base.model.req.RegisterAccountParams;
import com.fadada.sdk.base.model.req.UploadDocsParams;
import com.fadada.sdk.verify.model.req.CompanyVerifyUrlParams;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.contract.CustomerContractProviderTo;
import com.wanmi.sbc.customer.api.provider.contract.ManagerContractProviderTo;
import com.wanmi.sbc.customer.api.provider.employeecontract.EmployeeContractProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractSaveRequest;
import com.wanmi.sbc.customer.api.request.fadada.WordParamsRequest;
import com.wanmi.sbc.customer.api.request.store.StoreSupplierPersonIdEditRequest;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractResponese;
import com.wanmi.sbc.customer.api.response.fadada.FadadaParamsResponese;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import com.wanmi.sbc.util.Coordinate;
import com.wanmi.sbc.util.CustomPDFTextStripper;
import com.wanmi.sbc.util.FadadaUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;


@Api(tags = "FadadaInterfaceController", description = "合同编辑 API")
@RestController("WrodEditController")
@RequestMapping("/wordedit")
@Slf4j
public class WrodEditController {
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private CustomerContractProviderTo customerContractProviderTo;
    @Autowired
    private EmployeeContractProvider employeeContractProvider;
    //回调地址
    @Value("${return.register.url}")
    private String returnRegister;
    private static final String REDIS_KEY = "fadada_";
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${fadada.version}")
    private String v;
    @Value("${fadada.app.id}")
    private String appId;
    @Value("${fadada.app.secret}")
    private String appSecret;
    @Value("${fadada.url}")
    private String fadadaUrl;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @Autowired
    private ManagerContractProviderTo managerContractProviderTo;
    @ApiOperation(value = "编辑WROD文档")
    @PostMapping(value = "/eidtWord")
    @Transactional
    @LcnTransaction
    public BaseResponse eidtWord(@RequestBody WordParamsRequest wordParamsRequest, HttpServletRequest request) throws Exception{
        log.info("编辑wrod文档========={}",wordParamsRequest.toString());
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();

        // 获取合同模版
//        String url = "https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202308041952557427.docx";
        String url = null;
        // 查询现在开启的个人或者是企业可用模版
        ContractUpdateRequest contractUpdateRequest = new ContractUpdateRequest();
        contractUpdateRequest.setIsPerson(wordParamsRequest.getIsPerson());
        List<UploadContractResponese> context1 = managerContractProviderTo.seachIsPersonContract(contractUpdateRequest).getContext();
        if (CollectionUtils.isNotEmpty(context1)) {
            if (StringUtils.isNotEmpty(context1.get(0).getContractUrl())) {
                url = context1.get(0).getContractUrl();
            }
        }

        if (StringUtils.isEmpty(url)) {
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
            String outputFilePath = processWordDocument(wordParamsRequest, tempFilePath);

            // word文档转成PDF
            convertDocxToPdf(outputFilePath,pdfFilePath,wordParamsRequest);

            File file = new File(pdfFilePath);
//            // 上传合同到法大大
            uploadContract(file,uuid);

//             保存到自己的数据库
            //保存到自己的数据库
            wordParamsRequest.setContractId(uuid);
            wordParamsRequest.setEmployeeId(employeeId);
            wordParamsRequest.setTransactionId(uuid);
            customerContractProviderTo.save(wordParamsRequest);
            // 返回法大大链接
            //注册法大大平台账户
            RegisterAccountParams params = new RegisterAccountParams();
            params.setAccountType(wordParamsRequest.getAccountType());
            params.setOpenId(employeeId);

            CompanyVerifyUrlParams companyVerifyUrlParams = new CompanyVerifyUrlParams();
            // 个人信息
//            CompanyVerifyUrlParams.LegalInfo legalInfo = new CompanyVerifyUrlParams.LegalInfo();
//            legalInfo.setLegalName(wordParamsRequest.getLegalPersonName());
//            legalInfo.setLegalId(wordParamsRequest.getLegalIdCardNo());
//            legalInfo.setLegalMobile(wordParamsRequest.getAccountName());
//            legalInfo.setLegalIdFrontPath(wordParamsRequest.getIdCradUrl());
//            companyVerifyUrlParams.setLegalInfo(legalInfo);
//            // 企业信息
//            CompanyVerifyUrlParams.CompanyInfo companyInfo = new CompanyVerifyUrlParams.CompanyInfo();
//            companyInfo.setCompanyName(wordParamsRequest.getCompanyName());
//            companyInfo.setCreditNo(wordParamsRequest.getCreditCode());
//            companyInfo.setCreditImagePath(wordParamsRequest.getBusinessUrl());
//            companyVerifyUrlParams.setCompanyInfo(companyInfo);
            companyVerifyUrlParams.setReturnUrl(returnRegister);
            companyVerifyUrlParams.setMVerifiedWay("4");
            FadadaUtil bulider = new FadadaUtil.Bulider().initBaseClient(appId,appSecret,v,fadadaUrl)
                    .setParam(params)
                    .initCustomer()
                    .setParam(companyVerifyUrlParams)
                    .initVerifyClient(appId,appSecret,v,fadadaUrl)
                    .initCompanyVerifyUrl()
                    .bulider();
            String customerId = bulider.getCustomerId();
            String transactionNo = bulider.getTransactionNo();
            log.info("存入redis数据===={},customerId:{}",REDIS_KEY+employeeId,customerId);
            log.info("存入redis数据===={},transactionNo:{}",REDIS_KEY+employeeId,customerId);
            redisTemplate.opsForHash().put(REDIS_KEY+employeeId, "customerId", customerId);
            redisTemplate.opsForHash().put(REDIS_KEY+employeeId, "transactionNo", transactionNo);
            redisTemplate.opsForHash().put(REDIS_KEY+transactionNo, "employeeId", employeeId);
            // 刷新用户合同表保存的数据
            EmployeeContractResponese context = employeeContractProvider.findByEmployeeId(employeeId).getContext();
            context.setContractId(uuid);
            context.setTransactionNo(transactionNo);
            EmployeeContractSaveRequest employeeContractSaveRequest = new EmployeeContractSaveRequest();
            KsBeanUtil.copyProperties(context,employeeContractSaveRequest);
            employeeContractProvider.save(employeeContractSaveRequest);

//             删除xml,pdf,docx文件
            deleteFile(tempFilePath);
            deleteFile(pdfFilePath);
            return BaseResponse.success(new FadadaParamsResponese(customerId,transactionNo,bulider.getCompanyUrl()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        operateLogMQUtil.convertAndSend("合同编辑", "编辑WROD文档", "操作成功");
        return null;
    }
    @ApiOperation(value = "查看客户签署信息")
    @PostMapping(value = "/findByEmployeeId")
    @Transactional
    @LcnTransaction
    public BaseResponse<WordParamsRequest> findByEmployeeId ( HttpServletRequest request) {
        log.info("查看客户签署信息=============={}",request.toString());
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        WordParamsRequest wordParamsRequest = new WordParamsRequest();
        wordParamsRequest.setEmployeeId(employeeId);
        return customerContractProviderTo.findByEmployeeId(wordParamsRequest);
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

    private static String processWordDocument(WordParamsRequest wordParamsRequest,String filePath) throws IOException {
        // 下载图片并读取为字节数组
        byte[] imageData = downloadImageFromURL(wordParamsRequest.getSignImage());
        try (FileInputStream fis = new FileInputStream(filePath)) {
            XWPFDocument doc = new XWPFDocument(fis);

            Map<String,String> fieldsMap = objectToMap(wordParamsRequest);

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

    // 将 BufferedImage 转换为字节数组
    private static byte[] toByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    // 根据图片文件的后缀名获取图片类型
    private static int getImageType(String imagePath) {
        return XWPFDocument.PICTURE_TYPE_PNG; // 根据您的图片格式调整返回值
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

    public static void convertDocxToPdf(String docxFilePath, String pdfFilePath,WordParamsRequest wordParamsRequest) throws Exception {
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

                if (text.equals("入驻商家签约条款") || text.equals("中国建设银行“惠市宝—对公专业结算综合服务平台”加入申请书")
                        || text.equals("加入申请书") || text.equals("（企事业单位版）")
                        || text.equals("市场方/参与分账方授权书")
                        || text.equals("结算账户使用授权书")) {
                    elements.setAlignment(Element.ALIGN_CENTER); // 设置对齐方式
                } else if (text.equals("乙方（授权单位盖章）：")) {
                    elements.add(new Chunk("\n\n\n\n\n\n\n", chineseFont));
                } else if (text.equals("附件5-2：") || text.equals("附件7：")) {
                    pdfDoc.add(Chunk.NEXTPAGE);
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

}

