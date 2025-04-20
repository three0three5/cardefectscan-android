package ru.hse.cardefectscan.presentation.ui.components

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.hse.cardefectscan.presentation.viewmodel.UploadViewModel
import ru.hse.cardefectscan.utils.ANOTHER_IMAGE
import ru.hse.cardefectscan.utils.CHOSEN_IMAGE
import ru.hse.cardefectscan.utils.IMAGE_HAS_BEEN_UPLOADED
import ru.hse.cardefectscan.utils.LOAD_IMAGE
import ru.hse.cardefectscan.utils.UPLOAD_IMAGE

@Composable
fun UploadScreen(
    vm: UploadViewModel = hiltViewModel()
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            vm.imageUri = it
            vm.loaded = false
        }
    }

    Scaffold { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            UploadElements(vm, launcher)
        }
    }
}

@Composable
fun UploadElements(
    vm: UploadViewModel,
    launcher: ManagedActivityResultLauncher<String, Uri?>,
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        Column(
            verticalArrangement = Arrangement.spacedBy(13.dp),
            modifier = Modifier
                .fillMaxSize(0.95f)
        ) {
            ImageBox(
                vm,
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .background(Color.Black)
            )
            UploadButtons(
                vm = vm,
                launcher = launcher,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize(0.95f)
        ) {
            ImageBox(
                vm,
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .background(Color.Black)
            )
            UploadButtons(
                vm = vm,
                launcher = launcher,
                modifier = Modifier
                    .fillMaxHeight(0.4f)
            )
        }
    }

}

@Composable
fun ImageBox(
    vm: UploadViewModel,
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        vm.imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = CHOSEN_IMAGE,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun UploadButtons(
    vm: UploadViewModel,
    modifier: Modifier,
    launcher: ManagedActivityResultLauncher<String, Uri?>,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(13.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            onClick = {
                if (vm.isLoading) return@Button
                launcher.launch("image/*")
            },
            modifier = modifier
        ) {
            val label = if (vm.imageUri == null) LOAD_IMAGE else ANOTHER_IMAGE
            Text(
                label,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        WithAnimation(vm.imageUri != null) {
            Button(
                onClick = {
                    if (vm.isLoading || vm.loaded) return@Button
                    scope.launch {
                        vm.upload()
                    }
                },
                modifier = modifier
            ) {
                Text(
                    UPLOAD_IMAGE,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        WithAnimation(vm.isLoading) {
            CircularProgressIndicator()
        }

        WithAnimation(vm.loaded && vm.exceptionMessage.isBlank()) {
            Text(
                IMAGE_HAS_BEEN_UPLOADED,
                color = Color.Green,
            )
        }

        DisplayMessage(vm)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:parent=pixel",
)
@Composable
fun UploadScreenPreview() {
    UploadScreen()
}