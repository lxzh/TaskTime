package com.lxzh123.ttime

import org.gradle.api.Plugin
import org.gradle.api.Project

class TaskTime implements Plugin<Project> {
    @Override
    void apply(Project project) {
        //添加自定义的监听
        project.getGradle().addListener(new TaskListener())
    }
}
