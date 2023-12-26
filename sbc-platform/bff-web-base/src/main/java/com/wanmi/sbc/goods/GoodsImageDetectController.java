package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.aliyun.imagesearch20210501.models.SearchByPicResponseBody;
import com.tencentcloudapi.tiia.v20190529.models.*;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.ImageDetectService;
import com.wanmi.sbc.common.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 商品分类Controller
 * Created by daiyitian on 17/4/12.
 */
@RestController
@RequestMapping("/goods")
@Slf4j
@Api(tags = "GoodsImageDetectController", description = "S2B web公用-商品图片识别")
public class GoodsImageDetectController {

    @Autowired
    private ImageDetectService imageDetectService;
    @Value("${tencentcloud.matchThreshold}")
    private String matchThreshold;
    @Value("${alicloud.matchThreshold}")
    private String alicoudMatchThreshold;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 商品图片识别
     */
    @ApiOperation(value = "商品图片识别")
    @RequestMapping(value = "/imageDetect", method = RequestMethod.POST)
    public BaseResponse<DetectProductBetaResponse> imageDetect(@RequestParam("file") MultipartFile file) throws IOException {
        // 校验文件格式
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(!".jpg".equals(fileSuffix) && !".jpeg".equals(fileSuffix)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR, "文件格式错误，请上传[jpg/jpeg]!");
        }
        CountDownLatch countDown = new CountDownLatch(3);

