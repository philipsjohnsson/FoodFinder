package se.umu.cs.phjo0015.mapapplication.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import se.umu.cs.phjo0015.mapapplication.R

@Composable
fun ImageCarouselLazy() {
    val scrollState = rememberLazyListState()

    val imageList = listOf(
        R.drawable.bun,
        R.drawable.dog,
        R.drawable.dog,
        R.drawable.bun
    )

    LazyRow(
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(imageList.size ) { idx ->
            Image(
                painter = painterResource(id = imageList[idx]),
                contentDescription = "",
                modifier = Modifier
                    .size(205.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}