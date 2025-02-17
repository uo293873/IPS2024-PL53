package shared.gestionProductos;

import java.awt.CardLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import backend.data.CreadorDataService;
import backend.data.clientes.ClienteDTO;
import backend.data.clientes.ClientesCRUDService;
import backend.data.merchandising.MerchandisingCRUDService;
import backend.data.merchandising.MerchandisingDTO;
import backend.data.productos.CompraProductoDTO;
import backend.data.productos.ProductoCRUDService;
import backend.data.productos.ProductoDTO;
import backend.data.ventas.VentaDto;
import backend.data.ventas.VentasCRUDService;
import backend.service.ventas.merchandising.CrearFactura;
import backend.service.ventas.merchandising.EnviarCorreo;
import backend.service.ventas.merchandising.Producto;
import backend.service.ventas.merchandising.VentaMerchandising;
import frontend.SwingUtil;
import frontend.merchandisingUI.PanelProducto;
import frontend.merchandisingUI.VentanaPrincipal;

public class GestionProductoShared {
	private ProductoCRUDService model;
	private VentanaPrincipal view;
	private VentaMerchandising ventaMerchandising;
	
	public GestionProductoShared(ProductoCRUDService m, VentanaPrincipal v) {
		this.model = m;
		this.view = v;
		
		model = CreadorDataService.getproductoService();

		this.initView();
	}
	
