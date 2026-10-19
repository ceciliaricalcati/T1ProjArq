package br.edu.voo.api;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(NoSuchElementException.class)
 public ResponseEntity<Map<String,Object>> notFound(Exception e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status",404,"erro","NAO_ENCONTRADO","mensagem",String.valueOf(e.getMessage())));}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class})
 public ResponseEntity<Map<String,Object>> badRequest(Exception e){return ResponseEntity.badRequest().body(Map.of("status",400,"erro","REQUISICAO_INVALIDA","mensagem",String.valueOf(e.getMessage())));}
}
