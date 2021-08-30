package io.github.jwgibanez.cartrack.view

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.By
import android.content.Intent
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import android.content.pm.PackageManager
import androidx.test.uiautomator.Until
import org.junit.Test
import androidx.test.uiautomator.UiObject2




@RunWith(AndroidJUnit4::class)
class UITest {

    private val PACKAGE = "io.github.jwgibanez.cartrack"
    private val LAUNCH_TIMEOUT = 5000L

    private lateinit var device: UiDevice

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(getInstrumentation())

        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage: String = getLauncherPackageName()
        assertThat(launcherPackage, notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)

        // Launch the app
        val context: Context = getApplicationContext()
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(PACKAGE).depth(0)), LAUNCH_TIMEOUT)
    }

    @Test
    fun checkPreconditions() {
        assertThat(device, notNullValue())
    }

    @Test
    fun loginAndLogoutSuccessfully() {
        device.apply {
            // Login
            findObject(By.res(PACKAGE, "username")).text = "admin"
            findObject(By.res(PACKAGE, "password")).text = "pass1234"
            findObject(By.res(PACKAGE, "country_spinner")).click()
            val items: List<UiObject2> = findObjects(
                By.res("android:id/text1").pkg(PACKAGE)
            )
            Thread.sleep(500)
            items[1].click()
            Thread.sleep(500)
            findObject(By.res(PACKAGE, "login")).click()
            // Logout
            Thread.sleep(500)
            device.findObject(By.res(PACKAGE, "action_logout")).click()
        }
    }

    private fun getLauncherPackageName(): String {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        val pm = getApplicationContext<Context>().packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo!!.activityInfo.packageName
    }
}