package toodoo.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Test class for the Security Utility methods.
 */
class SecurityUtilsUnitTest {
    @BeforeEach
    @AfterEach
    fun cleanup() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun testgetCurrentUserLogin() {
        val securityContext = SecurityContextHolder.createEmptyContext()
        securityContext.authentication = UsernamePasswordAuthenticationToken("admin", "admin")
        SecurityContextHolder.setContext(securityContext)
        val login = getCurrentUserLogin()
        assertThat(login).contains("admin")
    }

    @Test
    fun testGetCurrentUserJWT() {
        val securityContext = SecurityContextHolder.createEmptyContext()
        securityContext.authentication = UsernamePasswordAuthenticationToken("admin", "token")
        SecurityContextHolder.setContext(securityContext)
        val jwt = getCurrentUserJWT()
        assertThat(jwt).contains("token")
    }

    @Test
    fun testIsAuthenticated() {
        val securityContext = SecurityContextHolder.createEmptyContext()
        securityContext.authentication = UsernamePasswordAuthenticationToken("admin", "admin")
        SecurityContextHolder.setContext(securityContext)
        val isAuthenticated = isAuthenticated()
        assertThat(isAuthenticated).isTrue
    }

    @Test
    fun testAnonymousIsNotAuthenticated() {
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authorities = listOf(SimpleGrantedAuthority(ANONYMOUS))
        securityContext.authentication = UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities)
        SecurityContextHolder.setContext(securityContext)
        val isAuthenticated = isAuthenticated()
        assertThat(isAuthenticated).isFalse
    }

    @Test
    fun testHasCurrentUserThisAuthority() {
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authorities = listOf(SimpleGrantedAuthority(USER))
        securityContext.authentication = UsernamePasswordAuthenticationToken("user", "user", authorities)
        SecurityContextHolder.setContext(securityContext)

        assertThat(hasCurrentUserThisAuthority(USER)).isTrue
        assertThat(hasCurrentUserThisAuthority(ADMIN)).isFalse
    }

    @Test
    fun testHasCurrentUserAnyOfAuthorities() {
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authorities = listOf(SimpleGrantedAuthority(USER))
        securityContext.setAuthentication(UsernamePasswordAuthenticationToken("user", "user", authorities))
        SecurityContextHolder.setContext(securityContext)

        assertThat(hasCurrentUserAnyOfAuthorities(USER, ADMIN)).isTrue
        assertThat(hasCurrentUserAnyOfAuthorities(ANONYMOUS, ADMIN)).isFalse
    }

    @Test
    fun testHasCurrentUserNoneOfAuthorities() {
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authorities = listOf(SimpleGrantedAuthority(USER))
        securityContext.setAuthentication(UsernamePasswordAuthenticationToken("user", "user", authorities))
        SecurityContextHolder.setContext(securityContext)

        assertThat(hasCurrentUserNoneOfAuthorities(USER, ADMIN)).isFalse
        assertThat(hasCurrentUserNoneOfAuthorities(ANONYMOUS, ADMIN)).isTrue
    }
}
