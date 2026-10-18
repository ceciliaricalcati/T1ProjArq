package br.edu.voo.infrastructure;
import jakarta.persistence.*;
@Entity @Table(name="registros", uniqueConstraints=@UniqueConstraint(columnNames={"tipo","chave"}))
public class Registro {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String tipo;
 @Column(nullable=false) public String chave;
 @Lob @Column(nullable=false) public String payload;
 protected Registro(){}
 public Registro(String tipo,String chave,String payload){this.tipo=tipo;this.chave=chave;this.payload=payload;}
}
