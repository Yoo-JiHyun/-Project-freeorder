package com.aloha.freeorder.controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aloha.freeorder.domain.Payment;
import com.aloha.freeorder.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TosspayContoller {
    

    @Autowired
    private PaymentService paymentService;

    @GetMapping({"/checkout"})
    public String payment() {
        return "checkout";
    }

    @RequestMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {
        log.info("결제성공 후 프로그램 DB에 연동 시도...");
        log.info(jsonBody);
        JSONParser parser = new JSONParser();
        String ordersId;
        String amount;
        String paymentKey;
        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            ordersId = (String) requestData.get("ordersId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        };
        JSONObject obj = new JSONObject();
        obj.put("ordersId", ordersId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        
        responseStream.close();
        log.info("로그" + jsonObject.toJSONString());

        Payment payment = paymentService.select(ordersId);
        if (payment == null) {
            int result = paymentService.insert(payment);
        }
        

        
        if (isSuccess) {

            JSONObject responseJson = new JSONObject();
            // 결제 성공 처리 로직
            log.info("결제 성공: ordersId={}, amount={}", ordersId, amount);
            
            // TODO: 데이터베이스에 결제 상태 업데이트
            paymentService.updatePaymentStatus(ordersId, "PAID");
            
            // TODO: 사용자에게 결제 성공 알림 발송 (예: 이메일, SMS)
            responseJson.put("status", "success");
            responseJson.put("message", "Payment confirmed successfully.");
            return ResponseEntity.ok(responseJson);
        } else {
            
            log.error("결제 실패: ordersId={}, code={}, message={}", ordersId, code, jsonObject.get("message"));
            JSONObject responseJson = new JSONObject();
            // 결제 실패 처리 로직
            
            // TODO: 결제 실패 사유를 저장 및 분석
            paymentService.updatePaymentStatus(ordersId, "PENDING");
            // TODO: 사용자에게 결제 실패 알림 발송
            responseJson.put("status", "failure");
            responseJson.put("message", "Payment confirmation failed. Please try again.");
            return ResponseEntity.status(code).body(responseJson);
        }
    }
}
