package factura.zen

import javax.security.cert.X509Certificate
import java.security.PrivateKey

/** Representa un usuario del sistema de FacturaZen.

@author Enrique Zamudio
*/
class Usuario {

	String rfc
	String nombre
	String password
	String email
	boolean personaMoral
	String dirCalle
	String dirNumExt
	String dirNumInt
	String dirColonia
	String dirMunicipio
	String dirEstado
	String dirCodpos
	Date nacimiento
	Date dateCreated //estas las maneja GORM
	Date lastUpdated //estas las maneja GORM
	byte[] cert
	/** La FIEL cifrada como la entrega el SAT. */
	byte[] fiel
	int status //1 activo, 2 inactivo, etc
	int saldo //en folios, no en pesos
	int tipo //prepago, suscripcion mensual, etc
	private X509Certificate _cert

	static transients = ['_cert']

	static mapping = {
		table 'usuario'
		id column:'uid'
	}

    static constraints = {
		rfc(unique:true, maxSize:13)
		dirCodpos(size:5..5, matches:/\d{5}/)
		email(email:true, blank:false)
		nombre(size:5..80, blank:false)
		password(size:5..40, blank:false)
    }

	/** Devuelve el certificado X509, decodificandolo una sola vez. */
	X509Certificate getCertificado() {
		if (_cert == null) {
			_cert = X509Certificate.getInstance(cert)
		}
		_cert
	}

}

