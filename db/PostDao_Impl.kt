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
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.PostEntity
import com.example.tripshare.`data`.model.PostLikeEntity
import com.example.tripshare.`data`.model.UserEntity
import com.example.tripshare.`data`.model.VerificationMethod
import java.lang.Class
import java.lang.StringBuilder
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
public class PostDao_Impl(
  __db: RoomDatabase,
) : PostDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfPostEntity: EntityInsertionAdapter<PostEntity>

  private val __insertionAdapterOfPostLikeEntity: EntityInsertionAdapter<PostLikeEntity>

  private val __deletionAdapterOfPostEntity: EntityDeletionOrUpdateAdapter<PostEntity>

  private val __updateAdapterOfPostEntity: EntityDeletionOrUpdateAdapter<PostEntity>

  private val __preparedStmtOfLikePost: SharedSQLiteStatement

  private val __preparedStmtOfSavePost: SharedSQLiteStatement

  private val __preparedStmtOfAddCommentCount: SharedSQLiteStatement

  private val __preparedStmtOfDeleteLike: SharedSQLiteStatement

  private val __preparedStmtOfDecrementLikes: SharedSQLiteStatement

  private val __preparedStmtOfDeleteById: SharedSQLiteStatement

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertionAdapterOfPostEntity = object : EntityInsertionAdapter<PostEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `posts` (`id`,`firebaseId`,`userId`,`userName`,`title`,`content`,`text`,`userAvatar`,`location`,`timeAgo`,`imageUrl`,`likes`,`comments`,`shares`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: PostEntity) {
        statement.bindLong(1, entity.id)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpFirebaseId)
        }
        statement.bindLong(3, entity.userId)
        statement.bindString(4, entity.userName)
        statement.bindString(5, entity.title)
        statement.bindString(6, entity.content)
        val _tmpText: String? = entity.text
        if (_tmpText == null) {
          statement.bindNull(7)
        } else {
          statement.bindString(7, _tmpText)
        }
        val _tmpUserAvatar: String? = entity.userAvatar
        if (_tmpUserAvatar == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpUserAvatar)
        }
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpLocation)
        }
        statement.bindString(10, entity.timeAgo)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(11)
        } else {
          statement.bindString(11, _tmpImageUrl)
        }
        statement.bindLong(12, entity.likes.toLong())
        statement.bindLong(13, entity.comments.toLong())
        statement.bindLong(14, entity.shares.toLong())
      }
    }
    this.__insertionAdapterOfPostLikeEntity = object : EntityInsertionAdapter<PostLikeEntity>(__db)
        {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `post_likes` (`postId`,`userId`) VALUES (?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: PostLikeEntity) {
        statement.bindLong(1, entity.postId)
        statement.bindLong(2, entity.userId)
      }
    }
    this.__deletionAdapterOfPostEntity = object : EntityDeletionOrUpdateAdapter<PostEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `posts` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: PostEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfPostEntity = object : EntityDeletionOrUpdateAdapter<PostEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `posts` SET `id` = ?,`firebaseId` = ?,`userId` = ?,`userName` = ?,`title` = ?,`content` = ?,`text` = ?,`userAvatar` = ?,`location` = ?,`timeAgo` = ?,`imageUrl` = ?,`likes` = ?,`comments` = ?,`shares` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: PostEntity) {
        statement.bindLong(1, entity.id)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpFirebaseId)
        }
        statement.bindLong(3, entity.userId)
        statement.bindString(4, entity.userName)
        statement.bindString(5, entity.title)
        statement.bindString(6, entity.content)
        val _tmpText: String? = entity.text
        if (_tmpText == null) {
          statement.bindNull(7)
        } else {
          statement.bindString(7, _tmpText)
        }
        val _tmpUserAvatar: String? = entity.userAvatar
        if (_tmpUserAvatar == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpUserAvatar)
        }
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpLocation)
        }
        statement.bindString(10, entity.timeAgo)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(11)
        } else {
          statement.bindString(11, _tmpImageUrl)
        }
        statement.bindLong(12, entity.likes.toLong())
        statement.bindLong(13, entity.comments.toLong())
        statement.bindLong(14, entity.shares.toLong())
        statement.bindLong(15, entity.id)
      }
    }
    this.__preparedStmtOfLikePost = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE posts SET likes = likes + 1 WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfSavePost = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE posts SET shares = shares + 1 WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfAddCommentCount = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE posts SET comments = comments + 1 WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteLike = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM post_likes WHERE postId = ? AND userId = ?"
        return _query
      }
    }
    this.__preparedStmtOfDecrementLikes = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE posts SET likes = likes - 1 WHERE id = ? AND likes > 0"
        return _query
      }
    }
    this.__preparedStmtOfDeleteById = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM posts WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insert(post: PostEntity): Long = CoroutinesRoom.execute(__db, true,
      object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfPostEntity.insertAndReturnId(post)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertLike(like: PostLikeEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfPostLikeEntity.insertAndReturnId(like)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(post: PostEntity): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfPostEntity.handle(post)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun update(post: PostEntity): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfPostEntity.handle(post)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun likePost(postId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfLikePost.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, postId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfLikePost.release(_stmt)
      }
    }
  })

  public override suspend fun savePost(postId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSavePost.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, postId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSavePost.release(_stmt)
      }
    }
  })

  public override suspend fun addCommentCount(postId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfAddCommentCount.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, postId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfAddCommentCount.release(_stmt)
      }
    }
  })

  public override suspend fun deleteLike(postId: Long, userId: Long): Int =
      CoroutinesRoom.execute(__db, true, object : Callable<Int> {
    public override fun call(): Int {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteLike.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, postId)
      _argIndex = 2
      _stmt.bindLong(_argIndex, userId)
      try {
        __db.beginTransaction()
        try {
          val _result: Int = _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
          return _result
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteLike.release(_stmt)
      }
    }
  })

  public override suspend fun incrementLikes(postId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfLikePost.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, postId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfLikePost.release(_stmt)
      }
    }
  })

  public override suspend fun decrementLikes(postId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDecrementLikes.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, postId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDecrementLikes.release(_stmt)
      }
    }
  })

  public override suspend fun deleteById(postId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteById.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, postId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteById.release(_stmt)
      }
    }
  })

  public override fun observePosts(): Flow<List<PostWithUser>> {
    val _sql: String = "SELECT * FROM posts ORDER BY id DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, true, arrayOf("users", "posts"), object :
        Callable<List<PostWithUser>> {
      public override fun call(): List<PostWithUser> {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
            val _cursorIndexOfUserName: Int = getColumnIndexOrThrow(_cursor, "userName")
            val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
            val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
            val _cursorIndexOfText: Int = getColumnIndexOrThrow(_cursor, "text")
            val _cursorIndexOfUserAvatar: Int = getColumnIndexOrThrow(_cursor, "userAvatar")
            val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
            val _cursorIndexOfTimeAgo: Int = getColumnIndexOrThrow(_cursor, "timeAgo")
            val _cursorIndexOfImageUrl: Int = getColumnIndexOrThrow(_cursor, "imageUrl")
            val _cursorIndexOfLikes: Int = getColumnIndexOrThrow(_cursor, "likes")
            val _cursorIndexOfComments: Int = getColumnIndexOrThrow(_cursor, "comments")
            val _cursorIndexOfShares: Int = getColumnIndexOrThrow(_cursor, "shares")
            val _collectionUser: LongSparseArray<UserEntity?> = LongSparseArray<UserEntity?>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfUserId)
              _collectionUser.put(_tmpKey, null)
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(_collectionUser)
            val _result: MutableList<PostWithUser> = ArrayList<PostWithUser>(_cursor.getCount())
            while (_cursor.moveToNext()) {
              val _item: PostWithUser
              val _tmpPost: PostEntity
              val _tmpId: Long
              _tmpId = _cursor.getLong(_cursorIndexOfId)
              val _tmpFirebaseId: String?
              if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
                _tmpFirebaseId = null
              } else {
                _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
              }
              val _tmpUserId: Long
              _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
              val _tmpUserName: String
              _tmpUserName = _cursor.getString(_cursorIndexOfUserName)
              val _tmpTitle: String
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
              val _tmpContent: String
              _tmpContent = _cursor.getString(_cursorIndexOfContent)
              val _tmpText: String?
              if (_cursor.isNull(_cursorIndexOfText)) {
                _tmpText = null
              } else {
                _tmpText = _cursor.getString(_cursorIndexOfText)
              }
              val _tmpUserAvatar: String?
              if (_cursor.isNull(_cursorIndexOfUserAvatar)) {
                _tmpUserAvatar = null
              } else {
                _tmpUserAvatar = _cursor.getString(_cursorIndexOfUserAvatar)
              }
              val _tmpLocation: String?
              if (_cursor.isNull(_cursorIndexOfLocation)) {
                _tmpLocation = null
              } else {
                _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
              }
              val _tmpTimeAgo: String
              _tmpTimeAgo = _cursor.getString(_cursorIndexOfTimeAgo)
              val _tmpImageUrl: String?
              if (_cursor.isNull(_cursorIndexOfImageUrl)) {
                _tmpImageUrl = null
              } else {
                _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl)
              }
              val _tmpLikes: Int
              _tmpLikes = _cursor.getInt(_cursorIndexOfLikes)
              val _tmpComments: Int
              _tmpComments = _cursor.getInt(_cursorIndexOfComments)
              val _tmpShares: Int
              _tmpShares = _cursor.getInt(_cursorIndexOfShares)
              _tmpPost =
                  PostEntity(_tmpId,_tmpFirebaseId,_tmpUserId,_tmpUserName,_tmpTitle,_tmpContent,_tmpText,_tmpUserAvatar,_tmpLocation,_tmpTimeAgo,_tmpImageUrl,_tmpLikes,_tmpComments,_tmpShares)
              val _tmpUser: UserEntity?
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfUserId)
              _tmpUser = _collectionUser.get(_tmpKey_1)
              if (_tmpUser == null) {
                error("Relationship item 'user' was expected to be NON-NULL but is NULL in @Relation involving a parent column named 'userId' and entityColumn named 'id'.")
              }
              _item = PostWithUser(_tmpPost,_tmpUser)
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

  public override suspend fun getPostByFirebaseId(fid: String): PostEntity? {
    val _sql: String = "SELECT * FROM posts WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<PostEntity?> {
      public override fun call(): PostEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfUserName: Int = getColumnIndexOrThrow(_cursor, "userName")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfText: Int = getColumnIndexOrThrow(_cursor, "text")
          val _cursorIndexOfUserAvatar: Int = getColumnIndexOrThrow(_cursor, "userAvatar")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfTimeAgo: Int = getColumnIndexOrThrow(_cursor, "timeAgo")
          val _cursorIndexOfImageUrl: Int = getColumnIndexOrThrow(_cursor, "imageUrl")
          val _cursorIndexOfLikes: Int = getColumnIndexOrThrow(_cursor, "likes")
          val _cursorIndexOfComments: Int = getColumnIndexOrThrow(_cursor, "comments")
          val _cursorIndexOfShares: Int = getColumnIndexOrThrow(_cursor, "shares")
          val _result: PostEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpUserName: String
            _tmpUserName = _cursor.getString(_cursorIndexOfUserName)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpText: String?
            if (_cursor.isNull(_cursorIndexOfText)) {
              _tmpText = null
            } else {
              _tmpText = _cursor.getString(_cursorIndexOfText)
            }
            val _tmpUserAvatar: String?
            if (_cursor.isNull(_cursorIndexOfUserAvatar)) {
              _tmpUserAvatar = null
            } else {
              _tmpUserAvatar = _cursor.getString(_cursorIndexOfUserAvatar)
            }
            val _tmpLocation: String?
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            }
            val _tmpTimeAgo: String
            _tmpTimeAgo = _cursor.getString(_cursorIndexOfTimeAgo)
            val _tmpImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl)
            }
            val _tmpLikes: Int
            _tmpLikes = _cursor.getInt(_cursorIndexOfLikes)
            val _tmpComments: Int
            _tmpComments = _cursor.getInt(_cursorIndexOfComments)
            val _tmpShares: Int
            _tmpShares = _cursor.getInt(_cursorIndexOfShares)
            _result =
                PostEntity(_tmpId,_tmpFirebaseId,_tmpUserId,_tmpUserName,_tmpTitle,_tmpContent,_tmpText,_tmpUserAvatar,_tmpLocation,_tmpTimeAgo,_tmpImageUrl,_tmpLikes,_tmpComments,_tmpShares)
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

  public override fun observePostsWithUsers(): Flow<List<PostWithUser>> {
    val _sql: String = "SELECT * FROM posts ORDER BY id DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, true, arrayOf("users", "posts"), object :
        Callable<List<PostWithUser>> {
      public override fun call(): List<PostWithUser> {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
            val _cursorIndexOfUserName: Int = getColumnIndexOrThrow(_cursor, "userName")
            val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
            val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
            val _cursorIndexOfText: Int = getColumnIndexOrThrow(_cursor, "text")
            val _cursorIndexOfUserAvatar: Int = getColumnIndexOrThrow(_cursor, "userAvatar")
            val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
            val _cursorIndexOfTimeAgo: Int = getColumnIndexOrThrow(_cursor, "timeAgo")
            val _cursorIndexOfImageUrl: Int = getColumnIndexOrThrow(_cursor, "imageUrl")
            val _cursorIndexOfLikes: Int = getColumnIndexOrThrow(_cursor, "likes")
            val _cursorIndexOfComments: Int = getColumnIndexOrThrow(_cursor, "comments")
            val _cursorIndexOfShares: Int = getColumnIndexOrThrow(_cursor, "shares")
            val _collectionUser: LongSparseArray<UserEntity?> = LongSparseArray<UserEntity?>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfUserId)
              _collectionUser.put(_tmpKey, null)
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(_collectionUser)
            val _result: MutableList<PostWithUser> = ArrayList<PostWithUser>(_cursor.getCount())
            while (_cursor.moveToNext()) {
              val _item: PostWithUser
              val _tmpPost: PostEntity
              val _tmpId: Long
              _tmpId = _cursor.getLong(_cursorIndexOfId)
              val _tmpFirebaseId: String?
              if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
                _tmpFirebaseId = null
              } else {
                _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
              }
              val _tmpUserId: Long
              _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
              val _tmpUserName: String
              _tmpUserName = _cursor.getString(_cursorIndexOfUserName)
              val _tmpTitle: String
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
              val _tmpContent: String
              _tmpContent = _cursor.getString(_cursorIndexOfContent)
              val _tmpText: String?
              if (_cursor.isNull(_cursorIndexOfText)) {
                _tmpText = null
              } else {
                _tmpText = _cursor.getString(_cursorIndexOfText)
              }
              val _tmpUserAvatar: String?
              if (_cursor.isNull(_cursorIndexOfUserAvatar)) {
                _tmpUserAvatar = null
              } else {
                _tmpUserAvatar = _cursor.getString(_cursorIndexOfUserAvatar)
              }
              val _tmpLocation: String?
              if (_cursor.isNull(_cursorIndexOfLocation)) {
                _tmpLocation = null
              } else {
                _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
              }
              val _tmpTimeAgo: String
              _tmpTimeAgo = _cursor.getString(_cursorIndexOfTimeAgo)
              val _tmpImageUrl: String?
              if (_cursor.isNull(_cursorIndexOfImageUrl)) {
                _tmpImageUrl = null
              } else {
                _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl)
              }
              val _tmpLikes: Int
              _tmpLikes = _cursor.getInt(_cursorIndexOfLikes)
              val _tmpComments: Int
              _tmpComments = _cursor.getInt(_cursorIndexOfComments)
              val _tmpShares: Int
              _tmpShares = _cursor.getInt(_cursorIndexOfShares)
              _tmpPost =
                  PostEntity(_tmpId,_tmpFirebaseId,_tmpUserId,_tmpUserName,_tmpTitle,_tmpContent,_tmpText,_tmpUserAvatar,_tmpLocation,_tmpTimeAgo,_tmpImageUrl,_tmpLikes,_tmpComments,_tmpShares)
              val _tmpUser: UserEntity?
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfUserId)
              _tmpUser = _collectionUser.get(_tmpKey_1)
              if (_tmpUser == null) {
                error("Relationship item 'user' was expected to be NON-NULL but is NULL in @Relation involving a parent column named 'userId' and entityColumn named 'id'.")
              }
              _item = PostWithUser(_tmpPost,_tmpUser)
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

  public override fun observePostsByUser(userId: Long): Flow<List<PostWithUser>> {
    val _sql: String = "SELECT * FROM posts WHERE userId = ? ORDER BY id DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, true, arrayOf("users", "posts"), object :
        Callable<List<PostWithUser>> {
      public override fun call(): List<PostWithUser> {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
            val _cursorIndexOfUserName: Int = getColumnIndexOrThrow(_cursor, "userName")
            val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
            val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
            val _cursorIndexOfText: Int = getColumnIndexOrThrow(_cursor, "text")
            val _cursorIndexOfUserAvatar: Int = getColumnIndexOrThrow(_cursor, "userAvatar")
            val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
            val _cursorIndexOfTimeAgo: Int = getColumnIndexOrThrow(_cursor, "timeAgo")
            val _cursorIndexOfImageUrl: Int = getColumnIndexOrThrow(_cursor, "imageUrl")
            val _cursorIndexOfLikes: Int = getColumnIndexOrThrow(_cursor, "likes")
            val _cursorIndexOfComments: Int = getColumnIndexOrThrow(_cursor, "comments")
            val _cursorIndexOfShares: Int = getColumnIndexOrThrow(_cursor, "shares")
            val _collectionUser: LongSparseArray<UserEntity?> = LongSparseArray<UserEntity?>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfUserId)
              _collectionUser.put(_tmpKey, null)
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(_collectionUser)
            val _result: MutableList<PostWithUser> = ArrayList<PostWithUser>(_cursor.getCount())
            while (_cursor.moveToNext()) {
              val _item: PostWithUser
              val _tmpPost: PostEntity
              val _tmpId: Long
              _tmpId = _cursor.getLong(_cursorIndexOfId)
              val _tmpFirebaseId: String?
              if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
                _tmpFirebaseId = null
              } else {
                _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
              }
              val _tmpUserId: Long
              _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
              val _tmpUserName: String
              _tmpUserName = _cursor.getString(_cursorIndexOfUserName)
              val _tmpTitle: String
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
              val _tmpContent: String
              _tmpContent = _cursor.getString(_cursorIndexOfContent)
              val _tmpText: String?
              if (_cursor.isNull(_cursorIndexOfText)) {
                _tmpText = null
              } else {
                _tmpText = _cursor.getString(_cursorIndexOfText)
              }
              val _tmpUserAvatar: String?
              if (_cursor.isNull(_cursorIndexOfUserAvatar)) {
                _tmpUserAvatar = null
              } else {
                _tmpUserAvatar = _cursor.getString(_cursorIndexOfUserAvatar)
              }
              val _tmpLocation: String?
              if (_cursor.isNull(_cursorIndexOfLocation)) {
                _tmpLocation = null
              } else {
                _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
              }
              val _tmpTimeAgo: String
              _tmpTimeAgo = _cursor.getString(_cursorIndexOfTimeAgo)
              val _tmpImageUrl: String?
              if (_cursor.isNull(_cursorIndexOfImageUrl)) {
                _tmpImageUrl = null
              } else {
                _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl)
              }
              val _tmpLikes: Int
              _tmpLikes = _cursor.getInt(_cursorIndexOfLikes)
              val _tmpComments: Int
              _tmpComments = _cursor.getInt(_cursorIndexOfComments)
              val _tmpShares: Int
              _tmpShares = _cursor.getInt(_cursorIndexOfShares)
              _tmpPost =
                  PostEntity(_tmpId,_tmpFirebaseId,_tmpUserId,_tmpUserName,_tmpTitle,_tmpContent,_tmpText,_tmpUserAvatar,_tmpLocation,_tmpTimeAgo,_tmpImageUrl,_tmpLikes,_tmpComments,_tmpShares)
              val _tmpUser: UserEntity?
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfUserId)
              _tmpUser = _collectionUser.get(_tmpKey_1)
              if (_tmpUser == null) {
                error("Relationship item 'user' was expected to be NON-NULL but is NULL in @Relation involving a parent column named 'userId' and entityColumn named 'id'.")
              }
              _item = PostWithUser(_tmpPost,_tmpUser)
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

  public override suspend fun getPostById(postId: Long): PostEntity? {
    val _sql: String = "SELECT * FROM posts WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, postId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<PostEntity?> {
      public override fun call(): PostEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfUserName: Int = getColumnIndexOrThrow(_cursor, "userName")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfText: Int = getColumnIndexOrThrow(_cursor, "text")
          val _cursorIndexOfUserAvatar: Int = getColumnIndexOrThrow(_cursor, "userAvatar")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfTimeAgo: Int = getColumnIndexOrThrow(_cursor, "timeAgo")
          val _cursorIndexOfImageUrl: Int = getColumnIndexOrThrow(_cursor, "imageUrl")
          val _cursorIndexOfLikes: Int = getColumnIndexOrThrow(_cursor, "likes")
          val _cursorIndexOfComments: Int = getColumnIndexOrThrow(_cursor, "comments")
          val _cursorIndexOfShares: Int = getColumnIndexOrThrow(_cursor, "shares")
          val _result: PostEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpUserName: String
            _tmpUserName = _cursor.getString(_cursorIndexOfUserName)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpText: String?
            if (_cursor.isNull(_cursorIndexOfText)) {
              _tmpText = null
            } else {
              _tmpText = _cursor.getString(_cursorIndexOfText)
            }
            val _tmpUserAvatar: String?
            if (_cursor.isNull(_cursorIndexOfUserAvatar)) {
              _tmpUserAvatar = null
            } else {
              _tmpUserAvatar = _cursor.getString(_cursorIndexOfUserAvatar)
            }
            val _tmpLocation: String?
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            }
            val _tmpTimeAgo: String
            _tmpTimeAgo = _cursor.getString(_cursorIndexOfTimeAgo)
            val _tmpImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl)
            }
            val _tmpLikes: Int
            _tmpLikes = _cursor.getInt(_cursorIndexOfLikes)
            val _tmpComments: Int
            _tmpComments = _cursor.getInt(_cursorIndexOfComments)
            val _tmpShares: Int
            _tmpShares = _cursor.getInt(_cursorIndexOfShares)
            _result =
                PostEntity(_tmpId,_tmpFirebaseId,_tmpUserId,_tmpUserName,_tmpTitle,_tmpContent,_tmpText,_tmpUserAvatar,_tmpLocation,_tmpTimeAgo,_tmpImageUrl,_tmpLikes,_tmpComments,_tmpShares)
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

  public override suspend fun getPostWithUser(id: Long): PostWithUser? {
    val _sql: String = "SELECT * FROM posts WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, true, _cancellationSignal, object : Callable<PostWithUser?> {
      public override fun call(): PostWithUser? {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
            val _cursorIndexOfUserName: Int = getColumnIndexOrThrow(_cursor, "userName")
            val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
            val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
            val _cursorIndexOfText: Int = getColumnIndexOrThrow(_cursor, "text")
            val _cursorIndexOfUserAvatar: Int = getColumnIndexOrThrow(_cursor, "userAvatar")
            val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
            val _cursorIndexOfTimeAgo: Int = getColumnIndexOrThrow(_cursor, "timeAgo")
            val _cursorIndexOfImageUrl: Int = getColumnIndexOrThrow(_cursor, "imageUrl")
            val _cursorIndexOfLikes: Int = getColumnIndexOrThrow(_cursor, "likes")
            val _cursorIndexOfComments: Int = getColumnIndexOrThrow(_cursor, "comments")
            val _cursorIndexOfShares: Int = getColumnIndexOrThrow(_cursor, "shares")
            val _collectionUser: LongSparseArray<UserEntity?> = LongSparseArray<UserEntity?>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfUserId)
              _collectionUser.put(_tmpKey, null)
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(_collectionUser)
            val _result: PostWithUser?
            if (_cursor.moveToFirst()) {
              val _tmpPost: PostEntity
              val _tmpId: Long
              _tmpId = _cursor.getLong(_cursorIndexOfId)
              val _tmpFirebaseId: String?
              if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
                _tmpFirebaseId = null
              } else {
                _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
              }
              val _tmpUserId: Long
              _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
              val _tmpUserName: String
              _tmpUserName = _cursor.getString(_cursorIndexOfUserName)
              val _tmpTitle: String
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
              val _tmpContent: String
              _tmpContent = _cursor.getString(_cursorIndexOfContent)
              val _tmpText: String?
              if (_cursor.isNull(_cursorIndexOfText)) {
                _tmpText = null
              } else {
                _tmpText = _cursor.getString(_cursorIndexOfText)
              }
              val _tmpUserAvatar: String?
              if (_cursor.isNull(_cursorIndexOfUserAvatar)) {
                _tmpUserAvatar = null
              } else {
                _tmpUserAvatar = _cursor.getString(_cursorIndexOfUserAvatar)
              }
              val _tmpLocation: String?
              if (_cursor.isNull(_cursorIndexOfLocation)) {
                _tmpLocation = null
              } else {
                _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
              }
              val _tmpTimeAgo: String
              _tmpTimeAgo = _cursor.getString(_cursorIndexOfTimeAgo)
              val _tmpImageUrl: String?
              if (_cursor.isNull(_cursorIndexOfImageUrl)) {
                _tmpImageUrl = null
              } else {
                _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl)
              }
              val _tmpLikes: Int
              _tmpLikes = _cursor.getInt(_cursorIndexOfLikes)
              val _tmpComments: Int
              _tmpComments = _cursor.getInt(_cursorIndexOfComments)
              val _tmpShares: Int
              _tmpShares = _cursor.getInt(_cursorIndexOfShares)
              _tmpPost =
                  PostEntity(_tmpId,_tmpFirebaseId,_tmpUserId,_tmpUserName,_tmpTitle,_tmpContent,_tmpText,_tmpUserAvatar,_tmpLocation,_tmpTimeAgo,_tmpImageUrl,_tmpLikes,_tmpComments,_tmpShares)
              val _tmpUser: UserEntity?
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfUserId)
              _tmpUser = _collectionUser.get(_tmpKey_1)
              if (_tmpUser == null) {
                error("Relationship item 'user' was expected to be NON-NULL but is NULL in @Relation involving a parent column named 'userId' and entityColumn named 'id'.")
              }
              _result = PostWithUser(_tmpPost,_tmpUser)
            } else {
              _result = null
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

  public override suspend fun hasUserLiked(postId: Long, userId: Long): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM post_likes WHERE postId = ? AND userId = ?)"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, postId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Boolean> {
      public override fun call(): Boolean {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Boolean
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp != 0
          } else {
            _result = false
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
      fun __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(_map: LongSparseArray<UserEntity?>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, false) {
        __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`firebaseId`,`name`,`email`,`passwordHash`,`icNumber`,`verificationMethod`,`phoneNumber`,`verifiedEmail`,`location`,`bio`,`profilePhoto`,`verified`,`tripsCompleted`,`createdAt` FROM `users` WHERE `id` IN (")
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
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfFirebaseId: Int = 1
      val _cursorIndexOfName: Int = 2
      val _cursorIndexOfEmail: Int = 3
      val _cursorIndexOfPasswordHash: Int = 4
      val _cursorIndexOfIcNumber: Int = 5
      val _cursorIndexOfVerificationMethod: Int = 6
      val _cursorIndexOfPhoneNumber: Int = 7
      val _cursorIndexOfVerifiedEmail: Int = 8
      val _cursorIndexOfLocation: Int = 9
      val _cursorIndexOfBio: Int = 10
      val _cursorIndexOfProfilePhoto: Int = 11
      val _cursorIndexOfVerified: Int = 12
      val _cursorIndexOfTripsCompleted: Int = 13
      val _cursorIndexOfCreatedAt: Int = 14
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        if (_map.containsKey(_tmpKey)) {
          val _item_1: UserEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpFirebaseId: String?
          if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
            _tmpFirebaseId = null
          } else {
            _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
          }
          val _tmpName: String
          _tmpName = _cursor.getString(_cursorIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash)
          val _tmpIcNumber: String?
          if (_cursor.isNull(_cursorIndexOfIcNumber)) {
            _tmpIcNumber = null
          } else {
            _tmpIcNumber = _cursor.getString(_cursorIndexOfIcNumber)
          }
          val _tmpVerificationMethod: VerificationMethod
          val _tmp: String
          _tmp = _cursor.getString(_cursorIndexOfVerificationMethod)
          _tmpVerificationMethod = __converters.toVerification(_tmp)
          val _tmpPhoneNumber: String?
          if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
          }
          val _tmpVerifiedEmail: String?
          if (_cursor.isNull(_cursorIndexOfVerifiedEmail)) {
            _tmpVerifiedEmail = null
          } else {
            _tmpVerifiedEmail = _cursor.getString(_cursorIndexOfVerifiedEmail)
          }
          val _tmpLocation: String
          _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
          val _tmpBio: String
          _tmpBio = _cursor.getString(_cursorIndexOfBio)
          val _tmpProfilePhoto: String?
          if (_cursor.isNull(_cursorIndexOfProfilePhoto)) {
            _tmpProfilePhoto = null
          } else {
            _tmpProfilePhoto = _cursor.getString(_cursorIndexOfProfilePhoto)
          }
          val _tmpVerified: Boolean
          val _tmp_1: Int
          _tmp_1 = _cursor.getInt(_cursorIndexOfVerified)
          _tmpVerified = _tmp_1 != 0
          val _tmpTripsCompleted: Int
          _tmpTripsCompleted = _cursor.getInt(_cursorIndexOfTripsCompleted)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
          _item_1 =
              UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
          _map.put(_tmpKey, _item_1)
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