        /**
         * 阿里云的图片识别接口
         */
        AtomicReference<ProductInfo> productInfoFromAlibaba = new AtomicReference<>();
        executor.execute(()->{
            try{
                List<SearchByPicResponseBody.SearchByPicResponseBodyDataAuctions> searchByPicResponseBodyDataAuctions = imageDetectService.SearchByPic(file.getInputStream());
                log.info("imageDetect_5_阿里识图结果:{}", JSON.toJSONString(searchByPicResponseBodyDataAuctions));
                if(CollectionUtils.isNotEmpty(searchByPicResponseBodyDataAuctions)){
                    SearchByPicResponseBody.SearchByPicResponseBodyDataAuctions searchByPicResponse = searchByPicResponseBodyDataAuctions.get(0);
                    ProductInfo productInfo = new ProductInfo();
                    productInfo.setName(searchByPicResponse.getResult().title);
                    productInfo.setScore(searchByPicResponse.getRankScore());
                    productInfoFromAlibaba.set(productInfo);
                    log.info("imageDetect_5_识图结果:{}", JSON.toJSONString(productInfoFromAlibaba));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                countDown.countDown();
            }
        });

        // 压缩文件到 1M 以下，并转换为 base64
        AtomicReference<String> imageBase64 = new AtomicReference<>();
        long maxSize = 1024L * 1024L;
        if(file.getSize() <= maxSize){
            imageBase64.set(Base64.getEncoder().encodeToString(file.getBytes()));
        }else {
            String classPath = HttpUtil.getProjectRealPath();
            File tempFile = new File(classPath + UUID.randomUUID() + fileSuffix);

            Thumbnails.of(file.getInputStream()).scale(0.25f).outputQuality(0.1).toFile(tempFile);
            byte[] bytes = Files.readAllBytes(tempFile.toPath());
            imageBase64.set(Base64.getEncoder().encodeToString(bytes));
            tempFile.delete();
        }
        BufferedImage image = ImageIO.read(file.getInputStream());
        if(image.getHeight() > 2000 || image.getHeight() > 2000){
            File newfile = null;
            newfile = File.createTempFile(originalFilename, fileSuffix);    //创建文件
            file.transferTo(newfile);
            //删除
            newfile.deleteOnExit();
            String classPath = HttpUtil.getProjectRealPath();
            File tempFile = new File(classPath + UUID.randomUUID() + fileSuffix);
            resizeImage(newfile,tempFile,2000,2000,true);
            byte[] bytes = Files.readAllBytes(tempFile.toPath());
            imageBase64.set(Base64.getEncoder().encodeToString(bytes));
            tempFile.delete();
        }
        /**
         * 检索自建图片库（腾讯云）
         */
        AtomicReference<ProductInfo> productInfoFromSearchImage = new AtomicReference<>();
        executor.execute(()->{
            try{
                SearchImageRequest req = new SearchImageRequest();
                req.setGroupId("xiyayaOnline");
                req.setImageBase64("data:image/jpeg;base64," + imageBase64.get());
                req.setLimit(1L);
                req.setMatchThreshold(Long.valueOf(this.matchThreshold)); // 商品图像搜索2.0升级版：45。

                // 返回的resp是一个SearchImageResponse的实例，与请求对象对应
                SearchImageResponse resp = imageDetectService.SearchImage(req);
                ImageInfo[] ImageInfos = resp.getImageInfos();
                if(ImageInfos.length > 0){
                    ImageInfo imageInfo = ImageInfos[0];
                    ProductInfo productInfo=new ProductInfo();
                    productInfo.setName(imageInfo.getPicName());
                    productInfo.setScore(Float.valueOf(imageInfo.getScore()));
                    productInfoFromSearchImage.set(productInfo);
                }
                log.info("imageDetect_1_搜图所有结果:{}", JSON.toJSONString(ImageInfos));
            } finally{
                countDown.countDown();
            }
        });
        /**
         *
         * 商品识别-微信识物版接口（腾讯云）
         */
        AtomicReference<ProductInfo> productInfoFromTencent = new AtomicReference<>();
        executor.execute(()->{
            try{
                DetectProductBetaRequest request = new DetectProductBetaRequest();
                request.setImageBase64(imageBase64.get());

                // 返回识别结果
                DetectProductBetaResponse detectProductBetaResponse = imageDetectService.imageDetect(request);
                ProductInfo productInfo = detectProductBetaResponse.getProductInfo();
                productInfoFromTencent.set(productInfo);
                log.info("imageDetect_3_识图结果:{}", JSON.toJSONString(detectProductBetaResponse));
            } finally{
                countDown.countDown();
            }
        });

        try {
            countDown.await();
        } catch (InterruptedException e) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "网络繁忙请稍后再试!");
        }

        // 按照权重分数进行结果的排序，然后返回结果
        ProductInfo productInfo1 = productInfoFromSearchImage.get();
        Float aFloat = Optional.ofNullable(productInfo1).map(ProductInfo::getScore).orElse(0.0f);
        log.info("imageDetect_4_productInfo1:{}", JSON.toJSONString(productInfo1));

        // 阿里图片识别
        ProductInfo productInfo2 = productInfoFromAlibaba.get();
        float bFloat = Optional.ofNullable(productInfo2).map(ProductInfo::getScore).orElse(0.0f);
        bFloat = bFloat * 100;
        log.info("imageDetect_4_productInfo2:{}", JSON.toJSONString(productInfo2));

        // 腾讯图片识别
        ProductInfo productInfo3 = productInfoFromTencent.get();
        float cFloat = Optional.ofNullable(productInfo3).map(ProductInfo::getScore).orElse(0.0f);
        cFloat = cFloat * 100;
        log.info("imageDetect_4_productInfo3:{}", JSON.toJSONString(productInfo3));


        log.info("imageDetect_4_图片识别分数对比:{},{},{}",aFloat, bFloat, Float.compare(aFloat, bFloat));

        // 低于权重的话，返回空结果
        if(Float.compare(aFloat, Float.parseFloat(matchThreshold)) < 0 && Float.compare(bFloat, Float.parseFloat(alicoudMatchThreshold)) < 0){
            return BaseResponse.SUCCESSFUL();
        }

        // 搜图分数高
        if(Float.compare(aFloat, Float.parseFloat(matchThreshold)) > 0 && StringUtils.isNotBlank(Optional.ofNullable(productInfo1).map(ProductInfo::getName).orElse(null))){
            log.info("imageDetect_6_搜图分数高:{},{}",aFloat, Float.valueOf(matchThreshold));
            DetectProductBetaResponse detectProductBetaResponse = new DetectProductBetaResponse();
            detectProductBetaResponse.setProductInfo(productInfo1);
            return BaseResponse.success(detectProductBetaResponse);
        }

        // 阿里识别效果最佳
        if(Float.compare(bFloat, cFloat) > 0 && StringUtils.isNotBlank(Optional.ofNullable(productInfo2).map(ProductInfo::getName).orElse(null))){
            log.info("imageDetect_6_阿里识图分数高:{},{}",aFloat, Float.valueOf(matchThreshold));
            DetectProductBetaResponse detectProductBetaResponse = new DetectProductBetaResponse();
            detectProductBetaResponse.setProductInfo(productInfo2);
            return BaseResponse.success(detectProductBetaResponse);
        }

        log.info("imageDetect_6_腾讯识图分数高:{},{}",aFloat, Float.valueOf(matchThreshold));

        boolean notBlank = StringUtils.isNotBlank(Optional.ofNullable(productInfo3).map(ProductInfo::getName).orElse(null));
        if(notBlank){
            DetectProductBetaResponse detectProductBetaResponse = new DetectProductBetaResponse();
            detectProductBetaResponse.setProductInfo(productInfo3);
            return BaseResponse.success(detectProductBetaResponse);
        }

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 重新生成图片宽、高
     *
     * @param srcPath   图片路径
     * @param destPath  新生成的图片路径
     * @param newWith   新的宽度
     * @param newHeight 新的高度
     * @param forceSize 是否强制使用指定宽、高,false:会保持原图片宽高比例约束
     * @return
     * @throws IOException
     */
    public static void resizeImage(File srcPath, File destPath, int newWith, int newHeight, boolean forceSize) throws IOException {
        try {
            if (forceSize) {
                Thumbnails.of(srcPath).forceSize(newWith, newHeight).toFile(destPath);
            } else {
                Thumbnails.of(srcPath).width(newWith).height(newHeight).toFile(destPath);
            }
            log.info("图片修改成功！");
        } catch (Exception e) {
            log.error("图片修改尺寸失败:{}", e);
        }
    }
}
