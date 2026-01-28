package com.example.tripshare.`data`.db

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.ReportEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class ReportDao_Impl(
  __db: RoomDatabase,
) : ReportDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfReportEntity: EntityInsertionAdapter<ReportEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfReportEntity = object : EntityInsertionAdapter<ReportEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `reports` (`id`,`reportedUserId`,`reporterUserId`,`reason`,`description`,`blockUser`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ReportEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.reportedUserId)
        statement.bindLong(3, entity.reporterUserId)
        statement.bindString(4, entity.reason)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpDescription)
        }
        val _tmp: Int = if (entity.blockUser) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(report: ReportEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfReportEntity.insertAndReturnId(report)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun reportsFiledBy(userId: Long): Flow<List<ReportEntity>> {
    val _sql: String = "SELECT * FROM reports WHERE reporterUserId = ? ORDER BY createdAt DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("reports"), object :
        Callable<List<ReportEntity>> {
      public override fun call(): List<ReportEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfReportedUserId: Int = getColumnIndexOrThrow(_cursor, "reportedUserId")
          val _cursorIndexOfReporterUserId: Int = getColumnIndexOrThrow(_cursor, "reporterUserId")
          val _cursorIndexOfReason: Int = getColumnIndexOrThrow(_cursor, "reason")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfBlockUser: Int = getColumnIndexOrThrow(_cursor, "blockUser")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _result: MutableList<ReportEntity> = ArrayList<ReportEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ReportEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpReportedUserId: Long
            _tmpReportedUserId = _cursor.getLong(_cursorIndexOfReportedUserId)
            val _tmpReporterUserId: Long
            _tmpReporterUserId = _cursor.getLong(_cursorIndexOfReporterUserId)
            val _tmpReason: String
            _tmpReason = _cursor.getString(_cursorIndexOfReason)
            val _tmpDescription: String?
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            }
            val _tmpBlockUser: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfBlockUser)
            _tmpBlockUser = _tmp != 0
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            _item =
                ReportEntity(_tmpId,_tmpReportedUserId,_tmpReporterUserId,_tmpReason,_tmpDescription,_tmpBlockUser,_tmpCreatedAt)
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

  public override fun reportsAgainst(userId: Long): Flow<List<ReportEntity>> {
    val _sql: String = "SELECT * FROM reports WHERE reportedUserId = ? ORDER BY createdAt DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("reports"), object :
        Callable<List<ReportEntity>> {
      public override fun call(): List<ReportEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfReportedUserId: Int = getColumnIndexOrThrow(_cursor, "reportedUserId")
          val _cursorIndexOfReporterUserId: Int = getColumnIndexOrThrow(_cursor, "reporterUserId")
          val _cursorIndexOfReason: Int = getColumnIndexOrThrow(_cursor, "reason")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfBlockUser: Int = getColumnIndexOrThrow(_cursor, "blockUser")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _result: MutableList<ReportEntity> = ArrayList<ReportEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ReportEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpReportedUserId: Long
            _tmpReportedUserId = _cursor.getLong(_cursorIndexOfReportedUserId)
            val _tmpReporterUserId: Long
            _tmpReporterUserId = _cursor.getLong(_cursorIndexOfReporterUserId)
            val _tmpReason: String
            _tmpReason = _cursor.getString(_cursorIndexOfReason)
            val _tmpDescription: String?
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            }
            val _tmpBlockUser: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfBlockUser)
            _tmpBlockUser = _tmp != 0
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            _item =
                ReportEntity(_tmpId,_tmpReportedUserId,_tmpReporterUserId,_tmpReason,_tmpDescription,_tmpBlockUser,_tmpCreatedAt)
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
