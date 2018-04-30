API
============

Main points:

* To start process consumer of the service should create a payment entity. 
All the nececary information is supposed to be presented on this
stage: account to withdraw, account to replenish and transfer amount.

* The process of money transfer is divided in two parts - authorization and confirmation. On the authorization stage checks 
(sufficiency of funds, limits, e.t.c) and withholding are performed. On the confirmation stage withdraw and replenish 
are carried out, withholding is truncated. Such approach lets us easily extend payment process with different types of 
authentication (3DS, Apple pay), confirmation and cancellation features.

* All API methods are idempotent. Every method returns payment in its current state, it doesn't matter 
how much times methods are called and in what order. Exception here is payment creaton method, it always returns a new payment, 
so it may be retried too. Such approach avoids mistakes and allows to retry all requests in case of network
problems.

Create payment
------------

**POST** /api/payment

request:
~~~
{
"accIn": "123",
"accOut" : "321",
"amount" : "100"
}
~~~

response:
~~~
{
    "id": "81d6d801-6b93-43c4-96a9-49972629a101",
    "accIn": "123",
    "accOut": "321",
    "amount": 100,
    "status": "INITIAL"
}
~~~


Get payment
------------

**GET** /api/payment/{id}

response:
~~~
{
    "id": "81d6d801-6b93-43c4-96a9-49972629a101",
    "accIn": "123",
    "accOut": "321",
    "amount": 100,
    "status": "INITIAL"
}
~~~

Authorize payment
------------

**PUT** /api/payment/{id}

request:
~~~
{
    "status": "AUTHORIZED"
}
~~~

response:
~~~
{
    "id": "81d6d801-6b93-43c4-96a9-49972629a101",
    "accIn": "123",
    "accOut": "321",
    "amount": 100,
    "status": "AUTHORIZED"
}
~~~

Confirm payment
------------

**PUT** /api/payment/{id}

request:
~~~
{
    "status": "CONFIRMED"
}
~~~

response:
~~~
{
    "id": "81d6d801-6b93-43c4-96a9-49972629a101",
    "accIn": "123",
    "accOut": "321",
    "amount": 100,
    "status": "CONFIRMED"
}
~~~

Error handling
------------

Every business error transfers a payment to **ERROR** state with reason of this state transition in *errorReason* field:
~~~
{
    "id": "19c17d74-7dbb-4db4-a823-217a0cdd601a",
    "accIn": "123",
    "accOut": "321",
    "amount": 199900,
    "status": "ERROR",
    "errorReason": 4
}
~~~ 

In case of technical error consumer receives default http error codes: 404, 500 and 403.

State model
------------

Possible state transitions:

* **INITIAL** -> **AUTHORIZED** -> **CONFIRMED**

* **INITIAL** -> **ERROR**

* **CONFIRMED** -> **ERROR**


Under the hood
============

Bussiness logic is divided on two parts: *payment* and *account*. First one is responsible for
payment processing, second one makes withdrawal and replenishment of accounts. 

Account domain doesn't expose any domain entites outside, so it can be easily put into
external service, as it is usually done in real banking applications.

Set of tests can be found in *PaymentGateTest* class.

There are two accounts with 1000 cents created after startup: *123* and *321*.

To run the app:
~~~
mvn jetty:run
~~~

To run tests:
~~~
mvn test
~~~