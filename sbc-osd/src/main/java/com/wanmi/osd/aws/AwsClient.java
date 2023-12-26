package com.wanmi.osd.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.wanmi.osd.bean.OsdConfig;

public class AwsClient {

    private AwsClient(){}

    private static AwsClient awsClient = new AwsClient();

    public AmazonS3 init(OsdConfig osdConfig) {
        ClientConfiguration config = new ClientConfiguration();
        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration(osdConfig.getEndPoint(), Regions.US_EAST_1.name());
        AWSCredentials awsCredentials = new BasicAWSCredentials(osdConfig.getAccessKeyId(),osdConfig.getAccessKeySecret());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfig)
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(config)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .build();
    }

    public static AwsClient instance() {
        return awsClient;
    }
}
