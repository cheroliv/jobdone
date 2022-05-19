package toodoo.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import toodoo.domain.Authority

/**
 * Spring Data R2DBC repository for the [Authority] entity.
 */

interface AuthorityRepository : R2dbcRepository<Authority, String>
