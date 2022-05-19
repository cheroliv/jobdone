package toodoo.repository

import org.springframework.data.jpa.repository.JpaRepository
import toodoo.domain.Authority

/**
 * Spring Data JPA repository for the [Authority] entity.
 */

interface AuthorityRepository : JpaRepository<Authority, String>
