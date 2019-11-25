# TaskTime

## What is it?

Sometimes we wonder what Gradle is doing, and why the building isn't over yet for "half-day".

TaskTime(ttime) is a simple gradle plugin that counts the build time for each gradle task in building.

And then you can find out what's taking time.

## Principle

This process mainly uses two key interfaces of gradle and one method:

- `org.gradle.api.execution.TaskExecutionListener`

This interface defines the callbacks before and after the execution of each task: `beforeExecute()` and `afterExecute() `

- `org.gradle.BuildListener`

This interface mainly defines callbacks for build start and build finish (and of course some other callbacks: call-start configured, all projects loaded, etc.): `buildStarted()` and `buildFinished()`

- `addListener()` method for gradle object in `org.gradle.api.project`

## Integration

### 1. Adding to project

Gradle dependency like following(jcenter) in root build.gradle:

```
buildscript {
    ...
    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.1'
        //Add classpath for ttime
        classpath 'com.lxzh123:ttime:0.1.0'
    }
}
```

### 2. Apply plugin

Apply plugin in module project like `com.android.application`

```
apply plugin: 'com.android.application'
apply plugin: 'ttime'
```

## Build your module any way you like

Output the elapsed time for each task as shown below:

```
> Task :app:preBuild UP-TO-DATE
:app:preBuild took 15ms

> Task :app:preDebugBuild UP-TO-DATE
:app:preDebugBuild took 1ms

> Task :app:checkDebugManifest UP-TO-DATE
:app:checkDebugManifest took 208ms

> Task :app:generateDebugBuildConfig UP-TO-DATE
:app:generateDebugBuildConfig took 49ms

> Task :app:prepareLintJar UP-TO-DATE
:app:prepareLintJar took 360ms

> Task :app:prepareLintJarForPublish UP-TO-DATE
:app:prepareLintJarForPublish took 20ms

> Task :app:compileDebugAidl NO-SOURCE
:app:compileDebugAidl took 21ms

> Task :app:compileDebugRenderscript NO-SOURCE
:app:compileDebugRenderscript took 116ms

> Task :app:generateDebugSources UP-TO-DATE
:app:generateDebugSources took 0ms

==================================================================================================================
Task timings(no sort):                                                                              | Time elapsed   
==================================================================================================================
:app:preBuild                                                                                       | 15ms           
------------------------------------------------------------------------------------------------------------------
:app:checkDebugManifest                                                                             | 208ms          
------------------------------------------------------------------------------------------------------------------
:app:compileDebugAidl                                                                               | 21ms           
------------------------------------------------------------------------------------------------------------------
:app:prepareLintJar                                                                                 | 360ms          
------------------------------------------------------------------------------------------------------------------
:app:compileDebugRenderscript                                                                       | 116ms          
------------------------------------------------------------------------------------------------------------------
:app:generateDebugBuildConfig                                                                       | 49ms           
------------------------------------------------------------------------------------------------------------------
:app:prepareLintJarForPublish                                                                       | 20ms           
------------------------------------------------------------------------------------------------------------------

==================================================================================================================
Task timings(sorted):                                                                               | Time elapsed   
==================================================================================================================
:app:preBuild                                                                                       | 15ms           
------------------------------------------------------------------------------------------------------------------
:app:prepareLintJarForPublish                                                                       | 20ms           
------------------------------------------------------------------------------------------------------------------
:app:compileDebugAidl                                                                               | 21ms           
------------------------------------------------------------------------------------------------------------------
:app:generateDebugBuildConfig                                                                       | 49ms           
------------------------------------------------------------------------------------------------------------------
:app:compileDebugRenderscript                                                                       | 116ms          
------------------------------------------------------------------------------------------------------------------
:app:checkDebugManifest                                                                             | 208ms          
------------------------------------------------------------------------------------------------------------------
:app:prepareLintJar                                                                                 | 360ms          
------------------------------------------------------------------------------------------------------------------



BUILD SUCCESSFUL in 22s
4 actionable tasks: 4 up-to-date
```