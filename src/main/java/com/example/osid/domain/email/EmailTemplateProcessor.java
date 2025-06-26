package com.example.osid.domain.email;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

public class EmailTemplateProcessor {

	public static String loadTemplate(String path, Map<String, String> variables) {
		try {
			InputStream inputStream = new ClassPathResource(path).getInputStream();
			String content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

			for (Map.Entry<String, String> entry : variables.entrySet()) {
				content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
			}

			return content;
		} catch (Exception e) {
			throw new RuntimeException("템플릿 로딩 실패: " + path, e);
		}
	}
}
