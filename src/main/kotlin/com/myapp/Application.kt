package com.myapp

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableEncryptableProperties
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
