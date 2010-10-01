package com.factzen.core;

import java.io.InputStream;
import java.security.PrivateKey;

import javax.security.cert.X509Certificate;

import mx.gob.sat.cfd.x2.ComprobanteDocument;

/** Define los metodos necesarios para manejar la parte criptografica de facturacion electronica.
 * La implementacion seguramente impedira que el mismo componente sea utilizable por varios Threads
 * debido a optimizaciones internas, pero un componente debe poder utilizarse varias veces en el mismo
 * Thread.
 * 
 * @author Enrique Zamudio
 */
public interface Crypto {

	/** Importa la FIEL de un usuario, que viene en un archivo PKCS#8 protegido por un password.
	 * @param stream Un InputStream para leer los datos del PKCS#8
	 * @param password El password con el que viene protegida la llave privada.
	 * @return Una llave privada RSA.
	 */
	public PrivateKey importaFIEL(InputStream stream, String password);

	/** Genera la cadena original de una factura. Esta es necesaria para el sello digital.
	 * @param factura La factura cuya cadena se debe generar.
	 * @return La cadena original, con los campos contenidos en la factura, en el formato requerido por el SAT. */
	public String generaCadenaOriginal(ComprobanteDocument factura);

	/** Genera la cadena original para la factura especificada, luego genera el sello digital para dicha cadena
	 * y finalmente incluye el sello en la factura. Se requiere el certificado de quien emite la factura para
	 * incluir el numero de serie del mismo y, opcionalmente, incluir el certificado mismo en la factura.
	 * @param doc La factura cuyo sello digital se va a generar
	 * @param fiel La llave privada con la que se genera el sello
	 * @param cert El certificado X509 correspondiente a la llave privada
	 * @param incluyeCert esta bandera indica si se debe incluir el certificado completo en la factura, o unicamente
	 * su numero de serie. */
	public void firmaFactura(ComprobanteDocument doc, PrivateKey fiel, X509Certificate cert, boolean incluyeCert);

	/** Verifica la firma de una factura, utilizando la llave publica del certificado.
	 * @param doc La factura cuyo sello digital se va a verificar.
	 * @param El certificado a utilizar. Si la factura contiene un sello digital, se utiliza ese sello en vez
	 * del especificado. */
	public boolean verificaFirma(ComprobanteDocument doc, X509Certificate cert);

	/** Verifica que el sello indicado corresponda a la cadena original indicada, utilizando la llave publica
	 * del certificado.
	 * @param cadenaOriginal La cadena original que proviene de una factura.
	 * @param sello El sello digital, codificado en Base 64.
	 * @param cert El certificado cuya llave publica se utiliza para verificar el sello. */
	public boolean verificaFirma(String cadenaOriginal, String sello, X509Certificate cert);

}
