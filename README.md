# NoHideApi
This project is used to to build a gradle plugin to make the hide api of android.jar visible to developers

## Usage
Usage
add classpath to your project

classpath "com.github.skyinu.NoHideApi:nohideapiplugin:0.1.1"
then apply the plugin will be ok

plugins {
    id("com.skyinu.nohideapi")
}
## Confirguration
just add options to gradle.properties 

```
nohide.enable=true
```
## Some issue
This project is semi-finished and may not be finished either. The project can only make part of hide api visible because there is a core problem that cannot be solved-
some reference's qualified name can't be correctly resolved