package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColmnKindEum;

public record BoardColumnInfoDTO(Long id, int order, BoardColmnKindEum kind) {
}
