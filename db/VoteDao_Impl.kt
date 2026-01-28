package com.example.tripshare.`data`.db

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.PollEntity
import com.example.tripshare.`data`.model.PollVoteEntity
import com.example.tripshare.`data`.model.PollVoterDetail
import com.example.tripshare.`data`.model.VoteOptionEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class VoteDao_Impl(
  __db: RoomDatabase,
) : VoteDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfPollEntity: EntityInsertionAdapter<PollEntity>

  private val __insertionAdapterOfVoteOptionEntity: EntityInsertionAdapter<VoteOptionEntity>

  private val __insertionAdapterOfPollVoteEntity: EntityInsertionAdapter<PollVoteEntity>

  private val __preparedStmtOfUpdatePollHeader: SharedSQLiteStatement

  private val __preparedStmtOfSetVoteCount: SharedSQLiteStatement

  private val __preparedStmtOfUpdateOptionText: SharedSQLiteStatement

  private val __preparedStmtOfDeletePoll: SharedSQLiteStatement

  private val __preparedStmtOfDeleteOption: SharedSQLiteStatement

  private val __preparedStmtOfIncrementVote: SharedSQLiteStatement

  private val __preparedStmtOfClearUserVotes: SharedSQLiteStatement

  private val __preparedStmtOfDecrementVote: SharedSQLiteStatement

  private val __preparedStmtOfDeleteUserVote: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfPollEntity = object : EntityInsertionAdapter<PollEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `polls` (`id`,`tripId`,`firebaseId`,`question`,`allowMultiple`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: PollEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.question)
        val _tmp: Int = if (entity.allowMultiple) 1 else 0
        statement.bindLong(5, _tmp.toLong())
      }
    }
    this.__insertionAdapterOfVoteOptionEntity = object :
        EntityInsertionAdapter<VoteOptionEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `vote_options` (`id`,`pollId`,`firebaseId`,`option`,`votes`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: VoteOptionEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.pollId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.option)
        statement.bindLong(5, entity.votes.toLong())
      }
    }
    this.__insertionAdapterOfPollVoteEntity = object : EntityInsertionAdapter<PollVoteEntity>(__db)
        {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `poll_votes` (`id`,`pollId`,`optionId`,`userId`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: PollVoteEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.pollId)
        statement.bindLong(3, entity.optionId)
        statement.bindLong(4, entity.userId)
        statement.bindLong(5, entity.createdAt)
      }
    }
    this.__preparedStmtOfUpdatePollHeader = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE polls SET question = ?, allowMultiple = ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfSetVoteCount = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE vote_options SET votes = ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfUpdateOptionText = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE vote_options SET option = ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeletePoll = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM polls WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteOption = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM vote_options WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfIncrementVote = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE vote_options SET votes = votes + 1 WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfClearUserVotes = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM poll_votes WHERE pollId = ? AND userId = ?"
        return _query
      }
    }
    this.__preparedStmtOfDecrementVote = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE vote_options SET votes = votes - 1 WHERE id = ? AND votes > 0"
        return _query
      }
    }
    this.__preparedStmtOfDeleteUserVote = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String =
            "DELETE FROM poll_votes WHERE pollId = ? AND userId = ? AND optionId = ?"
        return _query
      }
    }
  }

  public override suspend fun insertPoll(poll: PollEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfPollEntity.insertAndReturnId(poll)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertOption(option: VoteOptionEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfVoteOptionEntity.insert(option)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertUserVote(vote: PollVoteEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfPollVoteEntity.insert(vote)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updatePollHeader(
    pollId: Long,
    question: String,
    allowMultiple: Boolean,
  ): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdatePollHeader.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, question)
      _argIndex = 2
      val _tmp: Int = if (allowMultiple) 1 else 0
      _stmt.bindLong(_argIndex, _tmp.toLong())
      _argIndex = 3
      _stmt.bindLong(_argIndex, pollId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUpdatePollHeader.release(_stmt)
      }
    }
  })

  public override suspend fun setVoteCount(optionId: Long, count: Int): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSetVoteCount.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, count.toLong())
      _argIndex = 2
      _stmt.bindLong(_argIndex, optionId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSetVoteCount.release(_stmt)
      }
    }
  })

  public override suspend fun updateOptionText(optionId: Long, text: String): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateOptionText.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, text)
      _argIndex = 2
      _stmt.bindLong(_argIndex, optionId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUpdateOptionText.release(_stmt)
      }
    }
  })

  public override suspend fun deletePoll(pollId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeletePoll.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, pollId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeletePoll.release(_stmt)
      }
    }
  })

  public override suspend fun deleteOption(optionId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteOption.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, optionId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteOption.release(_stmt)
      }
    }
  })

  public override suspend fun incrementVote(optionId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfIncrementVote.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, optionId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfIncrementVote.release(_stmt)
      }
    }
  })

  public override suspend fun clearUserVotes(pollId: Long, userId: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfClearUserVotes.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, pollId)
      _argIndex = 2
      _stmt.bindLong(_argIndex, userId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfClearUserVotes.release(_stmt)
      }
    }
  })

  public override suspend fun decrementVote(optionId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDecrementVote.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, optionId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDecrementVote.release(_stmt)
      }
    }
  })

  public override suspend fun deleteUserVotesForPoll(pollId: Long, userId: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfClearUserVotes.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, pollId)
      _argIndex = 2
      _stmt.bindLong(_argIndex, userId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfClearUserVotes.release(_stmt)
      }
    }
  })

  public override suspend fun deleteUserVote(
    pollId: Long,
    userId: Long,
    optionId: Long,
  ): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteUserVote.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, pollId)
      _argIndex = 2
      _stmt.bindLong(_argIndex, userId)
      _argIndex = 3
      _stmt.bindLong(_argIndex, optionId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteUserVote.release(_stmt)
      }
    }
  })

  public override fun observePolls(tripId: Long): Flow<List<PollEntity>> {
    val _sql: String = "SELECT * FROM polls WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("polls"), object :
        Callable<List<PollEntity>> {
      public override fun call(): List<PollEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfQuestion: Int = getColumnIndexOrThrow(_cursor, "question")
          val _cursorIndexOfAllowMultiple: Int = getColumnIndexOrThrow(_cursor, "allowMultiple")
          val _result: MutableList<PollEntity> = ArrayList<PollEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: PollEntity
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
            val _tmpQuestion: String
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion)
            val _tmpAllowMultiple: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAllowMultiple)
            _tmpAllowMultiple = _tmp != 0
            _item = PollEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpQuestion,_tmpAllowMultiple)
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

  public override fun observeOptions(pollId: Long): Flow<List<VoteOptionEntity>> {
    val _sql: String = "SELECT * FROM vote_options WHERE pollId = ? ORDER BY id ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, pollId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("vote_options"), object :
        Callable<List<VoteOptionEntity>> {
      public override fun call(): List<VoteOptionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfPollId: Int = getColumnIndexOrThrow(_cursor, "pollId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOption: Int = getColumnIndexOrThrow(_cursor, "option")
          val _cursorIndexOfVotes: Int = getColumnIndexOrThrow(_cursor, "votes")
          val _result: MutableList<VoteOptionEntity> =
              ArrayList<VoteOptionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: VoteOptionEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpPollId: Long
            _tmpPollId = _cursor.getLong(_cursorIndexOfPollId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOption: String
            _tmpOption = _cursor.getString(_cursorIndexOfOption)
            val _tmpVotes: Int
            _tmpVotes = _cursor.getInt(_cursorIndexOfVotes)
            _item = VoteOptionEntity(_tmpId,_tmpPollId,_tmpFirebaseId,_tmpOption,_tmpVotes)
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

  public override suspend fun getOptionsList(pollId: Long): List<VoteOptionEntity> {
    val _sql: String = "SELECT * FROM vote_options WHERE pollId = ? ORDER BY id ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, pollId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<VoteOptionEntity>> {
      public override fun call(): List<VoteOptionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfPollId: Int = getColumnIndexOrThrow(_cursor, "pollId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOption: Int = getColumnIndexOrThrow(_cursor, "option")
          val _cursorIndexOfVotes: Int = getColumnIndexOrThrow(_cursor, "votes")
          val _result: MutableList<VoteOptionEntity> =
              ArrayList<VoteOptionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: VoteOptionEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpPollId: Long
            _tmpPollId = _cursor.getLong(_cursorIndexOfPollId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOption: String
            _tmpOption = _cursor.getString(_cursorIndexOfOption)
            val _tmpVotes: Int
            _tmpVotes = _cursor.getInt(_cursorIndexOfVotes)
            _item = VoteOptionEntity(_tmpId,_tmpPollId,_tmpFirebaseId,_tmpOption,_tmpVotes)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getUserVotes(pollId: Long, userId: Long): List<PollVoteEntity> {
    val _sql: String = "SELECT * FROM poll_votes WHERE pollId = ? AND userId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, pollId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<PollVoteEntity>> {
      public override fun call(): List<PollVoteEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfPollId: Int = getColumnIndexOrThrow(_cursor, "pollId")
          val _cursorIndexOfOptionId: Int = getColumnIndexOrThrow(_cursor, "optionId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _result: MutableList<PollVoteEntity> = ArrayList<PollVoteEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: PollVoteEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpPollId: Long
            _tmpPollId = _cursor.getLong(_cursorIndexOfPollId)
            val _tmpOptionId: Long
            _tmpOptionId = _cursor.getLong(_cursorIndexOfOptionId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            _item = PollVoteEntity(_tmpId,_tmpPollId,_tmpOptionId,_tmpUserId,_tmpCreatedAt)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getPollByFirebaseId(fid: String): PollEntity? {
    val _sql: String = "SELECT * FROM polls WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<PollEntity?> {
      public override fun call(): PollEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfQuestion: Int = getColumnIndexOrThrow(_cursor, "question")
          val _cursorIndexOfAllowMultiple: Int = getColumnIndexOrThrow(_cursor, "allowMultiple")
          val _result: PollEntity?
          if (_cursor.moveToFirst()) {
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
            val _tmpQuestion: String
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion)
            val _tmpAllowMultiple: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAllowMultiple)
            _tmpAllowMultiple = _tmp != 0
            _result = PollEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpQuestion,_tmpAllowMultiple)
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

  public override suspend fun getPollById(pollId: Long): PollEntity? {
    val _sql: String = "SELECT * FROM polls WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, pollId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<PollEntity?> {
      public override fun call(): PollEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfQuestion: Int = getColumnIndexOrThrow(_cursor, "question")
          val _cursorIndexOfAllowMultiple: Int = getColumnIndexOrThrow(_cursor, "allowMultiple")
          val _result: PollEntity?
          if (_cursor.moveToFirst()) {
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
            val _tmpQuestion: String
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion)
            val _tmpAllowMultiple: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAllowMultiple)
            _tmpAllowMultiple = _tmp != 0
            _result = PollEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpQuestion,_tmpAllowMultiple)
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

  public override suspend fun getOptionByFirebaseId(fid: String): VoteOptionEntity? {
    val _sql: String = "SELECT * FROM vote_options WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<VoteOptionEntity?> {
      public override fun call(): VoteOptionEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfPollId: Int = getColumnIndexOrThrow(_cursor, "pollId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOption: Int = getColumnIndexOrThrow(_cursor, "option")
          val _cursorIndexOfVotes: Int = getColumnIndexOrThrow(_cursor, "votes")
          val _result: VoteOptionEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpPollId: Long
            _tmpPollId = _cursor.getLong(_cursorIndexOfPollId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOption: String
            _tmpOption = _cursor.getString(_cursorIndexOfOption)
            val _tmpVotes: Int
            _tmpVotes = _cursor.getInt(_cursorIndexOfVotes)
            _result = VoteOptionEntity(_tmpId,_tmpPollId,_tmpFirebaseId,_tmpOption,_tmpVotes)
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

  public override fun observePollVoters(pollId: Long): Flow<List<PollVoterDetail>> {
    val _sql: String = """
        |
        |    SELECT 
        |        pv.optionId, 
        |        pv.userId, 
        |        u.name as displayName,  
        |        u.profilePhoto            
        |    FROM poll_votes pv
        |    INNER JOIN users u ON pv.userId = u.id
        |    WHERE pv.pollId = ?
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, pollId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("poll_votes", "users"), object :
        Callable<List<PollVoterDetail>> {
      public override fun call(): List<PollVoterDetail> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfOptionId: Int = 0
          val _cursorIndexOfUserId: Int = 1
          val _cursorIndexOfDisplayName: Int = 2
          val _result: MutableList<PollVoterDetail> = ArrayList<PollVoterDetail>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: PollVoterDetail
            val _tmpOptionId: Long
            _tmpOptionId = _cursor.getLong(_cursorIndexOfOptionId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpDisplayName: String
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
            _item = PollVoterDetail(_tmpOptionId,_tmpUserId,_tmpDisplayName,null)
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
