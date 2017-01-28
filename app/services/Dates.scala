package services

import java.util.{Calendar, Date}

object Dates {
  def daysFromNow(days: Int): Date = {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, days)
    calendar.getTime
  }
}
