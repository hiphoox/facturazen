package mx.factzen

import java.text.SimpleDateFormat;

import mx.factzen.core.Crypto

class UserAuthService {

	static transactional = true
	def cryptoService

	Usuario autenticaUsuario(String rfc, String token) {
		Usuario u = Usuario.findBy(rfc:rfc)
		if (u) {
			Crypto c = cryptoService.crypto
			token = c.descifraToken(token, u.certificado.publicKey)
			if (token.startsWith(u.rfc)) {
				//Ahora validamos la fecha
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				Date d = sdf.parse(token.substring(u.rfc.length()+1))
				//Tienen que haber pasado menos de 15 minutos
				//Luego lo hacemos configurable en algun lado
				if ((d.time - System.currentTimeMillis()).abs < 900000) {
					u
				}
			}
		}
		null
	}

}
