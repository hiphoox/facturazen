package com.factzen.core.impl

import java.io.StringWriter
import java.security.PrivateKey
import java.security.PublicKey;
import java.security.Signature
import java.util.Arrays

import javax.crypto.Cipher;
import javax.security.cert.X509Certificate
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult

import org.apache.commons.ssl.Base64
import org.apache.commons.ssl.PKCS8Key
import org.xml.sax.InputSource

import com.factzen.core.Crypto

import mx.gob.sat.cfd.x2.ComprobanteDocument

/** Esta clase realiza las operaciones criptograficas necesarias para generar el sello digital de una factura
 * asi como verificar el sello de facturas existentes.
 * IMPORTANTE: Esta clase no debe ser utilizada por distintos threads simultaneamente. Cada thread debe tener
 * su propia instancia, por lo que se recomienda usar un pool de objetos o tenerlos en un ThreadLocal.
 *
 * @author Enrique Zamudio
 */
public class CryptoImpl implements Crypto {

	private Transformer xform
	private Signature rsaSign = Signature.getInstance("MD5withRSA")
	private Cipher rsa = Cipher.getInstance("RSA")

	/** Fija la ruta al archivo XSLT para generar la cadena origina. Se busca dentro del classpath. */
	void setXsltPath(String value) {
		InputStream is = getClass().getResourceAsStream(value)
		if (is) {
			xform = TransformerFactory.newInstance().newTransformer(
				new SAXSource(new InputSource(new ByteArrayInputStream(is.getBytes()))))
			is.close()
		} else {
			throw new IllegalArgumentException("No encuentro el recurso ${value} dentro del classpath")
		}
	}

	PrivateKey importaFIEL(InputStream stream, String password) {
		PKCS8Key pk8 = new PKCS8Key(stream, password.toCharArray())
		pk8.privateKey
	}

	PrivateKey importaFIEL(byte[] bytea, String password) {
		PKCS8Key pk8 = new PKCS8Key(bytea, password)
		pk8.privateKey
	}

	/** Genera la cadena original de una factura, utilizando el XSLT provisto por el SAT. */
	String generaCadenaOriginal(ComprobanteDocument factura) {
		//Generamos la cadena original a partir de la factura con el XSLT del SAT
		StringWriter writer = new StringWriter()
		xform.transform(new SAXSource(new InputSource(
			new StringReader(factura.toString()))), new StreamResult(writer))
		return writer.getBuffer().toString()
	}

	/** Genera el sello digital para una factura y lo guarda en la misma. Se incluye tambien el numero de serie
	 * del certificado X509 especificado y, opcionalmente, el certificado mismo, codificado en base64. */
	void firmaFactura(ComprobanteDocument doc, PrivateKey fiel, X509Certificate cert, boolean incluyeCert) {
		String cadenaOriginal = generaCadenaOriginal(doc)
		ComprobanteDocument.Comprobante factura = doc.comprobante;
		rsaSign.initSign(fiel)
		rsaSign.update(cadenaOriginal.getBytes())
		factura.sello = Base64.encodeBase64String(rsaSign.sign()).split().join("")
		factura.noCertificado = new String(cert.getSerialNumber().toByteArray())
		if (incluyeCert) {
			factura.certificado = Base64.encodeBase64String(cert.getEncoded()).split().join("")
		}
	}

	/** Verifica que el sello digital sea valido en una factura. */
	boolean verificaFirma(ComprobanteDocument doc, X509Certificate cert) {
		if (doc.comprobante.certificado) {
			//Usamos este certificado
			cert = X509Certificate.getInstance(Base64.decodeBase64(doc.comprobante.certificado))
		}
		//Comparamos el numero de serie de la factura con el numero de serie del certificado
		//Resulta que se crea mal en el X509 que generamos nosotros, por lo que hay que comparar ambas posibilidades
		if (doc.comprobante.noCertificado == cert.serialNumber.toString()
				|| new BigInteger(doc.comprobante.noCertificado.getBytes()) == cert.serialNumber) {
			return verificaFirma(generaCadenaOriginal(doc), doc.comprobante.sello, cert)
		} else {
			println "no coincide el numero de serie del certificado ${cert.serialNumber} con el de la factura ${doc.comprobante.noCertificado}"
			return false
		}
	}

	boolean verificaFirma(String cadenaOriginal, String sello, X509Certificate cert) {
		rsaSign.initVerify(cert.publicKey)
		rsaSign.update(cadenaOriginal.getBytes())
		return rsaSign.verify(Base64.decodeBase64(sello))
	}

	boolean match(PrivateKey fiel, X509Certificate cert) {
		byte[] block = new byte[64]
		Arrays.fill(block, 0)
		rsaSign.initSign(fiel)
		rsaSign.update(block)
		byte[] b2 = rsaSign.sign()
		rsaSign.initVerify(cert.publicKey)
		rsaSign.update(block)
		rsaSign.verify(b2)
	}

	String descifraToken(String token, PublicKey key) {
		rsa.init(Cipher.ENCRYPT_MODE, key)
		byte[] raw = Base64.decodeBase64(token)
		raw = rsa.doFinal(raw)
		new String(raw)
	}

}
