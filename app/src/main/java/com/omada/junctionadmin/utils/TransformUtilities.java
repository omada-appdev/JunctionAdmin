package com.omada.junctionadmin.utils;

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
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;

public class TransformUtilities {

    public static float DP_TO_PX(Context context, float dp_value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp_value, context.getResources().getDisplayMetrics());
    }

    public static ZonedDateTime convertUtcLocalDateTimeToSystemZone(@Nonnull LocalDateTime time) {
        return time.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
    }

    public static LocalDateTime convertSystemZoneLocalDateTimeToUtc(@Nonnull ZonedDateTime time) {
        return time.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }


    public static LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        return timestamp.toDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public static String convertUtcLocalDateTimeToHHMM(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = convertUtcLocalDateTimeToSystemZone(localDateTime);
        return convertSystemZoneDateTimeToHHMM(zonedDateTime);
    }

    public static String convertSystemZoneDateTimeToHHMM(ZonedDateTime zonedDateTime) {
        String res = zonedDateTime.getHour() + ":";
        res += String.valueOf(zonedDateTime.getMinute()).length() < 2 ? "0" : "";
        res += zonedDateTime.getMinute();
        return res;
    }

    public static String convertUtcLocalDateTimeToddDDMM(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = convertUtcLocalDateTimeToSystemZone(localDateTime);
        return convertSystemZoneDateTimeToddDDMM(zonedDateTime);
    }

    public static String convertSystemZoneDateTimeToddDDMM(ZonedDateTime zonedDateTime) {
        return String.format(Locale.ENGLISH, "%s %d %s",
                zonedDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                zonedDateTime.getDayOfMonth(),
                zonedDateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        );
    }

    public static Timestamp convertUtcLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
        return new Timestamp(Date.from(localDateTime.atZone(ZoneId.of("UTC")).toInstant()));
    }
}
