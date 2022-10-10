package utn.triponometry.repos

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import utn.triponometry.domain.Code
import utn.triponometry.domain.User
import java.util.*

@Repository
interface CodeRepository : MongoRepository<Code, String> {
    fun findByUserIdAndCode(id: ObjectId,code: String): Optional<Code>
}