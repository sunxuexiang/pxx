package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.provider.storeresource.StoreResourceQueryProvider;
import com.wanmi.sbc.setting.api.provider.storeresourcecate.StoreResourceCateQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemresource.SystemResourceQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemresource.SystemResourceSaveProvider;
import com.wanmi.sbc.setting.api.request.storeresource.StoreResourceListRequest;
import com.wanmi.sbc.setting.api.request.storeresourcecate.StoreResourceCateListRequest;
import com.wanmi.sbc.setting.api.request.systemresource.*;
import com.wanmi.sbc.setting.api.request.systemresourcecate.SystemResourceCateQueryRequest;
import com.wanmi.sbc.setting.api.response.storeresourcecate.StoreResourceCateListResponse;
import com.wanmi.sbc.setting.api.response.systemresource.SystemResourceModifyResponse;
import com.wanmi.sbc.setting.api.response.systemresource.SystemResourcePageResponse;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.setting.bean.vo.StoreResourceVO;
import com.wanmi.sbc.setting.bean.vo.SystemResourceVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 素材服务
 * Created by yinxianzhi on 2018/10/13.
 */
@Api(tags = "ResourceController", description = "素材服务 Api")
@RestController
@RequestMapping("/system")
public class ResourceController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);


    @Autowired
    private SystemResourceQueryProvider systemResourceQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private SystemResourceSaveProvider systemResourceSaveProvider;

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private StoreResourceCateQueryProvider StoreResourceCateQueryProvider;
    @Autowired
    private StoreResourceQueryProvider storeResourceQueryProvider;

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * 分页素材
     *
     * @param pageReq 素材参数
     * @return
     */
    @ApiOperation(value = "分页素材")
    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<SystemResourceVO>> page(@RequestBody @Valid SystemResourcePageRequest pageReq) {
        BaseResponse<SystemResourcePageResponse> response= systemResourceQueryProvider.page(pageReq);
        return BaseResponse.success(response.getContext().getSystemResourceVOPage());

    }

    /**
     * 导出素材成压缩包
     * */
    @ApiOperation(value= "运营端导出素材压缩包")
    @RequestMapping(value = "/resourceReport/{cateId}",method = RequestMethod.GET)
    public void resourceReport (@PathVariable Long cateId, HttpServletResponse response) {
        operateLogMQUtil.convertAndSend("素材服务", "导出素材压缩包", "导出素材压缩包：资源id" + (Objects.nonNull(cateId) ? cateId : ""));
        Map<Long, String> context = systemResourceQueryProvider.resourceReport(SystemResourceCateQueryRequest.builder().cateId(cateId).build()).getContext();
        logger.info("获取到处素材======={}",context.size());
        // 获取项目的工作目录
        Set<String> set = new HashSet<>();
        // 循环遍历所有节点
        context.entrySet().forEach(item->{
            String value = item.getValue();
            logger.info("获取节点数据=========={}",value);
            String workingDir = System.getProperty("user.dir") + File.separator;
            String[] split = value.split("/");
            for (int i=0;i<split.length;i++ ) {
                String[] split1 = split[i].split("-");
                String name = split1[0];
                String cateIdArr = split1[1];
                workingDir+=name + File.separator;
                if (i == 0) {
                    set.add(name);
                }
                logger.info("workingDir======{}",workingDir);
                File folder = new File(workingDir);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                downLoadImg(cateIdArr,folder);
            }
        });
        String workingDir = System.getProperty("user.dir") + File.separator;
        StringBuffer sb = new StringBuffer();
        set.forEach(item->{
            String zipFilePath = workingDir+item;
            sb.append(zipFilePath);
            try (FileOutputStream fos = new FileOutputStream(zipFilePath+".zip");
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                File sourceDirectory = new File(item);
                zipDirectory(sourceDirectory, sourceDirectory.getName(), zipOut);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (FileInputStream fis = new FileInputStream(zipFilePath+".zip")) {
                item = URLEncoder.encode(item, "UTF-8");
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename="+item+".zip"); // Change this to your desired file name

                OutputStream out = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        File file = new File(sb.toString());
        deleteFile(sb.toString()+".zip");
        deleteFolder(file);
    }


    /**
     * 导出素材成压缩包
     * */
    @ApiOperation(value= "商家端导出素材压缩包")
    @RequestMapping(value = "/storeResourceReport/{cateId}",method = RequestMethod.GET)
    public void storeResourceReport (@PathVariable Long cateId, HttpServletResponse response) {
        StoreResourceCateListRequest queryRequest = StoreResourceCateListRequest.builder()
                .storeId(commonUtil.getStoreId()).cateId(cateId).build();
        Map<Long, String> context = StoreResourceCateQueryProvider.resourceReport(queryRequest).getContext();
        logger.info("获取到处素材======={}",context.size());
        // 获取项目的工作目录
        Set<String> set = new HashSet<>();
        // 循环遍历所有节点
        context.entrySet().forEach(item->{
            String value = item.getValue();
            logger.info("获取节点数据=========={}",value);
            String workingDir = System.getProperty("user.dir") + File.separator;
            String[] split = value.split("/");
            for (int i=0;i<split.length;i++ ) {
                String[] split1 = split[i].split("-");
                String name = split1[0];
                String cateIdArr = split1[1];
                workingDir+=name + File.separator;
                if (i == 0) {
                    set.add(name);
                }
                logger.info("workingDir======{}",workingDir);
                File folder = new File(workingDir);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                downLoadStoreImg(cateIdArr,folder);
            }
        });
        String workingDir = System.getProperty("user.dir") + File.separator;
        StringBuffer sb = new StringBuffer();
        set.forEach(item->{
            String zipFilePath = workingDir+item;
            sb.append(zipFilePath);
            try (FileOutputStream fos = new FileOutputStream(zipFilePath+".zip");
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                File sourceDirectory = new File(item);
                zipDirectory(sourceDirectory, sourceDirectory.getName(), zipOut);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (FileInputStream fis = new FileInputStream(zipFilePath+".zip")) {
                item = URLEncoder.encode(item, "UTF-8");
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename="+item+".zip"); // Change this to your desired file name

                OutputStream out = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        File file = new File(sb.toString());
        deleteFile(sb.toString()+".zip");
        deleteFolder(file);

    }

    public void downLoadImg(String cateIdArr,File folder){
        List<SystemResourceVO> systemResourceVOList = systemResourceQueryProvider.reportList(SystemResourceListRequest.builder().cateId(Long.valueOf(cateIdArr)).resourceType(ResourceType.IMAGE).delFlag(DeleteFlag.NO).build()).getContext().getSystemResourceVOList();

        systemResourceVOList.forEach(photo -> {
            InputStream photoStream = null;
            try {
                String safeFileName = photo.getResourceName().replaceAll("[./()·]", "_");
                String compressedPhotoFilePath = folder.getPath() + File.separator + safeFileName+".jpg"; // Specify the compressed photo file path
                photoStream = new URL(photo.getArtworkUrl()).openStream();

                BufferedImage originalImage = ImageIO.read(photoStream);
                BufferedImage compressedImage = compressImage(originalImage, 0.5); // Adjust compression ratio as needed

                ImageIO.write(compressedImage, "jpg", new File(compressedPhotoFilePath));

            } catch (IOException e) {
                logger.info("图片导入异常======={}",e);
            }finally {
                try {
                    if (photoStream != null) {
                        photoStream.close();
                    }
                } catch (IOException e) {
                    logger.info("图片下载异常");
                }
            }
        });
    }

    public void downLoadStoreImg(String cateIdArr,File folder){

        List<StoreResourceVO> storeResourceVOList = storeResourceQueryProvider.reportListSource(StoreResourceListRequest.builder().cateId(Long.valueOf(cateIdArr)).resourceType(ResourceType.IMAGE).delFlag(DeleteFlag.NO).build()).getContext().getStoreResourceVOList();
        storeResourceVOList.forEach(photo -> {
            InputStream photoStream = null;
            try {
                String safeFileName = photo.getResourceName().replaceAll("[./()·]", "_");
                String compressedPhotoFilePath = folder.getPath() + File.separator + safeFileName+".jpg"; // Specify the compressed photo file path
                photoStream = new URL(photo.getArtworkUrl()).openStream();

                BufferedImage originalImage = ImageIO.read(photoStream);
                BufferedImage compressedImage = compressImage(originalImage, 0.5); // Adjust compression ratio as needed

                ImageIO.write(compressedImage, "jpg", new File(compressedPhotoFilePath));

            } catch (IOException e) {
                logger.info("图片导入异常======={}",e);
            }finally {
                try {
                    if (photoStream != null) {
                        photoStream.close();
                    }
                } catch (IOException e) {
                    logger.info("图片下载异常");
                }
            }
        });
    }

    public static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else if (file.isFile()) {
                        file.delete();
                    }
                }
            }
            folder.delete();
            logger.info("文件夹已成功删除！");
        } else {
            logger.info("文件夹不存在。");
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

    public static BufferedImage compressImage(BufferedImage image, double compressionRatio) {
        int newWidth = (int) (image.getWidth() * compressionRatio);
        int newHeight = (int) (image.getHeight() * compressionRatio);

        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    private  void zipDirectory(File directoryToZip, String zipEntryPath, ZipOutputStream zipOut) throws IOException {
        File[] files = directoryToZip.listFiles();
            try {
                for (File file : files) {
                    String entryName = zipEntryPath + File.separator + file.getName();
                    if (file.isDirectory()) {
                        zipOut.putNextEntry(new ZipEntry(entryName + "/"));
                        zipOut.closeEntry();
                        zipDirectory(file, entryName, zipOut);
                    } else {
                        FileInputStream fis = new FileInputStream(file);
                        zipOut.putNextEntry(new ZipEntry(entryName));

                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zipOut.write(bytes, 0, length);
                        }

                        fis.close();
                        zipOut.closeEntry();
                    }
                }
            } catch (Exception e ) {
                logger.info("下载图片异常========={}",e);
            }
    }
    /**
     * 编辑素材
     */
    @ApiOperation(value = "编辑素材")
    @RequestMapping(value = "/resource", method = RequestMethod.PUT)
    public BaseResponse<SystemResourceModifyResponse> modify(@RequestBody @Valid SystemResourceModifyRequest
                                                                     modifyReq) {
        operateLogMQUtil.convertAndSend("素材服务", "编辑素材", "编辑素材：素材资源ID" + (Objects.nonNull(modifyReq) ? modifyReq.getResourceId() : ""));
        modifyReq.setUpdateTime(LocalDateTime.now());
        return systemResourceSaveProvider.modify(modifyReq);

    }

    /**
     * 删除素材
     */
    @ApiOperation(value = "删除素材")
    @RequestMapping(value = "/resource", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody @Valid SystemResourceDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("素材服务", "删除素材", "删除素材");
        return systemResourceSaveProvider.deleteByIdList(delByIdListReq);
    }

    /**
     * 批量修改素材的分类
     */
    @ApiOperation(value = "批量修改素材的分类")
    @RequestMapping(value = "/resource/resourceCate", method = RequestMethod.PUT)
    public BaseResponse updateCate(@RequestBody @Valid SystemResourceMoveRequest
                                           moveRequest) {

        if (moveRequest.getCateId() == null || CollectionUtils.isEmpty(moveRequest.getResourceIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("素材服务", "批量修改素材的分类", "批量修改素材的分类:资源ID" + moveRequest.getCateId());
        return systemResourceSaveProvider.move(moveRequest);
    }
}
