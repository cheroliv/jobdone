package toodoo.security.jwt

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.util.ReflectionTestUtils
import tech.jhipster.config.JHipsterProperties
import toodoo.management.SecurityMetersService
import toodoo.security.USER

class JWTFilterTest {

    private lateinit var tokenProvider: TokenProvider

    private lateinit var jwtFilter: JWTFilter

    @BeforeEach
    fun setup() {
        val jHipsterProperties = JHipsterProperties()
        val base64Secret = "fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8"
        jHipsterProperties.security.authentication.jwt.base64Secret = base64Secret
        val securityMetersService = SecurityMetersService(SimpleMeterRegistry())
        tokenProvider = TokenProvider(jHipsterProperties, securityMetersService)
        ReflectionTestUtils.setField(tokenProvider, "key", Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret)))

        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", 60000)
        jwtFilter = JWTFilter(tokenProvider)
        SecurityContextHolder.getContext().authentication = null
    }

    @Test
    @Throws(Exception::class)
    fun testJWTFilter() {
        val authentication = UsernamePasswordAuthenticationToken(
            "test-user",
            "test-password",
            listOf(SimpleGrantedAuthority(USER))
        )
        val jwt = tokenProvider.createToken(authentication, false)
        val request = MockHttpServletRequest()
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer $jwt")
        request.requestURI = "/api/test"
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        jwtFilter.doFilter(request, response, filterChain)
        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(SecurityContextHolder.getContext().authentication.name).isEqualTo("test-user")
        assertThat(SecurityContextHolder.getContext().authentication.credentials.toString()).hasToString(jwt)
    }

    @Test
    @Throws(Exception::class)
    fun testJWTFilterInvalidToken() {
        val jwt = "wrong_jwt"
        val request = MockHttpServletRequest()
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer $jwt")
        request.requestURI = "/api/test"
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        jwtFilter.doFilter(request, response, filterChain)
        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    @Throws(Exception::class)
    fun testJWTFilterMissingAuthorization() {
        val request = MockHttpServletRequest()
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer ")
        request.requestURI = "/api/test"
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        jwtFilter.doFilter(request, response, filterChain)
        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    @Throws(Exception::class)
    fun testJWTFilterMissingToken() {
        val request = MockHttpServletRequest()
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer ")
        request.requestURI = "/api/test"
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        jwtFilter.doFilter(request, response, filterChain)
        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    @Throws(Exception::class)
    fun testJWTFilterWrongScheme() {
        val authentication = UsernamePasswordAuthenticationToken(
            "test-user",
            "test-password",
            listOf(SimpleGrantedAuthority(USER))
        )
        val jwt = tokenProvider.createToken(authentication, false)
        val request = MockHttpServletRequest()
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Basic $jwt")
        request.requestURI = "/api/test"
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        jwtFilter.doFilter(request, response, filterChain)
        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }
}
