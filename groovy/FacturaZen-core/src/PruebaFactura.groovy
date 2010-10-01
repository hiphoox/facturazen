
import java.security.PrivateKey;

import javax.security.cert.X509Certificate;

import com.factzen.core.impl.CryptoImpl;

import mx.gob.sat.cfd.x2.ComprobanteDocument
import mx.gob.sat.cfd.x2.ComprobanteDocument.Comprobante
import mx.gob.sat.cfd.x2.ComprobanteDocument.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd.x2.ComprobanteDocument.Comprobante.Impuestos.Traslados.Traslado;
import mx.gob.sat.cfd.x2.ComprobanteDocument.Comprobante.Impuestos.Traslados.Traslado.Impuesto;
import mx.gob.sat.cfd.x2.TUbicacion;
import mx.gob.sat.cfd.x2.TUbicacionFiscal;

ComprobanteDocument doc = ComprobanteDocument.Factory.newInstance()
Comprobante factura = doc.addNewComprobante()
Comprobante.Conceptos concs = factura.addNewConceptos()
Comprobante.Emisor emisor = factura.addNewEmisor()
Comprobante.Receptor receptor = factura.addNewReceptor()
factura.fecha = new GregorianCalendar()
factura.folio = "1234567890"
factura.subTotal = 0
emisor.nombre = "Enrique Zamudio Lopez"
emisor.rfc = "ZALE730125IM8"
TUbicacionFiscal dir = TUbicacionFiscal.Factory.newInstance()
dir.calle = "San Felipe"
dir.codigoPostal ="03330"
dir.colonia = "Xoco"
dir.estado = "Distrito Federal"
dir.municipio = "Benito Juarez"
dir.noExterior = "85"
dir.noInterior = "D104"
dir.pais = "Mexico"
emisor.domicilioFiscal = dir
receptor.nombre = "Web Comunicaciones S. C."
receptor.rfc = "WCO010203ABC"
TUbicacion dir2 = TUbicacion.Factory.newInstance()
dir2.calle = "San Francisco Figuraco"
dir2.colonia = "Coyoacan"
dir2.municipio = "Coyoacan"
dir2.noExterior = "5A"
receptor.domicilio = dir2
Concepto c = concs.addNewConcepto()
c.cantidad = 1
c.descripcion = "Item de prueba"
c.valorUnitario = 46.58
c.importe = c.valorUnitario * c.cantidad
c.unidad = "Kg"
factura.subTotal += c.valorUnitario
c = concs.addNewConcepto()
c.cantidad = 3
c.descripcion = "Otro item de prueba"
c.unidad = "m"
c.valorUnitario = 19.57
c.importe = c.valorUnitario * c.cantidad
factura.subTotal += c.valorUnitario
Traslado iva = factura.addNewImpuestos().addNewTraslados().addNewTraslado()
iva.impuesto = Impuesto.IVA
iva.tasa = 15
iva.importe = factura.subTotal * iva.tasa.movePointLeft(2)
factura.total = factura.subTotal + iva.importe
println "Generando XML para factura recien creada en memoria: ${doc.toString()}"
CryptoImpl crypto = new CryptoImpl()
crypto.setXsltPath("/cadenaoriginal_2_0.xslt")
LineNumberReader stdin = new LineNumberReader(new InputStreamReader(System.in))
println "Dame la ruta completa al archivo de la FIEL:"
File f = new File(stdin.readLine())
FileInputStream fin = new FileInputStream(f)
println "Dame el password de la FIEL:"
PrivateKey fiel = crypto.importaFIEL(fin, stdin.readLine())
fin.close()
f = new File(f.parentFile, f.name.substring(0, f.name.lastIndexOf('.')) + ".cer")
X509Certificate cert = X509Certificate.getInstance(f.getBytes())
crypto.firmaFactura(doc, fiel, cert, true)
println "Factura en XML nuevamente, pero ya firmada: ${doc.toString()}"
println "Verificando... ${crypto.verificaFirma(doc, cert)}"

println "Leyendo XML"
ComprobanteDocument fromXml = ComprobanteDocument.Factory.parse(new File("/Users/ezamudio/Projects/FacturaZen/ejemplo.xml"))
fact = fromXml.getComprobante()
println "Datos basicos: ${fact.fecha.time} folio ${fact.folio} forma de pago ${fact.formaDePago}"
println "Emisor: ${fact.emisor.nombre} (${fact.emisor.rfc})"
println "Receptor: ${fact.receptor.nombre} (${fact.receptor.rfc})"
println "Conceptos:"
fact.conceptos.conceptoArray.each {
	println "${it.cantidad} ${it.descripcion} (${it.valorUnitario} C/U) = ${it.importe}"
}
println "Subtotal: ${fact.subTotal} total ${fact.total}"
println "Firma es valida? ${crypto.verificaFirma(fromXml, null)}"
