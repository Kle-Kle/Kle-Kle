package com.example.klekle.main

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*


class SaturdayDecorator : DayViewDecorator {
    private val calendar: Calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay): Boolean {
        day.copyTo(calendar)
        val weekDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.BLUE))
    }
}