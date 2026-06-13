package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RefundPaymentWorker {

    private val logger = LoggerFactory.getLogger(RefundPaymentWorker::class.java)

    @JobWorker(type = "refund-payment", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]
        val errorReason = job.variablesAsMap["errorReason"] ?: "UNKNOWN"

        logger.info("=" .repeat(60))
        logger.info(">>> [RefundPayment] ⚠️ КОМПЕНСАЦИЯ: Полный возврат средств")
        logger.info(">>> [RefundPayment] Платёж ID: $paymentId")
        logger.info(">>> [RefundPayment] Причина ошибки: $errorReason")
        logger.info(">>> [RefundPayment] Деньги уже были переведены контрагенту")
        logger.info(">>> [RefundPayment] Инициирован возврат от контрагента клиенту")

        // Имитация возврата
        Thread.sleep(700)

        logger.info(">>> [RefundPayment] Возврат выполнен успешно")
        logger.info(">>> [RefundPayment] Средства возвращены на счёт клиента")

        return mapOf(
            "paymentStatus" to "REFUNDED",
            "refundedAt" to System.currentTimeMillis().toString()
        )
    }
}