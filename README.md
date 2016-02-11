# About
## Docker

The dockerfile to build and run this project is on this repo it install maven and use it to build the src.
It also expose the running spark server on spark default port **4567**

## Frontend

 The frontend API is being served by spark and it's source is located on app/company-app/src/main/resources/public
 It contains:
 
 * A style
 * JS files for angular app
 * HTML files for directives

  It communicates with the backend trhough json ajax requests

## Backend

The backend s located on app/company-app/src/main/java and can be built with maven

## database 

The databse is a sqlite fle and is on base.db

It has 2 tables employee and company with the follow structure:

**company**

```sql
CREATE TABLE "company" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL,
    "address" TEXT NOT NULL,
     "city" TEXT NOT NULL,
     "country" TEXT NOT NULL,
     "email" TEXT,
     "phone" TEXT
)

```

**employee**

```sql
CREATE TABLE "employee" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT ,
    "name" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "is_owner" INTEGER,
    "company_id" INTEGER NOT NULL
)
```

The lack of foreign keys is due to the use of SQL lite and the example nature of this project 

# CURL Examples

## A note about the request form

The requests always return a JSON object, in case of errors it will be an object on the form

```json
{"success":"false"} 
```

Or sometimes

```json
{"success":"false", "error":{"field":"Why field is wrong"}} 
```

 The requests are received on a rest-like logic so PUT for updates, POST for creations , GET for fetching there is no DELETE or PATCH support at the moment

##Company 

### List all companies

```sh
curl http://localhost:4567/company
```

### List a specific company 

```sh
curl http://localhost:4567/company/:id
```

### Save a company

When saving a company the resulting object is returned 

```sh
curl -H "Content-Type: application/json" -X POST -d '{"name":"evil corp","address":"Avenue b","city":"Some city","country":"BR","email":"foo@bar.baz","phone":"+55 11 12122121 13221"}'  http://localhost:4567/company
```

### Update a company

```sh
curl -H "Content-Type: application/json" -X PUT -d '{"name":"Even more evil corp"}'  http://localhost:4567/company/3
```

##Employees

### List employees

```sh
curl  http://localhost:4567/company/1/employees
```

### Add a employee

Adding a employee is dony on the company end point to prevent adding companyless employees

```sh
curl -H "Content-Type: application/json" -X POST -d '{"name":"Mr Mr","email":"mr@mr.mr","is_owner":0}'  http://localhost:4567/company/1/employee
```

### Fetch a single employee

```sh
curl   http://localhost:4567/employee/:employee_id
```

### Update a employee

```sh
curl -H "Content-Type: application/json" -X PUT -d '{"name":"New name"}'  http://localhost:4567/employee/:employee_id
```