package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationWorker {

    private val logger = LoggerFactory.getLogger(NotificationWorker::class.java)

    @JobWorker(type = "send-notification", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]
        val paymentStatus = job.variablesAsMap["paymentStatus"]?.toString() ?: "UNKNOWN"

        logger.info("=" .repeat(60))
        logger.info(">>> [Notification] Отправка уведомления клиенту")
        logger.info(">>> [Notification] Платёж ID: $paymentId")
        logger.info(">>> [Notification] Статус: $paymentStatus")

        // Имитация отправки
        Thread.sleep(200)

        val channel = when (paymentStatus) {
            "SETTLED" -> "Email + Push"
            "RELEASED", "REFUNDED" -> "Email + SMS"
            else -> "Email"
        }

        logger.info(">>> [Notification] Канал отправки: $channel")
        logger.info(">>> [Notification] Уведомление отправлено успешно")
        logger.info("=" .repeat(60))

        return mapOf("notificationSent" to "true")
    }
}