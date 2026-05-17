package com.campusred.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class WechatClient {

    private final String appId;
    private final String appSecret;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WechatClient(@Value("${wechat.app-id}") String appId,
                        @Value("${wechat.app-secret}") String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public record WxSession(String openid, String sessionKey, String unionid) {}

    public WxSession code2Session(String code) {
        if (appId == null || appId.isEmpty() || appId.startsWith("your-")) {
            return devFallback(code);
        }

        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode json = objectMapper.readTree(response.body());
            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                throw new RuntimeException("微信登录失败: " + json.path("errmsg").asText());
            }

            return new WxSession(
                    json.get("openid").asText(),
                    json.path("session_key").asText(null),
                    json.path("unionid").asText(null)
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("微信接口调用失败", e);
        }
    }

    private WxSession devFallback(String code) {
        return new WxSession(code, null, null);
    }
}
