package gr.assignment.movierama.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.assignment.movierama.core.security.AccessDeniedHandlerImpl;
import gr.assignment.movierama.domain.application.service.AccountService;
import gr.assignment.movierama.core.security.DelegatingUserService;
import gr.assignment.movierama.web.apis.authenticate.DefaultAuthenticationFailureHandler;
import gr.assignment.movierama.web.apis.authenticate.DefaultAuthenticationSuccessHandler;
import gr.assignment.movierama.web.apis.authenticate.DefaultLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Arrays;
import java.util.Collection;

@EnableWebSecurity(debug = true)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_ENDPOINT = "/api/authentication";

    private static final String REGISTER_ENDPOINT = "/api/registrations";

    private static final Collection<String> PUBLIC_RESOURCES =
            Arrays.asList("/login", "/logout", "/register","/", REGISTER_ENDPOINT, LOGIN_ENDPOINT);

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**", "/js/**", "/css/**", "/images/**", "/favicon.ico");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AbstractAuthenticationProcessingFilter authenticationFilter(AuthenticationManager authenticationManager,
                                                                       ObjectMapper objectMapper,
                                                                       AuthenticationSuccessHandler authenticationSuccessHandler,
                                                                       AuthenticationFailureHandler authenticationFailureHandler) {
        AbstractAuthenticationProcessingFilter authenticationFilter = new gr.assignment.movierama.web.apis.authenticate.AuthenticationFilter(LOGIN_ENDPOINT, objectMapper);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return authenticationFilter;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(ObjectMapper objectMapper) {
        return new DefaultAuthenticationSuccessHandler(objectMapper);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(ObjectMapper objectMapper) {
        return new DefaultAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(ObjectMapper objectMapper) {
        return new DefaultLogoutSuccessHandler(objectMapper);
    }

    @Bean
    public UserDetailsService userDetailsService(AccountService accountService) {
        return new DelegatingUserService(accountService);
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .authorizeRequests()
                .antMatchers(PUBLIC_RESOURCES.toArray(new String[0])).permitAll()
                .antMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAt(lookup(gr.assignment.movierama.web.apis.authenticate.AuthenticationFilter.class), UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(lookup("userDetailsService"))
                .formLogin()
                .loginPage("/login")
                .and()
                .logout()
                .logoutUrl("/api/me/logout")
                .logoutSuccessHandler(lookup(LogoutSuccessHandler.class))
                .and()
                .csrf()
                .disable();
    }

    protected <T> T lookup(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    protected <T> T lookup(String beanName) {
        return (T) getApplicationContext().getBean(beanName);
    }
}


