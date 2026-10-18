package br.edu.voo.infrastructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface RegistroRepository extends JpaRepository<Registro,Long>{
 Optional<Registro> findByTipoAndChave(String tipo,String chave);
 List<Registro> findByTipoOrderByChave(String tipo);
 boolean existsByTipoAndChave(String tipo,String chave);
}
