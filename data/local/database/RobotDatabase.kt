import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

// Data class for Command entity
@Entity(tableName = "commands")
data class Command(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val command: String,
    val timestamp: Long
)

// Data class for History entity
@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val commandId: Long,
    val executedAt: Long
)

// DAO for Command and History entities
@Dao
interface RobotDao {
    @Insert
    suspend fun insertCommand(command: Command)

    @Insert
    suspend fun insertHistory(history: History)

    @Query("SELECT * FROM commands ORDER BY timestamp DESC")
    suspend fun getAllCommands(): List<Command>

    @Query("SELECT * FROM history ORDER BY executedAt DESC")
    suspend fun getHistory(): List<History>
}

// Room Database class
@Database(entities = [Command::class, History::class], version = 1)
abstract class RobotDatabase : RoomDatabase() {
    abstract fun robotDao(): RobotDao

    companion object {  
        @Volatile  
        private var INSTANCE: RobotDatabase? = null

        fun getDatabase(context: Context): RobotDatabase {  
            return INSTANCE ?: synchronized(this) {  
                val instance = Room.databaseBuilder(  
                    context.applicationContext,
                    RobotDatabase::class.java,
                    "robot_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}