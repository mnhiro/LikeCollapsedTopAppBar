package com.example.likecollapsedtopappbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.likecollapsedtopappbar.ui.theme.LikeCollapsedTopAppBarTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LikeCollapsedTopAppBarTheme {
                LikeCollapsedTopAppBarContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LikeCollapsedTopAppBarContent() {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("アーティスト詳細画面") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "top app bar back"
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            val pagerState = rememberPagerState()
            val tabs = listOf("Content", "Profile", "Recommended")

            val density = LocalDensity.current
            val playDistance = with(density) { 12.dp.toPx() }
            var isShowTopBarArea by remember { mutableStateOf(true) }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        if (available.y.absoluteValue > playDistance) {
                            isShowTopBarArea = available.y > 0
                        }
                        return Offset.Zero
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .nestedScroll(nestedScrollConnection)
            ) {
                AnimatedVisibility(
                    visible = isShowTopBarArea ,
                    enter = expandVertically {
                        0
                    } + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.thumbnail),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            // enable event when scroll image.
                            .scrollable(
                                orientation = Orientation.Vertical,
                                state = rememberScrollableState { it }
                            )
                    )
                }
                TabBar(
                    pagerState = pagerState,
                    scrollBehavior = scrollBehavior,
                    tabs = tabs
                )
                TabContent(
                    pagerState = pagerState,
                    scrollBehavior = scrollBehavior,
                    tabs = tabs,
                )
            }
        }
    )
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun TabBar(
    pagerState: PagerState,
    scrollBehavior: TopAppBarScrollBehavior,
    tabs: List<String>
) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = {
            Spacer(modifier = Modifier.height(5.dp))
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, header ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            ) {
                Text(text = header)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TabContent(
    pagerState: PagerState,
    tabs: List<String>,
    scrollBehavior: TopAppBarScrollBehavior
) {
    HorizontalPager(
        pageCount = tabs.size,
        state = pagerState
    ) { index ->
        when (index) {
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    val list = (0..75).map { it.toString() }
                    items(count = list.size) {
                        Text(
                            text = list[it],
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    val list = (0..75).map { it.toString() }
                    items(count = list.size) {
                        Text(
                            text = list[it],
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            2 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    val list = (0..75).map { it.toString() }
                    items(count = list.size) {
                        Text(
                            text = list[it],
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LikeCollapsedTopAppBarPreview() {
    LikeCollapsedTopAppBarTheme {
        LikeCollapsedTopAppBarContent()
    }
}