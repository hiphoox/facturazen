package mx.factzen

import java.util.Date;

/** Esta clase representa un rango de folios de un usuario.
 * 
 * @author Enrique Zamudio
 */
class Folio {

	String serie
	int inicio
	int fin
	String serieCertEmisor
	Date fechaSolicitud
	int ultimo
	Date dateCreated //estas las maneja GORM
	Date lastUpdated //estas las maneja GORM

	static belongsTo = [owner:Usuario]

	static mapping = {
		table 'folio'
		id column: 'fid'
	}
    static constraints = {
		inicio(min:1)
		fin(min:inicio+1)
		ultimo(range:inicio..fin)
		serieCertEmisor(size:5..20, matches:/\d+/)
		serie(size:1..10, unique:'owner', matches:/[A-Z]+/)
    }

}
