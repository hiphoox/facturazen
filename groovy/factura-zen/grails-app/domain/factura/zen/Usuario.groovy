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
	byte[] fiel      //Ver mapeo
	int status //1 activo, 2 inactivo, etc
	int saldo //en folios, no en pesos

	static mapping = {
		table 'usuario'
		id column:'uid'
	}

    static constraints = {
		rfc(unique:true, maxSize:13)
		dirCodpos(size:5..5, matches:"[0-9]{5}")
		email(email:true, blank:false)
		nombre(size:5..80, blank:false)
		password(size:5..40, blank:false)
    }

}

