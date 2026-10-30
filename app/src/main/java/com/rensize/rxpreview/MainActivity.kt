package com.rensize.rxpreview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

private val Ink = Color(0xFF070707)
private val Paper = Color(0xFFF3F3F3)
private val Panel = Color(0xFF121212)
private val Subtle = Color(0xFF888888)
private val Border = Color(0xFF292929)
private val Soft = Color(0xFF1B1B1B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 7, 7)
        window.navigationBarColor = android.graphics.Color.rgb(7, 7, 7)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = 0
        setContent { RXApp() }
    }
}

private enum class Page(val label: String) {
    Home("Главная"), Games("Игры"), Activity("Активность"), Settings("Настройки")
}

@Composable
fun RXApp() {
    var page by remember { mutableStateOf(Page.Home) }
    var dialog by remember { mutableStateOf<String?>(null) }
    var overlay by remember { mutableStateOf(true) }
    var haptics by remember { mutableStateOf(true) }
    var animations by remember { mutableStateOf(true) }

    BackHandler(enabled = page != Page.Home) { page = Page.Home }
    MaterialTheme(colorScheme = darkColorScheme(primary = Paper, surface = Panel, background = Ink)) {
        Column(Modifier.fillMaxSize().background(Ink)) {
            RXTopBar(onAccountClick = { dialog = "Профиль" })
            Box(Modifier.weight(1f)) {
                Crossfade(targetState = page, label = "page", modifier = Modifier.fillMaxSize()) { current ->
                    when (current) {
                        Page.Home -> HomeScreen(onLaunch = { dialog = "Запуск игры" }, onGames = { page = Page.Games })
                        Page.Games -> GamesScreen(onLaunch = { dialog = "Запуск игры" })
                        Page.Activity -> ActivityScreen()
                        Page.Settings -> SettingsScreen(
                            overlay = overlay, onOverlay = { overlay = it },
                            haptics = haptics, onHaptics = { haptics = it },
                            animations = animations, onAnimations = { animations = it },
                            onAbout = { dialog = "О демоверсии" }
                        )
                    }
                }
            }
            RXBottomBar(page = page, onClick = { page = it })
        }
        if (dialog != null) {
            AlertDialog(
                onDismissRequest = { dialog = null },
                containerColor = Color(0xFF181818),
                shape = RoundedCornerShape(24.dp),
                title = { Text(dialog ?: "", color = Paper, fontWeight = FontWeight.SemiBold) },
                text = {
                    Text(
                        when (dialog) {
                            "Запуск игры" -> "Это интерактивное превью интерфейса. Запуск игры и подключение к серверу здесь не реализованы."
                            "Профиль" -> "Демо-профиль. Реальной авторизации и хранения пользовательских данных пока нет."
                            else -> "RX UI Concept • Kotlin + Jetpack Compose. Отдельное демо, не изменяет установленный RX."
                        }, color = Subtle, lineHeight = 21.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { dialog = null }) { Text("ПОНЯТНО", color = Paper) }
                }
            )
        }
    }
}

@Composable
private fun RXTopBar(onAccountClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RXMark(Modifier.size(34.dp))
        Spacer(Modifier.width(12.dp))
        Text("rensize", fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = (-1.1).sp, color = Paper)
        Text(" / X", color = Subtle, fontSize = 14.sp)
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(39.dp).clip(CircleShape).border(1.dp, Border, CircleShape)
                .clickable(onClick = onAccountClick),
            contentAlignment = Alignment.Center
        ) {
            Text("R", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Paper)
        }
    }
}

@Composable
private fun RXMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        // Symbol-only brand mark (no letters)
        drawLine(Paper, Offset(w * .19f, h * .76f), Offset(w * .54f, h * .18f), strokeWidth = w * .17f, cap = StrokeCap.Round)
        drawLine(Paper, Offset(w * .48f, h * .77f), Offset(w * .83f, h * .20f), strokeWidth = w * .17f, cap = StrokeCap.Round)
    }
}

@Composable
private fun SectionHeading(eyebrow: String, heading: String, description: String? = null) {
    Text(eyebrow, fontSize = 10.sp, color = Subtle, letterSpacing = 2.4.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(9.dp))
    Text(heading, fontSize = 32.sp, color = Paper, fontWeight = FontWeight.SemiBold, letterSpacing = (-1.8).sp)
    if (description != null) {
        Spacer(Modifier.height(9.dp))
        Text(description, color = Subtle, fontSize = 13.sp)
    }
}

