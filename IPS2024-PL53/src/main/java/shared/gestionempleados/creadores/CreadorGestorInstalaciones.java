package shared.gestionempleados.creadores;

import java.util.Date;

import backend.service.empleados.EmpleadoNoDeportivo;
import backend.service.empleados.nodeportivos.GestorInstalaciones;

public class CreadorGestorInstalaciones implements CreadorEmpleadoNoDeportivo {

	@Override
	public EmpleadoNoDeportivo getEmpleado(String nombre, String apellido, String DNI, String telefono, Date fechaNac,
			double salario) {
		return new GestorInstalaciones(nombre, apellido, DNI, telefono, fechaNac);
	}

}
