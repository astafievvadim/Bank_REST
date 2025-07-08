# Система управления банковскими картами

## Описание проекта

Backend-приложение на Java (Spring Boot) для управления банковскими картами с поддержкой авторизации по JWT, разграничением прав доступа (роли `ADMIN` и `USER`), миграциями базы данных через Liquibase и контейнеризацией с Docker Compose.

## Сущности проекта

![bank_erd](https://github.com/user-attachments/assets/bf8b44bd-e624-4b4f-ab61-7f1a4cf59562)

## Используемые технологии

- Java 17+
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Liquibase
- Docker Compose
- Swagger (OpenAPI)

## Требования

- Docker
- Docker Compose

## Инструкция по запуску

1. Клонировать репозиторий:

   git clone https://github.com/astafievvadim/Bank_REST.git
   cd Bank_REST
   
По умолчанию в src/main/resources/application.yml уже указаны актуальные настройки для работы в контейнере.

2. Запустить сборку и запуск контейнеров:

    docker-compose up --build

3. Проверить доступность приложения:

    API документация (Swagger UI): http://localhost:8080/swagger-ui/index.html

## Краткое описание эндпоинтов

    Полное описание эндпоинтов и DTO моделей: docs/openapi.yaml

| HTTP       | Эндпоинт                                    | Описание                                                        | Роль доступа |
| :--------- | :------------------------------------------ | :-------------------------------------------------------------- | :----------- |
| **POST**   | `/auth/login`                               | Аутентификация пользователя                                     | Открытый     |
| **GET**    | `/cards`                                    | Получение списка карт текущего пользователя с пагинацией        | USER         |
| **GET**    | `/cards/{cardId}/balance`                   | Получение баланса по карте                                      | USER         |
| **POST**   | `/cards/transfer`                           | Перевод между своими картами                                    | USER         |
| **POST**   | `/cards/{cardId}/block`                     | Отправка запроса на блокировку карты                            | USER         |
| **GET**    | `/cards/admin/users/{userId}/cards`         | Получение карт конкретного пользователя (пагинация, сортировка) | ADMIN        |
| **GET**    | `/admin/users`                              | Получение списка всех пользователей                             | ADMIN        |
| **POST**   | `/admin/users`                              | Создание нового пользователя                                    | ADMIN        |
| **GET**    | `/admin/users/{userId}`                     | Получение пользователя по ID                                    | ADMIN        |
| **PUT**    | `/admin/users/{userId}`                     | Обновление данных пользователя по ID                            | ADMIN        |
| **DELETE** | `/admin/users/{userId}`                     | Удаление пользователя по ID                                     | ADMIN        |
| **GET**    | `/admin/block-requests`                     | Получение всех запросов на блокировку                           | ADMIN        |
| **POST**   | `/admin/block-requests/{requestId}/approve` | Подтверждение запроса на блокировку карты                       | ADMIN        |
| **POST**   | `/admin/block-requests/{requestId}/decline` | Отклонение запроса на блокировку карты с комментарием           | ADMIN        |
