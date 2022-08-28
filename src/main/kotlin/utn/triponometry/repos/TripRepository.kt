package utn.triponometry.repos

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import utn.triponometry.domain.Trip
import utn.triponometry.domain.User
import java.util.*

@Repository
interface TripRepository : MongoRepository<Trip, String>{
   fun findByUser(user: User): List<Trip>
   fun findByUserAndName(user: User, name: String): Optional<Trip>
   fun findByUserAndId(user: User, id: ObjectId): Optional<Trip>
}