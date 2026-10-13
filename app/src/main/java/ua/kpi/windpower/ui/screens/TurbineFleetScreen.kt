package ua.kpi.windpower.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.kpi.windpower.data.TurbineModel
import ua.kpi.windpower.data.TurbineStatus
import ua.kpi.windpower.ui.theme.AmberWarning
import ua.kpi.windpower.ui.theme.CyanAccent
import ua.kpi.windpower.ui.theme.EmeraldLight
import ua.kpi.windpower.ui.theme.StormAlert

/**
 * Екран диспетчерського моніторингу парку вітрогенераторів (Turbine Fleet Management).
 *
 * Надає деталізовану інформацію щодо кожного агрегату вітропарку:
 * статус підключення до мережі, рівень завантаження генератора,
 * поточні обороти ротора та кут установки лопатей (pitch control).
 */
@Composable
fun TurbineFleetScreen() {
    val turbines = remember {
        listOf(
            TurbineModel(
                id = "WTG-01",
                name = "ВЕС-01 «Борей»",
                model = "Vestas V112-3.0MW",
                ratedPowerKw = 3000.0,
                bladeLengthMeters = 56.0,
                currentSpeedMps = 10.4,
                currentPowerKw = 2150.0,
                rpm = 14.8,
                pitchAngleDeg = 2.4,
                status = TurbineStatus.OPERATING
            ),
            TurbineModel(
                id = "WTG-02",
                name = "ВЕС-02 «Зефір»",
                model = "Nordex N131-3.3MW",
                ratedPowerKw = 3300.0,
                bladeLengthMeters = 65.5,
                currentSpeedMps = 9.8,
                currentPowerKw = 1920.0,
                rpm = 13.2,
                pitchAngleDeg = 3.1,
                status = TurbineStatus.OPERATING
            ),
            TurbineModel(
                id = "WTG-03",
                name = "ВЕС-03 «Стрибог»",
                model = "Enercon E-126-3.5MW",
                ratedPowerKw = 3500.0,
                bladeLengthMeters = 63.0,
                currentSpeedMps = 11.2,
                currentPowerKw = 2680.0,
                rpm = 15.6,
                pitchAngleDeg = 1.8,
                status = TurbineStatus.OPERATING
            ),
            TurbineModel(
                id = "WTG-04",
                name = "ВЕС-04 «Еол»",
                model = "Siemens Gamesa 3.4MW",
                ratedPowerKw = 3400.0,
                bladeLengthMeters = 66.0,
                currentSpeedMps = 2.1,
                currentPowerKw = 0.0,
                rpm = 0.0,
                pitchAngleDeg = 85.0,
                status = TurbineStatus.STANDBY
            ),
            TurbineModel(
                id = "WTG-05",
                name = "ВЕС-05 «Нот»",
                model = "Vestas V126-3.45MW",
                ratedPowerKw = 3450.0,
                bladeLengthMeters = 63.0,
                currentSpeedMps = 0.0,
                currentPowerKw = 0.0,
                rpm = 0.0,
                pitchAngleDeg = 90.0,
                status = TurbineStatus.MAINTENANCE
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Зведений статус вітропарку
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ПАРК ВІТРОГЕНЕРАТОРІВ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "5 турбін встановлено",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "В мережі: 3 / 5",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent
                    )
                }
            }
        }

        items(turbines) { turbine ->
            TurbineCard(turbine = turbine)
        }
    }
}

/**
 * Картка індивідуального вітрогенератора зі шкалою навантаження та телеметрією.
 */
@Composable
private fun TurbineCard(turbine: TurbineModel) {
    val progress = (turbine.currentPowerKw / turbine.ratedPowerKw).toFloat().coerceIn(0f, 1f)

    val statusColor = when (turbine.status) {
        TurbineStatus.OPERATING -> EmeraldLight
        TurbineStatus.STANDBY -> AmberWarning
        TurbineStatus.EMERGENCY_BRAKE -> StormAlert
        TurbineStatus.MAINTENANCE -> Color(0xFF64748B)
        TurbineStatus.OPTIMIZING -> CyanAccent
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = turbine.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${turbine.model} • Лопать: ${turbine.bladeLengthMeters} м",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = turbine.status.title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Шкала відносної генерації від номіналу
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${turbine.currentPowerKw.toInt()} кВт",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
                Text(
                    text = "Номінал: ${turbine.ratedPowerKw.toInt()} кВт",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EmeraldLight,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailParam(title = "Вітер", value = "${turbine.currentSpeedMps} м/с")
                DetailParam(title = "Оберти", value = "${turbine.rpm} об/хв")
                DetailParam(title = "Pitch-кут", value = "${turbine.pitchAngleDeg}°")
            }
        }
    }
}

/**
 * Елемент відображення технічного параметра турбіни.
 */
@Composable
private fun DetailParam(title: String, value: String) {
    Column {
        Text(text = title, fontSize = 11.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
