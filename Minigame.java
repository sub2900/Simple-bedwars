import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BedWarsMiniGame extends JavaPlugin {

    private List<Team> teams;
    private Map<Player, Team> playerTeams;

    private int gameDuration = 300; // seconds
    private int countdownDuration = 10; // seconds

    @Override
    public void onEnable() {
        teams = new ArrayList<>();
        playerTeams = new HashMap<>();

        // Create teams
        teams.add(new Team(ChatColor.RED + "Red", ChatColor.RED));
        teams.add(new Team(ChatColor.BLUE + "Blue", ChatColor.BLUE));
        teams.add(new Team(ChatColor.GREEN + "Green", ChatColor.GREEN));
        teams.add(new Team(ChatColor.YELLOW + "Yellow", ChatColor.YELLOW));

        // Register commands, listeners, etc.

        // Start the game
        startGame();
    }

    private void startGame() {
        // Randomly assign players to teams
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Random random = new Random();
        for (Player player : onlinePlayers) {
            Team team = teams.get(random.nextInt(teams.size()));
            team.addPlayer(player);
            playerTeams.put(player, team);
        }

        // Teleport players to team spawns
        for (Team team : teams) {
            Location spawnLocation = team.getSpawnLocation();
            for (Player player : team.getPlayers()) {
                player.teleport(spawnLocation);
            }
        }

        // Start countdown
        new BukkitRunnable() {
            int countdown = countdownDuration;

            @Override
            public void run() {
                if (countdown == 0) {
                    startGameTimer();
                    this.cancel();
                } else {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Game starting in " + countdown + " seconds!");
                    countdown--;
                }
            }
        }.runTaskTimer(this, 0, 20); // Run every second
    }

    private void startGameTimer() {
        new BukkitRunnable() {
            int timeLeft = gameDuration;

            @Override
            public void run() {
                if (timeLeft == 0) {
                    endGame();
                    this.cancel();
                } else {
                    // Handle game actions

                    timeLeft--;
                }
            }
        }.runTaskTimer(this, 0, 20); // Run every second
    }

    private void endGame() {
        // Determine the winning team and display a message
        Team winningTeam = null;
        for (Team team : teams) {
            if (team.isBedIntact()) {
                if (winningTeam == null) {
                    winningTeam = team;
                } else {
                    // More than one team's bed is intact, so it's a draw
                    winningTeam = null;
                    break;
                }
            }
        }

        if (winningTeam != null) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Congratulations to " + winningTeam.getName() + " team for winning the game!");
        } else {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "The game ended in a draw!");
        }

        // Reset game state, clear inventories, etc.

        // Start a new game
        startGame();
    }

    private class Team {
        private String name;
        private ChatColor color;
        private List<Player> players;
        private Location spawnLocation;

        public Team(String name, ChatColor color) {
            this.name = name;
            this.color = color;
            this.players = new ArrayList<>();
            // Set the spawn location for the team
            World world = Bukkit.getWorld("world");
            this.spawnLocation = new Location(world, 0, 100, 0);
        }

        public String getName() {
            return name;
        }

        public ChatColor getColor() {
            return color;
        }

        public List<Player> getPlayers() {
            return players;
        }

        public void addPlayer(Player player) {
            players.add(player);
        }

        public Location getSpawnLocation() {
            return spawnLocation;
        }

        public boolean isBedIntact() {
            // Check if the team's bed is intact
            // You can implement your own logic here based on how beds are represented in your game
            return true;
        }
    }
}
