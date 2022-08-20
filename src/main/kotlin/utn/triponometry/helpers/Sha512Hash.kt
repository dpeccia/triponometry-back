package utn.triponometry.helpers

import org.springframework.stereotype.Service
import utn.triponometry.properties.TriponometryProperties
import java.security.MessageDigest

@Service
class Sha512Hash(val triponometryProperties: TriponometryProperties) {
    fun getSHA512(_toHash: String): String {
        var toHash = _toHash
        for (i in 0..99999) {
            toHash = SHA512once(toHash + triponometryProperties.hash.salt)
        }
        return SHA512once(toHash)
    }

    private fun SHA512once(toHash: String): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        md.update(toHash.toByteArray())
        val mb = md.digest()
        var out = ""
        for (i in mb.indices) {
            val temp = mb[i]
            var s = Integer.toHexString(temp.toInt())
            while (s.length < 2) {
                s = "0$s"
            }
            s = s.substring(s.length - 2)
            out += s
        }
        return out
    }
}