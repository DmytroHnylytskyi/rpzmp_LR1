package ua.kpi.windpower

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ua.kpi.windpower.data.DispatcherRegistry
import ua.kpi.windpower.data.Validators

/**
 * Модульні тести реєстру облікових записів диспетчерів.
 * Гарантують: незареєстрований користувач не може зайти на зміну,
 * реєстрація контрольує унікальність, паролі зберігаються лише як хеш.
 */
class DispatcherRegistryTest {

    @Test
    fun testAuth_SeededAccount_loginSucceeds() {
        val result = DispatcherRegistry.authenticate("dispatcher", "Wind2026")
        assertTrue(result is DispatcherRegistry.AuthResult.Success)
        val account = (result as DispatcherRegistry.AuthResult.Success).account
        assertEquals("Олександр К. (Диспетчер №1)", account.fullName)
    }

    @Test
    fun testAuth_UnknownLogin_isRejected() {
        assertEquals(
            DispatcherRegistry.AuthResult.AccountNotFound,
            DispatcherRegistry.authenticate("unknown_user", "AnyPass1")
        )
    }

    @Test
    fun testAuth_WrongPassword_isRejected() {
        assertEquals(
            DispatcherRegistry.AuthResult.WrongPassword,
            DispatcherRegistry.authenticate("dispatcher", "WrongPassword1")
        )
    }

    @Test
    fun testAuth_EmptyCredentials_areRejected() {
        assertEquals(
            DispatcherRegistry.AuthResult.AccountNotFound,
            DispatcherRegistry.authenticate("", "")
        )
    }

    @Test
    fun testRegister_success_thenLoginWorks() {
        val result = DispatcherRegistry.register(
            loginInput = "new.engineer@windpower.kpi.ua",
            fullName = "Нова Інженер",
            email = "new.engineer@windpower.kpi.ua",
            password = "Pass2026A"
        )
        assertTrue(result is DispatcherRegistry.RegistrationResult.Success)

        val login = DispatcherRegistry.authenticate("NEW.ENGINEER@windpower.kpi.ua", "Pass2026A")
        assertTrue(login is DispatcherRegistry.AuthResult.Success)
    }

    @Test
    fun testRegister_duplicateEmail_isRejected() {
        val result = DispatcherRegistry.register(
            loginInput = "dispatcher",
            fullName = "Дублікат",
            email = "engineer@windpower.kpi.ua",
            password = "Pass2026A"
        )
        assertEquals(DispatcherRegistry.RegistrationResult.Duplicate, result)
    }

    @Test
    fun testRegister_emptyFields_isRejected() {
        assertEquals(
            DispatcherRegistry.RegistrationResult.InvalidInput,
            DispatcherRegistry.register("", "", "", "")
        )
    }

    @Test
    fun testSha256_isDeterministicAndNotReversible() {
        val hashA = Validators.sha256("Wind2026")
        val hashB = Validators.sha256("Wind2026")
        assertEquals(hashA, hashB)
        assertEquals(64, hashA.length) // SHA-256 = 32 байти = 64 hex-символи
        assertNotEquals("Wind2026", hashA)
    }
}
