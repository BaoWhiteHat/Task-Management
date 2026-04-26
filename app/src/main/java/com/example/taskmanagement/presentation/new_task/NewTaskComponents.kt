package com.example.taskmanagement.presentation.new_task

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmanagement.presentation.my_tasks.Priority
import com.example.taskmanagement.presentation.my_tasks.TaskTag

@Composable
fun TaskTagItem(
    modifier: Modifier = Modifier,
    tag: TaskTag,
    isSelected: Boolean,
    onTagClick: (TaskTag) -> Unit
) {
    InputChip(
        selected = isSelected,
        onClick = {onTagClick(tag)},
        label = { Text(tag.name.lowercase()) },
        modifier = modifier
    )

}

@Composable
fun TaskPriority(
    modifier: Modifier = Modifier,
    priority: Priority,
    isSelected: Boolean,
    onPriorityClick: () -> Unit
) {
    val animatedColor = animateColorAsState(
        targetValue = if (isSelected) priority.color else MaterialTheme.colorScheme.surface,
        label = "colorAnimation",
        animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
    )

    Surface(
        modifier = modifier,
        color = animatedColor.value,
        onClick = onPriorityClick
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ){
            Text(
                text = priority.name,
                color = contentColorFor(animatedColor.value)
            )
            Spacer(Modifier.width(4.dp))
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = contentColorFor(animatedColor.value)
                )
            )
        }


    }

}

@Composable
fun TaskItemContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        shadowElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ){
        content()
    }

}