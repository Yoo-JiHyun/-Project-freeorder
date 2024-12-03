package com.aloha.freeorder.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.aloha.freeorder.domain.CustomUser;
import com.aloha.freeorder.domain.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 성공 처리 이벤트 핸들러
 */
@Slf4j
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    
    /*
     * 로그인 성공시 호출되는 메소드
     * 아이디 저장 쿠키 생성
     * 로그인 후 이전페이지로 리다이렉션
     * 
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
                                       ,HttpServletResponse response
                                       ,Authentication authentication) 
        throws ServletException, IOException {
        
        log.info("로그인 성공...");
        
        // 아이디 저장
        String rememberId = request.getParameter("remember-id");
        String username = request.getParameter("id");
        log.info("rememberId : " + rememberId);
        log.info("username" + username);

        // 아이디 저장 체크 여부 확인
        if (rememberId != null && rememberId.equals("on")) {
            Cookie cookie = new Cookie("remember-id", username);
            cookie.setMaxAge(60 * 60 * 24 * 7);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        else {
            Cookie cookie = new Cookie("remember-id", username);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }




        // 인증된 사용자 정보
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Users user = customUser.getUser();
        
        log.info("아이디 : " + user.getUsername());
        log.info("권한 : " + user.getAuthList());

        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    
    
}
