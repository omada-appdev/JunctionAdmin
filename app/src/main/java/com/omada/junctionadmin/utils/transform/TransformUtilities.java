package com.omada.junctionadmin.utils.transform;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransformUtilities {

    public static float DP_TO_PX(Context context, float dp_value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp_value, context.getResources().getDisplayMetrics());
    }

    public static ZonedDateTime convertUtcLocalDateTimeToSystemZone(LocalDateTime time) {
        return time.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
    }

    public static LocalDateTime convertSystemZoneLocalDateTimeToUtc(ZonedDateTime time) {
        return time.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }


    public static LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        return timestamp.toDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public static Timestamp convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
        return new Timestamp(Date.from(localDateTime.toInstant(ZoneOffset.UTC)));
    }
}
