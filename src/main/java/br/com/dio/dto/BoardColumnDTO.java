package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColmnKindEum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColmnKindEum kind,
                             int cardsAmount) {

}
