package br.edu.voo.infrastructure;
import jakarta.persistence.*;
@Entity @Table(name="reservas_voo", indexes={@Index(columnList="planoId"),@Index(columnList="recursoTipo,recursoId,slotInicio")})
public class ReservaRegistro {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public Long planoId;
 @Column(nullable=false) public String recursoTipo;
 @Column(nullable=false) public String recursoId;
 @Column(nullable=false) public String slotInicio;
 @Column(nullable=false) public String detalhe;
 protected ReservaRegistro(){}
 public ReservaRegistro(Long p,String t,String r,String s,String d){planoId=p;recursoTipo=t;recursoId=r;slotInicio=s;detalhe=d;}
}
