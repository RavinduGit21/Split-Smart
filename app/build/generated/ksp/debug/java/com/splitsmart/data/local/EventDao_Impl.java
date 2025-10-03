package com.splitsmart.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.splitsmart.data.model.EventEntity;
import com.splitsmart.data.model.EventSummary;
import com.splitsmart.data.model.ExpenseItemEntity;
import com.splitsmart.data.model.ParticipantEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EventDao_Impl implements EventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EventEntity> __insertionAdapterOfEventEntity;

  private final EntityInsertionAdapter<ParticipantEntity> __insertionAdapterOfParticipantEntity;

  private final EntityInsertionAdapter<ExpenseItemEntity> __insertionAdapterOfExpenseItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteParticipantsForEvent;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItemsForEvent;

  public EventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventEntity = new EntityInsertionAdapter<EventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `events` (`id`,`name`,`dateMillis`,`location`,`description`,`totalAmountCents`,`emoji`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getDateMillis());
        statement.bindString(4, entity.getLocation());
        statement.bindString(5, entity.getDescription());
        statement.bindLong(6, entity.getTotalAmountCents());
        statement.bindString(7, entity.getEmoji());
      }
    };
    this.__insertionAdapterOfParticipantEntity = new EntityInsertionAdapter<ParticipantEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `participants` (`id`,`eventId`,`name`,`shareAmountCents`,`paidAmountCents`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ParticipantEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEventId());
        statement.bindString(3, entity.getName());
        statement.bindLong(4, entity.getShareAmountCents());
        statement.bindLong(5, entity.getPaidAmountCents());
      }
    };
    this.__insertionAdapterOfExpenseItemEntity = new EntityInsertionAdapter<ExpenseItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `expense_items` (`id`,`eventId`,`name`,`amountCents`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEventId());
        statement.bindString(3, entity.getName());
        statement.bindLong(4, entity.getAmountCents());
      }
    };
    this.__preparedStmtOfDeleteParticipantsForEvent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM participants WHERE eventId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteItemsForEvent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expense_items WHERE eventId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertEvent(final EventEntity event, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEventEntity.insertAndReturnId(event);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertParticipants(final List<ParticipantEntity> participants,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfParticipantEntity.insert(participants);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<ExpenseItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExpenseItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEventWithParticipants(final EventEntity event,
      final List<ParticipantEntity> participants, final List<ExpenseItemEntity> items,
      final Continuation<? super Long> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> EventDao.DefaultImpls.insertEventWithParticipants(EventDao_Impl.this, event, participants, items, __cont), $completion);
  }

  @Override
  public Object updateEventWithParticipants(final EventEntity event,
      final List<ParticipantEntity> participants, final List<ExpenseItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> EventDao.DefaultImpls.updateEventWithParticipants(EventDao_Impl.this, event, participants, items, __cont), $completion);
  }

  @Override
  public Object deleteParticipantsForEvent(final long eventId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteParticipantsForEvent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, eventId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteParticipantsForEvent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItemsForEvent(final long eventId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItemsForEvent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, eventId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteItemsForEvent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getEvent(final long eventId, final Continuation<? super EventEntity> $completion) {
    final String _sql = "SELECT * FROM events WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, eventId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EventEntity>() {
      @Override
      @Nullable
      public EventEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTotalAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmountCents");
          final int _cursorIndexOfEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "emoji");
          final EventEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final long _tmpTotalAmountCents;
            _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            final String _tmpEmoji;
            _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
            _result = new EventEntity(_tmpId,_tmpName,_tmpDateMillis,_tmpLocation,_tmpDescription,_tmpTotalAmountCents,_tmpEmoji);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getParticipants(final long eventId,
      final Continuation<? super List<ParticipantEntity>> $completion) {
    final String _sql = "SELECT * FROM participants WHERE eventId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, eventId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ParticipantEntity>>() {
      @Override
      @NonNull
      public List<ParticipantEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "eventId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfShareAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "shareAmountCents");
          final int _cursorIndexOfPaidAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAmountCents");
          final List<ParticipantEntity> _result = new ArrayList<ParticipantEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ParticipantEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEventId;
            _tmpEventId = _cursor.getLong(_cursorIndexOfEventId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpShareAmountCents;
            _tmpShareAmountCents = _cursor.getLong(_cursorIndexOfShareAmountCents);
            final long _tmpPaidAmountCents;
            _tmpPaidAmountCents = _cursor.getLong(_cursorIndexOfPaidAmountCents);
            _item = new ParticipantEntity(_tmpId,_tmpEventId,_tmpName,_tmpShareAmountCents,_tmpPaidAmountCents);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getItems(final long eventId,
      final Continuation<? super List<ExpenseItemEntity>> $completion) {
    final String _sql = "SELECT * FROM expense_items WHERE eventId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, eventId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExpenseItemEntity>>() {
      @Override
      @NonNull
      public List<ExpenseItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "eventId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amountCents");
          final List<ExpenseItemEntity> _result = new ArrayList<ExpenseItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEventId;
            _tmpEventId = _cursor.getLong(_cursorIndexOfEventId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpAmountCents;
            _tmpAmountCents = _cursor.getLong(_cursorIndexOfAmountCents);
            _item = new ExpenseItemEntity(_tmpId,_tmpEventId,_tmpName,_tmpAmountCents);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getEventSummaries(final Continuation<? super List<EventSummary>> $completion) {
    final String _sql = "\n"
            + "\t\tSELECT e.id, e.name, e.dateMillis, e.totalAmountCents,\n"
            + "\t\t\t(SELECT COUNT(*) FROM participants p WHERE p.eventId = e.id) AS participantCount,\n"
            + "\t\t\t(SELECT COALESCE(SUM(p.paidAmountCents),0) FROM participants p WHERE p.eventId = e.id) AS totalPaidCents\n"
            + "\t\tFROM events e ORDER BY e.dateMillis DESC\n"
            + "\t\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EventSummary>>() {
      @Override
      @NonNull
      public List<EventSummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfDateMillis = 2;
          final int _cursorIndexOfTotalAmountCents = 3;
          final int _cursorIndexOfParticipantCount = 4;
          final int _cursorIndexOfTotalPaidCents = 5;
          final List<EventSummary> _result = new ArrayList<EventSummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventSummary _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final long _tmpTotalAmountCents;
            _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            final int _tmpParticipantCount;
            _tmpParticipantCount = _cursor.getInt(_cursorIndexOfParticipantCount);
            final long _tmpTotalPaidCents;
            _tmpTotalPaidCents = _cursor.getLong(_cursorIndexOfTotalPaidCents);
            _item = new EventSummary(_tmpId,_tmpName,_tmpDateMillis,_tmpTotalAmountCents,_tmpParticipantCount,_tmpTotalPaidCents);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
