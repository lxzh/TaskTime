package com.lxzh123.ttime

import org.gradle.api.Plugin
import org.gradle.api.Project

class TaskTime implements Plugin<Project> {
    @Override
    void apply(Project project) {
        //添加自定义的监听
        project.getGradle().addListener(new TaskListener())
        println("Counting the build time for each task in building (Version:v0.1.2)")
    }
}
