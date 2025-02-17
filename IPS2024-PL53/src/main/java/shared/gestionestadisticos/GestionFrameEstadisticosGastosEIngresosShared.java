package shared.gestionestadisticos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import backend.data.gastos.GastoDto;
import backend.data.ventas.VentaDto;
import frontend.SwingUtil;
import frontend.estadisticos.FrameEstadisticosGastosEIngresos;

public class GestionFrameEstadisticosGastosEIngresosShared {

	private static final String NO_PERIOCIDAD = "";
	private static final String PERIOCIDAD_MENSUAL = "Mensual";
	private static final String PERIOCIDAD_ANUAL = "Anual";

	private GestionEstadisticosGastosEIngresosShared gesEst = new GestionEstadisticosGastosEIngresosShared();
	private FrameEstadisticosGastosEIngresos view;
	private String periocidad = "";
	private ChartPanel graficoActual;
	// Configurar la región a España para el idioma de los objetos Month
	private Locale spanishLocale = new Locale("es", "ES");

	public GestionFrameEstadisticosGastosEIngresosShared(FrameEstadisticosGastosEIngresos view) {
		this.view = view;
	}

	public void initController() {
		view.getCbPeriocidad().addActionListener(e -> SwingUtil.exceptionWrapper(() -> seleccionarPeriocidad()));
		view.getBtMostrarGrafico().addActionListener(e -> SwingUtil.exceptionWrapper(() -> mostrarGrafico()));
	}

	private void mostrarGrafico() {
		if (graficoActual != null)
			view.getPnGrafico().remove(graficoActual);
		
		if (periocidad == PERIOCIDAD_ANUAL) {
			mostrarGraficoAnual();
		} else if (periocidad == PERIOCIDAD_MENSUAL) {
			mostrarGraficoMensual();
		}

	}

	private void mostrarGraficoMensual() {
		Month mesObjetoMonth = Month.of(view.getMonthChooser().getMonth()+1);
		int year = view.getYearChooser().getYear();

		List<VentaDto> ingresosPeriocidadMensual = gesEst.getIngresosMensuales(mesObjetoMonth, year);
		List<GastoDto> gastosPeriocidadMensual = gesEst.getGastosMensuales(mesObjetoMonth, year);

		// Crea el dataset y añade los datos de la lista de ventas
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Añadimos gastos al gráfico
		for (GastoDto gasto : gastosPeriocidadMensual) {
			int dia = gasto.dia;
			dataset.addValue(gasto.gasto, "Gastos", dia + "");
		}

		// Crea un mapa de ingresos por mes para facilitar el acceso
		Map<Integer, Float> ingresosPorDia = new HashMap<>();
		// Añadimos los ingresos al dataset del gráfico y al map de ingresos por mes
		for (VentaDto venta : ingresosPeriocidadMensual) {
			int dia = venta.dia;
			dataset.addValue(venta.getCoste(), "Ingresos", dia + "");
			ingresosPorDia.put(dia, venta.getCoste());
		}

		// Añadimos balance al gráfico
		for (GastoDto gasto : gastosPeriocidadMensual) {
			double ingreso = ingresosPorDia.getOrDefault(gasto.dia, (float) 0.0); // Si no hay ingresos, asigna 0
			double balance = ingreso - gasto.gasto;

			dataset.addValue(balance, "Balance", gasto.dia + "");
		}
		
		// Obtener el nombre del mes en español (estilo completo)
        String nombreMes = mesObjetoMonth.getDisplayName(TextStyle.FULL, spanishLocale);
        

		// Crea el gráfico de barras agrupado
		JFreeChart barChart = ChartFactory.createBarChart("Gráfico de Gastos/Ingresos " + nombreMes.toUpperCase(), // Título
				"Día", // Etiqueta del eje X
				"Cantidad (\u20AC)", // Etiqueta del eje Y
				dataset, // Dataset
				PlotOrientation.VERTICAL, true, true, false);

		// Cambiamos los colores del gráfico
		CategoryPlot plot = barChart.getCategoryPlot();

		// Configura un renderizador para personalizar los colores
		BarRenderer renderer = (BarRenderer) plot.getRenderer();

		// Asigna colores a cada serie (por índice)
		renderer.setSeriesPaint(0, Color.RED); // Color para "Gastos"
		renderer.setSeriesPaint(1, Color.GREEN); // Color para "Ingresos"
		renderer.setSeriesPaint(2, Color.BLUE); // Color para "Balance"

		plot.setRenderer(renderer);

		// Inserta el gráfico en un ChartPanel
		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new Dimension(800, 600));

