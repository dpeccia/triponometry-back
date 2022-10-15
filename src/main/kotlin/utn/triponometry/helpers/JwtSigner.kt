package utn.triponometry.helpers

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
object JwtSigner {
    private val keyPair: KeyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)

    fun createJwt(userId: String) =
        Jwts.builder()
            .signWith(keyPair.private, SignatureAlgorithm.RS256)
            .setSubject(userId)
            .setIssuer("identity")
            .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(60))))
            .setIssuedAt(Date.from(Instant.now()))
            .compact()

    fun validateJwt(jwt: String?): Jws<Claims> {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(keyPair.public)
                .build()
                .parseClaimsJws(jwt)
        } catch (e: Exception) {
            throw TokenException("Tu sesión expiró. Iniciá sesión nuevamente")
        }
    }
}