package utn.triponometry.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
class User (@Indexed val mail: String, val password: String) {
    @Id
    var id: ObjectId? = ObjectId.get()
}