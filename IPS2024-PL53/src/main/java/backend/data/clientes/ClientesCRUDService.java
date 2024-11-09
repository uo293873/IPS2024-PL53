package backend.data.clientes;

public interface ClientesCRUDService {

	ClienteDTO findByDniCliente(String dni);
	
	void addCliente(ClienteDTO cliente);
}
