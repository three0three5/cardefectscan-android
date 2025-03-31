package ru.hse.cardefectscan.presentation.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import ru.hse.cardefectscan.presentation.viewmodel.RequestsViewModel
import ru.hse.cardefectscan.utils.DATE_FORMAT
import ru.hse.cardefectscan.utils.LOAD_DATE
import ru.hse.cardefectscan.utils.LOGIN_SCREEN
import ru.hse.cardefectscan.utils.REQUEST_STATUS
import ru.hse.cardefectscan.utils.RESULT_SCREEN
import ru.hse.cardefectscan.utils.TRANSLATED_STATUS
import ru.hse.cardefectscan.utils.UNKNOWN_STATUS
import ru.hse.generated.models.ImageRequestElement
import java.time.format.DateTimeFormatter

@Composable
fun RequestsScreen(
    navController: NavController,
    vm: RequestsViewModel = hiltViewModel(),
) {
    val items = vm.handleResult(runCatching {
        vm.pagerFlow.collectAsLazyPagingItems()
    })
    items?.let {
        Scaffold { innerPadding ->
            RequestElements(
                navController = navController,
                requests = items,
                imageLoader = vm.imageLoader,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        }
    }
    DisplayMessage(vm)
}

@Composable
fun RequestElements(
    navController: NavController,
    requests: LazyPagingItems<ImageRequestElement>,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
) {
    when (requests.loadState.refresh) {
        is LoadState.Error -> {
            val error = (requests.loadState.refresh as LoadState.Error).error
            Log.d("RequestsScreen", "Refresh Error: $error")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier,
            ) {
                Text(text = "Ошибка загрузки: ${error.localizedMessage}")
                Button(onClick = { requests.retry() }) {
                    Text("Повторить")
                }
            }
        }

        is LoadState.Loading -> {
            CircularProgressIndicator(modifier = modifier.size(24.dp))
        }

        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ) {
                itemsIndexed(requests.itemSnapshotList) { _, item ->
                    item?.let {
                        RequestElement(
                            navController = navController,
                            request = item,
                            imageLoader = imageLoader,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestElement(
    navController: NavController,
    request: ImageRequestElement,
    imageLoader: ImageLoader,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val buttonHeight = screenHeight * 0.12f
    val status = TRANSLATED_STATUS[request.status] ?: UNKNOWN_STATUS.format(request.status)

    Button(
        onClick = {
            navController.navigate("$RESULT_SCREEN/${request.imageId}")
        },
        modifier = Modifier
            .height(buttonHeight)
            .fillMaxWidth(0.9f),
        contentPadding = PaddingValues(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            ThumbnailImage(request, imageLoader)

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = REQUEST_STATUS.format(status),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = LOAD_DATE.format(
                        request.createdAt.format(
                            DateTimeFormatter.ofPattern(
                                DATE_FORMAT
                            )
                        )
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ThumbnailImage(
    request: ImageRequestElement,
    imageLoader: ImageLoader,
) {
    val context = LocalContext.current
    if (request.thumbnailLink == null) return
    val imageRequest = ImageRequest.Builder(context)
        .data(Uri.parse(request.thumbnailLink))
        .crossfade(true)
        .build()
    SubcomposeAsyncImage(
        model = imageRequest,
        imageLoader = imageLoader,
        contentDescription = "Thumbnail",
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        },
        error = {
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL,
)
@Composable
fun RequestsScreenPreview() {
    RequestsScreen(rememberNavController())
}