require 'factzen_privatekeymanager.rb'

puts "Dame la ruta completa al archivo de la FIEL:"
pk8 = File.read(gets().chop)
puts "Dame el password de la FIEL:"
pass = gets().chop
llave = FactZen::PrivateKeyManager.convert_pkcs8(pk8, pass)
if (llave)
	puts "Esta es la llave privada de la FIEL, ya en PEM:"
	puts llave
	fact = "Esta se supone que es una cadena original que se va a firmar digitalmente con la llave privada que ya leimos, solamente tenemos que pasar nuevamente el password porque no se queda en memoria y este texto a partir de aqui (bueno desde hace un rato ya) es puro relleno para tener un texto arbitrariamente largo y poder comprobar que se cifra bien."
	firma = FactZen::PrivateKeyManager.firma_datos(fact, llave, pass)
	if (firma)
		puts "Esta es la firma digital de los datos de prueba:"
		puts firma
		puts "Dame la ruta completa al certificado correspondiente a la FIEL:"
		cert = File.read(gets().chop)
		puts "Verificando que la firma sea buena"
		es_buena = FactZen::PrivateKeyManager.verifica_firma(firma, fact, cert)
		puts "Es buena? #{es_buena}"
	else
		puts "No se pudo leer la llave privada para generar la firma"
	end
else
	puts "No se pudo leer la llave, puede que el password esta mal"
end
