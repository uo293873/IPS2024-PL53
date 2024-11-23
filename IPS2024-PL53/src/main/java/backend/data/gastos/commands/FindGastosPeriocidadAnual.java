package backend.data.gastos.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backend.data.Database;
import backend.data.gastos.GastoDto;

public class FindGastosPeriocidadAnual {
	
	private static final String QUERY = "SELECT"
			+ " SUM(SALARIO_ANUAL) / 12 AS SALARIO_MENSUAL_TOTAL "
			+ "FROM"
			+ " EMPLEADO_NO_DEPORTIVO "
			+ "UNION ALL "
			+ "SELECT"
			+ " SUM(SALARIO_ANUAL) / 12 "
			+ "FROM"
			+ " EMPLEADO_DEPORTIVO";

	private Database db = new Database();

	public List<GastoDto> execute() {
		List<Map<String, Object>> mapGastos = db.executeQueryMap(QUERY);
		GastoDto gastoMensualPeriodico = mapsToGastos(mapGastos).get(0);
		List<GastoDto> gastos = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			GastoDto gasto = new GastoDto();
			gasto.mes = i;
			gasto.gasto = gastoMensualPeriodico.gasto;
			gastos.add(gasto);
		}
		return gastos;
	}
	
	private List<GastoDto> mapsToGastos(List<Map<String, Object>> listaMap) {
		List<GastoDto> lista = new ArrayList<>();
	    
	    // Recorre cada mapa y convierte los datos en un objeto.
	    for (Map<String, Object> fila : listaMap) {
	    	GastoDto dto = new GastoDto(); // Renombrado a dto
	        BigDecimal gasto = (BigDecimal) fila.get("SALARIO_MENSUAL_TOTAL");
	        dto.gasto = gasto.floatValue();
	        
	        lista.add(dto);
	    }
	    return lista; 		
	}


}
