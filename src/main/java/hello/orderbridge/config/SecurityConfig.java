package hello.orderbridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
            /login 은 인증 없이 접근 허용 (permitAll)
            /css/**, /js/** 정적 리소스도 허용
            나머지는 인증 필요 (authenticated)
            formLogin() — 로그인 페이지를 /login으로 지정, 성공 시 /orders로 이동
            logout() — 로그아웃 시 /login으로 이동
         */
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        (auth) -> auth.requestMatchers("/login", "/css/**", "/js/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .formLogin((form) -> form.loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/orders", true)
                )
                .logout(
                        (logout) -> logout.logoutSuccessUrl("/login")
                );

        return http.build();
    }
}
