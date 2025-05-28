package com.example.osid.common.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestController
public class CommonControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !Void.TYPE.equals(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {

        //CommonResponse 반환, 상태코드와 함께
        //created, 에러 등
        if (body instanceof CommonResponse<?> commonBody) {
            response.setStatusCode(commonBody.getStatus());
            return commonBody;
        }

        //response객체 없을때
        if (body == null) {
            return CommonResponse.ok();
        }

        ResponseStatus responseStatus = returnType.getMethodAnnotation(ResponseStatus.class);
        if (responseStatus != null && responseStatus.value() == HttpStatus.CREATED) {
            return CommonResponse.created(body);
        }

        //response객체 있는 ok상태코드 반환
        return CommonResponse.ok(body);

    }

}
