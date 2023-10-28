package fwf.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "funwithflags")
public interface Configuration {
    @WithName("players")
    @WithDefault("2")
    public int numberOfPlayersPerGame();

    @WithName("turns")
    @WithDefault("4")
    public int numberOfTurnsPerGame();

    @WithDefault("5")
    public int numberOfSecondsPerResult();

    @WithName("numberOfSecondsPerTurn")
    @WithDefault("30")
    public int numberOfSecondsPerTurn();
}
