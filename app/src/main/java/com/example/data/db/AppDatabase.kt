package com.example.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.data.model.AttendanceEntity
import com.example.data.model.CommentEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.ShotLogEntity
import com.example.data.model.StaffEntity
import com.example.data.model.TestRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipantDao {
    @Query("SELECT * FROM participants ORDER BY id ASC")
    fun getAllParticipants(): Flow<List<ParticipantEntity>>

    @Query("SELECT * FROM participants ORDER BY id ASC")
    suspend fun getAllParticipantsSync(): List<ParticipantEntity>

    @Query("SELECT * FROM participants WHERE id = :id")
    fun getParticipantById(id: Int): Flow<ParticipantEntity?>

    @Query("SELECT * FROM participants WHERE id = :id")
    suspend fun getParticipantByIdSync(id: Int): ParticipantEntity?

    @Query("SELECT * FROM participants WHERE docNumber = :doc OR telegramUser = :tg OR nick LIKE :query OR fullName LIKE :query LIMIT 1")
    suspend fun findParticipantByQuery(doc: String, tg: String, query: String): ParticipantEntity?

    @Query("SELECT * FROM participants WHERE nick LIKE '%' || :query || '%' OR fullName LIKE '%' || :query || '%' OR docNumber LIKE '%' || :query || '%' OR telegramUser LIKE '%' || :query || '%'")
    fun searchParticipants(query: String): Flow<List<ParticipantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ParticipantEntity)

    @Update
    suspend fun updateParticipant(participant: ParticipantEntity)

    @Delete
    suspend fun deleteParticipant(participant: ParticipantEntity)

    @Query("UPDATE participants SET sampleNumber = :sample, testDate = :testDate, testResult = :result WHERE id = :id")
    suspend fun updateTestInfo(id: Int, sample: String, testDate: String, result: String)

    @Query("UPDATE participants SET balance = :newBalance WHERE id = :id")
    suspend fun updateBalance(id: Int, newBalance: Int)

    @Query("UPDATE participants SET isConfirmedForShoot = :isConfirmed WHERE id = :id")
    suspend fun updateConfirmation(id: Int, isConfirmed: Boolean)

    @Query("SELECT COUNT(*) FROM participants")
    suspend fun getCount(): Int

    @RawQuery(observedEntities = [ParticipantEntity::class])
    fun rawQueryParticipants(query: SupportSQLiteQuery): Flow<List<ParticipantEntity>>
}

@Dao
interface ShootDao {
    @Query("SELECT * FROM shoot_events ORDER BY shootCode DESC")
    fun getAllShoots(): Flow<List<ShootEventEntity>>

    @Query("SELECT * FROM shoot_events WHERE shootCode = :code")
    fun getShootByCode(code: String): Flow<ShootEventEntity?>

    @Query("SELECT * FROM shoot_events WHERE shootCode = :code")
    suspend fun getShootByCodeSync(code: String): ShootEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoot(shoot: ShootEventEntity)

    @Update
    suspend fun updateShoot(shoot: ShootEventEntity)
}

@Dao
interface StaffDao {
    @Query("SELECT * FROM staff_members WHERE shootCode = :shootCode")
    fun getStaffForShoot(shootCode: String): Flow<List<StaffEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffList(staff: List<StaffEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity)

    @Update
    suspend fun updateStaff(staff: StaffEntity)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM shoot_attendances WHERE shootCode = :shootCode")
    fun getAttendanceForShoot(shootCode: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM shoot_attendances WHERE shootCode = :shootCode")
    suspend fun getAttendanceForShootSync(shootCode: String): List<AttendanceEntity>

    @Query("SELECT * FROM shoot_attendances WHERE shootCode = :shootCode AND participantId = :participantId")
    fun getAttendance(shootCode: String, participantId: Int): Flow<AttendanceEntity?>

    @Query("SELECT * FROM shoot_attendances WHERE shootCode = :shootCode AND participantId = :participantId")
    suspend fun getAttendanceSync(shootCode: String, participantId: Int): AttendanceEntity?

    @Query("SELECT * FROM shoot_attendances WHERE shootCode = :shootCode AND lockerNumber = :lockerNumber")
    fun getAttendanceByLocker(shootCode: String, lockerNumber: Int): Flow<AttendanceEntity?>

    @Query("SELECT * FROM shoot_attendances WHERE shootCode = :shootCode AND lockerNumber = :lockerNumber")
    suspend fun getAttendanceByLockerSync(shootCode: String, lockerNumber: Int): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(attendances: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)
}

@Dao
interface ShotLogDao {
    @Query("SELECT * FROM shot_logs WHERE shootCode = :shootCode ORDER BY id DESC")
    fun getLogsForShoot(shootCode: String): Flow<List<ShotLogEntity>>

    @Query("SELECT * FROM shot_logs WHERE shootCode = :shootCode ORDER BY id DESC")
    suspend fun getLogsForShootSync(shootCode: String): List<ShotLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ShotLogEntity)

    @Query("DELETE FROM shot_logs WHERE id = :id")
    suspend fun deleteLog(id: Int)

    @Query("SELECT COUNT(*) FROM shot_logs WHERE shootCode = :shootCode AND bonus > 0")
    suspend fun getPrizesCountForShoot(shootCode: String): Int
}

@Dao
interface TestRecordDao {
    @Query("SELECT * FROM test_records WHERE participantId = :participantId ORDER BY id DESC")
    fun getRecordsForParticipant(participantId: Int): Flow<List<TestRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: TestRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<TestRecordEntity>)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE participantId = :participantId ORDER BY timestamp DESC")
    fun getCommentsForParticipant(participantId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}

@Database(
    entities = [
        ParticipantEntity::class,
        TestRecordEntity::class,
        ShootEventEntity::class,
        StaffEntity::class,
        AttendanceEntity::class,
        ShotLogEntity::class,
        CommentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun participantDao(): ParticipantDao
    abstract fun shootDao(): ShootDao
    abstract fun staffDao(): StaffDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun shotLogDao(): ShotLogDao
    abstract fun testRecordDao(): TestRecordDao
    abstract fun commentDao(): CommentDao
}
