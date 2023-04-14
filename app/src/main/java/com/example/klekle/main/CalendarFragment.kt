package com.example.klekle.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.klekle.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarView.setSelectedDate(CalendarDay.today())

        binding.calendarView.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        val oneDayDecorator = OneDayDecorator()
        binding.calendarView.addDecorators(
            SundayDecorator(),
            SaturdayDecorator()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}