package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ExecutePaymentWorker {

    private val logger = LoggerFactory.getLogger(ExecutePaymentWorker::class.java)

    @JobWorker(type = "execute-payment", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val paymentId = job.variablesAsMap["paymentId"]
        val amount = job.variablesAsMap["amount"]

        logger.info("=" .repeat(60))
        logger.info(">>> [ExecutePayment] *** PIVOT POINT ***")
        logger.info(">>> [ExecutePayment] Перевод средств контрагенту")
        logger.info(">>> [ExecutePayment] Платёж ID: $paymentId")
        logger.info(">>> [ExecutePayment] Сумма перевода: $amount")

        // Имитация перевода средств
        Thread.sleep(800)

        // 90% успех, 10% ошибка (чтобы протестировать Refund)
        val success = Random.nextInt(10) < 9

        return if (success) {
            logger.info(">>> [ExecutePayment] Средства переведены успешно")
            logger.info(">>> [ExecutePayment] Статус: EXECUTED")

            mapOf(
                "paymentStatus" to "EXECUTED",
                "executedAt" to System.currentTimeMillis().toString()
            )
        } else {
            logger.error(">>> [ExecutePayment] ОШИБКА перевода средств!")
            logger.info(">>> [ExecutePayment] Статус: FAILED - требуется возврат (Refund)")

            mapOf(
                "paymentStatus" to "FAILED",
                "executedAt" to System.currentTimeMillis().toString(),
                "errorReason" to "BANK_GATEWAY_TIMEOUT"
            )
        }
    }
}