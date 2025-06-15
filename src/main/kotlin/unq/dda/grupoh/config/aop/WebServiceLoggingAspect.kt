package unq.dda.grupoh.config.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Aspect
@Component
class WebServiceLoggingAspect {

    private val logger = LoggerFactory.getLogger(WebServiceLoggingAspect::class.java)

    @Around("execution(public * unq.dda.grupoh.webservice..*(..))")
    fun logWebServiceCalls(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val methodSignature = joinPoint.signature as MethodSignature
        val methodName = "${methodSignature.declaringType.simpleName}.${methodSignature.name}"
        val args = joinPoint.args.joinToString(", ") { it?.toString() ?: "null" }
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val user = try {
            SecurityContextHolder.getContext().authentication?.name ?: "ANONYMOUS"
        } catch (e: Exception) {
            "ANONYMOUS"
        }

        return try {
            val result = joinPoint.proceed()
            val elapsed = System.currentTimeMillis() - start
            logger.info("[$timestamp,$user,$methodName,params=[$args],${elapsed}ms]")
            result
        } catch (ex: Throwable) {
            val elapsed = System.currentTimeMillis() - start
            logger.error("[$timestamp,$user,$methodName,params=[$args],${elapsed}ms] ERROR: ${ex.message}")
            throw ex
        }
    }
}