	/**
	 * Inicializacion del controlador: anyade los manejadores de eventos a los objetos del UI.
	 * Cada manejador de eventos se instancia de la misma forma, para que invoque un metodo privado
	 * de este controlador, encerrado en un manejador de excepciones generico para mostrar ventanas
	 * emergentes cuando ocurra algun problema o excepcion controlada.
	 */
	public void initController() {
		view.getBtnCancel().addActionListener(e -> SwingUtil.exceptionWrapper(() -> initView()));
		
		view.getBtnNext1().addActionListener(e -> SwingUtil.exceptionWrapper(() -> accionSiguiente1()));
		
		view.getBtnPrevious2().addActionListener(e -> SwingUtil.exceptionWrapper(() -> accionAnterior()));
		
		view.getBtnNext2().addActionListener(e -> SwingUtil.exceptionWrapper(() -> showPn3()));
		
		view.getBtTodos().addActionListener(e -> SwingUtil.exceptionWrapper(() -> createProductPanels(model.findAllProducts())));
		
		view.getBtEquipaciones().addActionListener(e -> SwingUtil.exceptionWrapper(() -> createProductPanels(model.getFilterProducts("equipacion"))));
		
		view.getBtModaTextil().addActionListener(e -> SwingUtil.exceptionWrapper(() -> createProductPanels(model.getFilterProducts("moda textil"))));
		
		view.getBtAccesorios().addActionListener(e -> SwingUtil.exceptionWrapper(() -> createProductPanels(model.getFilterProducts("accesorio"))));
		
		view.getBtnFinish().addActionListener(e -> SwingUtil.exceptionWrapper(() -> accionFinal()));
		
		view.getTfCorreo().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfCorreo());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfCorreo());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfCorreo());
            } 
        });
		
		view.getTfNombre().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfNombre());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfNombre());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfNombre());
            } 
        });
		
		view.getTfDNI().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfDNI());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfDNI());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfDNI());
            } 
        });
		
		view.getTfDomicilio().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfDomicilio());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfDomicilio());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleButtonState();
                setBorderColor(view.getTfDomicilio());
            } 
        });
	}

	public void initView() {
		ventaMerchandising = new VentaMerchandising();
		view.getTxtPrice().setText("");
		view.getCarritoTextArea().setText("");
		view.getTfCorreo().setText("");
		view.getBtnNext1().setEnabled(false);
		createProductPanels(model.findAllProducts());
		showPn1();
	}
	
	private void showPn1() {
		view.getPnInfo1().add(view.getPnOrder());
		view.getPnBts1().add(view.getTxtPrice(), 0);
		((CardLayout) view.getPnContents().getLayout()).show(view.getPnContents(), "pn1");
	}
	
	private void showPn2() {
		view.getPnInfo2().add(view.getTxtPrice(), 0);
		((CardLayout) view.getPnContents().getLayout()).show(view.getPnContents(), "pn2");
	}
	
	private void showPn3() {
		((CardLayout) view.getPnContents().getLayout()).show(view.getPnContents(), "pn3");
		
	}
	
	private void accionSiguiente1() {
		addProductosResumen();
		showPn2();
	}
	
	private void accionAnterior() {
		showPn1();
	}
	
	private void accionFinal() {
		List<ProductoDTO> list = new ArrayList<ProductoDTO>();
		String codCompra = ventaMerchandising.getCodCompra();
		for(Producto p : ventaMerchandising.getProductos()) {
			list.add(new ProductoDTO(p.getCode(), p.getType(), p.getName(), p.getPrice(),p.getUnits(),codCompra));
		}
		saveOrder(list, codCompra, ventaMerchandising.getFechaCompra(),ventaMerchandising.getPrecioTotal());
		enviarCorreo();
		crearFactura();
		mostrarMensaje();
		initView();	
	}
	
	private void mostrarMensaje() {
		JOptionPane.showMessageDialog(null, "Gracias por la compra!", "Confirmacion de la compra", JOptionPane.INFORMATION_MESSAGE);
	}

	private void toggleButtonState() {
        view.getBtnFinish().setEnabled(!view.getTfNombre().getText().trim().isEmpty() && !view.getTfDNI().getText().trim().isEmpty()
        		&& !view.getTfDomicilio().getText().trim().isEmpty() && !view.getTfCorreo().getText().trim().isEmpty());
    }
	
	private void setBorderColor(JTextField tf) {
        if (tf.getText().isEmpty()) {
        	tf.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        } else {
        	tf.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        }
    }

	private void enviarCorreo() {
		String remitente = view.getTfCorreo().getText();
		EnviarCorreo en = new EnviarCorreo(ventaMerchandising, remitente);
		en.enviarMensaje();
	}
	
	private void crearFactura() {
	    String codCompra = ventaMerchandising.getCodCompra();
	    List<Producto> productos = ventaMerchandising.getProductos();
	    float precioTotal = ventaMerchandising.getPrecioTotal();
	    
	    // Ruta relativa al directorio de trabajo actual
	    String filePath = "src/main/resources/facturas/factura_" + codCompra + ".pdf";		

	    // Generar la factura
	    CrearFactura.crearFactura(filePath, codCompra,view.getTfCorreo().getText(),view.getTfDNI().getText(),
	    		view.getTfNombre().getText(),view.getTfDomicilio().getText(), productos, precioTotal);
	}



	private void createProductPanels(List<ProductoDTO> productos) {
	    // Limpiar el panel de productos
	    view.getPnProductos().removeAll();  // Limpiar el panel de productos, no el JScrollPane

	    // Generar y agregar los productos al panel
	    for (ProductoDTO p : productos) {
	    	Producto producto = new Producto(p.getCodigo(),p.getTipo(),p.getNombre(),p.getPrecio(),p.getUnidades());
	    	PanelProducto pn = new PanelProducto(producto);
	    	GestionPanelProductoShared gp = new GestionPanelProductoShared(pn, this, producto);
	    	gp.initController();
	        view.getPnProductos().add(pn);
	    }

	    // Refrescar la vista para que Swing actualice correctamente
	    view.getPnProductos().revalidate();
	    view.getPnProductos().repaint();
	}
	
	// Método para añadir un producto al resumen
	private void addProductosResumen() {
		restablecerTablaModelo();
		for(Producto p : ventaMerchandising.getProductos()) {
			String total = String.format(" %.2f €", p.getUnits() * p.getPrice());
		    Object[] rowData = {p.getName(), p.getUnits(), p.getPrice() + " €", total};
		    view.getTableModel().addRow(rowData);
		}
	}
	
	private void restablecerTablaModelo() {
		DefaultTableModel model = (DefaultTableModel) (view.getTableResumenPedido()).getModel();
		model.setRowCount(0);
	}
	
	public void saveOrder(List<ProductoDTO> orderList,String cod_compra, Date fecha, float precio) {
		ClientesCRUDService serviceClientes = CreadorDataService.getClientesService();
		VentasCRUDService service = CreadorDataService.getVentasService();
		MerchandisingCRUDService service2 =CreadorDataService.getMerchandisingService();
		serviceClientes.addCliente(new ClienteDTO(view.getTfDNI().getText(),view.getTfNombre().getText()));
		service.addVentas(new VentaDto(cod_compra,view.getTfDNI().getText(),fecha,precio));
		service2.addMerchandising(new MerchandisingDTO(cod_compra));
		model.addOrderProducts(new CompraProductoDTO(orderList,cod_compra));
	}
	
	public VentaMerchandising getVentaMerchandising() {
		return this.ventaMerchandising;
	}
	
	public VentanaPrincipal getVp() {
		return this.view;
	}
}
