package br.com.alura.screenmatch.dto;
import br.com.alura.screenmatch.model.Categoria;
import jakarta.persistence.*;

public record SerieDTO(Long id,
                     String titulo,
                     Integer totalTemporadas,
                     Double avaliacao,
                     Categoria generos,
                     String atores,
                     String poster,
                     String sinopse) {
}
