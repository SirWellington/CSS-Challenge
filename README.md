==============================================
# CSS-Challenge

## Introduction

This is the Cloud Kitchens submission challenge. It was completed using Kotlin,
and will run fine on any recent JVM.

### Technologies Used
1. Kotlin
2. Maven
3. JDK
4. Alchemy Libraries


### Top Challenges

+ The calculation of an order's value over time proved really tricky, 
  because it depends on how long an order sits in its proper shelf 
  vs the overflow shelf.
  
+ Overall this was a really challenging problem. 
  There are many complexities to this system that one would just not expect.

+ Synchronizing the shelves across multiple threads.
  Between the Kitchen adding orders and Drivers who come to pick
  them up, there were many opportunities for race conditions.


---------------------------------------------------------------------------------------------------------


## Getting Started

### From Code
To get started from code, open the `Main.kt` file and run the main function.

### From Maven
To run this program using Maven, run the following commands at the project root:

```
mvn clean install
mvn exec:java
```

---------------------------------------------------------------------------------------------------------

### Areas for improvement 

The result code is far from perfect. Here are some areas I have 
identified for improvement.

+ **Dependency Injection**: Dependencies between different parts of the system could have been better handled by the use
of a DI framework such as Guice or Spring.

+ **Display**: For the purposes of time, I used a primary text-based display, but with more time 
we could have a nice UI to show the system status.

+ **REST API**: Right now this is just a console app. It would be 
nice if this thing had a REST API that allowed you to place
orders and get the current system status.



---------------------------------------------------------------------------------------------------------

### Software Used
1. IntelliJ Ultimate 😎
2. Oracle JDK
3. Atom Text editor
4. Google Music 🎧

### Hardware Used
1. iMac Mid-2011
2. Apple Chiclet keyboard
3. Apple Magic Mouse