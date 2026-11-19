package com.example.demo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@SpringBootApplication(scanBasePackages = "com.example.*")
public class DemoApplication {

	@Value("${aws.s3.bucket.name}")
	private String bucketName;

	private final S3Client s3Client;

	@Autowired
	public DemoApplication(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	int execute() throws IOException {
		int mode = 0;
		if (mode == 1) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName)
					.key("2024-04-26_21-11-57.mp4").build();

			ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);

			InputStream stream = new ByteArrayInputStream(responseInputStream.readAllBytes());
			File file = new File("C:\\Users\\DevNtbkUsr\\Downloads\\video.mp4");
			try (OutputStream outputStream = new FileOutputStream(file)) {
				IOUtils.copy(stream, outputStream);
			} catch (FileNotFoundException e) {
				// handle exception here
			} catch (IOException e) {
				// handle exception here
			}
		}
		if (mode == 2) {
			SqsClient sqsClient = SqsClient.builder().region(Region.US_EAST_1)
					.credentialsProvider(ProfileCredentialsProvider.create()).build();

			GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder().queueName("TestQ").build();

			String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
			SendMessageRequest sendMsgRequest = SendMessageRequest.builder().queueUrl(queueUrl)
					.messageBody("{\"sampleKey\":\"sampleValue\"}").delaySeconds(2).build();

			sqsClient.sendMessage(sendMsgRequest);

			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
					.queueUrl("https://sqs.us-east-1.amazonaws.com/730335259534/TestQ").waitTimeSeconds(10)
					.maxNumberOfMessages(10).build();

			sqsClient.receiveMessage(receiveMessageRequest).messages().forEach(e -> {
				System.out.println(e.body());
			});

		}

		if (mode == 3) {
			SnsClient snsClient = SnsClient.builder().credentialsProvider(ProfileCredentialsProvider.create())
					.region(Region.US_EAST_1).build();
			try {
				PublishRequest request = PublishRequest.builder()
						.message("Test message from Spring Boot Code Development ").phoneNumber("+919873157312")
						.build();

				PublishResponse result = snsClient.publish(request);
				System.out.println(
						result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

			} catch (SnsException e) {
				System.err.println(e.awsErrorDetails().errorMessage());
				System.exit(1);
			}
			snsClient.close();
		}
		return 0;
	}

}
