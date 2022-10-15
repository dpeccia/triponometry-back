package utn.triponometry.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import utn.triponometry.domain.dtos.CodeRequest
import utn.triponometry.domain.dtos.UserDtoWithoutSensitiveInformation

@Document(collection = "Codes")
class Code (@Indexed val user: User, val code: String) {
    @Id
    var id: ObjectId? = ObjectId.get()

    fun code() = CodeRequest(user.mail,code)

}