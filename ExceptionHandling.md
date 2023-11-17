With new exception handling - included inside our code -  
we can handle thrown exception like JsonValue.
This is printed by reaching each RestControllers mapping.

For creating your exception and its handling you need to create the message 
inside ExceptionMessage class like this below:
```java
UNAUTHORIZED("You are not authorized!")
```
or you can use your own message like before.

Then you will create your own class with exception - e.g. MissingNameException class 
with two constructor which is extended by main exception PfpException.
- first constructor with basic thrown (implicit) message and http status without parameters
- second one with the message in parameter what you have created in ExceptionMessage before.

When you have your exception ready you can implement it into your services and controllers.

After implementation, you have to handle this exception also inside 
"exceptionhandling/RestExceptionHandler.java".
Here you need write the code like given in this example:
```java
  @ExceptionHandler(UnauthorizedException.class)
  protected ResponseEntity<Object> handleUnauthorized(UnauthorizedException exception) {
    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }
```

This section of code will create our ApiError with JsonValue and return it.

I have created for whole handling: ApiError class, ApiSubError class,
ApiValidationError class, RestExceptionHandler class, ExceptionMessage,
MissingParameterException class and PfpException.

ApiError class has all fields for creating Json message for us.

ApiSubError class work with any sub errors what can be thrown together with our exceptions.

ApiValidationError class create JsonValue with fields what we need to put into message.

RestExceptionHandler class handle all exception and create JsonValue from thrown exception.

ExceptionMessage class has getter for create message what will be thrown with exception.

MissingParameterException class create own message what you create
or change it to missing parameter message.

PfpException class where we extend Exceptions and create our own throwing constructor for it.


