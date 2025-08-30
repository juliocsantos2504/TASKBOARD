package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;

@AllArgsConstructor
public class CardDAO {
    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (title, description, board_column_id, `order`) VALUES (?,?,?,?);";
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setLong(i++, entity.getBoardColumn().getId());
            // Define a ordem, se não tiver, usa 0
            statement.setInt(i, entity.getOrder() != null ? entity.getOrder() : 0);

            statement.executeUpdate();

            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    public void moveToColumn(final Long columnId, final Long cardId)throws SQLException{
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try (var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i ++,columnId);
            statement.setLong(i,cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.block_at,
                       b.block_reason,
                       c.board_column_id,
                       bc.name,
                       (SELECT COUNT(sub_b.id)
                            FROM BLOCKS sub_b 
                            WHERE sub_b.card_id = c.id) AS blocks_amount
                FROM CARDS c 
                LEFT JOIN BLOCKS b
                       ON c.id = b.card_id
                      AND b.unblock_at IS NULL
                INNER JOIN BOARDS_COLUMNS bc
                       ON bc.id = c.board_column_id
                WHERE c.id = ?;
                """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("id"),                // c.id
                        resultSet.getString("title"),           // c.title
                        resultSet.getString("description"),     // c.description
                        resultSet.getString("block_reason") != null, // true se bloqueado
                        toOffsetDateTime(resultSet.getTimestamp("block_at")),
                        resultSet.getString("block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("board_column_id"),   // c.board_column_id
                        resultSet.getString("name")             // bc.name
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }




}
