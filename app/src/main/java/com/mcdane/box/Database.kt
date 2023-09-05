package com.mcdane.box

import android.content.Context
import android.os.HandlerThread
import androidx.room.*
import java.sql.Timestamp
import java.util.Date
import android.os.Handler
import android.util.Log

@Entity(tableName="scores")
data class ScoreRecord(
    @PrimaryKey(true) val id: Int,
    @ColumnInfo(name="player_name") val playerName: String,
    val score: Long,
    val time: Date
)

@Dao
interface ScoreRecordDao {
    companion object {
        const val HIGH_SCORE_COUNT = 20
    }

    @Query("SELECT * FROM scores ORDER BY time DESC LIMIT $HIGH_SCORE_COUNT")
    fun getTopRecords(): List<ScoreRecord>

    @Insert
    fun add(record: ScoreRecord)
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Database(entities = [ScoreRecord::class], version=1)
@TypeConverters(Converters::class)
abstract class BoxDatabase: RoomDatabase() {
    companion object {
        private val thread = HandlerThread("database").apply { start() }
        val handler = Handler(thread.looper)
        var instance: BoxDatabase? = null

        fun init(context: Context) {
            handler.post {
                instance = Room.databaseBuilder(
                    context,
                    BoxDatabase::class.java,
                    "box"
                ).build()
                Log.i(TAG, "BoxDatabase initialized")
            }
        }

        fun stop() {
            thread.quitSafely()
        }
    }

    abstract fun scoreDao(): ScoreRecordDao
}