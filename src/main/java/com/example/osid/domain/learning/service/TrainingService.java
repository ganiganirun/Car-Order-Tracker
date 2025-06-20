package com.example.osid.domain.learning.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class TrainingService {

    @Value("${MLP_UPLOAD_URL}")
    private String uploadUrl;

    public void triggerTrainingAndUpload() {
        try {
            System.out.println("[스케줄러] train.py 실행 시작");

            ProcessBuilder pb = new ProcessBuilder("venv\\Scripts\\python.exe", "train.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python] " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("[스케줄러] train.py 종료 코드: " + exitCode);

            if (exitCode == 0) {
                uploadModel();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadModel() {
        File modelFile = new File("mlp_single_custom.pkl");
        try {
            if (!modelFile.exists()) {
                System.err.println("[Spring] 모델 파일이 없습니다.");
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("model", new FileSystemResource(modelFile));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.postForObject(uploadUrl, request, String.class);

            System.out.println("[Spring] 업로드 응답: " + response);

        } catch (Exception e) {
            System.err.println("[Spring] 모델 업로드 실패: " + e.getMessage());
        } finally {
            if (modelFile.exists()) {
                boolean deleted = modelFile.delete();
                System.out.println("[Spring] 모델 파일 삭제 여부: " + deleted);
            }
        }
    }
}
