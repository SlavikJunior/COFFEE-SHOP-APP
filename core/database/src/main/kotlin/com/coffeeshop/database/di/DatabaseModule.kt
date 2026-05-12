package com.coffeeshop.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.coffeeshop.database.common.CoffeeShopDatabase
import com.coffeeshop.database.common.TypeConverter
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeeshop.di.qualifiers.DispatcherIO
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher

@Module
object DatabaseModule {

    @[Provides DatabaseScope]
    fun provideCoffeeShopDatabase(
        typeConverter: TypeConverter,
        @DispatcherIO dispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context
    ): CoffeeShopDatabase = Room.databaseBuilder(
        context = context,
        klass = CoffeeShopDatabase::class.java,
        name = CoffeeShopDatabase.COFFEE_SHOP_DATABASE_NAME
    )
        .addTypeConverter(typeConverter)
        .setQueryCoroutineContext(dispatcher)
        .setJournalMode(journalMode = RoomDatabase.JournalMode.AUTOMATIC)
        .build()
}