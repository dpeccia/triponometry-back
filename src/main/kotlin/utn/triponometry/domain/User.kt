package utn.triponometry.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import utn.triponometry.domain.dtos.UserDtoWithoutSensitiveInformation

@Document(collection = "Users")
class User (@Indexed val mail: String, var password: String, val username: String, var verified: Boolean, var googleAccount: Boolean) {
    @Id
    var id: ObjectId? = ObjectId.get()

    fun dto() = UserDtoWithoutSensitiveInformation(id.toString(), mail, username, verified, googleAccount)
}