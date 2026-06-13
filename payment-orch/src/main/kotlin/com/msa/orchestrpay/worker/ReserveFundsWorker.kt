package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReserveFundsWorker {

    private val logger = LoggerFactory.getLogger(ReserveFundsWorker::class.java)

    @JobWorker(type = "reserve-funds", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]

        logger.info("=" .repeat(60))
        logger.info(">>> [ReserveFunds] Резервирование средств")
        logger.info(">>> [ReserveFunds] Платёж ID: $paymentId")

        // Имитация холдирования средств
        Thread.sleep(500)

        logger.info(">>> [ReserveFunds] Средства зарезервированы успешно")
        logger.info(">>> [ReserveFunds] Статус: FUNDS_RESERVED")

        return mapOf("paymentStatus" to "FUNDS_RESERVED")
    }
}