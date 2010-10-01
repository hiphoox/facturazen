require 'openssl'
require 'base64'
require 'facets/string/xor'
require 'base64'

module FactZen

=begin
Esta clase decodifica las FIEL (llaves privadas RSA en formato PKCS#8) y las convierte
a cadenas mas simples, o instancias de OpenSSL::PKey::RSA para simplificar su manejo.
=end
class PrivateKeyManager

=begin
Convierte una llave PKCS8 en una llave RSA sin cifrar (vienen publica y privada).
	@param llave Los bytes con la llave codificada en PKCS8 como las entrega el SAT.
	@param pass El password del usuario para decodificar la llave.
	@return Una instancia de OpenSSL::PKey::RSA
=end
def self.convert_pkcs8(llave, pass)
	pk8 = OpenSSL::ASN1.decode(llave)
	#Esto nos da 'PBES2' pk8.value[0].value[0].value
	#Esto es el tipo de codificacion PKCS5
	tipo = pk8.value[0].value[1].value[0].value[0].value
	#Esto es la sal para derivar la llave de PBE
	sal1 = pk8.value[0].value[1].value[0].value[1].value[0].value
	#El numero de iteraciones para la derivacion de la llave PBE
	iters = pk8.value[0].value[1].value[0].value[1].value[1].value
	algoritmo = pk8.value[0].value[1].value[1].value[0].value
	#Esta es la sal para descifrar la llave publica
	sal2 = pk8.value[0].value[1].value[1].value[1].value
	#Esta es la llave privada, cifrada
	cipher = pk8.value[1].value

	#Comenzamos descifrado
	final_result = ""
	des = OpenSSL::Cipher.new(algoritmo)
	if (tipo == 'PBKDF2')
		hmac = OpenSSL::HMAC.new(pass, "SHA1")
		blocks = (20 + des.key_len + des.iv_len - 1) / 20
		block_idx = [0,0,0,0]
		1.upto(blocks) { |i|
			block_idx[0] = (i >> 24)&0xff
			block_idx[1] = (i >> 16)&0xff
			block_idx[2] = (i >> 8)&0xff
			block_idx[3] = i & 0xff
			hmac.reset
			hmac << sal1
			hmac << block_idx.pack('C*')
			r2 = hmac.digest
			result = r2.clone
			1.upto(iters-1) {
				hmac.reset
				hmac << r2
				r2 = hmac.digest
				result ^= r2
			}
			final_result << result
		}
		des.key=final_result[0...des.key_len]
		des.iv=sal2
	else
		#Esto creo que ni sirve
		md5 = OpenSSL::Digest.new("MD5")
		#Derivamos la llave
		md5 << pass
		md5 << sal
		deriv = md5.digest
		1.upto(iters-1) {
			md5.reset
			md5 << deriv
			deriv = md5.digest
		}
		des.key=deriv + deriv[0..7]
		des.iv=sal
	end
	puts "Descifrando con #{des.name}"
	des.decrypt
	begin
		llave = des.update(cipher)
		llave << des.final
		#Y todavia viene una mierda en ASN y dentro viene la llave
		pks8 = OpenSSL::ASN1.decode(llave).value[2].value
		rsa = OpenSSL::PKey::RSA.new(pks8)
		encrypt_key(rsa, pass)
	rescue OpenSSL::Cipher::CipherError, OpenSSL::ASN1::ASN1Error
		nil #Aqui llegamos si la llave se hizo mal
	end
end

# Cifra la llave privada (una instancia de OpenSSL::PKey::RSA) y la codifica en una cadena PEM.
def self.encrypt_key(rsa, pass)
	rsa.to_pem(OpenSSL::Cipher.new("AES-128-CBC"), pass)
end

#Firma los datos usando la llave indicada (es una cadena PEM que se descifra con el password)
def self.firma_datos(datos, llave, pass)
	md5 = OpenSSL::Digest.new("MD5")
	md5.update(datos)
	datos = md5.digest
	begin
		rsa = OpenSSL::PKey::RSA.new(llave, pass)
		Base64.encode64(rsa.private_encrypt(datos))
	rescue OpenSSL::PKey::RSAError
		nil
	end
end

# Verifica que la firma especificada corresponda a los datos indicados, usando la llave
# publica contenida en el certificado indicado (archivo DER o PEM)
def self.verifica_firma(firma, datos, certdata)
	cert = OpenSSL::X509::Certificate.new(certdata)
	#Sacarmos el MD5 de los datos
	md5 = OpenSSL::Digest.new("MD5")
	md5.update(datos)
	datos = md5.digest
	begin
		firma = cert.public_key.public_decrypt(Base64.decode64(firma))
		firma == datos
	rescue OpenSSL::PKey::RSAError
		false
	end
end

end #class PrivateKeyManager

end #module