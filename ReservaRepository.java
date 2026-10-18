package br.edu.voo.infrastructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ReservaRepository extends JpaRepository<ReservaRegistro,Long>{
 List<ReservaRegistro> findByPlanoId(Long planoId);
 boolean existsByRecursoTipoAndRecursoIdAndSlotInicio(String tipo,String recurso,String slot);
}
