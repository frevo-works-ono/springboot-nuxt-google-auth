package springboot.nuxt.google.auth.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import springboot.nuxt.google.auth.backend.repository.UserRepository;
import springboot.nuxt.google.auth.backend.util.FirebaseClient;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserRepository userRepository;
    private FirebaseClient firebaseClient;

    public SecurityConfig(UserRepository userRepository, FirebaseClient firebaseClient) {
        this.userRepository = userRepository;
        this.firebaseClient = firebaseClient;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated();

        http.addFilter(preAuthenticatedProcessingFilter());

        // CSRF無効
        http.csrf().disable();

        // CORS対応（フロントエンドとオリジンが異なるため必要）
        http.cors().configurationSource(getCorsConfigurationSource());

        // セッション管理無効
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private CorsConfigurationSource getCorsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();

        // 全てのメソッドを許可
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);

        // 全てのヘッダを許可
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);

        // localhost:3002のみ許可
        corsConfiguration.addAllowedOrigin("http://localhost:3000");

        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();

        corsSource.registerCorsConfiguration("/**", corsConfiguration);

        return corsSource;
    }

    // フィルター登録
    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {

        var preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();

        preAuthenticatedAuthenticationProvider
                .setPreAuthenticatedUserDetailsService(new AuthService(firebaseClient, userRepository));

        preAuthenticatedAuthenticationProvider.setUserDetailsChecker(new AccountStatusUserDetailsChecker());

        return preAuthenticatedAuthenticationProvider;
    }

    // 作成したフィルター
    private AbstractPreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {

        var preAuthenticatedProcessingFilter = new AuthFilter();
        preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());

        return preAuthenticatedProcessingFilter;
    }
}
