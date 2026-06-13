package com.msa.orchestrpay

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentOrchestratorApplication

fun main(args: Array<String>) {
    runApplication<PaymentOrchestratorApplication>(*args)
}