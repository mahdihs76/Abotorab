package ir.nilva.abotorab.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ir.nilva.abotorab.ApplicationContext
import ir.nilva.abotorab.db.dao.DeliveryDao
import ir.nilva.abotorab.db.model.DeliveryEntity

/**
 * The Room database for this app
 */
@Database(entities = [DeliveryEntity::class], version = 1)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao
//    abstract fun plantDao(): PlantDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(ApplicationContext.context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app")
//                .addCallback(object : RoomDatabase.Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
//                        WorkManager.getInstance(context).enqueue(request)
//                    }
//                })
                .build()
        }
    }
}