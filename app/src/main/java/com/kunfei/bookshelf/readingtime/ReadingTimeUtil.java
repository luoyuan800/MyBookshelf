package com.kunfei.bookshelf.readingtime;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Calendar;

public class ReadingTimeUtil {

  private final static String READ_TIME_CONTENT =
      "<strong>当周阅读时长</strong>： <font color=\"green\">%s</font>\n"
          + "<br> \n"
          + "    <small><i>上周阅读时长: %s</i></small>\n"
          + "<br>\n"
          + "<hr>\n"
          + "<strong>当月阅读时长</strong><font color=\"blue\"> %s</font>\n"
          + "<br>\n"
          + "<small><i>上月阅读时长：%s</i></small>\n"
          + "<br>\n"
          + "<hr>\n"
          + "<div align=\"right\">\n"
          + "<strong>总阅读时长</strong>：<font color=\"red\">%s</font>\n"
          + "  </div>";
  private static boolean startReading = false;
  private static long currentStarTime;
  private static String currentMonth;
  private static String currentWeek;

  static {
    Calendar calendar = Calendar.getInstance();
    currentMonth = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH);
    currentWeek = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar
        .get(Calendar.WEEK_OF_MONTH);
  }

  public static void startReading() {
    startReading = true;
    currentStarTime = System.currentTimeMillis();
  }

  public static void stopReading(Context context) {
    startReading = false;
    long readingTime = System.currentTimeMillis() - currentStarTime;
    SharedPreferences pref = context.getSharedPreferences("reading_time", MODE_PRIVATE);
    long priorTotal = pref.getLong("total_reading_time", 0);
    long priorMonth = 0;
    long priorWeek = 0;
    long lastMonthReadingTime = 0;
    String monthKey = pref.getString("month_reading_time_key", currentMonth);
    if (!currentMonth.equalsIgnoreCase(monthKey)) {
      lastMonthReadingTime = pref.getLong(monthKey, 0);
    } else {
      priorMonth = pref.getLong(monthKey, 0);
    }
    long lastWeekReadingTime = 0;
    String weekKey = pref.getString("week_reading_time_key", currentWeek);
    if (!currentWeek.equalsIgnoreCase(weekKey)) {
      lastWeekReadingTime = pref.getLong(weekKey, 0);
    } else {
      priorWeek = pref.getLong(weekKey, 0);
    }

    if(priorTotal > 100000L * 60 * 60 * 1000){
      priorMonth = 0;
      priorTotal= 0;
      priorWeek = 0;
    }

    Editor editor = pref.edit();
    editor.putLong("total_reading_time", priorTotal + readingTime);
    if (!currentMonth.equalsIgnoreCase(monthKey)) {
      editor.putLong("last_month_reading_time", lastMonthReadingTime);
      monthKey = currentMonth;
      editor.putString("month_reading_time_key", monthKey);
    }
    editor.putLong(monthKey, priorMonth + readingTime);

    if (!currentWeek.equalsIgnoreCase(weekKey)) {
      editor.putLong("last_week_reading_time", lastWeekReadingTime);
      weekKey = currentWeek;
      editor.putString("week_reading_time_key", weekKey);
    }
    editor.putLong(weekKey, priorWeek + readingTime);

    editor.apply();
  }

  public static String getReadingTimeContent(Context context) {
    SharedPreferences pref = context.getSharedPreferences("reading_time", MODE_PRIVATE);
    return String
        .format(READ_TIME_CONTENT,
            formatUnit(pref.getLong(currentWeek, 0)),
            formatUnit(pref.getLong("last_week_reading_time", 0)),
            formatUnit(pref.getLong(currentMonth, 0)),
            formatUnit(pref.getLong("last_month_reading_time", 0)),
            formatUnit(pref.getLong("total_reading_time", 0)));
  }

  private static String formatUnit(long currentWeekReadingTime) {
    if (currentWeekReadingTime < 60 * 1000 * 60) {
      return currentWeekReadingTime / (1000 * 60) + "分钟";
    } else {
      long h = currentWeekReadingTime / (1000 * 60 * 60);
      long m = currentWeekReadingTime % (1000 * 60 * 60);
      return h + "小时: " + m + "分钟";
    }
  }

}
