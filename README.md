# krpc-plugin

Generate kotlin multiplatform RPC code from .proto files.

Krpc base on [wire](https://github.com/square/wire).

## Usage

### Use plugin

```Kotlin
// build.gradle.kts
plugins {
    id("org.szkug.krpc") version "<version>"
}

wire {
    // wire configuration
}
```

### Import Schema Handler

```Kotlin
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("org.szkug.krpc:schema:<version>")
  }
}
```

Then configuration WireExtension schema handler with custom.

```Kotlin
// build.gradle.kts
wire {
    custom {
        schemaHandlerFactory = KrpcSchemaHandlerFactory.client()
        out = "${layout.buildDirectory.asFile.get()}/generated"
    }
    // wire configuration
}

// must import runtime without plugin
dependencies {
    implementation("org.szkug.krpc:runtime:<version>")
}
```

## Sample

```proto
message ActiveReq {
  string os = 1; // System
  string brd = 2; // Device
  string ver = 3; // App Version
  optional int64 uid = 4; // User id if logged in
}

message ActiveResp {}

service ActiveService {

  rpc report(ActiveReq) returns (ActiveResp);

}
```

This proto file will generate kotlin code like:

```Kotlin
interface KrpcActiveService {
    fun report(req: ActiveReq): ActiveResp
}
```
