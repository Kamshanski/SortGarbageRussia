package edu.kamshanski.sortgarbagerussia.ui

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import edu.kamshanski.sortgarbagerussia.model.localDatabase.ObjectBox
import edu.kamshanski.sortgarbagerussia.model.RecycleRepository
import edu.kamshanski.sortgarbagerussia.model.RepositoryProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class App : Application(), CameraXConfig.Provider, RepositoryProvider {
    @Volatile private var repositoryInstance: RecycleRepository? = null

    override fun onCreate() {
        // https://github.com/objectbox/objectbox-examples/blob/main/android-app-kotlin/src/main/java/io/objectbox/example/kotlin/App.kt
//      if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
//          return // Skip app initialization.
//      }
        super.onCreate()
        ObjectBox.init(applicationContext)
    }

    //https://developer.android.com/reference/androidx/camera/lifecycle/ProcessCameraProvider#getInstance(android.content.Context)
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
    }

    override val repository : RecycleRepository get() {
        if (repositoryInstance == null) {
            synchronized(this) {
                if (repositoryInstance == null) {
                    repositoryInstance = RecycleRepository(this)
                }
            }
        }
        return repositoryInstance!!
    }
}