package com.tobe.healthy.common;

import com.amazonaws.services.s3.AmazonS3;
import com.tobe.healthy.config.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import static com.tobe.healthy.config.error.ErrorCode.FILE_REMOVE_ERROR;

@Component
@Slf4j
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

	private final AmazonS3 amazonS3;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer, AmazonS3 amazonS3) {
		super(listenerContainer);
        this.amazonS3 = amazonS3;
    }

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String fileUrl = message.toString();
		if (fileUrl != null && fileUrl.startsWith("https://to-be-healthy-bucket.s3.ap-northeast-2.amazonaws.com")) {
			try {
				amazonS3.deleteObject(bucketName, fileUrl.replaceAll("https://to-be-healthy-bucket.s3.ap-northeast-2.amazonaws.com/", ""));
			} catch (Exception e) {
				e.printStackTrace();
				throw new CustomException(FILE_REMOVE_ERROR);
			}
		}
		log.info("onMessage pattern => {} | {}", new String(pattern), message.toString());
	}
}
