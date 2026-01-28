package com.example.tripshare.`data`.db

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import com.example.tripshare.`data`.model.TripCategory
import com.example.tripshare.`data`.model.TripEntity
import com.example.tripshare.`data`.model.Visibility
import java.lang.Class
import java.time.LocalDate
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
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
public class TripHistoryDao_Impl(
  __db: RoomDatabase,
) : TripHistoryDao {
  private val __db: RoomDatabase

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
  }

  public override fun observePastTrips(userId: Long): Flow<List<TripEntity>> {
    val _sql: String = """
        |
        |        SELECT t.* FROM trips t 
        |        INNER JOIN trip_participants p ON p.tripId = t.id
        |        WHERE p.userId = ? AND t.isArchived = 1
        |        ORDER BY t.startDate DESC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips", "trip_participants"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
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
