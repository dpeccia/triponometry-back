package utn.triponometry.repos

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import utn.triponometry.domain.User
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByMail(mail: String): Optional<User>
}