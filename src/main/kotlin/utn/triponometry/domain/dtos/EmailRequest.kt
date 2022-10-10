package utn.triponometry.domain.dtos

data class EmailRequest (val email: String)
data class CodeRequest (val email: String, val code: String)
data class PasswordRecoverRequest (val email: String, val code: String, val newPassword: String)