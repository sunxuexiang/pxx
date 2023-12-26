package com.wanmi.osd;

import com.wanmi.osd.bean.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Test {
    private static final String aliAccessKeyId = "LTAIcvnPzCTKgdFW";
    private static final String aliAccessKeySecret = "4By4Ag5zFQLjaMcvtePL9povz1zC7W";
    private static final String aliBucket = "wanmi-x-site";
    private static final String aliRegion = "oss-cn-shanghai";
    private static final String aliEndPoint = "oss-cn-shanghai.aliyuncs.com";
    private static final String ali = "{\"accessKeyId\":\"LTAIcvnPzCTKgdFW\"," +
            "\"accessKeySecret\":\"4By4Ag5zFQLjaMcvtePL9povz1zC7W\",\"bucketName\":\"wanmi-x-site\"," +
            "\"endPoint\":\"oss-cn-shanghai.aliyuncs.com\"}";

    private static final String huaweiAccessKeyId = "SGPKCMX0ZTGWPQHFE3IR";
    private static final String huaweiAccessKeySecret = "MpqzyMpLyeJLT4iuWy6lu6kc5kiVGSYeDMbrjVpm";
    private static final String huaweiBucket = "wanmi";
    private static final String huaweiEndPoint = "obs.cn-east-2.myhuaweicloud.com";
    private static final String huawei = "{\"accessKeyId\":\"SGPKCMX0ZTGWPQHFE3IR\"," +
            "\"accessKeySecret\":\"MpqzyMpLyeJLT4iuWy6lu6kc5kiVGSYeDMbrjVpm\",\"bucketName\":\"sbc\"," +
            "\"endPoint\":\"obs.cn-north-4.myhuaweicloud.com\"}";

    private static final String txAccessKeyId = "AKID2CBuSmShT3VklZ4TbeQwEH4e2DLDnuQj";
    private static final String txAccessKeySecret = "DF7Tr5bDerrc8OyLcvylUxBBxZw3whV3";
    private static final String txBucket = "kyw-1257829190";
    private static final String txEndPoint = "cos.ap-beijing.myqcloud.com";
    private static final String txRegion = "ap-beijing";
    private static final String tx = "{\"accessKeyId\":\"AKID2CBuSmShT3VklZ4TbeQwEH4e2DLDnuQj\"," +
            "\"accessKeySecret\":\"DF7Tr5bDerrc8OyLcvylUxBBxZw3whV3\",\"bucketName\":\"kyw-1257829190\"," +
            "\"endPoint\":\"cos.ap-beijing.myqcloud.com\",\"region\": \"ap-beijing\"}";

    private static final String resourceKey = "test/Test.txt";
    private static final String path = "/Users/edz/workspace/sbc-osd/src/main/resources/Test.txt";


    private static final String minio = "{\"accessKeyId\":\"wanmi@2021\"," +
            "\"accessKeySecret\":\"wanmi@2021\",\"bucketName\":\"wanmi-b2b\"," +
            "\"endPoint\":\"minio.kstore.shop\",\"region\": \"us-east-1\"}";

    private static final String jd = "{\"accessKeyId\":\"6660F4471C608AAB0029B4FE95C59E63\"," +
            "\"accessKeySecret\":\"D51420C7E3E46710B254D40AA4059DA5\",\"bucketName\":\"xian-b2b-buk-x1\"," +
            "\"endPoint\":\"s3-ipv6.cn-north-1.jdcloud-oss.com\",\"region\": \"us-east-1\"}";

    /**
     *

    public static void main(String[] args) throws Exception{
////        测试阿里上传
//        aliUpload();
////        测试阿里是否存在资源
//        aliExists();
////        测试阿里资源内容
//        aliObject();
////        测试阿里删除
//        aliDelete();
////        测试阿里是否存在资源
//        aliExists();
////        测试华为上传
//        huaweiUpload();
////        测试华为是否存在资源
//        huaweiExist();
////        测试华为资源内容
//        huaweiObject();
////        测试华为删除
//        huaweiDelete();
////        测试华为是否存在资源
//        huaweiExist();
////        测试腾讯上传
//        txUpload();
////        测试腾讯是否存在资源
//        txExist();
////        测试腾讯资源内容
//        txObject();
////        测试腾讯删除
//        txDelete();
////        测试腾讯是否存在资源
//        txExist();

//        minioUpload();
//
//        minioExist();
//
//        minioObject();
//
//        minioDelete();
//
//        minioExist();
//
//
//        jdUpload();
//
//        jdExist();
//
//        jdObject();
//
//        jdDelete();
//
//        jdExist();
    }
*/


    public static void aliUpload() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("minio")
                .context(minio)
                .build();
        OsdResource osdResource = OsdResource.builder()
                .osdResourceKey(resourceKey)
                .osdInputStream(new FileInputStream(path))
                .build();
        OsdClient.instance().putObject(osdClientParam,osdResource);
    }


    public static void aliDelete() throws Exception{
        List<String> list = new ArrayList<>();
        list.add(resourceKey);
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("aliYun")
                .context(ali)
                .build();
        OsdClient.instance().deleteObject(osdClientParam,list);
    }

    public static void aliExists() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("aliYun")
                .context(ali)
                .build();
        Boolean isExist = OsdClient.instance().doesObjectExist(osdClientParam, resourceKey);
        System.out.println("阿里云是否存在资源："+isExist);
    }

    public static void aliObject() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("aliYun")
                .context(ali)
                .build();
        byte[] bytes = OsdClient.instance().getObject(osdClientParam, resourceKey);
        System.out.println("阿里云资源内容："+new String(bytes));
    }


    public static void huaweiUpload() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("hwYun")
                .context(huawei)
                .build();
        OsdResource osdResource = OsdResource.builder()
                .osdResourceKey(resourceKey)
                .osdInputStream(new FileInputStream(path))
                .build();
        OsdClient.instance().putObject(osdClientParam,osdResource);
    }


    public static void huaweiDelete() throws Exception{
        List<String> list = new ArrayList<>();
        list.add(resourceKey);
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("hwYun")
                .context(huawei)
                .build();
        OsdClient.instance().deleteObject(osdClientParam,list);
    }

    public static void huaweiExist() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("hwYun")
                .context(huawei)
                .build();
        Boolean isExist = OsdClient.instance().doesObjectExist(osdClientParam, resourceKey);
        System.out.println("华为云是否存在资源："+isExist);
    }

    public static void huaweiObject() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("hwYun")
                .context(huawei)
                .build();
        byte[] bytes = OsdClient.instance().getObject(osdClientParam, resourceKey);
        System.out.println("华为云资源内容："+new String(bytes));
    }

    public static void txUpload() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("txYun")
                .context(tx)
                .build();
        OsdResource osdResource = OsdResource.builder()
                .osdResourceKey(resourceKey)
                .osdInputStream(new FileInputStream(path))
                .build();
        OsdClient.instance().putObject(osdClientParam,osdResource);
    }


    public static void txDelete() throws Exception{
        List<String> list = new ArrayList<>();
        list.add(resourceKey);
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("txYun")
                .context(tx)
                .build();
        OsdClient.instance().deleteObject(osdClientParam,list);
    }

    public static void txExist() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("txYun")
                .context(tx)
                .build();
        Boolean isExist = OsdClient.instance().doesObjectExist(osdClientParam, resourceKey);
        System.out.println("腾讯云是否存在资源："+isExist);
    }


    public static void txObject() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("txYun")
                .context(tx)
                .build();
        byte[] bytes = OsdClient.instance().getObject(osdClientParam, resourceKey);
        System.out.println("腾讯云资源内容："+new String(bytes));
    }



    public static void minioUpload() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("minio")
                .context(minio)
                .build();
        OsdResource osdResource = OsdResource.builder()
                .osdResourceKey(resourceKey)
                .osdInputStream(new FileInputStream(path))
                .build();
        OsdClient.instance().putObject(osdClientParam,osdResource);
    }


    public static void minioDelete() throws Exception{
        List<String> list = new ArrayList<>();
        list.add(resourceKey);
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("minio")
                .context(minio)
                .build();
        OsdClient.instance().deleteObject(osdClientParam,list);
    }

    public static void minioExist() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("minio")
                .context(minio)
                .build();
        Boolean isExist = OsdClient.instance().doesObjectExist(osdClientParam, resourceKey);
        System.out.println("minio是否存在资源："+isExist);
    }


    public static void minioObject() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("minio")
                .context(minio)
                .build();
        byte[] bytes = OsdClient.instance().getObject(osdClientParam, resourceKey);
        System.out.println("minio资源内容："+new String(bytes));
    }


    public static void jdUpload() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("jdYun")
                .context(jd)
                .build();
        OsdResource osdResource = OsdResource.builder()
                .osdResourceKey(resourceKey)
                .osdInputStream(new FileInputStream(path))
                .build();
        OsdClient.instance().putObject(osdClientParam,osdResource);
    }


    public static void jdDelete() throws Exception{
        List<String> list = new ArrayList<>();
        list.add(resourceKey);
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("jdYun")
                .context(jd)
                .build();
        OsdClient.instance().deleteObject(osdClientParam,list);
    }

    public static void jdExist() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("jdYun")
                .context(jd)
                .build();
        Boolean isExist = OsdClient.instance().doesObjectExist(osdClientParam, resourceKey);
        System.out.println("京东云是否存在资源："+isExist);
    }


    public static void jdObject() throws Exception{
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType("jdYun")
                .context(jd)
                .build();
        byte[] bytes = OsdClient.instance().getObject(osdClientParam, resourceKey);
        System.out.println("京东云资源内容："+new String(bytes));
    }
}
