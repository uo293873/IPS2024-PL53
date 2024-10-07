package backend.data.empleados;

import java.util.List;

import backend.data.empleados.commands.AddEmpleadoDeportivo;
import backend.data.empleados.commands.AddEmpleadoNoDeportivo;
import backend.data.empleados.commands.CargarEmpleadosDeportivos;
import backend.data.empleados.commands.ModEmpleado;

public class EmpleadoCRUDImpl implements EmpleadosCRUDService {

	@Override
	public void addEmpleadoDeportivo(EmpleadoDTO dto) {
		new AddEmpleadoDeportivo(dto).execute();
	}

	@Override
	public void addEmpleadoNoDeportivo(EmpleadoDTO dto) {
		new AddEmpleadoNoDeportivo(dto).execute();
	}

	@Override
	public void modEmpleado(EmpleadoDTO dto) {
		new ModEmpleado(dto).execute();
	}

	@Override
	public List<EmpleadoDTO> cargarEmpleadosDeportivos() {
		return new CargarEmpleadosDeportivos().execute();
	}

	@Override
	public List<EmpleadoDTO> cargarEmpleadosNoDeportivos() {
		// TODO Auto-generated method stub
		return null;
	}

}
