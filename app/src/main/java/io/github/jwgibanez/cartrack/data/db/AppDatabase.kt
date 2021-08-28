package io.github.jwgibanez.cartrack.data.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.jwgibanez.cartrack.data.Converters
import io.github.jwgibanez.cartrack.data.model.Account
import io.github.jwgibanez.cartrack.data.model.User
import java.util.concurrent.Executors

@Database(entities = [Account::class, User::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "Sample.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // insert the data on the IO Thread
                        ioThread {
                            getInstance(context).accountDao().insert(PREPOPULATE_DATA)
                        }
                    }
                })
                .build()

        fun ioThread(f : () -> Unit) {
            IO_EXECUTOR.execute(f)
        }

        val PREPOPULATE_DATA = listOf(
            Account(username = "admin", password = "pass1234"),
            Account(username = "user1", password = "pass1234"),
            Account(username = "user2", password = "pass1234")
        )
    }
}