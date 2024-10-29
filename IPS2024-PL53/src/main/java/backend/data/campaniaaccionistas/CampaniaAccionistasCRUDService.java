package backend.data.campaniaaccionistas;

import java.util.Optional;

import backend.data.accionistas.AccionistaDTO;

public interface CampaniaAccionistasCRUDService {

	/**
	 * @return campania en curso
	 */
	Optional<CampaniaDTO> findEnCurso();

	void crearCampania(CampaniaDTO dto);

	void actualizarCampania(CampaniaDTO dto);

	Optional<AccionistaEnCampaniaDTO> getAccionistaEnCampaniaByDni(String dniAccionista);

	void addAccionistaEnCampania(String idAccionista, String codCampania, int numAccionesIniciales);
}
