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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.kpi.windpower.ui.theme.CyanAccent
import ua.kpi.windpower.ui.theme.EmeraldLight
import ua.kpi.windpower.ui.theme.StormAlert

/**
 * Екран управління сесією диспетчера та доступом до телеметрії SCADA.
 *
 * Забезпечує:
 * - Перевірку статусу поточної сесії користувача;
 * - Відображення профілю чергового інженера зміни та параметрів шифрування зв'язку;
 * - Форми безпечної авторизації та реєстрації нового оперативного персоналу;
 * - Валідацію облікових даних та імітацію захищеного запиту автентифікації.
 */
@Composable
fun AuthProfileScreen() {
    var isLoggedIn by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("Олександр К. (Диспетчер №1)") }
    var userRole by remember { mutableStateOf("Головний інженер зміни ВЕС") }

    if (isLoggedIn) {
        DispatcherProfileView(
            name = userName,
            role = userRole,
            onLogout = { isLoggedIn = false }
        )
    } else {
        DispatcherAuthView(
            onLoginSuccess = { name ->
                userName = name
                isLoggedIn = true
            }
        )
    }
}

/**
 * Відображення профілю активного диспетчера вітроелектростанції.
 */
@Composable
private fun DispatcherProfileView(
    name: String,
    role: String,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Картка профілю інженера
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(EmeraldLight.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = role,
                    fontSize = 13.sp,
                    color = EmeraldLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileBadge(label = "Статус", value = "На зміні", color = EmeraldLight)
                    ProfileBadge(label = "SCADA", value = "Рівень 3", color = CyanAccent)
                    ProfileBadge(label = "Зміна", value = "#2 (Денна)", color = Color(0xFFF1F5F9))
                }
            }
        }

        // Параметри безпеки каналів зв'язку
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "ПАРАМЕТРИ ПІДКЛЮЧЕННЯ СКАДА",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )

                InfoLine(label = "Сервер телеметрії", value = "scada.grid.internal (Онлайн)")
                InfoLine(label = "Шифрування каналів", value = "TLS 1.3 / AES-256")
                InfoLine(label = "Останнє опитування датчиків", value = "Щойно (затримка 14 мс)")
                InfoLine(label = "Аварійне сповіщення SMS", value = "Увімкнено (при вітрі > 25 м/с)")
            }
        }

        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = StormAlert
            )
        ) {
            Text("Завершити зміну (Вийти)")
        }
    }
}

/**
 * Форма авторизації та реєстрації оператора в SCADA.
 */
@Composable
private fun DispatcherAuthView(
    onLoginSuccess: (String) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isRegisterMode) "Реєстрація інженера ВЕС" else "Авторизація диспетчера",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldLight
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Службовий доступ до системи телеметрії ВЕС",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isRegisterMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("ПІБ інженера") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Корпоративний Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль (від 6 символів)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Підтвердження пароля") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Позивний або Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль доступу") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = errorMessage, color = StormAlert, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (isRegisterMode) {
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() -> {
                            errorMessage = "Заповніть усі обов'язкові поля"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Паролі не співпадають"
                        }
                        password.length < 6 -> {
                            errorMessage = "Пароль має містити щонайменше 6 символів"
                        }
                        else -> {
                            errorMessage = ""
                            coroutineScope.launch {
                                isLoading = true
                                delay(900)
                                isLoading = false
                                onLoginSuccess(name)
                            }
                        }
                    }
                } else {
                    if (login.isBlank() || password.isBlank()) {
                        errorMessage = "Введіть логін та пароль"
                    } else {
                        errorMessage = ""
                        coroutineScope.launch {
                            isLoading = true
                            delay(900)
                            isLoading = false
                            onLoginSuccess(login)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight, contentColor = Color(0xFF0F172A))
        ) {
            Text(if (isRegisterMode) "Зареєструвати інженера" else "Увійти на зміну", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Швидкий вхід у демонстраційному режимі
        TextButton(
            onClick = { onLoginSuccess("Черговий диспетчер (Демо)") }
        ) {
            Text("Швидкий вхід у демо-режимі", color = CyanAccent)
        }

        TextButton(
            onClick = {
                isRegisterMode = !isRegisterMode
                errorMessage = ""
            }
        ) {
            Text(
                if (isRegisterMode) "Вже маєте обліковий запис? Увійти" else "Новий інженер? Зареєструватися",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator(color = EmeraldLight)
        }
    }
}

/**
 * Бейдж статусу в профілі диспетчера.
 */
@Composable
private fun ProfileBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

/**
 * Інформаційний рядок системних налаштувань.
 */
@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
