import mx.factzen.core.Crypto
import mx.factzen.core.impl.CryptoImpl

/** Este servicio provee instancias de Crypto a los componentes que necesiten.
 * 
 * @author Enrique Zamudio
 */
public class CryptoService {

	static transactional = false

	Crypto getCrypto() {
		new CryptoImpl()
	}

}
