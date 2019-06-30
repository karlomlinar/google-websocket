package hr.tvz.nba.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message object which is sent to the front application.
 * If <code>matchId</code> is <code>null</code>, then the game has finished.
 * If <code>matchId</code> is not <code>null</code>, but every other property is <code>null</code>, then the game has started.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessage {

    String matchId;
    String playerId;
    Integer points;
}
