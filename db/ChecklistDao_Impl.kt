package com.example.tripshare.`data`.db

import android.database.Cursor
import android.os.CancellationSignal
import androidx.collection.LongSparseArray
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.appendPlaceholders
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.newStringBuilder
import androidx.room.util.query
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.ChecklistCategoryEntity
import com.example.tripshare.`data`.model.ChecklistItemEntity
import com.example.tripshare.`data`.model.InviteStatus
import com.example.tripshare.`data`.model.ItineraryItemEntity
import com.example.tripshare.`data`.model.ParticipantInviteEntity
import com.example.tripshare.`data`.model.ParticipantRole
import com.example.tripshare.`data`.model.ParticipationStatus
import com.example.tripshare.`data`.model.PaymentMethod
import com.example.tripshare.`data`.model.RouteStopEntity
import com.example.tripshare.`data`.model.StopType
import com.example.tripshare.`data`.model.TripCategory
import com.example.tripshare.`data`.model.TripEntity
import com.example.tripshare.`data`.model.TripMeetingPointEntity
import com.example.tripshare.`data`.model.TripParticipantEntity
import com.example.tripshare.`data`.model.TripPaymentMethodEntity
import com.example.tripshare.`data`.model.Visibility
import java.lang.Class
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
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
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class ChecklistDao_Impl(
  __db: RoomDatabase,
) : ChecklistDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfTripEntity: EntityInsertionAdapter<TripEntity>

  private val __converters: Converters = Converters()

  private val __insertionAdapterOfChecklistCategoryEntity:
      EntityInsertionAdapter<ChecklistCategoryEntity>

  private val __insertionAdapterOfChecklistItemEntity: EntityInsertionAdapter<ChecklistItemEntity>

  private val __updateAdapterOfChecklistCategoryEntity:
      EntityDeletionOrUpdateAdapter<ChecklistCategoryEntity>

  private val __updateAdapterOfChecklistItemEntity:
      EntityDeletionOrUpdateAdapter<ChecklistItemEntity>

  private val __preparedStmtOfSetCompleted: SharedSQLiteStatement

  private val __preparedStmtOfDeleteItem: SharedSQLiteStatement

  private val __preparedStmtOfDeleteCategory: SharedSQLiteStatement

  private val __preparedStmtOfSetQuantity: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfTripEntity = object : EntityInsertionAdapter<TripEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `trips` (`id`,`name`,`firebaseId`,`organizerId`,`category`,`visibility`,`startDate`,`endDate`,`maxParticipants`,`costSharing`,`paymentDeadline`,`waitlistEnabled`,`budget`,`budgetDisplay`,`description`,`coverImgUrl`,`isArchived`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TripEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.organizerId)
        val _tmp: String? = __converters.catTo(entity.category)
        if (_tmp == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmp)
        }
        val _tmp_1: String? = __converters.visTo(entity.visibility)
        if (_tmp_1 == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmp_1)
        }
        val _tmpStartDate: LocalDate? = entity.startDate
        val _tmp_2: Long? = __converters.toEpochDay(_tmpStartDate)
        if (_tmp_2 == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmp_2)
        }
        val _tmpEndDate: LocalDate? = entity.endDate
        val _tmp_3: Long? = __converters.toEpochDay(_tmpEndDate)
        if (_tmp_3 == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmp_3)
        }
        statement.bindLong(9, entity.maxParticipants.toLong())
        val _tmp_4: Int = if (entity.costSharing) 1 else 0
        statement.bindLong(10, _tmp_4.toLong())
        val _tmpPaymentDeadline: LocalDate? = entity.paymentDeadline
        val _tmp_5: Long? = __converters.toEpochDay(_tmpPaymentDeadline)
        if (_tmp_5 == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmp_5)
        }
        val _tmp_6: Int = if (entity.waitlistEnabled) 1 else 0
        statement.bindLong(12, _tmp_6.toLong())
        val _tmpBudget: Double? = entity.budget
        if (_tmpBudget == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpBudget)
        }
        val _tmpBudgetDisplay: String? = entity.budgetDisplay
        if (_tmpBudgetDisplay == null) {
          statement.bindNull(14)
        } else {
          statement.bindString(14, _tmpBudgetDisplay)
        }
        statement.bindString(15, entity.description)
        val _tmpCoverImgUrl: String? = entity.coverImgUrl
        if (_tmpCoverImgUrl == null) {
          statement.bindNull(16)
        } else {
          statement.bindString(16, _tmpCoverImgUrl)
        }
        val _tmp_7: Int = if (entity.isArchived) 1 else 0
        statement.bindLong(17, _tmp_7.toLong())
      }
    }
    this.__insertionAdapterOfChecklistCategoryEntity = object :
        EntityInsertionAdapter<ChecklistCategoryEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `checklist_categories` (`categoryId`,`tripId`,`firebaseId`,`categoryName`,`sort`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: ChecklistCategoryEntity) {
        statement.bindLong(1, entity.categoryId)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.categoryName)
        statement.bindLong(5, entity.sort.toLong())
      }
    }
    this.__insertionAdapterOfChecklistItemEntity = object :
        EntityInsertionAdapter<ChecklistItemEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `checklist_items` (`itemId`,`categoryId`,`firebaseId`,`title`,`completed`,`dueDate`,`note`,`sort`,`quantity`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ChecklistItemEntity) {
        statement.bindLong(1, entity.itemId)
        statement.bindLong(2, entity.categoryId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.title)
        val _tmp: Int = if (entity.completed) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmpDueDate: LocalDate? = entity.dueDate
        val _tmp_1: Long? = __converters.toEpochDay(_tmpDueDate)
        if (_tmp_1 == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmp_1)
        }
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(7)
        } else {
          statement.bindString(7, _tmpNote)
        }
        statement.bindLong(8, entity.sort.toLong())
        statement.bindLong(9, entity.quantity.toLong())
      }
    }
    this.__updateAdapterOfChecklistCategoryEntity = object :
        EntityDeletionOrUpdateAdapter<ChecklistCategoryEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `checklist_categories` SET `categoryId` = ?,`tripId` = ?,`firebaseId` = ?,`categoryName` = ?,`sort` = ? WHERE `categoryId` = ?"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: ChecklistCategoryEntity) {
        statement.bindLong(1, entity.categoryId)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.categoryName)
        statement.bindLong(5, entity.sort.toLong())
        statement.bindLong(6, entity.categoryId)
      }
    }
    this.__updateAdapterOfChecklistItemEntity = object :
        EntityDeletionOrUpdateAdapter<ChecklistItemEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `checklist_items` SET `itemId` = ?,`categoryId` = ?,`firebaseId` = ?,`title` = ?,`completed` = ?,`dueDate` = ?,`note` = ?,`sort` = ?,`quantity` = ? WHERE `itemId` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ChecklistItemEntity) {
        statement.bindLong(1, entity.itemId)
        statement.bindLong(2, entity.categoryId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.title)
        val _tmp: Int = if (entity.completed) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmpDueDate: LocalDate? = entity.dueDate
        val _tmp_1: Long? = __converters.toEpochDay(_tmpDueDate)
        if (_tmp_1 == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmp_1)
        }
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(7)
        } else {
          statement.bindString(7, _tmpNote)
        }
        statement.bindLong(8, entity.sort.toLong())
        statement.bindLong(9, entity.quantity.toLong())
        statement.bindLong(10, entity.itemId)
      }
    }
    this.__preparedStmtOfSetCompleted = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE checklist_items SET completed = ? WHERE itemId = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteItem = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM checklist_items WHERE itemId = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteCategory = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM checklist_categories WHERE categoryId = ?"
        return _query
      }
    }
    this.__preparedStmtOfSetQuantity = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE checklist_items SET quantity = ? WHERE itemId = ?"
        return _query
      }
    }
  }

  public override suspend fun upsertTrip(entity: TripEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfTripEntity.insertAndReturnId(entity)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertCategory(entity: ChecklistCategoryEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfChecklistCategoryEntity.insertAndReturnId(entity)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertItem(entity: ChecklistItemEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfChecklistItemEntity.insertAndReturnId(entity)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateCategory(cat: ChecklistCategoryEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfChecklistCategoryEntity.handle(cat)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateItem(item: ChecklistItemEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfChecklistItemEntity.handle(item)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun toggleCompleted(itemId: Long, current: Boolean) {
    __db.withTransaction {
      super@ChecklistDao_Impl.toggleCompleted(itemId, current)
    }
  }

  public override suspend fun setCompleted(itemId: Long, completed: Boolean): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSetCompleted.acquire()
      var _argIndex: Int = 1
      val _tmp: Int = if (completed) 1 else 0
      _stmt.bindLong(_argIndex, _tmp.toLong())
      _argIndex = 2
      _stmt.bindLong(_argIndex, itemId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSetCompleted.release(_stmt)
      }
    }
  })

  public override suspend fun deleteItem(itemId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteItem.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, itemId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteItem.release(_stmt)
      }
    }
  })

  public override suspend fun deleteCategory(categoryId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteCategory.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, categoryId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteCategory.release(_stmt)
      }
    }
  })

  public override suspend fun toggle(itemId: Long, completed: Boolean): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSetCompleted.acquire()
      var _argIndex: Int = 1
      val _tmp: Int = if (completed) 1 else 0
      _stmt.bindLong(_argIndex, _tmp.toLong())
      _argIndex = 2
      _stmt.bindLong(_argIndex, itemId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSetCompleted.release(_stmt)
      }
    }
  })

  public override suspend fun setQuantity(itemId: Long, quantity: Int): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSetQuantity.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, quantity.toLong())
      _argIndex = 2
      _stmt.bindLong(_argIndex, itemId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSetQuantity.release(_stmt)
      }
    }
  })

  public override fun observeCategoriesWithItems(tripId: Long): Flow<List<CategoryWithItems>> {
    val _sql: String = """
        |
        |        SELECT * FROM checklist_categories
        |        WHERE tripId = ?
        |        ORDER BY sort, categoryId
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, true, arrayOf("checklist_items", "checklist_categories"),
        object : Callable<List<CategoryWithItems>> {
      public override fun call(): List<CategoryWithItems> {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "categoryId")
            val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfCategoryName: Int = getColumnIndexOrThrow(_cursor, "categoryName")
            val _cursorIndexOfSort: Int = getColumnIndexOrThrow(_cursor, "sort")
            val _collectionItems: LongSparseArray<ArrayList<ChecklistItemEntity>> =
                LongSparseArray<ArrayList<ChecklistItemEntity>>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId)
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, ArrayList<ChecklistItemEntity>())
              }
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshipchecklistItemsAscomExampleTripshareDataModelChecklistItemEntity(_collectionItems)
            val _result: MutableList<CategoryWithItems> =
                ArrayList<CategoryWithItems>(_cursor.getCount())
            while (_cursor.moveToNext()) {
              val _item: CategoryWithItems
              val _tmpCategory: ChecklistCategoryEntity
              val _tmpCategoryId: Long
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId)
              val _tmpTripId: Long
              _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
              val _tmpFirebaseId: String?
              if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
                _tmpFirebaseId = null
              } else {
                _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
              }
              val _tmpCategoryName: String
              _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName)
              val _tmpSort: Int
              _tmpSort = _cursor.getInt(_cursorIndexOfSort)
              _tmpCategory =
                  ChecklistCategoryEntity(_tmpCategoryId,_tmpTripId,_tmpFirebaseId,_tmpCategoryName,_tmpSort)
              val _tmpItemsCollection: ArrayList<ChecklistItemEntity>
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId)
              _tmpItemsCollection = checkNotNull(_collectionItems.get(_tmpKey_1))
              _item = CategoryWithItems(_tmpCategory,_tmpItemsCollection)
              _result.add(_item)
            }
            __db.setTransactionSuccessful()
            return _result
          } finally {
            _cursor.close()
          }
        } finally {
          __db.endTransaction()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeTrips(): Flow<List<TripEntity>> {
    val _sql: String = "SELECT * FROM trips ORDER BY id DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object :
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

  public override suspend fun getTripFull(id: Long): TripFullAggregate {
    val _sql: String = "SELECT * FROM trips WHERE id = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, true, _cancellationSignal, object : Callable<TripFullAggregate> {
      public override fun call(): TripFullAggregate {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
            val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
            val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
            val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
            val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
            val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor,
                "maxParticipants")
            val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
            val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor,
                "paymentDeadline")
            val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor,
                "waitlistEnabled")
            val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
            val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
            val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
            val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
            val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
            val _collectionRouteStops: LongSparseArray<ArrayList<RouteStopEntity>> =
                LongSparseArray<ArrayList<RouteStopEntity>>()
            val _collectionParticipants: LongSparseArray<ArrayList<TripParticipantEntity>> =
                LongSparseArray<ArrayList<TripParticipantEntity>>()
            val _collectionMeetingPoints: LongSparseArray<ArrayList<TripMeetingPointEntity>> =
                LongSparseArray<ArrayList<TripMeetingPointEntity>>()
            val _collectionItinerary: LongSparseArray<ArrayList<ItineraryItemEntity>> =
                LongSparseArray<ArrayList<ItineraryItemEntity>>()
            val _collectionPaymentMethods: LongSparseArray<ArrayList<TripPaymentMethodEntity>> =
                LongSparseArray<ArrayList<TripPaymentMethodEntity>>()
            val _collectionInvites: LongSparseArray<ArrayList<ParticipantInviteEntity>> =
                LongSparseArray<ArrayList<ParticipantInviteEntity>>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionRouteStops.containsKey(_tmpKey)) {
                _collectionRouteStops.put(_tmpKey, ArrayList<RouteStopEntity>())
              }
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionParticipants.containsKey(_tmpKey_1)) {
                _collectionParticipants.put(_tmpKey_1, ArrayList<TripParticipantEntity>())
              }
              val _tmpKey_2: Long
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionMeetingPoints.containsKey(_tmpKey_2)) {
                _collectionMeetingPoints.put(_tmpKey_2, ArrayList<TripMeetingPointEntity>())
              }
              val _tmpKey_3: Long
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionItinerary.containsKey(_tmpKey_3)) {
                _collectionItinerary.put(_tmpKey_3, ArrayList<ItineraryItemEntity>())
              }
              val _tmpKey_4: Long
              _tmpKey_4 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionPaymentMethods.containsKey(_tmpKey_4)) {
                _collectionPaymentMethods.put(_tmpKey_4, ArrayList<TripPaymentMethodEntity>())
              }
              val _tmpKey_5: Long
              _tmpKey_5 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionInvites.containsKey(_tmpKey_5)) {
                _collectionInvites.put(_tmpKey_5, ArrayList<ParticipantInviteEntity>())
              }
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshiprouteStopsAscomExampleTripshareDataModelRouteStopEntity(_collectionRouteStops)
            __fetchRelationshiptripParticipantsAscomExampleTripshareDataModelTripParticipantEntity(_collectionParticipants)
            __fetchRelationshiptripMeetingPointsAscomExampleTripshareDataModelTripMeetingPointEntity(_collectionMeetingPoints)
            __fetchRelationshipitineraryItemsAscomExampleTripshareDataModelItineraryItemEntity(_collectionItinerary)
            __fetchRelationshiptripPaymentMethodsAscomExampleTripshareDataModelTripPaymentMethodEntity(_collectionPaymentMethods)
            __fetchRelationshipparticipantInvitesAscomExampleTripshareDataModelParticipantInviteEntity(_collectionInvites)
            val _result: TripFullAggregate
            if (_cursor.moveToFirst()) {
              val _tmpTrip: TripEntity
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
              _tmpTrip =
                  TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
              val _tmpRouteStopsCollection: ArrayList<RouteStopEntity>
              val _tmpKey_6: Long
              _tmpKey_6 = _cursor.getLong(_cursorIndexOfId)
              _tmpRouteStopsCollection = checkNotNull(_collectionRouteStops.get(_tmpKey_6))
              val _tmpParticipantsCollection: ArrayList<TripParticipantEntity>
              val _tmpKey_7: Long
              _tmpKey_7 = _cursor.getLong(_cursorIndexOfId)
              _tmpParticipantsCollection = checkNotNull(_collectionParticipants.get(_tmpKey_7))
              val _tmpMeetingPointsCollection: ArrayList<TripMeetingPointEntity>
              val _tmpKey_8: Long
              _tmpKey_8 = _cursor.getLong(_cursorIndexOfId)
              _tmpMeetingPointsCollection = checkNotNull(_collectionMeetingPoints.get(_tmpKey_8))
              val _tmpItineraryCollection: ArrayList<ItineraryItemEntity>
              val _tmpKey_9: Long
              _tmpKey_9 = _cursor.getLong(_cursorIndexOfId)
              _tmpItineraryCollection = checkNotNull(_collectionItinerary.get(_tmpKey_9))
              val _tmpPaymentMethodsCollection: ArrayList<TripPaymentMethodEntity>
              val _tmpKey_10: Long
              _tmpKey_10 = _cursor.getLong(_cursorIndexOfId)
              _tmpPaymentMethodsCollection = checkNotNull(_collectionPaymentMethods.get(_tmpKey_10))
              val _tmpInvitesCollection: ArrayList<ParticipantInviteEntity>
              val _tmpKey_11: Long
              _tmpKey_11 = _cursor.getLong(_cursorIndexOfId)
              _tmpInvitesCollection = checkNotNull(_collectionInvites.get(_tmpKey_11))
              _result =
                  TripFullAggregate(_tmpTrip,_tmpRouteStopsCollection,_tmpParticipantsCollection,_tmpMeetingPointsCollection,_tmpItineraryCollection,_tmpPaymentMethodsCollection,_tmpInvitesCollection)
            } else {
              error("The query result was empty, but expected a single row to return a NON-NULL object of type <com.example.tripshare.`data`.db.TripFullAggregate>.")
            }
            __db.setTransactionSuccessful()
            return _result
          } finally {
            _cursor.close()
            _statement.release()
          }
        } finally {
          __db.endTransaction()
        }
      }
    })
  }

  public override suspend fun getCategoryByFirebaseId(fid: String): ChecklistCategoryEntity? {
    val _sql: String = "SELECT * FROM checklist_categories WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ChecklistCategoryEntity?> {
      public override fun call(): ChecklistCategoryEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "categoryId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfCategoryName: Int = getColumnIndexOrThrow(_cursor, "categoryName")
          val _cursorIndexOfSort: Int = getColumnIndexOrThrow(_cursor, "sort")
          val _result: ChecklistCategoryEntity?
          if (_cursor.moveToFirst()) {
            val _tmpCategoryId: Long
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpCategoryName: String
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName)
            val _tmpSort: Int
            _tmpSort = _cursor.getInt(_cursorIndexOfSort)
            _result =
                ChecklistCategoryEntity(_tmpCategoryId,_tmpTripId,_tmpFirebaseId,_tmpCategoryName,_tmpSort)
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

  public override suspend fun getItemByFirebaseId(fid: String): ChecklistItemEntity? {
    val _sql: String = "SELECT * FROM checklist_items WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ChecklistItemEntity?> {
      public override fun call(): ChecklistItemEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfItemId: Int = getColumnIndexOrThrow(_cursor, "itemId")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "categoryId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_cursor, "completed")
          val _cursorIndexOfDueDate: Int = getColumnIndexOrThrow(_cursor, "dueDate")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _cursorIndexOfSort: Int = getColumnIndexOrThrow(_cursor, "sort")
          val _cursorIndexOfQuantity: Int = getColumnIndexOrThrow(_cursor, "quantity")
          val _result: ChecklistItemEntity?
          if (_cursor.moveToFirst()) {
            val _tmpItemId: Long
            _tmpItemId = _cursor.getLong(_cursorIndexOfItemId)
            val _tmpCategoryId: Long
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpCompleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfCompleted)
            _tmpCompleted = _tmp != 0
            val _tmpDueDate: LocalDate?
            val _tmp_1: Long?
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate)
            }
            _tmpDueDate = __converters.fromEpochDay(_tmp_1)
            val _tmpNote: String?
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote)
            }
            val _tmpSort: Int
            _tmpSort = _cursor.getInt(_cursorIndexOfSort)
            val _tmpQuantity: Int
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity)
            _result =
                ChecklistItemEntity(_tmpItemId,_tmpCategoryId,_tmpFirebaseId,_tmpTitle,_tmpCompleted,_tmpDueDate,_tmpNote,_tmpSort,_tmpQuantity)
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

  public override suspend fun getCategoryById(id: Long): ChecklistCategoryEntity? {
    val _sql: String = "SELECT * FROM checklist_categories WHERE categoryId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ChecklistCategoryEntity?> {
      public override fun call(): ChecklistCategoryEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "categoryId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfCategoryName: Int = getColumnIndexOrThrow(_cursor, "categoryName")
          val _cursorIndexOfSort: Int = getColumnIndexOrThrow(_cursor, "sort")
          val _result: ChecklistCategoryEntity?
          if (_cursor.moveToFirst()) {
            val _tmpCategoryId: Long
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpCategoryName: String
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName)
            val _tmpSort: Int
            _tmpSort = _cursor.getInt(_cursorIndexOfSort)
            _result =
                ChecklistCategoryEntity(_tmpCategoryId,_tmpTripId,_tmpFirebaseId,_tmpCategoryName,_tmpSort)
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

  public override suspend fun getItemById(id: Long): ChecklistItemEntity? {
    val _sql: String = "SELECT * FROM checklist_items WHERE itemId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ChecklistItemEntity?> {
      public override fun call(): ChecklistItemEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfItemId: Int = getColumnIndexOrThrow(_cursor, "itemId")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "categoryId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_cursor, "completed")
          val _cursorIndexOfDueDate: Int = getColumnIndexOrThrow(_cursor, "dueDate")
          val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_cursor, "note")
          val _cursorIndexOfSort: Int = getColumnIndexOrThrow(_cursor, "sort")
          val _cursorIndexOfQuantity: Int = getColumnIndexOrThrow(_cursor, "quantity")
          val _result: ChecklistItemEntity?
          if (_cursor.moveToFirst()) {
            val _tmpItemId: Long
            _tmpItemId = _cursor.getLong(_cursorIndexOfItemId)
            val _tmpCategoryId: Long
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpCompleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfCompleted)
            _tmpCompleted = _tmp != 0
            val _tmpDueDate: LocalDate?
            val _tmp_1: Long?
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate)
            }
            _tmpDueDate = __converters.fromEpochDay(_tmp_1)
            val _tmpNote: String?
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote)
            }
            val _tmpSort: Int
            _tmpSort = _cursor.getInt(_cursorIndexOfSort)
            val _tmpQuantity: Int
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity)
            _result =
                ChecklistItemEntity(_tmpItemId,_tmpCategoryId,_tmpFirebaseId,_tmpTitle,_tmpCompleted,_tmpDueDate,_tmpNote,_tmpSort,_tmpQuantity)
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

  private
      fun __fetchRelationshipchecklistItemsAscomExampleTripshareDataModelChecklistItemEntity(_map: LongSparseArray<ArrayList<ChecklistItemEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshipchecklistItemsAscomExampleTripshareDataModelChecklistItemEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `itemId`,`categoryId`,`firebaseId`,`title`,`completed`,`dueDate`,`note`,`sort`,`quantity` FROM `checklist_items` WHERE `categoryId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "categoryId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfItemId: Int = 0
      val _cursorIndexOfCategoryId: Int = 1
      val _cursorIndexOfFirebaseId: Int = 2
      val _cursorIndexOfTitle: Int = 3
      val _cursorIndexOfCompleted: Int = 4
      val _cursorIndexOfDueDate: Int = 5
      val _cursorIndexOfNote: Int = 6
      val _cursorIndexOfSort: Int = 7
      val _cursorIndexOfQuantity: Int = 8
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<ChecklistItemEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: ChecklistItemEntity
          val _tmpItemId: Long
          _tmpItemId = _cursor.getLong(_cursorIndexOfItemId)
          val _tmpCategoryId: Long
          _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId)
          val _tmpFirebaseId: String?
          if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
            _tmpFirebaseId = null
          } else {
            _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
          }
          val _tmpTitle: String
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
          val _tmpCompleted: Boolean
          val _tmp: Int
          _tmp = _cursor.getInt(_cursorIndexOfCompleted)
          _tmpCompleted = _tmp != 0
          val _tmpDueDate: LocalDate?
          val _tmp_1: Long?
          if (_cursor.isNull(_cursorIndexOfDueDate)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate)
          }
          _tmpDueDate = __converters.fromEpochDay(_tmp_1)
          val _tmpNote: String?
          if (_cursor.isNull(_cursorIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _cursor.getString(_cursorIndexOfNote)
          }
          val _tmpSort: Int
          _tmpSort = _cursor.getInt(_cursorIndexOfSort)
          val _tmpQuantity: Int
          _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity)
          _item_1 =
              ChecklistItemEntity(_tmpItemId,_tmpCategoryId,_tmpFirebaseId,_tmpTitle,_tmpCompleted,_tmpDueDate,_tmpNote,_tmpSort,_tmpQuantity)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private fun __StopType_stringToEnum(_value: String): StopType = when (_value) {
    "START" -> StopType.START
    "END" -> StopType.END
    "STOP" -> StopType.STOP
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private
      fun __fetchRelationshiprouteStopsAscomExampleTripshareDataModelRouteStopEntity(_map: LongSparseArray<ArrayList<RouteStopEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiprouteStopsAscomExampleTripshareDataModelRouteStopEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`type`,`label`,`lat`,`lng`,`orderInRoute`,`startDate`,`endDate`,`nights` FROM `route_stops` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfType: Int = 2
      val _cursorIndexOfLabel: Int = 3
      val _cursorIndexOfLat: Int = 4
      val _cursorIndexOfLng: Int = 5
      val _cursorIndexOfOrderInRoute: Int = 6
      val _cursorIndexOfStartDate: Int = 7
      val _cursorIndexOfEndDate: Int = 8
      val _cursorIndexOfNights: Int = 9
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<RouteStopEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: RouteStopEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpType: StopType
          _tmpType = __StopType_stringToEnum(_cursor.getString(_cursorIndexOfType))
          val _tmpLabel: String
          _tmpLabel = _cursor.getString(_cursorIndexOfLabel)
          val _tmpLat: Double?
          if (_cursor.isNull(_cursorIndexOfLat)) {
            _tmpLat = null
          } else {
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
          }
          val _tmpLng: Double?
          if (_cursor.isNull(_cursorIndexOfLng)) {
            _tmpLng = null
          } else {
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
          }
          val _tmpOrderInRoute: Int
          _tmpOrderInRoute = _cursor.getInt(_cursorIndexOfOrderInRoute)
          val _tmpStartDate: String?
          if (_cursor.isNull(_cursorIndexOfStartDate)) {
            _tmpStartDate = null
          } else {
            _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate)
          }
          val _tmpEndDate: String?
          if (_cursor.isNull(_cursorIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate)
          }
          val _tmpNights: Int
          _tmpNights = _cursor.getInt(_cursorIndexOfNights)
          _item_1 =
              RouteStopEntity(_tmpId,_tmpTripId,_tmpType,_tmpLabel,_tmpLat,_tmpLng,_tmpOrderInRoute,_tmpStartDate,_tmpEndDate,_tmpNights)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private fun __ParticipationStatus_stringToEnum(_value: String): ParticipationStatus = when
      (_value) {
    "JOINED" -> ParticipationStatus.JOINED
    "INVITED" -> ParticipationStatus.INVITED
    "PENDING" -> ParticipationStatus.PENDING
    "LEFT" -> ParticipationStatus.LEFT
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ParticipantRole_stringToEnum(_value: String): ParticipantRole = when (_value) {
    "OWNER" -> ParticipantRole.OWNER
    "MEMBER" -> ParticipantRole.MEMBER
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private
      fun __fetchRelationshiptripParticipantsAscomExampleTripshareDataModelTripParticipantEntity(_map: LongSparseArray<ArrayList<TripParticipantEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiptripParticipantsAscomExampleTripshareDataModelTripParticipantEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`userId`,`email`,`displayName`,`status`,`role`,`joinedAt` FROM `trip_participants` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfUserId: Int = 2
      val _cursorIndexOfEmail: Int = 3
      val _cursorIndexOfDisplayName: Int = 4
      val _cursorIndexOfStatus: Int = 5
      val _cursorIndexOfRole: Int = 6
      val _cursorIndexOfJoinedAt: Int = 7
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<TripParticipantEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: TripParticipantEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpUserId: Long
          _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
          val _tmpEmail: String
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
          val _tmpDisplayName: String
          _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
          val _tmpStatus: ParticipationStatus
          _tmpStatus = __ParticipationStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
          val _tmpRole: ParticipantRole
          _tmpRole = __ParticipantRole_stringToEnum(_cursor.getString(_cursorIndexOfRole))
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt)
          _item_1 =
              TripParticipantEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpEmail,_tmpDisplayName,_tmpStatus,_tmpRole,_tmpJoinedAt)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private
      fun __fetchRelationshiptripMeetingPointsAscomExampleTripshareDataModelTripMeetingPointEntity(_map: LongSparseArray<ArrayList<TripMeetingPointEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiptripMeetingPointsAscomExampleTripshareDataModelTripMeetingPointEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`label`,`lat`,`lng` FROM `trip_meeting_points` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfLabel: Int = 2
      val _cursorIndexOfLat: Int = 3
      val _cursorIndexOfLng: Int = 4
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<TripMeetingPointEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: TripMeetingPointEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpLabel: String
          _tmpLabel = _cursor.getString(_cursorIndexOfLabel)
          val _tmpLat: Double?
          if (_cursor.isNull(_cursorIndexOfLat)) {
            _tmpLat = null
          } else {
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
          }
          val _tmpLng: Double?
          if (_cursor.isNull(_cursorIndexOfLng)) {
            _tmpLng = null
          } else {
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
          }
          _item_1 = TripMeetingPointEntity(_tmpId,_tmpTripId,_tmpLabel,_tmpLat,_tmpLng)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private
      fun __fetchRelationshipitineraryItemsAscomExampleTripshareDataModelItineraryItemEntity(_map: LongSparseArray<ArrayList<ItineraryItemEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshipitineraryItemsAscomExampleTripshareDataModelItineraryItemEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`firebaseId`,`day`,`title`,`date`,`time`,`location`,`notes`,`category`,`attachment`,`assignedTo`,`endDate`,`endTime`,`lat`,`lng` FROM `itinerary_items` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfFirebaseId: Int = 2
      val _cursorIndexOfDay: Int = 3
      val _cursorIndexOfTitle: Int = 4
      val _cursorIndexOfDate: Int = 5
      val _cursorIndexOfTime: Int = 6
      val _cursorIndexOfLocation: Int = 7
      val _cursorIndexOfNotes: Int = 8
      val _cursorIndexOfCategory: Int = 9
      val _cursorIndexOfAttachment: Int = 10
      val _cursorIndexOfAssignedTo: Int = 11
      val _cursorIndexOfEndDate: Int = 12
      val _cursorIndexOfEndTime: Int = 13
      val _cursorIndexOfLat: Int = 14
      val _cursorIndexOfLng: Int = 15
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<ItineraryItemEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: ItineraryItemEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpFirebaseId: String?
          if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
            _tmpFirebaseId = null
          } else {
            _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
          }
          val _tmpDay: Int
          _tmpDay = _cursor.getInt(_cursorIndexOfDay)
          val _tmpTitle: String
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
          val _tmpDate: String
          _tmpDate = _cursor.getString(_cursorIndexOfDate)
          val _tmpTime: String
          _tmpTime = _cursor.getString(_cursorIndexOfTime)
          val _tmpLocation: String?
          if (_cursor.isNull(_cursorIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
          }
          val _tmpNotes: String?
          if (_cursor.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes)
          }
          val _tmpCategory: String?
          if (_cursor.isNull(_cursorIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
          }
          val _tmpAttachment: String?
          if (_cursor.isNull(_cursorIndexOfAttachment)) {
            _tmpAttachment = null
          } else {
            _tmpAttachment = _cursor.getString(_cursorIndexOfAttachment)
          }
          val _tmpAssignedTo: String?
          if (_cursor.isNull(_cursorIndexOfAssignedTo)) {
            _tmpAssignedTo = null
          } else {
            _tmpAssignedTo = _cursor.getString(_cursorIndexOfAssignedTo)
          }
          val _tmpEndDate: String?
          if (_cursor.isNull(_cursorIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate)
          }
          val _tmpEndTime: String?
          if (_cursor.isNull(_cursorIndexOfEndTime)) {
            _tmpEndTime = null
          } else {
            _tmpEndTime = _cursor.getString(_cursorIndexOfEndTime)
          }
          val _tmpLat: Double?
          if (_cursor.isNull(_cursorIndexOfLat)) {
            _tmpLat = null
          } else {
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
          }
          val _tmpLng: Double?
          if (_cursor.isNull(_cursorIndexOfLng)) {
            _tmpLng = null
          } else {
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
          }
          _item_1 =
              ItineraryItemEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpDay,_tmpTitle,_tmpDate,_tmpTime,_tmpLocation,_tmpNotes,_tmpCategory,_tmpAttachment,_tmpAssignedTo,_tmpEndDate,_tmpEndTime,_tmpLat,_tmpLng)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private fun __PaymentMethod_stringToEnum(_value: String): PaymentMethod = when (_value) {
    "EWALLET" -> PaymentMethod.EWALLET
    "CASH" -> PaymentMethod.CASH
    "BANK_TRANSFER" -> PaymentMethod.BANK_TRANSFER
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private
      fun __fetchRelationshiptripPaymentMethodsAscomExampleTripshareDataModelTripPaymentMethodEntity(_map: LongSparseArray<ArrayList<TripPaymentMethodEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiptripPaymentMethodsAscomExampleTripshareDataModelTripPaymentMethodEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`method` FROM `trip_payment_methods` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfMethod: Int = 2
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<TripPaymentMethodEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: TripPaymentMethodEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpMethod: PaymentMethod
          _tmpMethod = __PaymentMethod_stringToEnum(_cursor.getString(_cursorIndexOfMethod))
          _item_1 = TripPaymentMethodEntity(_tmpId,_tmpTripId,_tmpMethod)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private fun __InviteStatus_stringToEnum(_value: String): InviteStatus = when (_value) {
    "INVITED" -> InviteStatus.INVITED
    "ACCEPTED" -> InviteStatus.ACCEPTED
    "DECLINED" -> InviteStatus.DECLINED
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private
      fun __fetchRelationshipparticipantInvitesAscomExampleTripshareDataModelParticipantInviteEntity(_map: LongSparseArray<ArrayList<ParticipantInviteEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshipparticipantInvitesAscomExampleTripshareDataModelParticipantInviteEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`identifier`,`status` FROM `participant_invites` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfIdentifier: Int = 2
      val _cursorIndexOfStatus: Int = 3
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<ParticipantInviteEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: ParticipantInviteEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpIdentifier: String
          _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier)
          val _tmpStatus: InviteStatus
          _tmpStatus = __InviteStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
          _item_1 = ParticipantInviteEntity(_tmpId,_tmpTripId,_tmpIdentifier,_tmpStatus)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
