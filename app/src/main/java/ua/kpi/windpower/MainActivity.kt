package ua.kpi.windpower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.kpi.windpower.ui.screens.AuthProfileScreen
import ua.kpi.windpower.ui.screens.CalculatorScreen
import ua.kpi.windpower.ui.screens.DashboardScreen
import ua.kpi.windpower.ui.screens.TurbineFleetScreen
import ua.kpi.windpower.ui.theme.WindPowerPlantTheme

/**
 * Головна точка входу мобільного застосунку «AeroGrid • ВЕС Моніторинг».
 *
 * Організовує модульну навігаційну структуру на базі Material 3 Scaffold
 * та NavigationBar (нижня панель навігації для оперативного керування вітропарком).
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WindPowerPlantTheme {
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                val navigationItems = listOf(
                    NavigationItem("Моніторинг", "⚡"),
                    NavigationItem("Калькулятор", "📐"),
                    NavigationItem("Вітропарк", "🌀"),
                    NavigationItem("Диспетчер", "👤")
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Індикатор активного онлайн-з'єднання зі SCADA-сервером
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "AeroGrid • ВЕС Моніторинг",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            navigationItems.forEachIndexed { index, item ->
                                val isSelected = selectedTabIndex == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { selectedTabIndex = index },
                                    icon = {
                                        Text(
                                            text = item.icon,
                                            fontSize = if (isSelected) 20.sp else 16.sp
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTabIndex) {
                            0 -> DashboardScreen()
                            1 -> CalculatorScreen()
                            2 -> TurbineFleetScreen()
                            3 -> AuthProfileScreen()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Модель елемента нижньої панелі навігації.
 *
 * @property title Назва розділу.
 * @property icon Символьна іконка розділу.
 */
private data class NavigationItem(
    val title: String,
    val icon: String
)