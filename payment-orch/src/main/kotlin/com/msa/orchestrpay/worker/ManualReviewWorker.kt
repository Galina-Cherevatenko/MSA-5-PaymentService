package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ManualReviewWorker {

    private val logger = LoggerFactory.getLogger(ManualReviewWorker::class.java)

    @JobWorker(type = "manual-review", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]

        logger.info("=" .repeat(60))
        logger.info(">>> [ManualReview] Ручная проверка оператором")
        logger.info(">>> [ManualReview] Платёж ID: $paymentId")
        logger.info(">>> [ManualReview] Ожидание решения оператора...")

        // Имитация ожидания оператора (для демо - 3 секунды)
        // В реальности здесь был бы user task с ожиданием до 20 минут
        Thread.sleep(3000)

        // Симуляция: 80% одобрение, 20% отказ
        val manualReviewResult = if (Random.nextInt(10) < 8) "approved" else "declined"

        logger.info(">>> [ManualReview] Решение оператора: ${manualReviewResult.uppercase()}")

        return mapOf(
            "manualReviewResult" to manualReviewResult,
            "manualReviewTimestamp" to System.currentTimeMillis().toString()
        )
    }
}