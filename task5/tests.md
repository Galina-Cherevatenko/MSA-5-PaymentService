# Интеграционные и End-to-End тесты для платёжного процесса OrchestrPay

## Описание

Набор тестов покрывает основной сценарий обработки платежа (Happy Path), компенсационные транзакции (Saga), сценарии с ручной проверкой (Manual Review) и таймаутом (Cut-off), а также отказоустойчивость при недоступности внешних сервисов.

**Используемые инструменты:**
- **Testcontainers** — для поднятия изолированного окружения (Zeebe, PostgreSQL, Redis)
- **Camunda Zeebe Test Container** — для тестирования BPMN-процессов
- **JUnit 5 + Spring Boot Test** — для интеграционных тестов

---

## Таблица тест-кейсов

| Название | Тип | Компоненты | Предусловия |
| :--- | :--- | :--- | :--- |
| **TC-01: Успешный платёж (Happy Path)** | End-to-End | Payment Orchestrator, FraudCheck Service (mock одобрения), Payment Service (mock), Notification Service (mock) | FraudCheck возвращает `approved`. Все внешние сервисы доступны. |
| **TC-02: Отклонение платежа антифрод-системой (Fraud Decline)** | Интеграционный | Payment Orchestrator, FraudCheck Service (mock отказа) | FraudCheck возвращает `declined`. Средства зарезервированы на счету клиента. |
| **TC-03: Ручная проверка — одобрение оператором** | End-to-End | Payment Orchestrator, FraudCheck Service (mock ручной проверки + одобрение) | FraudCheck возвращает `manual_review`. Оператор принимает решение `approved` в течение 20 минут. |
| **TC-04: Ручная проверка — отклонение оператором** | Интеграционный | Payment Orchestrator, FraudCheck Service (mock ручной проверки + отказ) | FraudCheck возвращает `manual_review`. Оператор принимает решение `declined`. |
| **TC-05: Ручная проверка — срабатывание cut-off таймера** | Интеграционный | Payment Orchestrator, FraudCheck Service (mock ручной проверки) | FraudCheck возвращает `manual_review`. Оператор не принимает решение в течение 20 минут. Таймер истекает, транзакция автоматически одобряется (`default allow`). |
| **TC-06: Компенсация Release Funds (возврат холда)** | Интеграционный | Payment Orchestrator, Payment Service (mock) | Платёж отклонён на этапе FraudCheck. Средства зарезервированы (`RESERVE_FUNDS` выполнен). `EXECUTE_PAYMENT` ещё не выполнялся. |
| **TC-07: Компенсация Refund Payment (возврат после исполнения)** | Интеграционный | Payment Orchestrator, Payment Service (mock) | Платёж прошёл FraudCheck, `EXECUTE_PAYMENT` выполнен. Обнаружена ошибка на этапе финализации. Требуется полный возврат средств. |
| **TC-08: Идемпотентность повторного запроса** | Интеграционный | Payment Orchestrator, Payment Service (mock) | Клиент отправляет два одинаковых запроса с одним `paymentId`. Второй запрос не должен создать дублирующий платёж. |
| **TC-09: Недоступность FraudCheck Service — retry и восстановление** | Интеграционный | Payment Orchestrator, FraudCheck Service (mock недоступности) | FraudCheck Service недоступен при первом вызове. Orchestrator выполняет retry (3 попытки). Сервис восстанавливается, проверка проходит успешно. |
| **TC-10: Недоступность Payment Service при холдировании** | Интеграционный | Payment Orchestrator, Payment Service (mock недоступности) | Payment Service недоступен на шаге `RESERVE_FUNDS`. Транзакция переходит в состояние `SUSPENDED`. После восстановления сервиса процесс продолжается. |
| **TC-11: Сбой после Execute Payment (Saga Rollback)** | End-to-End | Payment Orchestrator, Payment Service (mock сбоя после execute), Notification Service (mock) | FraudCheck одобрен. `EXECUTE_PAYMENT` выполнен. Сервис финализации падает. Запускается компенсация `REFUND_PAYMENT`. Клиент получает уведомление об ошибке. |
| **TC-12: Параллельные транзакции одного пользователя** | End-to-End | Payment Orchestrator, FraudCheck Service, Payment Service | Один пользователь инициирует 10 платежей одновременно. Все транзакции обрабатываются независимо, без блокировок и дедлоков. |
| **TC-13: Проверка SLA — успешная транзакция < 5 секунд** | End-to-End | Все компоненты | Система работает в штатном режиме. FraudCheck отрабатывает мгновенно (`approved`). Время от создания платежа до финализации не превышает 5 секунд в 90% случаев. |
| **TC-14: Уведомление клиента при успешном платеже** | Интеграционный | Payment Orchestrator, Notification Service (mock) | Платёж успешно завершён (статус `SETTLED`). Notification Service вызывается с корректными параметрами (paymentId, статус, сумма). |
| **TC-15: Уведомление клиента при отклонении платежа** | Интеграционный | Payment Orchestrator, Notification Service (mock) | Платёж отклонён FraudCheck (статус `DECLINED`). Клиент получает уведомление об отказе с указанием причины. |
| **TC-16: Восстановление состояния после перезапуска Orchestrator** | Интеграционный | Payment Orchestrator, Zeebe | Процесс находится в состоянии `PENDING_MANUAL_REVIEW`. Orchestrator перезапускается. После перезапуска процесс продолжается с того же шага, таймер не сбрасывается. |

