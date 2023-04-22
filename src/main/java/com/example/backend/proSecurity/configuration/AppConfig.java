package com.example.backend.proSecurity.configuration;

import com.example.backend.proSecurity.session.SessionFilter;
import com.example.backend.proSecurity.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class AppConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private SessionFilter sessionFilter;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(currentUserService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = http.cors().and().csrf().disable();

        http = http.exceptionHandling().authenticationEntryPoint(
                (request, response, ex) -> response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ex.getMessage())
        ).and();

        http.authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/userRegister").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/advertisement").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/category").hasAnyAuthority( "ADMIN")
                .antMatchers(HttpMethod.POST, "/myproducts").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/favourite").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/favourite").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/deleteproduct").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/editproduct").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/feedback").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/feedback").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/deletefeedback").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/removefavourite").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/createcategory").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/addProductToCompare").hasAnyAuthority("USER","ADMIN")
                .antMatchers(HttpMethod.GET, "/getCompareProducts").hasAnyAuthority("USER","ADMIN")
                .antMatchers(HttpMethod.POST,"/chat").hasAnyAuthority("USER","ADMIN")
                .antMatchers(HttpMethod.POST,"/chatGet").hasAnyAuthority("USER","ADMIN")
                .antMatchers("/addChatContact").hasAnyAuthority("USER","ADMIN")
                .antMatchers(HttpMethod.GET).permitAll()
                .antMatchers(HttpMethod.POST).permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(
                sessionFilter,
                UsernamePasswordAuthenticationFilter.class
        );
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://bazar-mh.vercel.app")
//                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("*")
                        .allowCredentials(true);
            }

        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //password decoder



}
