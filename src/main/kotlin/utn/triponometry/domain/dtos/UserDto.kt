package utn.triponometry.domain.dtos

data class UserDto (val mail: String, val password: String)
data class UserDtoWithoutSensitiveInformation(val id: String, val mail: String)