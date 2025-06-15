package unq.dda.grupoh.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Configuration
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    @Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/ping",
                    "/auth/register",
                    "/auth/login",
                    "/error",
                    "/actuator/**"
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                it.authenticationEntryPoint { request, response, authException ->
                    if (response.isCommitted) return@authenticationEntryPoint
                    resolver.resolveException(request, response, null, authException)
                }
                it.accessDeniedHandler { request, response, accessDeniedException ->
                    resolver.resolveException(request, response, null, accessDeniedException)
                }
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic { it.disable() }
            .requestCache { it.disable() }

        return http.build()
    }
}