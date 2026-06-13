package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReleaseFundsWorker {

    private val logger = LoggerFactory.getLogger(ReleaseFundsWorker::class.java)

    @JobWorker(type = "release-funds", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]
        val fraudResult = job.variablesAsMap["fraudResult"]
        val manualReviewResult = job.variablesAsMap["manualReviewResult"]

        val reason = when {
            fraudResult == "declined" -> "FraudCheck отклонил автоматически"
            manualReviewResult == "declined" -> "Оператор отклонил вручную"
            else -> "UNKNOWN"
        }

        logger.info("=" .repeat(60))
        logger.info(">>> [ReleaseFunds] ⚠️ КОМПЕНСАЦИЯ: Возврат холда")
        logger.info(">>> [ReleaseFunds] Платёж ID: $paymentId")
        logger.info(">>> [ReleaseFunds] Причина: $reason")
        logger.info(">>> [ReleaseFunds] Деньги НЕ были переведены контрагенту")
        logger.info(">>> [ReleaseFunds] Просто разблокируем холд на счёте клиента")

        // Имитация возврата холда
        Thread.sleep(500)

        logger.info(">>> [ReleaseFunds] Холд успешно отменён")
        logger.info(">>> [ReleaseFunds] Средства разблокированы на счёте клиента")

        return mapOf(
            "paymentStatus" to "RELEASED",
            "releasedAt" to System.currentTimeMillis().toString(),
            "releaseReason" to reason
        )
    }
}