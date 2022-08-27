package utn.triponometry.controllers

import org.bson.types.ObjectId
import org.springframework.web.util.WebUtils
import utn.triponometry.helpers.JwtSigner
import javax.servlet.http.HttpServletRequest

abstract class BaseController {
    fun checkAndGetUserId(request: HttpServletRequest): ObjectId {
        val cookie = WebUtils.getCookie(request, "X-Auth")
        val jwt = cookie?.value
        return ObjectId(JwtSigner.validateJwt(jwt).body.subject)
    }
}