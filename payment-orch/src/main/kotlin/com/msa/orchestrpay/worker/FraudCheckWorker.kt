package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class FraudCheckWorker {

    private val logger = LoggerFactory.getLogger(FraudCheckWorker::class.java)

    @JobWorker(type = "fraud-check", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]
        val amount = job.variablesAsMap["amount"]

        logger.info("=" .repeat(60))
        logger.info(">>> [FraudCheck] Запуск антифрод-проверки")
        logger.info(">>> [FraudCheck] Платёж ID: $paymentId")
        logger.info(">>> [FraudCheck] Сумма проверки: $amount")

        // Имитация проверки
        Thread.sleep(1000)

        // Для демонстрации: случайный результат
        // 40% - approved, 40% - manual_review, 20% - declined
        val fraudResult = when (Random.nextInt(10)) {
            in 0..3 -> "approved"
            in 4..7 -> "manual_review"
            else -> "declined"
        }

        logger.info(">>> [FraudCheck] Результат проверки: ${fraudResult.uppercase()}")

        return mapOf(
            "fraudResult" to fraudResult,
            "fraudCheckTimestamp" to System.currentTimeMillis().toString()
        )
    }
}