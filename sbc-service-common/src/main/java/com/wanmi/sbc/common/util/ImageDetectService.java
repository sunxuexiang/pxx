package com.wanmi.sbc.common.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tiia.v20190529.TiiaClient;
import com.tencentcloudapi.tiia.v20190529.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.imagesearch20210501.Client;
import com.aliyun.imagesearch20210501.models.SearchByPicAdvanceRequest;
import com.aliyun.imagesearch20210501.models.SearchByPicResponse;
import com.aliyun.imagesearch20210501.models.SearchByPicResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class ImageDetectService {

    @Value("${tencent.secretId}")
    private String SecretId;
    @Value("${tencent.secretKey}")
    private String SecretKey;
    @Value("${alicloud.accessKeyId}")
    private String accessKeyId;
    @Value("${alicloud.accessKeySecret}")
    private String accessKeySecret;
    @Value("${alicloud.pid}")
    private String pid;
    @Value("${alicloud.fields}")
    private String fields;
    @Value("${alicloud.endpoint}")
    private String endpoint;
    @Value("${alicloud.regionId}")
    private String regionId;
    @Value("${alicloud.num}")
    private Integer num;

    public static TiiaClient client = null;


    public void createClient() {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(SecretId, SecretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tiia.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            client = new TiiaClient(cred, "ap-guangzhou", clientProfile);
        } catch (Exception e) {
            log.error("创建TiiaClient失败:", e);
        }
    }


    public DetectProductBetaResponse imageDetect(DetectProductBetaRequest req) {
        this.createClient();
        try {
            // 实例化一个请求对象,每个接口都会对应一个request对象
            // 返回的resp是一个DetectProductBetaResponse的实例，与请求对象对应
            DetectProductBetaResponse resp = client.DetectProductBeta(req);
            // 输出json格式的字符串回包
            System.out.println(DetectProductBetaResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除图片
     * @param req
     * @return
     */
    public DeleteImagesResponse DeleteImages(DeleteImagesRequest req) {
        this.createClient();
        try {
            // 实例化一个请求对象,每个接口都会对应一个request对象
            // 返回的resp是一个DetectProductBetaResponse的实例，与请求对象对应
            DeleteImagesResponse resp = client.DeleteImages(req);
            // 输出json格式的字符串回包
            System.out.println(DeleteImagesResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建图片
     * @param req
     * @return
     */
    public CreateImageResponse CreateImage(CreateImageRequest req) {
        this.createClient();
        try {
            // 实例化一个请求对象,每个接口都会对应一个request对象
            // 返回的resp是一个DetectProductBetaResponse的实例，与请求对象对应
            CreateImageResponse resp = client.CreateImage(req);
            // 输出json格式的字符串回包
            System.out.println(CreateImageResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查看图片
     * @param req
     * @return
     */
    public DescribeImagesResponse DescribeImages(DescribeImagesRequest req) {
        this.createClient();
        try {
            // 实例化一个请求对象,每个接口都会对应一个request对象
            // 返回的resp是一个DetectProductBetaResponse的实例，与请求对象对应
            DescribeImagesResponse  resp = client.DescribeImages(req);
            // 输出json格式的字符串回包
            System.out.println(DescribeImagesResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 搜索图片
     * @param req
     * @return
     */
    public SearchImageResponse SearchImage(SearchImageRequest req) {
        this.createClient();
        try {
            // 实例化一个请求对象,每个接口都会对应一个request对象
            // 返回的resp是一个DetectProductBetaResponse的实例，与请求对象对应
            SearchImageResponse resp = client.SearchImage(req);
            // 输出json格式的字符串回包
            System.out.println(DetectProductBetaResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }

    public Long DescribeGroups(DescribeGroupsRequest req){
        this.createClient();
        try {
        DescribeGroupsResponse resp = client.DescribeGroups(req);
            GroupInfo[] Groups=resp.getGroups();
            if(Groups.length>0){
                GroupInfo groupInfo=Groups[0];
                Long PicCount=groupInfo.getPicCount();
                return PicCount;
            }
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
        return 0l;
    }

    public List<SearchByPicResponseBody.SearchByPicResponseBodyDataAuctions> SearchByPic(InputStream picName){
        try {
            Config authConfig = new Config();
            authConfig.accessKeyId = this.accessKeyId;
            authConfig.accessKeySecret = this.accessKeySecret;
            authConfig.endpoint = this.endpoint;
            authConfig.regionId = this.regionId;
            Client client = new Client(authConfig);

            SearchByPicAdvanceRequest request = new SearchByPicAdvanceRequest();
            request.picContentObject = picName;
            // 必填 PID
            request.pid = this.pid;
            // 需要返回的字段list。不同的字段用逗号分割。默认PicUrl,ReservePrice,Title,Url,ZkFinalPrice字段必返回。
            request.fields = this.fields;
            // 选填，返回结果的起始位置。取值范围：0-499。默认值：0。
            request.start = 0;

            // 选填，返回结果的数目。取值范围：1-20。默认值：10。
            request.num = this.num;

            // 2 创建RuntimeObject实例并设置运行参数
            RuntimeOptions runtimeOptions = new RuntimeOptions();
            runtimeOptions.autoretry = true;
            SearchByPicResponse response = client.searchByPicAdvance(request, runtimeOptions);
            return  response.getBody().getData().getAuctions();

        } catch (TeaException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }
}
