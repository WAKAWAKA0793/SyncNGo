package com.example.tripshare.`data`.db

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.CalendarEventEntity
import com.example.tripshare.`data`.model.DailyNoteEntity
import com.example.tripshare.`data`.model.TripDocumentEntity
import com.example.tripshare.`data`.model.TripNoteEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class TripCalendarDao_Impl(
  __db: RoomDatabase,
) : TripCalendarDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfCalendarEventEntity: EntityInsertionAdapter<CalendarEventEntity>

  private val __insertionAdapterOfTripDocumentEntity: EntityInsertionAdapter<TripDocumentEntity>

  private val __insertionAdapterOfDailyNoteEntity: EntityInsertionAdapter<DailyNoteEntity>

  private val __insertionAdapterOfTripNoteEntity: EntityInsertionAdapter<TripNoteEntity>

  private val __updateAdapterOfDailyNoteEntity: EntityDeletionOrUpdateAdapter<DailyNoteEntity>

  private val __updateAdapterOfTripNoteEntity: EntityDeletionOrUpdateAdapter<TripNoteEntity>

  private val __preparedStmtOfDeleteNoteForDay: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfCalendarEventEntity = object :
        EntityInsertionAdapter<CalendarEventEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `calendar_events` (`id`,`tripId`,`title`,`date`,`time`,`location`,`category`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CalendarEventEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, entity.title)
        statement.bindString(4, entity.date)
        val _tmpTime: String? = entity.time
        if (_tmpTime == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpTime)
        }
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpLocation)
        }
        statement.bindString(7, entity.category)
      }
    }
    this.__insertionAdapterOfTripDocumentEntity = object :
        EntityInsertionAdapter<TripDocumentEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `trip_documents` (`id`,`tripId`,`fileName`,`fileSize`,`fileUrl`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TripDocumentEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, entity.fileName)
        statement.bindString(4, entity.fileSize)
        statement.bindString(5, entity.fileUrl)
      }
    }
    this.__insertionAdapterOfDailyNoteEntity = object :
        EntityInsertionAdapter<DailyNoteEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `daily_notes` (`id`,`tripId`,`note`,`reminderTime`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: DailyNoteEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, entity.note)
        val _tmpReminderTime: String? = entity.reminderTime
        if (_tmpReminderTime == null) {
          statement.bindNull(4)
        } else {
          statement.bindString(4, _tmpReminderTime)
        }
      }
    }
    this.__insertionAdapterOfTripNoteEntity = object : EntityInsertionAdapter<TripNoteEntity>(__db)
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `trip_notes` (`noteId`,`firebaseId`,`tripId`,`date`,`note`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TripNoteEntity) {
        statement.bindLong(1, entity.noteId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpFirebaseId)
        }
        val _tmpTripId: Long? = entity.tripId
        if (_tmpTripId == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpTripId)
        }
        statement.bindString(4, entity.date)
        statement.bindString(5, entity.note)
      }
    }
    this.__updateAdapterOfDailyNoteEntity = object :
        EntityDeletionOrUpdateAdapter<DailyNoteEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `daily_notes` SET `id` = ?,`tripId` = ?,`note` = ?,`reminderTime` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: DailyNoteEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, entity.note)
        val _tmpReminderTime: String? = entity.reminderTime
        if (_tmpReminderTime == null) {
          statement.bindNull(4)
        } else {
          statement.bindString(4, _tmpReminderTime)
        }
        statement.bindLong(5, entity.id)
      }
    }
    this.__updateAdapterOfTripNoteEntity = object :
        EntityDeletionOrUpdateAdapter<TripNoteEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `trip_notes` SET `noteId` = ?,`firebaseId` = ?,`tripId` = ?,`date` = ?,`note` = ? WHERE `noteId` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TripNoteEntity) {
        statement.bindLong(1, entity.noteId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpFirebaseId)
        }
        val _tmpTripId: Long? = entity.tripId
        if (_tmpTripId == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpTripId)
        }
        statement.bindString(4, entity.date)
        statement.bindString(5, entity.note)
        statement.bindLong(6, entity.noteId)
      }
    }
    this.__preparedStmtOfDeleteNoteForDay = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        DELETE FROM trip_notes
            |        WHERE tripId = ? AND date = ?
            |        
            """.trimMargin()
        return _query
      }
    }
  }

  public override suspend fun addEvent(event: CalendarEventEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCalendarEventEntity.insert(event)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun addDoc(doc: TripDocumentEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTripDocumentEntity.insert(doc)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun addNote(note: DailyNoteEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfDailyNoteEntity.insert(note)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertNote(note: TripNoteEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfTripNoteEntity.insertAndReturnId(note)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateNote(note: DailyNoteEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfDailyNoteEntity.handle(note)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateNote(note: TripNoteEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfTripNoteEntity.handle(note)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun upsertNote(
    tripId: Long,
    date: String,
    text: String,
  ) {
    __db.withTransaction {
      super@TripCalendarDao_Impl.upsertNote(tripId, date, text)
    }
  }

  public override suspend fun deleteNoteForDay(tripId: Long, date: String): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteNoteForDay.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, tripId)
      _argIndex = 2
      _stmt.bindString(_argIndex, date)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteNoteForDay.release(_stmt)
      }
    }
  })

  public override fun observeEvents(tripId: Long): Flow<List<CalendarEventEntity>> {
    val _sql: String = "SELECT * FROM calendar_events WHERE tripId = ? ORDER BY date, time"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("calendar_events"), object :
        Callable<List<CalendarEventEntity>> {
      public override fun call(): List<CalendarEventEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfTime: Int = getColumnIndexOrThrow(_cursor, "time")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _result: MutableList<CalendarEventEntity> =
              ArrayList<CalendarEventEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CalendarEventEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpTime: String?
            if (_cursor.isNull(_cursorIndexOfTime)) {
              _tmpTime = null
            } else {
              _tmpTime = _cursor.getString(_cursorIndexOfTime)
            }
            val _tmpLocation: String?
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            }
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            _item =
                CalendarEventEntity(_tmpId,_tmpTripId,_tmpTitle,_tmpDate,_tmpTime,_tmpLocation,_tmpCategory)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeDocs(tripId: Long): Flow<List<TripDocumentEntity>> {
    val _sql: String = "SELECT * FROM trip_documents WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_documents"), object :
        Callable<List<TripDocumentEntity>> {
      public override fun call(): List<TripDocumentEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFileName: Int = getColumnIndexOrThrow(_cursor, "fileName")
          val _cursorIndexOfFileSize: Int = getColumnIndexOrThrow(_cursor, "fileSize")
          val _cursorIndexOfFileUrl: Int = getColumnIndexOrThrow(_cursor, "fileUrl")
          val _result: MutableList<TripDocumentEntity> =
              ArrayList<TripDocumentEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripDocumentEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpFileName: String
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName)
            val _tmpFileSize: String
            _tmpFileSize = _cursor.getString(_cursorIndexOfFileSize)
            val _tmpFileUrl: String
            _tmpFileUrl = _cursor.getString(_cursorIndexOfFileUrl)
            _item = TripDocumentEntity(_tmpId,_tmpTripId,_tmpFileName,_tmpFileSize,_tmpFileUrl)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getNoteByDate(tripId: Long?, date: String): TripNoteEntity? {
    val _sql: String = """
        |
        |    SELECT * FROM trip_notes
        |    WHERE (
        |        (? IS NULL AND tripId IS NULL) OR (tripId = ?)
        |    )
        |    AND date = ?
        |    LIMIT 1
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 3)
    var _argIndex: Int = 1
    if (tripId == null) {
      _statement.bindNull(_argIndex)
    } else {
      _statement.bindLong(_argIndex, tripId)
    }
    _argIndex = 2
    if (tripId == null) {
      _statement.bindNull(_argIndex)
    } else {
      _statement.bindLong(_argIndex, tripId)
    }
    _argIndex = 3
    _statement.bindString(_argIndex, date)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<TripNoteEntity?> {
      public override fun call(): TripNoteEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfNoteId: Int = getColumnIndexOrThrow(_cursor, "noteId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _result: TripNoteEntity?
          if (_cursor.moveToFirst()) {
            val _tmpNoteId: Long
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTripId: Long?
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null
            } else {
              _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            }
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpNote: String
            _tmpNote = _cursor.getString(_cursorIndexOfNote)
            _result = TripNoteEntity(_tmpNoteId,_tmpFirebaseId,_tmpTripId,_tmpDate,_tmpNote)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun getNotesFlow(tripId: Long): Flow<List<TripNoteEntity>> {
    val _sql: String = "SELECT * FROM trip_notes WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_notes"), object :
        Callable<List<TripNoteEntity>> {
      public override fun call(): List<TripNoteEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfNoteId: Int = getColumnIndexOrThrow(_cursor, "noteId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _result: MutableList<TripNoteEntity> = ArrayList<TripNoteEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripNoteEntity
            val _tmpNoteId: Long
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTripId: Long?
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null
            } else {
              _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            }
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpNote: String
            _tmpNote = _cursor.getString(_cursorIndexOfNote)
            _item = TripNoteEntity(_tmpNoteId,_tmpFirebaseId,_tmpTripId,_tmpDate,_tmpNote)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeNotes(tripId: Long): Flow<List<TripNoteEntity>> {
    val _sql: String = """
        |
        |    SELECT * FROM trip_notes
        |    WHERE tripId = ?
        |    ORDER BY date
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_notes"), object :
        Callable<List<TripNoteEntity>> {
      public override fun call(): List<TripNoteEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfNoteId: Int = getColumnIndexOrThrow(_cursor, "noteId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _result: MutableList<TripNoteEntity> = ArrayList<TripNoteEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripNoteEntity
            val _tmpNoteId: Long
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTripId: Long?
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null
            } else {
              _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            }
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpNote: String
            _tmpNote = _cursor.getString(_cursorIndexOfNote)
            _item = TripNoteEntity(_tmpNoteId,_tmpFirebaseId,_tmpTripId,_tmpDate,_tmpNote)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeGlobalNotes(): Flow<List<TripNoteEntity>> {
    val _sql: String = """
        |
        |    SELECT * FROM trip_notes
        |    WHERE tripId IS NULL
        |    ORDER BY date DESC
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_notes"), object :
        Callable<List<TripNoteEntity>> {
      public override fun call(): List<TripNoteEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfNoteId: Int = getColumnIndexOrThrow(_cursor, "noteId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _result: MutableList<TripNoteEntity> = ArrayList<TripNoteEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripNoteEntity
            val _tmpNoteId: Long
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTripId: Long?
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null
            } else {
              _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            }
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpNote: String
            _tmpNote = _cursor.getString(_cursorIndexOfNote)
            _item = TripNoteEntity(_tmpNoteId,_tmpFirebaseId,_tmpTripId,_tmpDate,_tmpNote)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getNoteByFirebaseId(fid: String): TripNoteEntity? {
    val _sql: String = "SELECT * FROM trip_notes WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<TripNoteEntity?> {
      public override fun call(): TripNoteEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfNoteId: Int = getColumnIndexOrThrow(_cursor, "noteId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _result: TripNoteEntity?
          if (_cursor.moveToFirst()) {
            val _tmpNoteId: Long
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTripId: Long?
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null
            } else {
              _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            }
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpNote: String
            _tmpNote = _cursor.getString(_cursorIndexOfNote)
            _result = TripNoteEntity(_tmpNoteId,_tmpFirebaseId,_tmpTripId,_tmpDate,_tmpNote)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getNoteById(id: Long): TripNoteEntity? {
    val _sql: String = "SELECT * FROM trip_notes WHERE noteId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<TripNoteEntity?> {
      public override fun call(): TripNoteEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfNoteId: Int = getColumnIndexOrThrow(_cursor, "noteId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _result: TripNoteEntity?
          if (_cursor.moveToFirst()) {
            val _tmpNoteId: Long
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTripId: Long?
            if (_cursor.isNull(_cursorIndexOfTripId)) {
              _tmpTripId = null
            } else {
              _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            }
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpNote: String
            _tmpNote = _cursor.getString(_cursorIndexOfNote)
            _result = TripNoteEntity(_tmpNoteId,_tmpFirebaseId,_tmpTripId,_tmpDate,_tmpNote)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