---

## Приоритеты тестов

| Приоритет | Тест-кейсы | Обоснование |
| :--- | :--- | :--- |
| **P0 (Критический)** | TC-01, TC-02, TC-06, TC-11 | Базовый сценарий и компенсации. Без них система не может работать. |
| **P1 (Высокий)** | TC-03, TC-04, TC-05, TC-07 | Ручная проверка и cut-off — ключевые требования бизнеса. |
| **P2 (Средний)** | TC-08, TC-09, TC-10, TC-16 | Отказоустойчивость и идемпотентность. |
| **P3 (Низкий)** | TC-12, TC-13, TC-14, TC-15 | Нагрузочное тестирование, SLA, уведомления. |

---

## Ожидаемые результаты

| Тест-кейс | Ожидаемый результат |
| :--- | :--- |
| TC-01 | Платёж создан → средства зарезервированы → FraudCheck пройден → средства переведены → статус `SETTLED` |
| TC-02 | Платёж создан → средства зарезервированы → FraudCheck отклонён → холд возвращён (`RELEASE_FUNDS`) → статус `DECLINED` |
| TC-03 | FraudCheck → Manual Review → оператор одобрил → Execute Payment → `SETTLED` |
| TC-04 | FraudCheck → Manual Review → оператор отклонил → Release Funds → `DECLINED` |
| TC-05 | FraudCheck → Manual Review → таймер 20 мин истёк → автоматическое одобрение → `SETTLED` |
| TC-06 | `RELEASE_FUNDS` выполнен, деньги разблокированы, статус `DECLINED` |
| TC-07 | `REFUND_PAYMENT` выполнен, деньги возвращены, статус `FAILED` |
| TC-08 | Повторный запрос возвращает тот же результат, дубликат не создан |
| TC-09 | После 3 ретраев FraundCheck восстанавливается, платёж завершается успешно |
| TC-10 | Процесс в `SUSPENDED`, после восстановления Payment Service завершается успешно |
| TC-11 | `EXECUTE` прошёл → сбой → `REFUND` → клиент уведомлён |
| TC-12 | Все 10 транзакций обработаны независимо, статусы корректны |
| TC-13 | 90% транзакций завершаются < 5 сек |
| TC-14 | Notification Service вызван с `paymentStatus=SETTLED` |
| TC-15 | Notification Service вызван с `paymentStatus=DECLINED` |
| TC-16 | Процесс продолжается с шага `PENDING_MANUAL_REVIEW`, таймер сохраняет оставшееся время |