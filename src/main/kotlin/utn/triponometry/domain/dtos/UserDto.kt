package utn.triponometry.domain.dtos

data class UserLogin (val mail: String, val password: String)
class UserRequest (val mail: String, val password: String, val username: String){
    fun userLogin() = UserLogin(mail,password)
}
data class UserDto (val mail: String, val password: String, val username: String, val verified: Boolean)
data class UserDtoWithoutSensitiveInformation(val id: String, val mail: String, val username: String, val verified: Boolean)