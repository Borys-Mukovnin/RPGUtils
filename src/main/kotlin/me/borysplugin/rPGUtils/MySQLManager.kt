import me.borysplugin.rPGUtils.PlayerStats
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class MySQLManager(
    private val host: String,
    private val port: String,
    private val database: String,
    private val username: String,
    private val password: String
) {

    private var connection: Connection? = null

    // Establish a connection to MySQL
    fun connect(): Connection? {
        if (connection != null) {
            return connection
        }

        try {
            val url = "jdbc:mysql://$host:$port/$database?useSSL=false&serverTimezone=UTC"
            connection = DriverManager.getConnection(url, username, password)
            println("MySQL connected successfully!")
            createTableIfNotExists()  // Create table if it doesn't exist
        } catch (e: SQLException) {
            println("MySQL connection failed: ${e.message}")
        }

        return connection
    }

    fun loadPlayerStatsFromDB(uuid: UUID): PlayerStats? {
        val query = "SELECT * FROM player_stats WHERE uuid = ?"
        try {
            val preparedStatement = connection!!.prepareStatement(query)
            preparedStatement.setString(1, uuid.toString())  // Convert UUID to String for DB
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                // Map the database fields to PlayerStats object
                return PlayerStats(
                    playerName = resultSet.getString("player_name"),
                    health = resultSet.getDouble("health"),
                    attackPower = resultSet.getDouble("attack_power"),
                    attackSpeed = resultSet.getDouble("attack_speed"),
                    defense = resultSet.getDouble("defense"),
                    critChance = resultSet.getDouble("crit_chance"),
                    critDamage = resultSet.getDouble("crit_damage")
                )
            }
        } catch (e: SQLException) {
            println("Error loading player stats: ${e.message}")
        }
        return null
    }

    // Create player stats in the database
    fun createPlayerStatsInDB(uuid: UUID, playerName: String) {
        val query = """
        INSERT INTO player_stats (uuid, player_name, health, attack_power, attack_speed, defense, crit_chance, crit_damage)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """
        try {
            val preparedStatement = connection!!.prepareStatement(query)
            preparedStatement.setString(1, uuid.toString())
            preparedStatement.setString(2, playerName)
            preparedStatement.setDouble(3, 20.0)  // Default health
            preparedStatement.setDouble(4, 0.0)   // Default attack damage
            preparedStatement.setDouble(5, 0.0)   // Default attack speed
            preparedStatement.setDouble(6, 0.0)   // Default defense
            preparedStatement.setDouble(7, 10.0)  // Default crit chance
            preparedStatement.setDouble(8, 0.0)   // Default crit damage

            preparedStatement.executeUpdate()
            println("Player stats created for $playerName in database.")
        } catch (e: SQLException) {
            println("Error creating player stats: ${e.message}")
        }
    }

    // Close the connection
    fun disconnect() {
        try {
            connection?.close()
            println("MySQL connection closed.")
        } catch (e: SQLException) {
            println("Error closing MySQL connection: ${e.message}")
        }
    }

    // Check if the connection is alive
    fun isConnected(): Boolean {
        return connection?.isValid(2) == true
    }

    // Create table if it doesn't exist
    private fun createTableIfNotExists() {
        val query = """
            CREATE TABLE IF NOT EXISTS player_stats (
                uuid VARCHAR(255) NOT NULL PRIMARY KEY,
                player_name VARCHAR(255) NOT NULL,
                health DOUBLE NOT NULL,
                attack_power DOUBLE NOT NULL,
                attack_speed DOUBLE NOT NULL,
                defense DOUBLE NOT NULL,
                crit_chance DOUBLE NOT NULL,
                crit_damage DOUBLE NOT NULL
            );
        """
        try {
            val statement: Statement = connection!!.createStatement()
            statement.executeUpdate(query)
            println("player_stats table is ready!")
        } catch (e: SQLException) {
            println("Error creating table: ${e.message}")
        }
    }
}
