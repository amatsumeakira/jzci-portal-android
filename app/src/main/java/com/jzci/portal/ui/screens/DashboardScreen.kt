package com.jzci.portal.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jzci.portal.JZCIPortalApp
import com.jzci.portal.data.model.*
import com.jzci.portal.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(username: String?) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("jzci_prefs", android.content.Context.MODE_PRIVATE)
    val apiService = JZCIPortalApp.instance.apiService

    var studentInfo by remember { mutableStateOf<StudentInfo?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            loading = true
            error = null
            val result = apiService.getStudentInfo()
            loading = false
            result.onSuccess { info ->
                studentInfo = info
            }.onFailure { e ->
                error = e.message
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐉", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("JZCI Portal", fontWeight = FontWeight.SemiBold)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!refreshing) {
                                refreshing = true
                                scope.launch {
                                    val result = apiService.getStudentInfo()
                                    refreshing = false
                                    result.onSuccess { studentInfo = it }
                                }
                            }
                        },
                        enabled = !loading
                    ) {
                        if (refreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "刷新")
                        }
                    }
                    if (username != null) {
                        Text(
                            text = username,
                            fontSize = 14.sp,
                            color = Gray500,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                apiService.logout()
                                prefs.edit().remove("session_id").remove("username").apply()
                                (context as? android.app.Activity)?.recreate()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Filled.ExitToApp,
                            contentDescription = "退出",
                            tint = Gray500
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Blue600
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Gray50)
        ) {
            when {
                loading && studentInfo == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Blue600)
                    }
                }
                error != null && studentInfo == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Red500
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error ?: "加载失败",
                            color = Red600,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { loadData() },
                            colors = ButtonDefaults.buttonColors(containerColor = Blue600)
                        ) {
                            Text("重试")
                        }
                    }
                }
                studentInfo != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        studentInfo?.baseInfo?.let { base ->
                            item {
                                BaseInfoCard(base, username)
                            }
                        }
                        studentInfo?.batchInfo?.let { batch ->
                            item {
                                BatchInfoCard(batch)
                            }
                        }
                        studentInfo?.dorm?.let { dorm ->
                            item {
                                DormInfoCard(dorm)
                            }
                        }
                        studentInfo?.manager?.let { manager ->
                            item {
                                ManagerCard(manager)
                            }
                        }
                        studentInfo?.family?.let { family ->
                            if (family.isNotEmpty()) {
                                item {
                                    FamilyCard(family)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    delay: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBgColor, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray700
                )
            }
            content()
        }
    }
}

@Composable
fun FieldRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Gray400,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Gray700,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BaseInfoCard(data: BaseInfo, username: String?) {
    InfoCard(
        title = "基本信息",
        icon = Icons.Default.Person,
        iconColor = Blue500,
        iconBgColor = Blue500.copy(alpha = 0.1f)
    ) {
        Column {
            Divider(color = Gray100, modifier = Modifier.padding(bottom = 8.dp))
            FieldRow("姓名", data.XM.ifBlank { username })
            FieldRow("学号", data.XSBH)
            FieldRow("性别", data.XBDM_DISPLAY)
            FieldRow("身份证", data.SFZJH)
            FieldRow("学院", data.DWDM_DISPLAY)
            FieldRow("专业", data.ZYDM_DISPLAY)
            FieldRow("班级", data.BJDM_DISPLAY)
            FieldRow("民族", data.MZDM_DISPLAY)
            FieldRow("政治面貌", data.ZZMMDM_DISPLAY)
            FieldRow("学籍状态", data.XJZTDM_DISPLAY)
            FieldRow("学生类型", data.XSLXDM_DISPLAY)
            FieldRow("住宿楼", data.SSL_DISPLAY)
            FieldRow("住宿地址", data.ZSDZ)
        }
    }
}

@Composable
fun BatchInfoCard(data: BatchInfo) {
    InfoCard(
        title = "学籍信息",
        icon = Icons.Default.School,
        iconColor = Indigo500,
        iconBgColor = Indigo50
    ) {
        Column {
            Divider(color = Gray100, modifier = Modifier.padding(bottom = 8.dp))
            FieldRow("年级", data.XZNJ)
            FieldRow("学院", data.DWDM_DISPLAY)
            FieldRow("专业", data.ZYDM_DISPLAY)
            FieldRow("班级", data.BJDM_DISPLAY)
            FieldRow("学生类别", data.XSLBDM_DISPLAY)
            FieldRow("学年", data.XN)
            FieldRow("文化开始", data.WHKSRQ)
            FieldRow("文化结束", data.WHJSRQ)
        }
    }
}

@Composable
fun DormInfoCard(data: DormInfo) {
    InfoCard(
        title = "宿舍信息",
        icon = Icons.Default.Home,
        iconColor = Green500,
        iconBgColor = Green50
    ) {
        Column {
            Divider(color = Gray100, modifier = Modifier.padding(bottom = 8.dp))
            FieldRow("校区", data.XQDM_DISPLAY)
            FieldRow("楼栋", data.SSDM_DISPLAY)
            FieldRow("房间号", data.FJH)
            FieldRow("床位", data.CWH)
            if (data.SFBZ.isNotBlank()) {
                FieldRow("收费标准", "${data.SFBZ} 元/年")
            }
        }
    }
}

@Composable
fun ManagerCard(data: ManagerInfo) {
    InfoCard(
        title = "辅导员",
        icon = Icons.Default.People,
        iconColor = Amber500,
        iconBgColor = Amber50
    ) {
        Column {
            Divider(color = Gray100, modifier = Modifier.padding(bottom = 8.dp))
            FieldRow("姓名", data.XM)
            FieldRow("工号", data.ZGH)
            FieldRow("联系电话", data.LXDH)
            FieldRow("任职类别", data.RZLBDM_DISPLAY)
            FieldRow("在任状态", data.ZRZT)
        }
    }
}

@Composable
fun FamilyCard(family: List<FamilyMember>) {
    InfoCard(
        title = "家庭成员",
        icon = Icons.Default.FamilyRestroom,
        iconColor = Pink500,
        iconBgColor = Pink50
    ) {
        Column {
            Divider(color = Gray100, modifier = Modifier.padding(bottom = 8.dp))
            family.forEachIndexed { index, member ->
                if (index > 0) {
                    Divider(
                        color = Gray100,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                FieldRow("姓名", member.CYXM.ifBlank { null })
                FieldRow("关系", member.GXDM_DISPLAY)
                FieldRow("电话", member.LXDH)
                FieldRow("工作单位", member.GZDW)
            }
        }
    }
}
