package pipeline

import io.poyarzun.concoursedsl.domain.Pipeline
import io.poyarzun.concoursedsl.domain.Task
import io.poyarzun.concoursedsl.dsl.*

val pipe = Pipeline().apply {
    resource("concourse-dsl-source", "git") {
        source = mapOf(
            "uri" to "git@github.com:Logiraptor/concourse-dsl",
            "private_key" to "((github-deploy-key))"
        )
    }

    job("Test") {
        plan {
            get("concourse-dsl-source") {
                trigger = true
            }
            task("run-tests") {
                config = Task(
                    platform = "linux",
                    imageResource = Task.Resource(
                        type = "docker-image",
                        source = mapOf(
                            "resource" to "maven"
                        )
                    ),
                    run = Task.RunConfig("/bin/sh", args = mutableListOf("-c", """
                        cd concourse-dsl-source
                        ./gradlew test
                    """.trimIndent())),
                    inputs = mutableListOf(Task.Input("concourse-dsl-source"))
                )
            }
        }
    }
}

fun main(args: Array<String>) {
    println(generateYML(pipe))
}