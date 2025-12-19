package com.gw_camel_integration.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.nio.file.Path;

@Service
public class S3Service {

  private final S3Client s3;
  private final String bucket;

  public S3Service() {
    String endpoint = System.getenv().getOrDefault("S3_ENDPOINT", "");
    String region = System.getenv().getOrDefault("S3_REGION", "us-east-1");
    String accessKey = System.getenv().getOrDefault("S3_ACCESS_KEY", "test");
    String secretKey = System.getenv().getOrDefault("S3_SECRET_KEY", "test");
    this.bucket = System.getenv().getOrDefault("S3_BUCKET", "gw-integration-sample");

    S3ClientBuilder builder = S3Client.builder()
      .region(Region.of(region))
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));

    if (!endpoint.isBlank()) {
      builder.endpointOverride(URI.create(endpoint));
    }
    this.s3 = builder.build();
  }

  public void upload(String key, Path file) {
    PutObjectRequest req = PutObjectRequest.builder()
      .bucket(bucket)
      .key(key)
      .build();
    s3.putObject(req, RequestBody.fromFile(file));
  }
}
