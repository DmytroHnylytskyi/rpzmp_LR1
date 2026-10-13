package ua.kpi.windpower.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.kpi.windpower.data.WindPowerCalculator
import ua.kpi.windpower.ui.components.WindTurbineVisualizer
import ua.kpi.windpower.ui.theme.AmberWarning
import ua.kpi.windpower.ui.theme.CyanAccent
import ua.kpi.windpower.ui.theme.EmeraldLight
import ua.kpi.windpower.ui.theme.StormAlert
import kotlin.math.roundToInt

/**
 * Екран оперативного моніторингу та телеметрії вітрової електростанції (Dashboard).
 *
 * Відображає:
 * - Сумарну миттєву потужність всього вітропарку (МВт);
 * - Анімовану векторну модель головної турбіни із синхронізованою швидкістю обертання ротора;
 * - Ключові телеметричні показники (швидкість вітру, частоту обертання, кут атаки лопатей);
 * - Інтерактивну панель симуляції вітрового навантаження зі швидкими метеорологічними пресетами;
 * - Автоматичний інженерний статус режиму роботи обладнання.
 */
@Composable
fun DashboardScreen() {
    var windSpeed by remember { mutableFloatStateOf(9.5f) }
    val calculator = remember { WindPowerCalculator() }

    // Розрахунок поточної генерації одиничної турбіни (параметри Vestas V112)
    val singleTurbinePowerKw = calculator.calculatePowerAdvanced(
        windSpeedInput = windSpeed.toString(),
        bladeRadiusInput = "52.0",
        cpInput = "43.0"
    )

    // Сумарна генерація вітропарку з 8 установок у мегаватах (МВт)
    val totalParkPowerMw = (singleTurbinePowerKw * 8 / 1000.0 * 100).roundToInt() / 100.0

    // Плавна анімація числових значень потужності
    val animatedTurbinePower by animateFloatAsState(
        targetValue = singleTurbinePowerKw.toFloat(),
        animationSpec = tween(durationMillis = 500),
        label = "TurbinePowerAnimation"
    )

    // Розрахунок частоти обертання ротора (об/хв)
    val rpm = when {
        windSpeed < 3.0f -> 0.0
        windSpeed > 25.0f -> 0.0
        else -> (windSpeed * 1.35).coerceAtMost(18.0)
    }

    // Динамічний колір системного статусу
    val statusColor by animateColorAsState(
        targetValue = when {
            windSpeed < 3.0f -> AmberWarning
            windSpeed > 25.0f -> StormAlert
            else -> EmeraldLight
        },
        label = "OperatingStatusColorAnimation"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Інформаційна картка сумарної потужності генерації
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "СУМАРНА ПОТУЖНІСТЬ ПАРКУ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when {
                                windSpeed < 3.0f -> "ШТИЛЬ"
                                windSpeed > 25.0f -> "ШТОРМ"
                                else -> "ГЕНЕРАЦІЯ"
                            },
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$totalParkPowerMw",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "МВт",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Одинична турбіна: ${(animatedTurbinePower * 10).roundToInt() / 10f} кВт • 8 турбін у роботі",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Векторна модель турбіни з живим обертанням ротора
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Турбіна ВЕС-01 (Vestas V112 / 3.0 MW)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                WindTurbineVisualizer(
                    windSpeed = windSpeed,
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Сітка ключових телеметричних параметрів
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TelemetryMetric(
                        label = "Швидкість вітру",
                        value = "${(windSpeed * 10).roundToInt() / 10f} м/с",
                        accentColor = CyanAccent
                    )
                    TelemetryMetric(
                        label = "Оберти ротора",
                        value = "${(rpm * 10).roundToInt() / 10.0} об/хв",
                        accentColor = EmeraldLight
                    )
                    TelemetryMetric(
                        label = "Кут атаки",
                        value = if (windSpeed > 25f) "89° (флюг)" else "3.2°",
                        accentColor = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Інтерактивне регулювання вітрового потоку (Слайдер та метеопресети)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Симуляція поривів вітру",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${(windSpeed * 10).roundToInt() / 10f} м/с",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                }

                Slider(
                    value = windSpeed,
                    onValueChange = { windSpeed = it },
                    valueRange = 0f..30f,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldLight,
                        activeTrackColor = EmeraldLight,
                        inactiveTrackColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Кнопки швидкого встановлення типових погодних умов
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeatherPresetButton(
                        title = "Штиль (1.5)",
                        isSelected = windSpeed == 1.5f,
                        onClick = { windSpeed = 1.5f },
                        modifier = Modifier.weight(1f)
                    )
                    WeatherPresetButton(
                        title = "Бриз (7.0)",
                        isSelected = windSpeed == 7.0f,
                        onClick = { windSpeed = 7.0f },
                        modifier = Modifier.weight(1f)
                    )
                    WeatherPresetButton(
                        title = "Номінал (12.5)",
                        isSelected = windSpeed == 12.5f,
                        onClick = { windSpeed = 12.5f },
                        modifier = Modifier.weight(1f)
                    )
                    WeatherPresetButton(
                        title = "Шторм (27.0)",
                        isSelected = windSpeed == 27.0f,
                        onClick = { windSpeed = 27.0f },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Експлуатаційний стан системи авторегулювання та захисту
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = statusColor.copy(alpha = 0.12f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = calculator.getOperatingStatus(windSpeed.toString()),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Допоміжний компонент для відображення одиничного показника телеметрії.
 */
@Composable
private fun TelemetryMetric(label: String, value: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

/**
 * Кнопка швидкого вибору погодного пресету вітрового потоку.
 */
@Composable
private fun WeatherPresetButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
