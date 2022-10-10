package utn.triponometry.helpers

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.MimeMessage


class EmailSender() {

    private val username = "triponometrytrips@gmail.com"
    private val password = "ckhzzjcnywwobplz"

    fun sendEmail(email: String, passwordCode: String) {
        val props = Properties()
        props["mail.smtp.host"] = "smtp.googlemail.com"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.auth"] = "true"
        props["mail.user"] = "Triponometry"
        props["mail.transport.protocol"] = "smtp"
        val session: Session = Session.getInstance(props, null)

        val msg = MimeMessage(session)
        msg.setFrom()
        msg.setRecipients(Message.RecipientType.TO, email)
        msg.subject = "Reestablecé tu contraseña de Triponometry"
        msg.sentDate = Date()
        msg.setText(
            """
           Tu código para reestablecer la contraseña es: """.trimIndent() + passwordCode +
            """
               
               
           Si no solicitaste el cambio de contraseña, por favor ignorá este email.
           
           Gracias ✈️
        """.trimIndent()
        )
        try {
            Transport.send(msg, username, password)
        } catch (e: Exception) {
            throw IllegalUserException("No se pudo enviar el email")
        }

    }

    fun generateCode() = (ThreadLocalRandom.current().nextInt(900000) + 100000).toString()

}