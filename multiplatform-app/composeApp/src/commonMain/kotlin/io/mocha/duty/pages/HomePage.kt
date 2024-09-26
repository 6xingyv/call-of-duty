package io.mocha.duty.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.mocha.duty.data.getCurrentLocalDate
import io.mocha.duty.data.net.WebReq
import xyz.junerver.compose.hooks.rememberPersistent
import xyz.junerver.compose.hooks.rememberRequest
import xyz.junerver.compose.hooks.userequest.RequestOptions

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePage(navController: NavController, modifier: Modifier = Modifier) {
    val (user) = rememberPersistent("user", "")
    val (todayDutyInfo, todayDutyLoading, todayDutyError, todayReq, _, todayRefresh) = rememberRequest({ WebReq.reqDuty() })
    val (_, postLoading, postError, postReq) = rememberRequest(
        requestFn = { WebReq.postTodayDuty(user) },
        options = RequestOptions.optionOf {
            manual = true
            defaultParams = arrayOf(user)
        })
    val (allDutyInfo, allDutyLoading, allDutyError, _, _, allDutyRefresh) = rememberRequest({ WebReq.reqAllDuty() })
    var clickCount by remember { mutableStateOf(0) }
    if (user.isBlank()) {
        navController.navigate(Routes.HELLO)
    }
    if (allDutyInfo != null) {
        if (!allDutyInfo.any { student -> student.name == user }) {
            navController.navigate(Routes.HELLO)
        }
    }


    AnimatedContent(todayDutyLoading or postLoading or allDutyLoading) { loading ->
        if (loading) {
            LoadingPage()
        } else if ((todayDutyError != null) or (postError != null) or (allDutyError != null)) {
            ErrorPage(onRetry = {
                todayRefresh()
            }) {
                todayDutyError?.message?.let {
                    Text(it)
                }
                postError?.message?.let {
                    Text(it)
                }
                allDutyError?.message?.let {
                    Text(it)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item("actions") {
                    FlowRow(
                        Modifier
                            .statusBarsPadding()
                    ) {
                        Button(onClick = {
                            todayRefresh()
                            allDutyRefresh()
                        }) {
                            Text("刷新")
                        }

                    }
                }
                if (todayDutyInfo != null) {
                    item("today-duty-info") {
                        AnimatedContent(todayDutyInfo) {
                            Column(
                                Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text("你好, $user", style = MaterialTheme.typography.headlineMedium)
                                    Text(
                                        "今天是 ${getCurrentLocalDate()}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                if (it.isDutyNeeded and it.assigned.isNullOrBlank()) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("今天还没有值日生")
                                    }

                                    Button(onClick = {
                                        postReq(arrayOf(user))
                                        todayRefresh()
                                    }, Modifier.fillMaxWidth()) {
                                        Text(
                                            "抢一下",
                                            style = MaterialTheme.typography.displaySmall
                                        )
                                    }
                                } else {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (it.isDutyNeeded) {
                                            if (it.assigned == user) {
                                                Text("今天你值日", style = MaterialTheme.typography.displayLarge)
                                            } else {
                                                Text(
                                                    "今天 ${it.assigned} 值日",
                                                    style = MaterialTheme.typography.displaySmall
                                                )
                                            }
                                        } else {
                                            Text(
                                                "今天不用值日",
                                                style = MaterialTheme.typography.displayLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!allDutyInfo.isNullOrEmpty()) {
                    item {
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(0.2f))
                        )
                    }
                    item("all-duty-info") {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("值日次数一览")
                            allDutyInfo.sortedByDescending { it.dutiesCount }.forEach { student ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(student.name)
                                    Text("${student.dutiesCount}次")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

