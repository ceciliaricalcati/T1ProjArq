package br.edu.voo.application;
import br.edu.voo.infrastructure.*;
import com.fasterxml.jackson.databind.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class RegistroService {
 private final RegistroRepository repo; private final ObjectMapper mapper;
 public RegistroService(RegistroRepository r,ObjectMapper m){repo=r;mapper=m;}
 public List<JsonNode> listar(String tipo){return repo.findByTipoOrderByChave(tipo).stream().map(this::json).toList();}
 public JsonNode obter(String tipo,String chave){return repo.findByTipoAndChave(tipo,chave).map(this::json).orElseThrow(()->new NoSuchElementException(tipo+" não encontrado: "+chave));}
 public boolean existe(String tipo,String chave){return repo.existsByTipoAndChave(tipo,chave);}
 @Transactional public JsonNode criar(String tipo,String chave,JsonNode payload){if(chave==null||chave.isBlank())throw new IllegalArgumentException("Identificador obrigatório");if(existe(tipo,chave))throw new IllegalArgumentException(tipo+" já cadastrado: "+chave);return json(repo.save(new Registro(tipo,chave,payload.toString())));}
 @Transactional public JsonNode salvar(String tipo,String chave,JsonNode payload){Registro r=repo.findByTipoAndChave(tipo,chave).orElseThrow(()->new NoSuchElementException(tipo+" não encontrado: "+chave));r.payload=payload.toString();return json(repo.save(r));}
 public JsonNode json(Registro r){try{return mapper.readTree(r.payload);}catch(Exception e){throw new IllegalStateException(e);}}
 public String texto(JsonNode n,String campo){if(n==null||n.get(campo)==null||n.get(campo).isNull())throw new IllegalArgumentException("Campo obrigatório: "+campo);return n.get(campo).asText();}
 public int inteiro(JsonNode n,String campo){if(n==null||!n.has(campo))throw new IllegalArgumentException("Campo obrigatório: "+campo);return n.get(campo).asInt();}
}
