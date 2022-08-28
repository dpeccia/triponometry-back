package utn.triponometry.repos

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import utn.triponometry.domain.User
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findById(id: ObjectId): Optional<User>
    fun findByMail(mail: String): Optional<User>
    fun findByMailAndPassword(mail: String, password: String): Optional<User>
}