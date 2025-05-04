package ru.hse.cardefectscan.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import ru.hse.cardefectscan.presentation.viewmodel.RequestsViewModel
import ru.hse.cardefectscan.utils.LOAD_DATE
import ru.hse.cardefectscan.utils.REQUEST_STATUS
import ru.hse.cardefectscan.utils.RESULT_SCREEN
import ru.hse.cardefectscan.utils.TRANSLATED_STATUS
import ru.hse.cardefectscan.utils.UNKNOWN_STATUS
import ru.hse.cardefectscan.utils.UtilsExtensions.formatDate
import ru.hse.generated.models.ImageRequestElement

@Composable
fun RequestsScreen(
    navController: NavController,
    vm: RequestsViewModel = hiltViewModel()
) {
    val lazyPagingItems = vm.pagerFlow.collectAsLazyPagingItems()

    Scaffold { inner ->
        RequestElements(
            navController = navController,
            requests = lazyPagingItems,
            imageLoader = vm.imageLoader,
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        )
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
    Box(modifier = modifier) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (requests.itemCount == 0 &&
                requests.loadState.refresh is LoadState.NotLoading &&
                requests.loadState.append is LoadState.NotLoading
            ) {
                item {
                    Text(
                        text = "Пока не было выполнено ни одного запроса",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    )
                }
            }
            items(
                count = requests.itemCount,
                key = requests.itemKey { it.imageId }
            ) { index ->
                requests[index]?.let { item ->
                    RequestElement(
                        navController = navController,
                        request = item,
                        imageLoader = imageLoader
                    )
                }
            }

            when (requests.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
                is LoadState.Error -> {
                    val e = requests.loadState.append as LoadState.Error
                    item {
                        Text(
                            text = "Ошибка при загрузке: ${e.error.localizedMessage}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { requests.retry() }
                        )
                    }
                }
                else -> Unit
            }
        }

        when (requests.loadState.refresh) {
            is LoadState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is LoadState.Error -> {
                val e = requests.loadState.refresh as LoadState.Error
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Не удалось загрузить:\n${e.error.localizedMessage}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { requests.retry() }) {
                        Text("Повторить еще раз")
                    }
                }
            }
            else -> Unit
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
                        request.createdAt.formatDate()
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