		graficoActual = chartPanel;
		view.getPnGrafico().add(chartPanel, BorderLayout.CENTER);
		view.getPnGrafico().revalidate(); // Asegura que el panel se redibuje
	}

	private void mostrarGraficoAnual() {
		int year = view.getYearChooser().getYear();
		
		List<VentaDto> ingresosPeriocidadAnual = gesEst.getVentasAnuales(year);
		List<GastoDto> gastosPeriocidadAnual = gesEst.getGastosAnuales();

		// Crea el dataset y añade los datos de la lista de ventas
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Añadimos gastos al gráfico
		for (GastoDto gasto : gastosPeriocidadAnual) {
			Month mes = Month.of(gasto.mes);
			// Obtener el nombre del mes en español (estilo completo)
	        String nombreMes = mes.getDisplayName(TextStyle.FULL, spanishLocale);
			
			dataset.addValue(gasto.gasto, "Gastos", nombreMes);
		}

		// Crea un mapa de ingresos por mes para facilitar el acceso
		Map<Integer, Float> ingresosPorMes = new HashMap<>();
		// Añadimos los ingresos al dataset del gráfico y al map de ingresos por mes
		for (VentaDto venta : ingresosPeriocidadAnual) {
			// Obtener el nombre del mes en español (estilo completo)
	        String nombreMes = Month.of(venta.mes).getDisplayName(TextStyle.FULL, spanishLocale);
			
			dataset.addValue(venta.getCoste(), "Ingresos", nombreMes);
			ingresosPorMes.put(venta.mes, venta.getCoste());
		}

		// Añadimos balance al gráfico
		for (GastoDto gasto : gastosPeriocidadAnual) {
			// Obtener el nombre del mes en español (estilo completo)
	        String nombreMes = Month.of(gasto.mes).getDisplayName(TextStyle.FULL, spanishLocale);

			double ingreso = ingresosPorMes.getOrDefault(gasto.mes, (float) 0.0); // Si no hay ingresos, asigna 0
			double balance = ingreso - gasto.gasto;

			dataset.addValue(balance, "Balance", nombreMes);
		}

		// Crea el gráfico de barras agrupado
		JFreeChart barChart = ChartFactory.createBarChart("Gráfico de Gastos/Ingresos " + year, // Título
				"Mes", // Etiqueta del eje X
				"Cantidad (\u20AC)", // Etiqueta del eje Y
				dataset, // Dataset
				PlotOrientation.VERTICAL, true, true, false);

		// Cambiamos los colores del gráfico
		CategoryPlot plot = barChart.getCategoryPlot();

		// Configura un renderizador para personalizar los colores
		BarRenderer renderer = (BarRenderer) plot.getRenderer();

		// Asigna colores a cada serie (por índice)
		renderer.setSeriesPaint(0, Color.RED); // Color para "Gastos"
		renderer.setSeriesPaint(1, Color.GREEN); // Color para "Ingresos"
		renderer.setSeriesPaint(2, Color.BLUE); // Color para "Balance"

		plot.setRenderer(renderer);

		// Inserta el gráfico en un ChartPanel
		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new Dimension(800, 600));

		graficoActual = chartPanel;
		view.getPnGrafico().add(chartPanel, BorderLayout.CENTER);
		view.getPnGrafico().revalidate(); // Asegura que el panel se redibuje
	}

	private void seleccionarPeriocidad() {
		String seleccion = (String) view.getCbPeriocidad().getSelectedItem();
		
		if (graficoActual != null)
			view.getPnGrafico().remove(graficoActual);

		view.getLbYear().setText("");
		view.getPnPeriocidad().remove(view.getMonthChooser());
		view.getPnPeriocidad().remove(view.getYearChooser());
		view.getPnPeriocidad().remove(view.getBtMostrarGrafico());
		view.getPnPeriocidad().remove(view.getLbMes());
		;
		view.repaint();
		if (seleccion.isEmpty()) {
			periocidad = NO_PERIOCIDAD;
		} else {
			if (seleccion.equalsIgnoreCase("Mensual")) {
				periocidad = PERIOCIDAD_MENSUAL;
				view.getLbYear().setText("Año:");
				view.getPnPeriocidad().add(view.getYearChooser());
				view.getPnPeriocidad().add(view.getLbMes());
				view.getPnPeriocidad().add(view.getMonthChooser());
				view.getLbMes().setText("Mes:");
			} else if (seleccion.equalsIgnoreCase("Anual")) {
				periocidad = PERIOCIDAD_ANUAL;
				view.getLbYear().setText("Año:");
				view.getPnPeriocidad().add(view.getYearChooser());
			}
			view.getPnPeriocidad().add(view.getBtMostrarGrafico());
		}

	}

}
