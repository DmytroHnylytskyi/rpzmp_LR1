package ua.kpi.windpower.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.kpi.windpower.data.DispatcherRegistry
import ua.kpi.windpower.data.Validators
import ua.kpi.windpower.ui.components.AppTextField
import ua.kpi.windpower.ui.theme.EmeraldLight
import ua.kpi.windpower.ui.theme.StormAlert

/** Мінімальна довжина пароля диспетчера. */
private const val PASSWORD_MIN_LENGTH = 6

/**
 * Екран управління сесією диспетчера та доступом до телеметрії SCADA.
 *
 * Забезпечує:
 * - форми входу та реєстрації оперативного персоналу (стартовий екран сесії);
 * - клієнтську валідацію даних (формат email, довжина та збіг паролів);
 * - індикацію стану обробки запиту авторизації (CircularProgressIndicator);
 * - робочий кабінет чергового інженера зміни з параметрами захищеного SCADA-каналу.
 *
 * UI розбитий на окремі composable-функції (заголовок, поля, кнопки, помилки)
 * та адаптований під різні розміри екранів: форма обмежена по ширині та
 * центрується на великих дисплеях, сітка бейджів перемикається Row/Column.
 */
@Composable
fun AuthProfileScreen() {
    // Сесія починається з екрану входу — демонструє повний цикл авторизації
    var isLoggedIn by remember { mutableStateOf(false) }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Картка профілю інженера
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚡",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldLight
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Адаптивна сітка бейджів: на широких екранах — рядок, на вузьких — колонка
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val badges: List<Pair<String, Pair<String, Color>>> = listOf(
                        "Статус" to ("На зміні" to EmeraldLight),
                        "SCADA" to ("Рівень 3" to MaterialTheme.colorScheme.secondary),
                        "Зміна" to ("#2 (Денна)" to MaterialTheme.colorScheme.onSurface)
                    )
                    if (maxWidth > 360.dp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            badges.forEach { (label, value) ->
                                ProfileBadge(label, value.first, value.second)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            badges.forEach { (label, value) ->
                                ProfileBadge(label, value.first, value.second)
                            }
                        }
                    }
                }
            }
        }

        // Параметри безпеки каналів зв'язку
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ПАРАМЕТРИ ПІДКЛЮЧЕННЯ SCADA",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                InfoLine("Сервер телеметрії", "scada.grid.internal (Онлайн)")
                InfoLine("Шифрування каналів", "TLS 1.3 / AES-256")
                InfoLine("Останнє опитування давачів", "Щойно (затримка 14 мс)")
                InfoLine("Аварійне сповіщення SMS", "Увімкнено (при вітрі > 25 м/с)")
            }
        }

        OutlinedButton(
            onClick = onLogout,
            shape = MaterialTheme.shapes.medium,
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
 *
 * Валідація виконується після першої спроби відправки (прапорець [attempted]):
 * повідомлення про помилки відображається під відповідним полем.
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
    var attempted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var authFailure by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // --- Клієнтська валідація полів  ---
    val nameError = if (isRegisterMode && attempted && name.isBlank()) "Обов'язкове поле" else null
    val emailError = when {
        !isRegisterMode || !attempted || email.isBlank() -> null
        !Validators.isValidEmail(email) -> "Некоректний формат Email"
        else -> null
    }
    val lengthError = if (isRegisterMode && attempted && password.length < PASSWORD_MIN_LENGTH) {
        "Пароль має містити щонайменше $PASSWORD_MIN_LENGTH символів"
    } else null
    val matchError = if (isRegisterMode && attempted && confirmPassword.isNotBlank() && password != confirmPassword) {
        "Паролі не співпадають"
    } else null
    val credentialsError = if (!isRegisterMode && attempted && (login.isBlank() || password.isBlank())) {
        "Введіть логін та пароль"
    } else null

    val hasValidationErrors =
        nameError != null || emailError != null || lengthError != null || matchError != null ||
            credentialsError != null || (isRegisterMode && attempted && password.isBlank())

    // Форма адаптується до розміру екрану: на планшетах обмежується по ширині
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val formWidth = if (maxWidth > 600.dp) 480.dp else maxWidth

        Column(
            modifier = Modifier.width(formWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isRegisterMode) "Реєстрація інженера ВЕС" else "Авторизація диспетчера",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Службовий доступ до системи телеметрії ВЕС",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (isRegisterMode) {
                AppTextField(
                    value = name,
                    onValueChange = { name = it; authFailure = null },
                    label = "ПІБ інженера",
                    errorMessage = nameError
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = email,
                    onValueChange = { email = it; authFailure = null },
                    label = "Корпоративний Email (він же логін)",
                    helperText = "Формат: engineer@windpower.kpi.ua",
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = password,
                    onValueChange = { password = it; authFailure = null },
                    label = "Пароль",
                    helperText = "Від $PASSWORD_MIN_LENGTH символів",
                    errorMessage = lengthError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; authFailure = null },
                    label = "Підтвердження пароля",
                    errorMessage = matchError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
            } else {
                AppTextField(
                    value = login,
                    onValueChange = { login = it; authFailure = null },
                    label = "Позивний або Email",
                    errorMessage = credentialsError
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = password,
                    onValueChange = { password = it; authFailure = null },
                    label = "Пароль доступу",
                    errorMessage = credentialsError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            // Повідомлення про результат автентифікації/реєстрації (позаклієнтські перевірки)
            if (authFailure != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = authFailure ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = StormAlert
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    attempted = true
                    if (!hasValidationErrors) {
                        // Імітація запиту до SCADA-серверу автентифікації через корутину
                        coroutineScope.launch {
                            isLoading = true
                            delay(900)
                            isLoading = false
                            if (isRegisterMode) {
                                // Реєстрація облікового запису у реєстрі персоналу ВЕС (email виступає логіном)
                                when (val result = DispatcherRegistry.register(
                                    loginInput = email,
                                    fullName = name,
                                    email = email,
                                    password = password
                                )) {
                                    is DispatcherRegistry.RegistrationResult.Success ->
                                        onLoginSuccess(result.account.fullName)
                                    DispatcherRegistry.RegistrationResult.Duplicate ->
                                        authFailure = "Обліковий запис з таким Email уже зареєстровано"
                                    else -> authFailure = "Заповніть усі обов'язкові поля"
                                }
                            } else {
                                // Вхід лише для зареєстрованих диспетчерів (перевірка пароля за SHA-256 хешем)
                                when (val result = DispatcherRegistry.authenticate(login, password)) {
                                    is DispatcherRegistry.AuthResult.Success ->
                                        onLoginSuccess(result.account.fullName)
                                    DispatcherRegistry.AuthResult.AccountNotFound ->
                                        authFailure =
                                            "Диспетчера з таким позивним/email не знайдено — зареєструйтеся"
                                    DispatcherRegistry.AuthResult.WrongPassword ->
                                        authFailure = "Невірний пароль доступу"
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (isRegisterMode) "Зареєструвати інженера" else "Увійти на зміну",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Підказка демонстраційного облікового запису чергового інженера
            Text(
                text = "Демонстраційний доступ: dispatcher / Wind2026",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = {
                    isRegisterMode = !isRegisterMode
                    attempted = false
                    authFailure = null
                }
            ) {
                Text(
                    text = if (isRegisterMode) "Вже маєте обліковий запис? Увійти" else "Новий інженер? Зареєструватися",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(color = EmeraldLight)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

}

/**
 * Бейдж статусу в профілі диспетчера.
 */
@Composable
private fun ProfileBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
