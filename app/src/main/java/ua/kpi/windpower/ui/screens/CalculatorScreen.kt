package ua.kpi.windpower.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.kpi.windpower.data.WindPowerCalculator
import ua.kpi.windpower.ui.theme.CyanAccent
import ua.kpi.windpower.ui.theme.EmeraldLight
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Екран інженерного калькулятора аеродинамічного моделювання ВЕС.
 *
 * Дозволяє інженеру або студенту розрахувати прогнозовану електричну та механічну
 * потужність вітрогенератора за вхідними фізичними параметрами:
 * - швидкістю вітрового потоку v (м/с);
 * - довжиною лопаті / радіусом ротора R (м);
 * - аеродинамічним коефіцієнтом потужності Cp (до межі Беца 59.3%).
 */
@Composable
fun CalculatorScreen() {
    var windSpeed by remember { mutableStateOf("11.5") }
    var bladeRadius by remember { mutableStateOf("54.0") }
    var cpEfficiency by remember { mutableStateOf("43.5") }

    val calculator = remember { WindPowerCalculator() }

    val radiusVal = bladeRadius.toDoubleOrNull() ?: 0.0

    // Розрахунок площі ометання ротора: S = π * R² (м²)
    val sweptArea = if (radiusVal > 0) PI * radiusVal.pow(2) else 0.0

    // Розрахунок виробленої потужності (кВт)
    val powerKw = calculator.calculatePowerAdvanced(windSpeed, bladeRadius, cpEfficiency)
    val powerMw = (powerKw / 1000.0 * 100).roundToInt() / 100.0

    // Прогноз добового виробітку енергії: E = P * 24 год (МВт·год)
    val dailyEnergyMwh = ((powerMw * 24.0) * 10).roundToInt() / 10.0

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Картка фізичної моделі та формули Беца
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ФІЗИЧНА МОДЕЛЬ БЕЦА",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "P = ½ • ρ • S • v³ • Cp",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "де ρ=1.225 кг/м³ (густина повітря), S=πR² (площа ротора), v — швидкість вітру, Cp — аеродинамічний коефіцієнт.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Швидкі пресети типових промислових турбін
        Text(
            text = "Типові конфігурації промислових ВЕУ:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetChip(
                label = "2.0 MW (R=45м)",
                onClick = { bladeRadius = "45.0"; cpEfficiency = "42.0" },
                modifier = Modifier.weight(1f)
            )
            PresetChip(
                label = "3.4 MW (R=54м)",
                onClick = { bladeRadius = "54.0"; cpEfficiency = "43.5" },
                modifier = Modifier.weight(1f)
            )
            PresetChip(
                label = "5.0 MW (R=68м)",
                onClick = { bladeRadius = "68.0"; cpEfficiency = "46.0" },
                modifier = Modifier.weight(1f)
            )
        }

        // Поля введення інженерних параметрів
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = windSpeed,
                    onValueChange = { windSpeed = it },
                    label = { Text("Швидкість вітрового потоку v (м/с)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bladeRadius,
                    onValueChange = { bladeRadius = it },
                    label = { Text("Радіус вітрового колеса R (м)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cpEfficiency,
                    onValueChange = { cpEfficiency = it },
                    label = { Text("Коефіцієнт потужності Cp (%) [макс. 59.3%]") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Картка результатів моделювання
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "РЕЗУЛЬТАТ РОЗРАХУНКУ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResultRowItem(label = "Генерація", value = "$powerKw кВт", highlight = true)
                    ResultRowItem(label = "В мегаватах", value = "$powerMw МВт", highlight = true)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResultRowItem(label = "Площа ометання S", value = "${sweptArea.roundToInt()} м²")
                    ResultRowItem(label = "Добовий виробіток", value = "$dailyEnergyMwh МВт•год")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Статус: ${calculator.getOperatingStatus(windSpeed)}",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

/**
 * Чіп попередньо визначеної конфігурації генератора.
 */
@Composable
private fun PresetChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Рядок результату розрахунку з назвою показника та значенням.
 */
@Composable
private fun ResultRowItem(label: String, value: String, highlight: Boolean = false) {
    Column {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8))
        Text(
            text = value,
            fontSize = if (highlight) 18.sp else 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) EmeraldLight else Color(0xFFF1F5F9)
        )
    }
}
