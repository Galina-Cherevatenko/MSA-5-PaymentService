package com.msa.orchestrpay.worker

import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CreatePaymentWorker {

    private val logger = LoggerFactory.getLogger(CreatePaymentWorker::class.java)

    @JobWorker(type = "create-payment", autoComplete = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val variables = job.variablesAsMap

        logger.info("=" .repeat(60))
        logger.info(">>> [CreatePayment] Создание платежа")
        logger.info(">>> [CreatePayment] ProcessInstanceKey: ${job.processInstanceKey}")

        val paymentId = variables["paymentId"]?.toString()
            ?: "PAY-${UUID.randomUUID()}"
        val amount = variables["amount"]?.toString() ?: "100.00"
        val currency = variables["currency"]?.toString() ?: "USD"

        logger.info(">>> [CreatePayment] Платёж ID: $paymentId")
        logger.info(">>> [CreatePayment] Сумма: $amount $currency")
        logger.info(">>> [CreatePayment] Статус: CREATED")

        return mapOf(
            "paymentId" to paymentId,
            "paymentStatus" to "CREATED",
            "amount" to amount,
            "currency" to currency
        )
    }
}