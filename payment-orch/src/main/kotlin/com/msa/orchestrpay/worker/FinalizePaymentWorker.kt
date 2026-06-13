package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FinalizePaymentWorker {

    private val logger = LoggerFactory.getLogger(FinalizePaymentWorker::class.java)

    @JobWorker(type = "finalize-payment", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]

        logger.info("=" .repeat(60))
        logger.info(">>> [FinalizePayment] Финализация платежа")
        logger.info(">>> [FinalizePayment] Платёж ID: $paymentId")

        Thread.sleep(300)

        logger.info(">>> [FinalizePayment] Статус изменён на SETTLED")
        logger.info(">>> [FinalizePayment] Платёж завершён успешно")

        return mapOf(
            "paymentStatus" to "SETTLED",
            "finalizedAt" to System.currentTimeMillis().toString()
        )
    }
}