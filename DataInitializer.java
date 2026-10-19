package br.edu.voo.application;
import br.edu.voo.infrastructure.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
@Component
public class DataInitializer implements CommandLineRunner {
 private final RegistroRepository repo; private final ReservaRepository reservas; private final ObjectMapper mapper;
 public static final String DATA_REFERENCIA="2026-10-20";
 public DataInitializer(RegistroRepository r,ReservaRepository rs,ObjectMapper m){repo=r;reservas=rs;mapper=m;}
 @Override @Transactional public void run(String... args)throws Exception{
  airport("POA","Aeroporto Internacional Salgado Filho - Porto Alegre",41);airport("FLN","Aeroporto Internacional de Florianópolis/Hercílio Luz",16);airport("CWB","Aeroporto Internacional Afonso Pena - Curitiba",26);airport("GRU","Aeroporto Internacional de São Paulo/Guarulhos",115);
  route("R1","POA","FLN",363);route("R2","POA","GRU",865);route("R3","GRU","POA",865);route("R4","FLN","CWB",245);route("R5","CWB","FLN",245);route("R6","CWB","GRU",360);route("R7","GRU","CWB",360);route("R8","FLN","POA",363);
  pilot("P1","Ana Souza");pilot("P2","Bruno Lima");pilot("P3","Carla Mendes");
  aircraft("PT-ABC","PARTICULAR",420,1800,"Manutenção Sul",null,null);
  aircraft("PR-PAX","COMERCIAL_PASSAGEIROS",800,5000,null,"Companhia Aérea Brasil",180);
  aircraft("PR-CAR","COMERCIAL_CARGA",700,4500,null,"Carga Aérea Sul",null);
  // Planos iniciais aprovados, cuja ocupação é criada exclusivamente por esses planos.
  if(repo.findByTipoOrderByChave("plano").isEmpty()){
   seedPlan(1,"P2","PR-PAX","POA","FLN","R1",28000,"2026-10-20T08:00:00",16);
   seedPlan(2,"P1","PR-PAX","POA","GRU","R2",29000,"2026-10-20T10:00:00",17);
   seedPlan(3,"P2","PR-PAX","GRU","POA","R3",30000,"2026-10-20T12:00:00",18);
   seedPlan(4,"P3","PR-PAX","FLN","CWB","R4",31000,"2026-10-20T14:00:00",19);
   seedPlan(5,"P1","PR-PAX","CWB","FLN","R5",32000,"2026-10-20T16:00:00",20);
  }
 }
 private void put(String type,String key,ObjectNode n){if(!repo.existsByTipoAndChave(type,key))repo.save(new Registro(type,key,n.toString()));}
 private ObjectNode obj(){return mapper.createObjectNode();}
 private void airport(String c,String n,int v){ObjectNode x=obj();x.put("codigo",c);x.put("nome",n);x.put("quantidadeVagas",v);put("aeroporto",c,x);}
 private void route(String id,String o,String d,int km){ObjectNode x=obj();x.put("id",id);x.put("origem",o);x.put("destino",d);x.put("distanciaKm",km);put("aerovia",id,x);}
 private void pilot(String id,String nome){ObjectNode x=obj();x.put("id",id);x.put("nome",nome);put("piloto",id,x);}
 private void aircraft(String p,String cat,int speed,int range,String manut,String companhia,Integer cap){ObjectNode x=obj();x.put("prefixo",p);x.put("categoria",cat);x.put("velocidadeCruzeiroKmh",speed);x.put("autonomiaKm",range);if(manut!=null)x.put("empresaManutencao",manut);if(companhia!=null)x.put("companhia",companhia);if(cap!=null)x.put("capacidadePassageiros",cap);if(cat.equals("COMERCIAL_CARGA"))x.put("capacidadeCargaToneladas",35);put("aeronave",p,x);}
 private void seedPlan(int id,String piloto,String aeronave,String origem,String destino,String aerovia,int alt,String entrada,int vaga){
  ObjectNode p=obj();p.put("id",id);p.put("pilotoId",piloto);p.put("aeronavePrefixo",aeronave);p.put("origem",origem);p.put("destino",destino);p.put("partida",entrada);p.put("status","APROVADO");ArrayNode ts=p.putArray("trechos");ObjectNode t=ts.addObject();t.put("aeroviaId",aerovia);t.put("altitudePes",alt);t.put("entrada",entrada);t.put("acao","POUSO");t.put("vaga",vaga);t.put("permanenciaMin",30);put("plano",String.valueOf(id),p);
  int km=switch(aerovia){case "R1","R8"->363;case "R2","R3"->865;case "R4","R5"->245;default->360;};int speed=800;LocalDateTime in=LocalDateTime.parse(entrada),end=in.plusSeconds((long)Math.ceil(km*3600.0/speed));
  for(LocalDateTime s=in.truncatedTo(ChronoUnit.HOURS);s.isBefore(end);s=s.plusHours(1))reservas.save(new ReservaRegistro((long)id,"AEROVIA",aerovia+":"+alt,s.toString(),"altitude "+alt));
  String res=dest+":"+vaga;for(LocalDateTime s=end.truncatedTo(ChronoUnit.HOURS);s.isBefore(end.plusMinutes(30));s=s.plusHours(1))reservas.save(new ReservaRegistro((long)id,"VAGA",res,s.toString(),"vaga "+res));
 }
}
