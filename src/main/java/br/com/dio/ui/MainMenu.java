package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColmnKindEum;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static br.com.dio.persistence.entity.BoardColmnKindEum.*;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards, escolha a opcao desejada");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opcao invalida, informe uma opcao do menu");

            }

        }
    }

    private void createBoard() {
        try {
            var entity = new BoardEntity();
            System.out.println("Informe o nome do seu board");
            entity.setName(scanner.next());

            System.out.println("Seu board tera coluna alem das 3 padroes ? Se sim informe quantas, se nao digite `0`");
            var additionalCollumns = scanner.nextInt();

            List<BoardColumnEntity> columns = new ArrayList<>();

            System.out.println("Informe o nome da coluna inicial do board");
            var initialColumnName = scanner.next();
            columns.add(createCloumn(initialColumnName, INITIAL, 0));

            for (int i = 0; i < additionalCollumns; i++) {
                System.out.println("Informe o nome da coluna de tarefa pendente ");
                var pendingColumnName = scanner.next();
                columns.add(createCloumn(pendingColumnName, PENDING, i + 1));
            }

            System.out.println("Informe o nome da coluna final ");
            var finalColumnName = scanner.next();
            columns.add(createCloumn(finalColumnName, FINAL, additionalCollumns + 1));

            System.out.println("Informe o nome da coluna de cancelamento do board ");
            var cancelColumnName = scanner.next();
            columns.add(createCloumn(cancelColumnName, CANCEL, additionalCollumns + 2));

            entity.setBoardColumns(columns);

            try (var connection = getConnection()) {
                connection.setAutoCommit(false);
                var service = new BoardService(connection);
                service.insert(entity);
            }

            System.out.println("Board criado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar board: " + e.getMessage());
        }
    }


    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Nao foi encontrado um board com id %s \n", id)
            );
        }

    }
    private void deleteBoard() throws SQLException {
        System.out.println("informe o id do board que sera excluido");
        var id = scanner.nextLong();
        try (var connection = getConnection()){
            var service = new BoardService(connection);
            if (service.delete(id)){
                System.out.printf("O board %s foi excluido\n", id);
            }else {
                System.out.printf("Nao foi encontrado um board com id %s \n", id);
            }

         }

    }
    private  BoardColumnEntity createCloumn(final String name, final BoardColmnKindEum kind, final int order ){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return  boardColumn;
    }
}


