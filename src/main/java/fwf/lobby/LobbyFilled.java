package fwf.lobby;

import java.util.List;

import fwf.player.Player;

public record LobbyFilled(List<Player> playersInLobby) {
}
