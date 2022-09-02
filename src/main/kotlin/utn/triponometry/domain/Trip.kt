package utn.triponometry.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import utn.triponometry.domain.dtos.CalculatorInputsDto
import utn.triponometry.domain.dtos.CalculatorOutputsDto
import utn.triponometry.domain.external.dtos.TripDto

enum class TripStatus {
    ACTIVE, ARCHIVED, DRAFT
}

@Document(collection = "Trips")
class Trip(
    var name: String, var calculatorInputs: CalculatorInputsDto,
    @DBRef val user: User, @Indexed var status: TripStatus,
    var calculatorOutputs: CalculatorOutputsDto? = null) {
    @Id
    var id: ObjectId? = ObjectId.get()

    fun isStatus(status: TripStatus): Boolean {
        return this.status == status
    }

    fun dto() = TripDto(id.toString(), name,calculatorInputs,user,status,calculatorOutputs)

    fun isComplete()  = status != TripStatus.DRAFT
}