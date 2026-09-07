# 📚 BookHub with GraphQL

A modern **Spring Boot + GraphQL** sample application that demonstrates how to build a production-style GraphQL API using **Spring for GraphQL**, **Spring Data JPA**, and **cursor-based pagination**.

The project is intended as a learning resource for Java developers who want to understand GraphQL from the backend perspective while following clean architecture and modern Spring practices.

---

## ✨ Features

* GraphQL API with Spring for GraphQL
* Cursor-based pagination using `Window<T>` and `ScrollSubrange`
* Spring Data JPA
* DTO-based API layer
* Clean Architecture / Use Case pattern
* Bean Validation
* Global GraphQL exception handling
* Entity relationships

    * Books
    * Authors
    * Publishers
* GraphQL Query support
* GraphQL Mutation support
* Java Record DTOs
* MapStruct mapping
* PostgreSQL support

---

## 🛠 Tech Stack

* Java 21
* Spring Boot 4.x
* Spring GraphQL
* Spring Data JPA
* Hibernate
* PostgreSQL
* Gradle
* MapStruct

---

# Project Structure

```text
src
├── application
│   ├── dto
│   ├── mapper
│   └── usecase
│
├── domain
│   ├── entity
│   ├── repository
│   └── service
│
├── infrastructure
│   ├── persistence
│   └── graphql
│
└── presentation
    └── graphql
```

The project follows a layered architecture where the GraphQL controllers communicate only with the application layer.

---

# Getting Started

## Clone the project

```bash
git clone https://github.com/miladsadeghi77/bookhub-with-grahpql.git

cd bookhub-with-grahpql
```

---

## Configure Database

Update your `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookhub
    username: postgres
    password: password
```

---

## Run

```bash
./gradlew bootRun
```

---

## Open GraphiQL

```
http://localhost:8080/graphiql
```

---

# Sample Queries

## Get Books

```graphql
query {
  books(first: 5) {
    edges {
      cursor
      node {
        title
        publishedYear
        author {
          name
        }
    }
    }
    pageInfo {
      hasNextPage
      endCursor
    }
  }
}
```

---

## Next Page

```graphql
query {
  books(
      first:5,
      after:"<cursor>"
  ) {
    edges {
      node {
        title
      }
    }
    pageInfo {
      hasNextPage
      endCursor
    }
  }
}
```

---

## Create Book

```graphql
mutation {

  createBook(

    input:{
      title:"Effective Java"
      publishedYear:2005
      authorId:1
      publisherId:2
    }

  ){

    id
    title

  }

}
```

---

# Cursor Pagination

This project demonstrates **keyset (cursor) pagination** instead of traditional offset pagination.

### Offset Pagination

```sql
SELECT *
FROM books
LIMIT 20 OFFSET 100000;
```

As the offset grows, the database must skip an increasing number of rows before returning the requested page.

### Cursor Pagination

```sql
SELECT *
FROM books
WHERE id > ?
ORDER BY id
LIMIT 20;
```

This approach scales significantly better for large datasets and is recommended for GraphQL APIs.

The implementation is based on:

* `Window<T>`
* `ScrollSubrange`
* `KeysetScrollPosition`

---

# GraphQL Concepts Covered

* Queries
* Mutations
* Input Types
* Nested Objects
* Relationships
* Validation
* Exception Handling
* Cursor Connections
* DTO Mapping
* Pagination
* Entity Mapping

---

# Future Improvements

* Batch Loading with DataLoader
* GraphQL Subscriptions
* Query Complexity Analysis
* Authentication & Authorization
* Federation
* Filtering
* Sorting
* Full-text Search
* Testing with GraphQlTester
* Docker Compose

---

# Learning Goals

This repository is designed to help Java developers learn:

* Spring for GraphQL
* GraphQL Schema Design
* Cursor-based Pagination
* Spring Data Scroll API
* Clean Architecture
* GraphQL Best Practices

---

# Contributing

Contributions, issues, and feature requests are welcome.

If you find a bug or have an idea for improving the project, feel free to open an issue or submit a pull request.

---

# License

This project is licensed under the MIT License.
