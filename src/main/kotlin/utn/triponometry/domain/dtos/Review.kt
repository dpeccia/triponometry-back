package utn.triponometry.domain.dtos

import org.bson.types.ObjectId
import utn.triponometry.domain.User

data class ReviewRequest(
    val stars: Int,
    val done: Boolean,
    val description: String? = null
)

class Review(
    val id: ObjectId,
    val user: User,
    val stars: Int,
    val done: Boolean,
    val description: String? = null
) {
    fun dto() = ReviewDto(id.toString(), user.id.toString(), stars, done, description)
    fun fromUser(userReq: ObjectId) = userReq == user.id
}


data class ReviewDto(
    val id: String,
    val user: String,
    val stars: Int,
    val done: Boolean,
    val description: String? = null
)