package io.mocha.duty.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.mocha.duty.data.net.WebReq
import xyz.junerver.compose.hooks.rememberPersistent
import xyz.junerver.compose.hooks.rememberRequest

@Composable
fun HelloPage(navController: NavController, modifier: Modifier = Modifier) {
    val (_, saveUser) = rememberPersistent("user", "")
    val (allDutyInfo, allDutyLoading, allDutyError, allDutyReq) = rememberRequest({ WebReq.reqAllDuty() })
    var username by remember { mutableStateOf("") }
    AnimatedContent(allDutyLoading and (allDutyInfo == null)) { loading ->
        if (loading) {
            LoadingPage()
        } else if (allDutyError != null) {
            ErrorPage({
                allDutyReq(arrayOf())
            }) {
                Text(allDutyError.message ?: "未知错误信息")
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("选择用户", style = MaterialTheme.typography.headlineMedium)
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    allDutyInfo?.forEach { student ->
                        Surface(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            tonalElevation = if (username == student.name) 16.dp else 8.dp
                        ) {
                            Column(
                                Modifier
                                    .clickable {
                                        username = student.name
                                    }
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(student.name)
                            }
                        }

                    }
                }

                Button(onClick = {
                    saveUser(username)
                    navController.popBackStack()
                }, Modifier.fillMaxWidth()) {
                    Text("确认")
                }
            }
        }
    }


}