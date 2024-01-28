plugins {
    id(libs.plugins.androidLibrary.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.protobuf.get().pluginId)
}

android {
    namespace = "com.ihridoydas.sduicompose.storage"

    buildFeatures {
        compose = true
    }
}

  dependencies {
      implementation(projects.common)
      implementation(libs.datastore)
      implementation(libs.protobuf.javaLite)
      implementation(libs.protobuf.kotlinLite)

  }
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("kotlin") {
                    option("lite")
                }
            }
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
