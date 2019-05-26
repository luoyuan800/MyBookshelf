package com.kunfei.bookshelf.readingtime;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Calendar;

public class ReadingTimeUtil {

  private final static String READ_TIME_CONTENT =
      "<p>\n"
          + " <font color=\"#87CEFA\">本周：%s</font>\n"
          + "<br>\n"
          + "  &nbsp;&nbsp;<font color=\"#D3D3D3\" size=\"2\">上周：%s</font>\n"
          + "<br>\n"
          + "</p>\n"
          + " <font color=\"#90EE90\"> 当月：%s</font>\n"
          + "<br>\n"
          + "  &nbsp;&nbsp;<font color=\"#D3D3D3\" size=\"2\">上月：%s</font>\n"
          + "<br>\n"
          + "<br>\n"
          + "  <font color=\"#FFA07A\">总读书时间：%s</font>";
  private static final String READING_TIME = "reading_time";
  private static final String TOTAL_READING_TIME = "total_reading_time";
  private static final String MONTH_READING_TIME_KEY = "month_reading_time_key";
  private static final String WEEK_READING_TIME_KEY = "week_reading_time_key";
  private static final String LAST_MONTH_READING_TIME = "last_month_reading_time";
  private static final String LAST_WEEK_READING_TIME = "last_week_reading_time";
  private static final int SECONDS_PER_MINUTE = 60;
  private static final int SECONDS_PER_HOUR = 60 * 60;

  private static boolean startReading = false;
  private static long currentStarTime;
  private static String currentMonth;
  private static String lastMonth;
  private static String currentWeek;
  private static String lastWeek;

  static {
    Calendar currentCalendar = Calendar.getInstance();
    Calendar lastMonthCalendar = (Calendar) currentCalendar.clone();
    lastMonthCalendar.add(Calendar.MONTH, -1);
    Calendar lastWeekCalendar = (Calendar) currentCalendar.clone();
    lastWeekCalendar.add(Calendar.WEEK_OF_MONTH, -1);
    currentMonth = currentCalendar.get(Calendar.YEAR) + "/" + currentCalendar.get(Calendar.MONTH);
    lastMonth = lastMonthCalendar.get(Calendar.YEAR) + "/" + lastMonthCalendar.get(Calendar.MONTH);
    currentWeek =
        currentCalendar.get(Calendar.YEAR) + "/" + currentCalendar.get(Calendar.MONTH) + "/"
            + currentCalendar.get(Calendar.WEEK_OF_MONTH);
    lastWeek =
        lastWeekCalendar.get(Calendar.YEAR) + "/" + lastWeekCalendar.get(Calendar.MONTH) + "/"
            + lastWeekCalendar.get(Calendar.WEEK_OF_MONTH);
  }

  public static void startReading() {
    startReading = true;
    currentStarTime = System.currentTimeMillis();
  }

  public static void stopReading(Context context) {
    startReading = false;
    long readingTime = System.currentTimeMillis() - currentStarTime;
    SharedPreferences pref = context.getSharedPreferences(READING_TIME, MODE_PRIVATE);
    long priorTotal = pref.getLong(TOTAL_READING_TIME, 0);
    long priorMonth = pref.getLong(currentMonth, 0);
    long priorWeek = pref.getLong(currentWeek, 0);

    Editor editor = pref.edit();
    editor.putLong(TOTAL_READING_TIME, priorTotal + readingTime);
    editor.putLong(currentMonth, priorMonth + readingTime);
    editor.putLong(currentWeek, priorWeek + readingTime);

    editor.apply();
  }

  public static String getReadingTimeContent(Context context) {
    SharedPreferences pref = context.getSharedPreferences(READING_TIME, MODE_PRIVATE);
    return String
        .format(READ_TIME_CONTENT,
            formatUnit(pref.getLong(currentWeek, 0)),
            formatUnit(pref.getLong(lastWeek, 0)),
            formatUnit(pref.getLong(currentMonth, 0)),
            formatUnit(pref.getLong(lastMonth, 0)),
            formatUnit(pref.getLong(TOTAL_READING_TIME, 0)));
  }

  private static String formatUnit(long readingTime) {
    long seconds = readingTime / 1000;
    long hours = seconds / SECONDS_PER_HOUR;
    int minutes = (int) ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
    int secs = (int) (seconds % SECONDS_PER_MINUTE);
    StringBuilder builder = new StringBuilder();
    if (hours > 0) {
      builder.append(hours).append("小时");
    }
    if (minutes > 0) {
      builder.append(minutes).append("分钟");
    }
    if (secs > 0) {
      builder.append(secs).append("秒");
    }
    if (hours == 0 && minutes == 0 && secs == 0) {
      builder.append("没有记录");
    }
    return builder.toString();
  }

}
