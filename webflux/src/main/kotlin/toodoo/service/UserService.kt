package toodoo.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import tech.jhipster.security.RandomUtil
import toodoo.config.DEFAULT_LANGUAGE
import toodoo.config.SYSTEM_ACCOUNT
import toodoo.domain.Authority
import toodoo.domain.User
import toodoo.repository.AuthorityRepository
import toodoo.repository.UserRepository
import toodoo.security.USER
import toodoo.security.getCurrentUserLogin
import toodoo.service.dto.AdminUserDTO
import toodoo.service.dto.UserDTO
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * Service class for managing users.
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authorityRepository: AuthorityRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun activateRegistration(key: String): Mono<User> {
        log.debug("Activating user for activation key $key")
        return userRepository.findOneByActivationKey(key)
            .flatMap { user ->
                // activate given user for the registration key.
                user.activated = true
                user.activationKey = null
                saveUser(user)
            }
            .doOnNext { user -> log.debug("Activated user: $user") }
    }

    @Transactional
    fun completePasswordReset(newPassword: String, key: String): Mono<User> {
        log.debug("Reset user password for reset key $key")
        return userRepository.findOneByResetKey(key)
            .filter { user -> user.resetDate?.isAfter(Instant.now().minus(1, ChronoUnit.DAYS)) ?: false }
            .publishOn(Schedulers.boundedElastic())
            .map {
                it.password = passwordEncoder.encode(newPassword)
                it.resetKey = null
                it.resetDate = null
                it
            }
            .flatMap(this::saveUser)
    }

    @Transactional
    fun requestPasswordReset(mail: String): Mono<User> {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .publishOn(Schedulers.boundedElastic())
            .map {
                it.resetKey = RandomUtil.generateResetKey()
                it.resetDate = Instant.now()
                it
            }
            .flatMap(this::saveUser)
    }

    @Transactional
    fun registerUser(userDTO: AdminUserDTO, password: String): Mono<User> {
        val login = userDTO.login ?: throw IllegalArgumentException("Empty login not allowed")
        val email = userDTO.email
        return userRepository.findOneByLogin(login.toLowerCase())
            .flatMap { existingUser ->
                if (existingUser.activated == false) {
                    userRepository.delete(existingUser)
                } else {
                    throw UsernameAlreadyUsedException()
                }
            }
            .then(userRepository.findOneByEmailIgnoreCase(email!!))
            .flatMap { existingUser ->
                if (existingUser.activated == false) {
                    userRepository.delete(existingUser)
                } else {
                    throw EmailAlreadyUsedException()
                }
            }
            .publishOn(Schedulers.boundedElastic())
            .then(
                Mono.fromCallable {
                    User().apply {
                        val encryptedPassword = passwordEncoder.encode(password)
                        this.login = login.toLowerCase()
                        // new user gets initially a generated password
                        this.password = encryptedPassword
                        firstName = userDTO.firstName
                        lastName = userDTO.lastName
                        this.email = email?.toLowerCase()
                        imageUrl = userDTO.imageUrl
                        langKey = userDTO.langKey
                        // new user is not active
                        activated = false
                        // new user gets registration key
                        activationKey = RandomUtil.generateActivationKey()
                    }
                }
            )
            .flatMap { newUser ->
                val authorities = mutableSetOf<Authority>()
                authorityRepository.findById(USER)
                    .map(authorities::add)
                    .thenReturn(newUser)
                    .doOnNext { user -> user.authorities = authorities }
                    .flatMap { saveUser(it) }
                    .doOnNext { user -> log.debug("Created Information for User: $user") }
            }
    }

    @Transactional
    fun createUser(userDTO: AdminUserDTO): Mono<User> {
        val user = User(
            login = userDTO.login?.toLowerCase(),
            firstName = userDTO.firstName,
            lastName = userDTO.lastName,
            email = userDTO.email?.toLowerCase(),
            imageUrl = userDTO.imageUrl,
            // default language
            langKey = userDTO.langKey ?: DEFAULT_LANGUAGE
        )
        return Flux.fromIterable(userDTO.authorities ?: mutableSetOf())
            .flatMap<Authority>(authorityRepository::findById)
            .doOnNext { authority -> user.authorities.add(authority) }
            .then(Mono.just(user))
            .publishOn(Schedulers.boundedElastic())
            .map {
                val encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword())
                it.password = encryptedPassword
                it.resetKey = RandomUtil.generateResetKey()
                it.resetDate = Instant.now()
                it.activated = true
                it
            }
            .flatMap(this::saveUser)
            .doOnNext { user -> log.debug("Changed Information for User: $user") }
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    @Transactional
    fun updateUser(userDTO: AdminUserDTO): Mono<AdminUserDTO> {
        return userRepository.findById(userDTO.id!!)
            .flatMap { user ->
                user.apply {
                    login = userDTO.login?.let { it.toLowerCase() }
                    firstName = userDTO.firstName
                    lastName = userDTO.lastName
                    email = userDTO.email?.toLowerCase()
                    imageUrl = userDTO.imageUrl
                    activated = userDTO.activated
                    langKey = userDTO.langKey
                }
                val managedAuthorities = user.authorities
                managedAuthorities.clear()

                userRepository
                    .deleteUserAuthorities(user.id!!)
                    .thenMany(Flux.fromIterable(userDTO.authorities!!))
                    .flatMap(authorityRepository::findById)
                    .map(managedAuthorities::add)
                    .then(Mono.just(user))
            }
            .flatMap(this::saveUser)
            .doOnNext { log.debug("Changed Information for User: $it") }
            .map { AdminUserDTO(it) }
    }

    @Transactional
    fun deleteUser(login: String): Mono<Void> {
        return userRepository.findOneByLogin(login)
            .flatMap { userRepository.delete(it).thenReturn(it) }
            .doOnNext { log.debug("Changed Information for User: $it") }
            .then()
    }
    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     * @return a completed {@link Mono}.
     */
    @Transactional
    fun updateUser(firstName: String?, lastName: String?, email: String?, langKey: String?, imageUrl: String?): Mono<Void> {
        return getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap {

                it.firstName = firstName
                it.lastName = lastName
                it.email = email?.toLowerCase()
                it.langKey = langKey
                it.imageUrl = imageUrl

                saveUser(it)
            }
            .doOnNext { log.debug("Changed Information for User: $it") }
            .then()
    }

    @Transactional
    fun saveUser(user: User): Mono<User> {
        return getCurrentUserLogin()
            .switchIfEmpty(Mono.just(SYSTEM_ACCOUNT))
            .flatMap { login ->
                if (user.createdBy == null) {
                    user.createdBy = login
                }
                user.lastModifiedBy = login
                // Saving the relationship can be done in an entity callback
                // once https://github.com/spring-projects/spring-data-r2dbc/issues/215 is done
                userRepository.save(user)
                    .flatMap { user ->
                        Flux.fromIterable(user.authorities)
                            .flatMap { user.id?.let { it1 -> it.name?.let { it2 -> userRepository.saveUserAuthority(it1, it2) } } }
                            .then(Mono.just(user))
                    }
            }
    }

    @Transactional
    fun changePassword(currentClearTextPassword: String, newPassword: String): Mono<Void> {
        return getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .publishOn(Schedulers.boundedElastic())
            .map { user ->
                val currentEncryptedPassword = user.password
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw InvalidPasswordException()
                }
                val encryptedPassword = passwordEncoder.encode(newPassword)
                user.password = encryptedPassword
                user
            }.flatMap { saveUser(it) }
            .doOnNext { user -> log.debug("Changed password for User: $user") }
            .then()
    }

    @Transactional(readOnly = true)
    fun getAllManagedUsers(pageable: Pageable): Flux<AdminUserDTO> {
        return userRepository.findAllWithAuthorities(pageable).map { AdminUserDTO(it) }
    }

    @Transactional(readOnly = true)
    fun getAllPublicUsers(pageable: Pageable): Flux<UserDTO> {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map { UserDTO(it) }
    }

    @Transactional(readOnly = true)
    fun countManagedUsers() = userRepository.count()

    @Transactional(readOnly = true)
    fun getUserWithAuthoritiesByLogin(login: String): Mono<User> =
        userRepository.findOneWithAuthoritiesByLogin(login)

    @Transactional(readOnly = true)
    fun getUserWithAuthorities(): Mono<User> =
        getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin)

    /**
     * Not activated users should be automatically deleted after 3 days.
     *
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    fun removeNotActivatedUsers() {
        removeNotActivatedUsersReactively().blockLast()
    }

    @Transactional
    fun removeNotActivatedUsersReactively(): Flux<User> {
        return userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(LocalDateTime.ofInstant(Instant.now().minus(3, ChronoUnit.DAYS), ZoneOffset.UTC))
            .flatMap { user -> userRepository.delete(user).thenReturn(user) }
            .doOnNext { user -> log.debug("Deleted User: $user") }
    }

    /**
     * @return a list of all the authorities
     */
    @Transactional(readOnly = true)
    fun getAuthorities() =
        authorityRepository.findAll().map(Authority::name)
}
