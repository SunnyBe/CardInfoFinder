# CardInfoFinder
Card Info Finder is a basic android app that takes card numbers and fetches card details. For the
sake of simplicity only card type, city and country is displayed.

### Tools used project
1. Android Studio 4.1.1
2. Kotlin 1.4.20
3. Gradle 4.1.2
4. XML for layouts

### Properties of Project
1. Architecture: Android project was built with MVI flavor of MVVM where view states and view events 
are extensively declared.
2. Monolithic Approach: Due to the simplicity in feature, the Monolithic approach was used. However, multi-module would be
advisable for bigger projects. A single Activity was used for simplicity of concept.
3. Concurrency: Coroutines and Flow were used to handle concurrency in the project.
4. Network: Retrofit was used for making network request.
5. UI & Unit Test: Basic UI and unit tests were included.