@Composable
private fun HomeScreen(onLaunch: () -> Unit, onGames: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 25.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        SectionHeading("YOUR SPACE", "Всё под контролем.", "Игры, сессии и настройки — в одном месте.")
        Spacer(Modifier.height(27.dp))
        FeaturedCard(onLaunch)
        Spacer(Modifier.height(27.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Обзор", color = Paper, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            Text("LIVE PREVIEW", color = Subtle, fontSize = 9.sp, letterSpacing = 1.5.sp)
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            StatCard("00:00", "Время в игре", Modifier.weight(1f))
            StatCard("01", "Доступные игры", Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Моя библиотека", color = Paper, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text("Смотреть все  ↗", color = Subtle, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onGames))
        }
        Spacer(Modifier.height(13.dp))
        GameRow(onClick = onGames)
        Spacer(Modifier.height(26.dp))
    }
}

@Composable
private fun FeaturedCard(onLaunch: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(270.dp).clip(RoundedCornerShape(25.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF252525), Color(0xFF101010), Color(0xFF080808))))
            .border(1.dp, Color(0xFF373737), RoundedCornerShape(25.dp))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val x = size.width
            val y = size.height
            for (i in 0..7) {
                drawLine(
                    color = Color.White.copy(alpha = .035f),
                    start = Offset(x * .60f + i * 20f, -50f),
                    end = Offset(x * .06f + i * 20f, y + 30f),
                    strokeWidth = 1.7f
                )
            }
            drawCircle(Color.White.copy(alpha = .028f), radius = x * .46f, center = Offset(x * .95f, y * .2f))
        }
        Column(Modifier.fillMaxSize().padding(23.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(Paper))
                Spacer(Modifier.width(8.dp))
                Text("READY TO PLAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Paper, letterSpacing = 1.8.sp)
                Spacer(Modifier.weight(1f))
                Text("01 / 01", fontSize = 10.sp, color = Subtle)
            }
            Spacer(Modifier.weight(1f))
            Text("CRITICAL", color = Paper, fontSize = 33.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.6).sp)
            Text("OPS", color = Paper, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.4).sp, lineHeight = 43.sp)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Демо-библиотека", fontSize = 12.sp, color = Color(0xFFB4B4B4))
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.clip(RoundedCornerShape(100.dp)).background(Paper).clickable(onClick = onLaunch)
                        .padding(horizontal = 20.dp, vertical = 13.dp),
                ) { Text("Запустить  ↗", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
private fun StatCard(value: String, caption: String, modifier: Modifier = Modifier) {
    Column(
        modifier.height(113.dp).clip(RoundedCornerShape(19.dp)).background(Panel)
            .border(1.dp, Border, RoundedCornerShape(19.dp)).padding(17.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(value, fontSize = 27.sp, fontWeight = FontWeight.SemiBold, color = Paper, letterSpacing = (-1).sp)
        Text(caption, color = Subtle, fontSize = 11.sp)
    }
}

@Composable
private fun GameRow(onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(18.dp))
            .background(Panel).border(1.dp, Border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(49.dp).clip(RoundedCornerShape(12.dp)).background(Soft), contentAlignment = Alignment.Center) {
            Text("C/", fontSize = 16.sp, color = Paper, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text("Critical Ops", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Paper)
            Spacer(Modifier.height(4.dp))
            Text("Концепт интеграции", color = Subtle, fontSize = 11.sp)
        }
        Spacer(Modifier.weight(1f))
        Text("↗", color = Paper, fontSize = 19.sp)
    }
}

@Composable
private fun GamesScreen(onLaunch: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 25.dp)) {
        Spacer(Modifier.height(28.dp))
        SectionHeading("LIBRARY", "Твои игры.", "Всё необходимое — внутри.")
        Spacer(Modifier.height(26.dp))
        GameRow(onLaunch)
        Spacer(Modifier.height(20.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(21.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(21.dp)).padding(22.dp)) {
            Column {
                Text("01   /   GAME DETAIL", color = Subtle, fontSize = 10.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(20.dp))
                Text("Critical Ops", color = Paper, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Демонстрационный экран игры", color = Subtle, fontSize = 13.sp)
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DataChip("VERSION", "1.30")
                    DataChip("BUILD", "DEMO")
                }
                Spacer(Modifier.height(24.dp))
                RXButton("Открыть игру", onLaunch)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Функции пока не подключены к серверу.", color = Subtle, fontSize = 11.sp)
    }
}

@Composable
private fun DataChip(title: String, value: String) {
    Column {
        Text(title, color = Subtle, fontSize = 9.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(7.dp))
        Text(value, color = Paper, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RXButton(label: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(13.dp)).background(Paper)
        .clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActivityScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 25.dp)) {
        Spacer(Modifier.height(28.dp))
        SectionHeading("INSIGHTS", "Активность.", "Статистика без лишнего шума.")
        Spacer(Modifier.height(28.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(24.dp)).padding(24.dp)) {
            Column {
                Text("ОБЩЕЕ ВРЕМЯ", color = Subtle, fontSize = 10.sp, letterSpacing = 1.6.sp)
                Spacer(Modifier.height(12.dp))
                Text("00:00", color = Paper, fontSize = 48.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-2).sp)
                Spacer(Modifier.height(6.dp))
                Text("Данных пока нет", color = Subtle, fontSize = 12.sp)
                Spacer(Modifier.height(28.dp))
                BarChartDemo()
                Spacer(Modifier.height(14.dp))
                Text("ПН        ВТ        СР        ЧТ        ПТ        СБ        ВС", fontSize = 9.sp, color = Subtle, letterSpacing = 1.sp)
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("Статистика появится после подключения к RX.", color = Subtle, fontSize = 12.sp)
    }
}

@Composable
private fun BarChartDemo() {
    Row(Modifier.fillMaxWidth().height(118.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        listOf(.22f, .48f, .34f, .7f, .58f, .92f, .41f).forEachIndexed { index, v ->
            Box(Modifier.width(25.dp).fillMaxHeight(v).clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                .background(if (index == 5) Paper else Color(0xFF343434)))
        }
    }
}

@Composable
private fun SettingsScreen(
    overlay: Boolean, onOverlay: (Boolean) -> Unit,
    haptics: Boolean, onHaptics: (Boolean) -> Unit,
    animations: Boolean, onAnimations: (Boolean) -> Unit,
    onAbout: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 25.dp)) {
        Spacer(Modifier.height(28.dp))
        SectionHeading("PREFERENCES", "Настройки.", "Всё под тебя.")
        Spacer(Modifier.height(25.dp))
        SmallHeading("ИНТЕРФЕЙС")
        Spacer(Modifier.height(12.dp))
        Column(Modifier.clip(RoundedCornerShape(21.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(21.dp))) {
            ToggleRow("Прозрачность", "Эффект стекла", overlay, onOverlay)
            HorizontalDivider(color = Border, thickness = 1.dp, modifier = Modifier.padding(start = 18.dp))
            ToggleRow("Анимации", "Плавные переходы", animations, onAnimations)
            HorizontalDivider(color = Border, thickness = 1.dp, modifier = Modifier.padding(start = 18.dp))
            ToggleRow("Вибрация", "Отклик на действия", haptics, onHaptics)
        }
        Spacer(Modifier.height(24.dp))
        SmallHeading("ПРИЛОЖЕНИЕ")
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().height(71.dp).clip(RoundedCornerShape(18.dp)).background(Panel)
            .border(1.dp, Border, RoundedCornerShape(18.dp)).clickable(onClick = onAbout).padding(horizontal = 19.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("О приложении", color = Paper, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text("RX Preview • v0.1", color = Subtle, fontSize = 11.sp)
            }
            Spacer(Modifier.weight(1f))
            Text("↗", color = Paper, fontSize = 18.sp)
        }
        Spacer(Modifier.height(30.dp))
        Text("RENSIZE / X     •     UI CONCEPT", color = Color(0xFF585858), fontSize = 9.sp, letterSpacing = 1.6.sp)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun SmallHeading(title: String) {
    Text(title, color = Subtle, fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.8.sp)
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().height(78.dp).clickable { onCheckedChange(!checked) }.padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(title, color = Paper, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = Subtle, fontSize = 11.sp)
        }
        Spacer(Modifier.weight(1f))
        CustomToggle(checked)
    }
}

@Composable
private fun CustomToggle(checked: Boolean) {
    val bg by animateColorAsState(if (checked) Paper else Color(0xFF3C3C3C), label = "toggle-color")
    val position by animateFloatAsState(if (checked) 1f else 0f, label = "toggle-position")
    Box(Modifier.width(46.dp).height(27.dp).clip(CircleShape).background(bg).padding(3.dp)) {
        Box(Modifier.offset(x = (19f * position).dp).size(21.dp).clip(CircleShape)
            .background(if (checked) Ink else Color(0xFFD1D1D1)))
    }
}

@Composable
private fun RXBottomBar(page: Page, onClick: (Page) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Ink)) {
        HorizontalDivider(color = Border, thickness = 1.dp)
        Row(Modifier.fillMaxWidth().height(73.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceAround) {
            Page.entries.forEach { item ->
                val selected = page == item
                val color by animateColorAsState(if (selected) Paper else Color(0xFF737373), label = "tab-color")
                Column(Modifier.weight(1f).fillMaxHeight().clickable { onClick(item) },
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    TabGlyph(item, color)
                    Spacer(Modifier.height(6.dp))
                    Text(item.label, color = color, fontSize = 10.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
private fun TabGlyph(page: Page, color: Color) {
    Canvas(Modifier.size(20.dp)) {
        val s = size.width
        val st = 1.75.dp.toPx()
        when (page) {
            Page.Home -> {
                drawLine(color, Offset(s * .15f, s * .49f), Offset(s * .5f, s * .17f), st, cap = StrokeCap.Round)
                drawLine(color, Offset(s * .5f, s * .17f), Offset(s * .85f, s * .49f), st, cap = StrokeCap.Round)
                drawLine(color, Offset(s * .25f, s * .48f), Offset(s * .25f, s * .85f), st, cap = StrokeCap.Round)
                drawLine(color, Offset(s * .75f, s * .48f), Offset(s * .75f, s * .85f), st, cap = StrokeCap.Round)
                drawLine(color, Offset(s * .25f, s * .85f), Offset(s * .75f, s * .85f), st, cap = StrokeCap.Round)
            }
            Page.Games -> {
                drawRoundRect(color, topLeft = Offset(s * .07f, s * .27f), size = androidx.compose.ui.geometry.Size(s * .86f, s * .53f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(s * .18f), style = Stroke(st))
                drawLine(color, Offset(s * .29f, s * .4f), Offset(s * .29f, s * .67f), st)
                drawLine(color, Offset(s * .2f, s * .54f), Offset(s * .38f, s * .54f), st)
                drawCircle(color, s * .045f, Offset(s * .7f, s * .46f))
                drawCircle(color, s * .045f, Offset(s * .8f, s * .6f))
            }
            Page.Activity -> {
                drawLine(color, Offset(s * .12f, s * .78f), Offset(s * .12f, s * .44f), st * 2.5f, cap = StrokeCap.Round)
                drawLine(color, Offset(s * .39f, s * .78f), Offset(s * .39f, s * .20f), st * 2.5f, cap = StrokeCap.Round)
                drawLine(color, Offset(s * .66f, s * .78f), Offset(s * .66f, s * .51f), st * 2.5f, cap = StrokeCap.Round)
                drawLine(color, Offset(s * .91f, s * .78f), Offset(s * .91f, s * .35f), st * 2.5f, cap = StrokeCap.Round)
            }
            Page.Settings -> {
                drawCircle(color, s * .31f, Offset(s * .5f, s * .5f), style = Stroke(st))
                drawCircle(color, s * .09f, Offset(s * .5f, s * .5f), style = Stroke(st))
                for (i in 0..5) {
                    val a = i * kotlin.math.PI / 3.0
                    val dx = kotlin.math.cos(a).toFloat()
                    val dy = kotlin.math.sin(a).toFloat()
                    drawLine(color, Offset(s * (.5f + .33f * dx), s * (.5f + .33f * dy)),
                        Offset(s * (.5f + .45f * dx), s * (.5f + .45f * dy)), st, cap = StrokeCap.Round)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070707)
@Composable
private fun RXPreview() { RXApp() }
