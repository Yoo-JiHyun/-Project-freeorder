package com.aloha.freeorder.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.aloha.freeorder.security.CustomAccessDeniedHandler;
import com.aloha.freeorder.security.CustomLogoutSuccessHandler;
import com.aloha.freeorder.security.LoginFailureHandler;
import com.aloha.freeorder.security.LoginSuccessHandler;
import com.aloha.freeorder.service.UserDetailServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityConfig {

    @Autowired 
    private DataSource dataSource;

    @Autowired
    private UserDetailServiceImpl detailService;

    @Autowired
    private LoginSuccessHandler successHandler;

    @Autowired
    private LoginFailureHandler failureHandler;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    // 스프링 시큐리티 설정 메소드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ✅ 인가 설정
        http.authorizeHttpRequests(auth -> auth
                                               .requestMatchers("/pos/**").hasRole("ADMIN")
                                            //    .requestMatchers("/qr/**").permitAll()
                                            //    .requestMatchers("/**").permitAll()
                                               .anyRequest().permitAll()
                                               );
        

        // 🔐 폼 로그인 설정
        http.formLogin(login -> login.loginPage("/login")
                                     .loginProcessingUrl("/login")
                                     .usernameParameter("id")
                                     .passwordParameter("pw")
                                     .successHandler(successHandler)
                                     .failureHandler(failureHandler)
                                    );

        
        // 로그아웃 설정
        http.logout(logout -> logout
                                    .logoutUrl("/logout")
                                    .logoutSuccessUrl("/login?logout")
                                    .invalidateHttpSession(true)
                                    .deleteCookies("remember-id")
                                    .logoutSuccessHandler(customLogoutSuccessHandler)
                            );

        // 사용자 정의 인증
        http.userDetailsService(detailService);

        // 🔄 자동 로그인 설정
        http.rememberMe(me -> me.key("aloha")
                                .tokenRepository(tokenRepository())
                                .rememberMeParameter("auto-login")
                                .tokenValiditySeconds(60 * 60 * 24 * 7));
        

        // 인증 예외 처리
        http.exceptionHandling( exception -> exception.accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }
    
    /**
     * AuthenticationManager 인증 관리자 빈 등록
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager( 
                                AuthenticationConfiguration authenticationConfiguration) 
                                throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    /**
     * 🍃 암호화 방식 빈 등록
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
    * 🍃 자동 로그인 저장소 빈 등록
    * ✅ 데이터 소스
    * ⭐ persistent_logins 테이블 생성
            create table persistent_logins (
                username varchar(64) not null
                , series varchar(64) primary key
                , token varchar(64) not null
                , last_used timestamp not null
            );
    * 🔄 자동 로그인 프로세스
    * ✅ 로그인 시 
    *     ➡ 👩‍💼(ID, 시리즈, 토큰) 저장
    * ✅ 로그아웃 시, 
    *     ➡ 👩‍💼(ID, 시리즈, 토큰) 삭제
    * @return
    */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        // JdbcTokenRepositoryImpl : 토큰 저장 데이터 베이스를 등록하는 객체
        JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl();
        // ✅ 토큰 저장소를 사용하는 데이터 소스 지정
        // - 시큐리티가 자동 로그인 프로세스를 처리하기 위한 DB를 지정합니다.
        repositoryImpl.setDataSource(dataSource);   
        // persistent_logins 테이블 생성
        try {
            repositoryImpl.getJdbcTemplate().execute(JdbcTokenRepositoryImpl.CREATE_TABLE_SQL);
        } 
        catch (BadSqlGrammarException e) {
            log.error("persistent_logins 테이블이 이미 존재합니다.");   
        }
        catch (Exception e) {
            log.error("자동 로그인 테이블 생성 중 , 예외 발생");
        }
        return repositoryImpl;
    }

}
