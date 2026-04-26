package com.example.taskmanagement.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun SegmentedButton(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedItem: String,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(12.dp)
            )
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            Button(
                onClick = { onItemClick(item) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    item
                )

            }
        }
    }

}

@Composable
fun CalendarGrid(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    currentMonth: LocalDate,
    markedDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    today: LocalDate
) {
    val firstDayOfMonth = currentMonth.with(TemporalAdjusters.firstDayOfMonth())
    val dayOfWeekOfFirstDay = firstDayOfMonth.dayOfWeek
    val daysInMonth = currentMonth.lengthOfMonth()
    val leadingEmptyDays = (dayOfWeekOfFirstDay.value - DayOfWeek.MONDAY.value + 7) % 7
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { dayName ->
                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

            }
        }
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false
        ) {
            items(leadingEmptyDays) {
                Spacer(Modifier.size(40.dp))
            }
            items(daysInMonth) { day ->
                val date = currentMonth.withDayOfMonth(day + 1)
                val isSelected = date == selectedDate
                val isToday = date == today
                val hasTasks = date in markedDates
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clickable { onDateSelected(date) }
                        .let {
                            if (isSelected) it.background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            else if (isToday) it.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            else it
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (day + 1).toString(),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (hasTasks && !isSelected){
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                                .align(Alignment.BottomCenter)
                                .offset(y = 12.dp)
                        )
                    }
                }
            }
        }

    }

}


@Composable
fun WeeklyCalendarGrid(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    currentWeekStartDate: LocalDate,
    markedDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    today: LocalDate
) {
    val weekDays = (0 until 7).map { currentWeekStartDate.plusDays(it.toLong()) }
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { dayName ->
                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

            }
        }
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false
        ) {
            items(weekDays.size) { index ->
                val date = weekDays[index]
                val isSelected = date == selectedDate
                val isToday = date == today
                val hasTasks = date in markedDates
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clickable { onDateSelected(date) }
                        .let {
                            if (isSelected) it.background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            else if (isToday) it.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            else it
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (hasTasks && !isSelected){
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                                .align(Alignment.BottomCenter)
                                .offset(y = 12.dp)
                        )
                    }
                }
            }
        }
    }

}