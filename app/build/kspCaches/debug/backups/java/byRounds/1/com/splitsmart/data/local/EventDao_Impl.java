package com.splitsmart.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.splitsmart.data.model.EventEntity;
import com.splitsmart.data.model.EventSummary;
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

  public EventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventEntity = new EntityInsertionAdapter<EventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `events` (`id`,`name`,`dateMillis`,`location`,`description`,`totalAmountCents`) VALUES (nullif(?, 0),?,?,?,?,?)";
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
      }
    };
    this.__insertionAdapterOfParticipantEntity = new EntityInsertionAdapter<ParticipantEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `participants` (`id`,`eventId`,`name`,`shareAmountCents`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ParticipantEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEventId());
        statement.bindString(3, entity.getName());
        statement.bindLong(4, entity.getShareAmountCents());
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
  public Object insertEventWithParticipants(final EventEntity event,
      final List<ParticipantEntity> participants, final Continuation<? super Long> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> EventDao.DefaultImpls.insertEventWithParticipants(EventDao_Impl.this, event, participants, __cont), $completion);
  }

  @Override
  public Object getEventSummaries(final Continuation<? super List<EventSummary>> $completion) {
    final String _sql = "\n"
            + "\t\tSELECT e.id, e.name, e.dateMillis, e.totalAmountCents,\n"
            + "\t\t\t(SELECT COUNT(*) FROM participants p WHERE p.eventId = e.id) AS participantCount\n"
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
            _item = new EventSummary(_tmpId,_tmpName,_tmpDateMillis,_tmpTotalAmountCents,_tmpParticipantCount);
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
