package ua.kpi.windpower.data

/**
 * Реєстр облікових записів оперативного персоналу ВЕС.
 * Вхід на зміну можливий лише за обліковим записом, створеним через
 * форму реєстрації; паролі зберігаються у вигляді SHA-256 хешів ([Validators.sha256]).
 */
object DispatcherRegistry {

    /** Результат спроби входу на зміну. */
    sealed interface AuthResult {
        /** Успішна автентифікація — обліковий запис диспетчера. */
        data class Success(val account: DispatcherAccount) : AuthResult

        /** Облікового запису з таким позивним/email не існує. */
        data object AccountNotFound : AuthResult

        /** Пароль не відповідає обліковому запису. */
        data object WrongPassword : AuthResult
    }

    /** Результат реєстрації нового облікового запису. */
    sealed interface RegistrationResult {
        /** Реєстрація успішна — повертає створений запис. */
        data class Success(val account: DispatcherAccount) : RegistrationResult

        /** Позивний (або email) вже зайнятий іншим інженером. */
        data object Duplicate : RegistrationResult

        /** Не заповнені обов'язкові поля. */
        data object InvalidInput : RegistrationResult
    }

    /**
     * Обліковий запис диспетчера ВЕС.
     *
     * @property login Позивний або корпоративний email для входу.
     * @property fullName ПІБ інженера (відображається в кабінеті).
     * @property passwordHash SHA-256 хеш пароля доступу.
     */
    data class DispatcherAccount(
        val login: String,
        val fullName: String,
        val email: String,
        val passwordHash: String
    )

    private val _accounts: androidx.compose.runtime.snapshots.SnapshotStateList<DispatcherAccount> =
        androidx.compose.runtime.mutableStateListOf(
            // Базовий обліковий запис чергового інженера
            DispatcherAccount(
                login = "dispatcher",
                fullName = "Олександр К. (Диспетчер №1)",
                email = "engineer@windpower.kpi.ua",
                passwordHash = Validators.sha256("Wind2026")
            )
        )

    /** Активні облікові записи персоналу (спостережувані для тестів/профілю). */
    val accounts: List<DispatcherAccount> get() = _accounts

    /**
     * Автентифікація диспетчера за позивним (або email) та паролем.
     *
     * @return [AuthResult.Success] з обліковим записом, [AuthResult.AccountNotFound]
     *         якщо запису немає, [AuthResult.WrongPassword] при невірному паролі.
     */
    fun authenticate(loginInput: String, password: String): AuthResult {
        val key = loginInput.trim()
        val account = _accounts.firstOrNull {
            it.login.equals(key, ignoreCase = true) || it.email.equals(key, ignoreCase = true)
        } ?: return AuthResult.AccountNotFound

        return if (account.passwordHash == Validators.sha256(password)) {
            AuthResult.Success(account)
        } else {
            AuthResult.WrongPassword
        }
    }

    /**
     * Реєструє нового інженера зміни.
     *
     * @return [RegistrationResult.Success] з новим записом, [RegistrationResult.Duplicate]
     *         якщо позивний/email вже зайняті, [RegistrationResult.InvalidInput] при
     *         порожніх обов'язкових полях.
     */
    fun register(
        loginInput: String,
        fullName: String,
        email: String,
        password: String
    ): RegistrationResult {
        val trimmedLogin = loginInput.trim()
        val trimmedEmail = email.trim()
        if (trimmedLogin.isBlank() || fullName.isBlank() || trimmedEmail.isBlank() || password.isBlank()) {
            return RegistrationResult.InvalidInput
        }
        val exists = _accounts.any {
            it.login.equals(trimmedLogin, ignoreCase = true) || it.email.equals(trimmedEmail, ignoreCase = true)
        }
        if (exists) return RegistrationResult.Duplicate

        val account = DispatcherAccount(
            login = trimmedLogin,
            fullName = fullName.trim(),
            email = trimmedEmail,
            passwordHash = Validators.sha256(password)
        )
        _accounts.add(account)
        return RegistrationResult.Success(account)
    }
}
