package mx.factzen

class FolioController {

    def index = { }

	def foliar = { Factura fact, Usuario u ->
		//Abrimos una transaccion
		//Buscamos un folio disponible de los que tiene el usuario
		//Generamos el folio, incrementamos la serie
		//Le ponemos el folio a la factura
		//Le ponemos status de foliada
    }

}
