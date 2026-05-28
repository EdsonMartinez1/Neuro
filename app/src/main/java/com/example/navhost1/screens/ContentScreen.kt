package com.example.navhost1.screens

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import androidx.compose.ui.res.stringResource

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)
private val PlayerColor = Color(0xFF1F2937)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

data class ContentItem(
    val id: Int,
    val title: String,
    val duration: String
)

@Composable
fun ContentScreen(navController: NavController) {

    val sampleItems = listOf(
        ContentItem(1, stringResource(R.string.content_titulo_video_1), "5:30"),
        ContentItem(2, stringResource(R.string.content_titulo_video_2), "8:15"),
        ContentItem(3, stringResource(R.string.content_titulo_video_3), "6:00"),
        ContentItem(4, stringResource(R.string.content_titulo_video_4), "10:20"),
        ContentItem(5, stringResource(R.string.content_titulo_video_5), "7:45"),
        ContentItem(6, stringResource(R.string.content_titulo_video_6), "12:00"),
    )

    var progress by remember {
        mutableStateOf(0.35f)
    }

    var selectedItem by remember {
        mutableStateOf(sampleItems.first())
    }

    val configuration = LocalConfiguration.current

    val isLandscape =
        configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BackgroundTop,
                        BackgroundBottom
                    )
                )
            )
    ) {

        if (isLandscape) {

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {

                    TopSection(
                        navController = navController
                    )

                    VideoPlayer(
                        item = selectedItem,
                        fillHeight = true
                    )
                }

                RightPanel(
                    items = sampleItems,
                    selectedItem = selectedItem,
                    progress = progress,
                    onSelect = {
                        selectedItem = it
                        progress = 0f
                    }
                )
            }

        } else {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                TopSection(
                    navController = navController
                )

                VideoPlayer(
                    item = selectedItem,
                    fillHeight = false
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {

                    Text(
                        text = selectedItem.title,
                        color = WhiteSoft,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        color = Primary,
                        trackColor = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedItem.duration,
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {

                    items(sampleItems) { item ->

                        ContentCard(
                            item = item,
                            isSelected = item.id == selectedItem.id,
                            onClick = {
                                selectedItem = item
                                progress = 0f
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopSection(
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = WhiteSoft
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Primary,
                            PrimaryLight
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {

            Text(
                text = "Contenido",
                color = WhiteSoft,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Videos y recursos emocionales",
                color = GrayText,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RightPanel(
    items: List<ContentItem>,
    selectedItem: ContentItem,
    progress: Float,
    onSelect: (ContentItem) -> Unit
) {

    Column(
        modifier = Modifier
            .width(420.dp)
            .fillMaxHeight()
            .padding(18.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardColor.copy(alpha = 0.96f)
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = selectedItem.title,
                    color = WhiteSoft,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = Primary,
                    trackColor = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = selectedItem.duration,
                    color = GrayText,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(items) { item ->

                ContentCard(
                    item = item,
                    isSelected = item.id == selectedItem.id,
                    onClick = {
                        onSelect(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun VideoPlayer(
    item: ContentItem,
    fillHeight: Boolean
) {

    Box(
        modifier =
            if (fillHeight)
                Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(PlayerColor)
            else
                Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(PlayerColor),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Primary,
                            PrimaryLight
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(54.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Color.Black.copy(alpha = 0.45f)
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                )
        ) {

            Text(
                text = item.title,
                color = WhiteSoft,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ContentCard(
    item: ContentItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val animatedColor by animateColorAsState(
        targetValue =
            if (isSelected)
                Primary.copy(alpha = 0.16f)
            else
                CardColor.copy(alpha = 0.96f),
        label = "card"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(animatedColor)
            .clickable {
                onClick()
            }
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected)
                            Brush.linearGradient(
                                listOf(
                                    Primary,
                                    PrimaryLight
                                )
                            )
                        else
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF334155),
                                    Color(0xFF475569)
                                )
                            )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Column {

                Text(
                    text = item.title,
                    color = WhiteSoft,
                    fontSize = 13.sp,
                    fontWeight =
                        if (isSelected)
                            FontWeight.Bold
                        else
                            FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.duration,
                    color = GrayText,
                    fontSize = 12.sp
                )
            }
        }
    }
}