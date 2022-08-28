package utn.triponometry.domain

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import utn.triponometry.domain.dtos.CalculatorInputsDto
import utn.triponometry.domain.dtos.CalculatorOutputsDto
import utn.triponometry.domain.dtos.UserDtoWithoutSensitiveInformation
import utn.triponometry.domain.external.dtos.TripDto

enum class TripStatus {
    ACTIVE, ARCHIVED, DRAFT
}

@Document(collection = "Trips")
class Trip(val name: String, val calculatorInputs: CalculatorInputsDto, val calculatorOutputs: CalculatorOutputsDto,
           @DBRef val user: User, @Indexed var status: TripStatus) {
    @Id
    var id: ObjectId? = ObjectId.get()

    fun isStatus(status: TripStatus): Boolean {
        return this.status == status
    }

    fun dto() = TripDto(id.toString(), name,calculatorInputs,calculatorOutputs,user,status)
}