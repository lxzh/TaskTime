package com.lxzh123.ttime

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class TaskListener implements TaskExecutionListener, BuildListener {
    private long clock                 //用于记录每个task执行所花的时间
    private long start                 //用于记录所有task执行所花的时间
    private def timings = new HashMap<String, Long>() //存储所有task和其所发时间的对应关系
    private def final MIN_COST = 5      //展示统计数据的下限 (小于此值时不输出统计数据)

    /**
     * 每个task执行之前调用
     */
    @Override
    void beforeExecute(Task task) {
        clock = System.currentTimeMillis()
    }

    /**
     * 每个task执行后调用
     * @param task
     * @param state
     */
    @Override
    void afterExecute(Task task, TaskState state) {
        long ms = System.currentTimeMillis() - clock
        timings.put(task.path, ms)
        //输出当前task的执行时间
        task.project.logger.warn "${task.path} took ${ms}ms"
    }

    /**
     * build开始时调用
     * @param gradle
     */
    @Override
    void buildStarted(Gradle gradle) {
        start = System.currentTimeMillis()
    }

    /**
     * build结束时调用 (所有task结束时调用)
     * @param result
     */
    @Override
    void buildFinished(BuildResult result) {
        //输出统计数据
        outputHeader(String.format("%-100s| %-15s", "Task timings(no sort):", "Time elapsed"))
        outputProfile(timings.iterator())
        //输出排序后的统计数据
        outputHeader(String.format("%-100s| %-15s", "Task timings(sorted): ", "Time elapsed"))
        outputProfile(sortProfileData(timings).iterator())
        println("\n")
//        uploadReport()
    }

    void outputHeader(String headerMessage) {
        println("")
        println("="*114)
        println(headerMessage)
        println("="*114)
    }

    /**
     * 输出收集的数据
     * @param it
     */
    void outputProfile(Iterator<Map.Entry<String, Long>> it) {
        for (entry in it) {
            if (entry.value >= MIN_COST) {
                printf("%-100s| %-15s\n", entry.key, entry.value + "ms")
                println("-"*114)
            }
        }
    }

    /**
     * 对task所花费的时间进行排序
     * @param profileData
     * @return
     */
    List<Map<String, Long>> sortProfileData(Map<String, Long> profileData) {
        List<Map.Entry<String, Long>> data = new ArrayList<>()
        for (timing in profileData) data.add(timing)
        Collections.sort(data, new Comparator<Map.Entry<String, Long>>() {
            @Override
            int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                if (o1.value > o2.value) return 1
                else if (o1.value < o2.value) return -1
                return 0
            }
        })
        return data
    }

    //将收集的数据上传到服务器做分析 (http-builder-ng)
    void uploadReport() {
        HttpBuilder.configure {
            request.uri = "http://xxxx:xx"
        }.postAsync {
            request.uri.path = '/time'
            request.body = ['timings': timings, 'user.name': System.getProperty("user.name"), "total_time": start.timeInMs]
            request.contentType = 'application/json'
            response.success { formServer, body -> //body => groovy.json.internal.LazyMap  (服务端相应类型Content-Type为application/json)
                println "POST Success: ${formServer.statusCode}, ${formServer.message}, ${body.getClass()}; code=${body.get('code')}, message=${body.get('message')}"
            }
            response.failure { formServer, errorMessage -> //errorMessage => byte[]
                println "POST Failure: ${formServer.statusCode}, ${formServer.message}, errorMessage=${new String(errorMessage)}"
            }
        }
    }

    @Override
    void settingsEvaluated(Settings settings) {}

    @Override
    void projectsLoaded(Gradle gradle) {}

    @Override
    void projectsEvaluated(Gradle gradle) {}
}
