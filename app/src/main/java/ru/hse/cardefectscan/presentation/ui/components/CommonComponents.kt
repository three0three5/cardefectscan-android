package ru.hse.cardefectscan.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.hse.cardefectscan.presentation.viewmodel.CommonViewModel

@Composable
fun DisplayMessage(vm: CommonViewModel) {
    WithAnimation(vm.displayMessage) {
        Text(vm.exceptionMessage, color = Color.Red)
    }
}

@Composable
fun WithAnimation(
    isVisible: Boolean,
    vm: CommonViewModel? = null,
    content: @Composable (vm: CommonViewModel?) -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(durationMillis = 500)),
        exit = fadeOut(tween(durationMillis = 500))
    ) {
        content(vm)
    }
}