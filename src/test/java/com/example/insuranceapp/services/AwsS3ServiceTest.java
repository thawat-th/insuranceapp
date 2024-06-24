package com.example.insuranceapp.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AwsS3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    private AwsS3Service awsS3Service;

    private String bucketName = "test-bucket";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        awsS3Service = new AwsS3Service(amazonS3, bucketName);
    }

    @Test
    public void testUploadFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + multipartFile.getOriginalFilename();

        // Mock the convertMultiPartToFile method to return a temporary file
        File tempFile = File.createTempFile("test", ".jpg");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

        AwsS3Service spyAwsS3Service = spy(awsS3Service);
        doReturn(tempFile).when(spyAwsS3Service).convertMultiPartToFile(any(MultipartFile.class));

        // Mock the putObject method to do nothing (to avoid actual upload)
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);

        String result = spyAwsS3Service.uploadFile(multipartFile);

        assertThat(result).isEqualTo(fileUrl);
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3, times(1)).putObject(captor.capture());

        // Ensure the temporary file is deleted after the test
        assertThat(tempFile).doesNotExist();
    }

    @Test
    public void testGetPresignedUrl() throws Exception {
        String fileName = "test.jpg";

        URL url = new URL("http://example.com");
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(url);

        String result = awsS3Service.getPresignedUrl(fileName);

        assertThat(result).isEqualTo(url.toString());
        ArgumentCaptor<GeneratePresignedUrlRequest> captor = ArgumentCaptor.forClass(GeneratePresignedUrlRequest.class);
        verify(amazonS3, times(1)).generatePresignedUrl(captor.capture());

        GeneratePresignedUrlRequest request = captor.getValue();
        assertThat(request.getBucketName()).isEqualTo(bucketName);
        assertThat(request.getKey()).isEqualTo(fileName);
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getExpiration()).isAfter(new Date());
    }

    @Test
    public void testDeleteFile() {
        String fileName = "test.jpg";

        // Mock the deleteObject method to do nothing (to avoid actual deletion)
        doNothing().when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

        awsS3Service.deleteFile(fileName);

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(amazonS3, times(1)).deleteObject(captor.capture());

        DeleteObjectRequest request = captor.getValue();
        assertThat(request.getBucketName()).isEqualTo(bucketName);
        assertThat(request.getKey()).isEqualTo(fileName);
    }
}
