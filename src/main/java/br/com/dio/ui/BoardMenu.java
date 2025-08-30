package br.com.dio.ui;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {
    private final BoardEntity entity;
    private final Scanner scanner = new Scanner(System.in);
    public void execute() {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a operacao desejada\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Ver Board");
                System.out.println("7 - Ver colunas com cards");
                System.out.println("8 - Ver card");
                System.out.println("9 - Voltar para o menu anterior um card");
                System.out.println("10 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("voltando para o menu anteior");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opcao invalida, informe uma opcao do menu");

                }
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Informe o titulo do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descricao do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()){
            new CardService(connection).create(card);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a proxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard()throws SQLException {
        System.out.println("Informe o id do card que sera bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException{
        System.out.println("Informe o id do card que sera bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();

        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, reason);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException{
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId,cancelColumn.getId(),boardColumnsInfo);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b ->{
                System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    private void showColumn() throws SQLException{
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)){
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(c-> System.out.printf("%s -%s [%s]\n", c.getId(),c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()){
           var column = new BoardColumnQueryService(connection).findById(selectedColumn);
           column.ifPresent(co -> {
                       System.out.printf("Coluna %s tipo %s\n", co.getName(),co.getKind());
                        co.getCards().forEach(ca -> System.out.printf("Card %s - %s\n Descricao: %s ",
                                ca.getId(),
                                ca.getTitle(),
                                ca.getDescription()));
           });
        }
    }

    private void showCard() throws SQLException{
        System.out.println("Informe o id do card que quer visualizar");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).FindById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s. \n",c.id(),c.title());
                                System.out.printf("Descricao: %s\n", c.description());
                                System.out.printf(c.blocked() ?
                                        "Esta bloqueado. Motivo: "+c.blockReason() :
                                        "Nao esta bloqueado ");
                                System.out.printf("Ja foi bloqueado %s vezes\n", c.blocksAmount());
                                System.out.printf("Esta no momento na coluna %s - %s\n", c.columnId(),c.columnName());
                            },

                            () -> System.out.printf("Nao existe um card com o id %s\n",selectedCardId));
        }
    }
}
