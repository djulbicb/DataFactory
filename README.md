
# DataFactory    
 DataFactory exposes [DataLibrary](https://github.com/djulbicb/DataLibrary) as an API service. You can easily create custom REST APIs for testing and prototyping. These APIs provide random but realistic data, similar to lorem ipsum services or [jsonplaceholder](https://jsonplaceholder.typicode.com/)
    
To configure it, send any JSON object to API service and it will return one or multiple mock JSON objects. These mock objects are structured and filled with data based on the sent object. (Check example below).  
  
## Run it as docker  
Available at [Dockerhub](https://hub.docker.com/r/djulb/datafactory)
```  
     docker run -p 8080:8080 djulb/datafactory  
```  
  ## REST Example 
  Send any JSON object to API and it will fill it with random data based on method call. 
  For a list of available methods go to commands section of [DataLibrary](https://github.com/djulbicb/DataLibrary) documentation page. 

### Example   
Sending this JSON to url `http://localhost:8080/set/djulb?apiCount=50` will create an API with 50 elements with identical JSON structure and fake data
``` 
     {
     "name":"getNameMale()",
     "test":{
        "testname":"getName()"
     },
     "surname":"getSurname()",
     "age":"getAgeAdult()",
     "someFloat":"getDoubleInRange(10,50)",
     "number":"getIntInRange(1,5)",
     "list":[
        "loremIpsum()",
        "getSentence()"
     ],
     "something":"This will remain same cause it is isnt a method call"
     }
``` 
This is the example of mock data created based on the JSON above.
``` 
     {
     "name":"Odell",
     "test":{
        "testname":"Rebecca"
     },
     "surname":"Heap",
     "age":49,
     "someFloat":44.12,
     "number":5,
     "list":[
        "Cras elit elit in gravida lectus.",
        "Utils Siemens ment processed limits."
     ],
     "something":"This will remain same cause it is isnt a method call"
     }
```
## Create
### Create REST service using GET method  
```
    http://localhost:8080/set/djulb?apiWait=500&apiCount=50&name=getName()&apiId=id&surname=getSurname()&age=getIntInRange(35,80)&id=iterator()
```
Creates service called `djulb` with 50 items. Contains a list of objects with properties *name*, *surname*, *age*, *id*.  
An example of mock object is   
```  
     {
     "surname":"Falconer",
     "name":"Darren",
     "userId":1,
     "age":77
     }
```  
> Difference between creating REST APIs with POST or GET methods is that POST method allow any JSON structure - as nested as you want. GET method allows only single depth JSON objects.
### Create REST service using POST method  
Send JSON object like this one to `http://localhost:8080/set/djulb?apiCount=10&apiId=userId`
```
     {
         "userId":"iterator()",
         "name":"getWord()",
         "surname":"getSurname()",
         "num":"getIntInRange(9,15)",
         "complex":{
            "adress":"getStreet()",
            "nested":{
               "city":"getCity()"
            }
         }
     }
```
This rest service will have 10 items. It's using custom id `userId` for identifing objects. When using custom ids you need to make sure they are initialized with some value. In this case it is using method `iterator()` - It is like auto-increment in sql databases.

You fetch an object using `http://localhost:8080/api/djulb/6` and get this
```
  {
     "surname":"Bookhart",
     "num":12,
     "complex":{
        "adress":"Liggett Ave",
        "nested":{
           "city":"Bristol"
        }
     },
     "name":"demographic",
     "apiId":6
  }
```

### Usage
> Fetch all items with GET 

 `  http://localhost:8080/api/djulb` 
 
> Fetch single item with GET method

 `  http://localhost:8080/api/djulb/1`. 
 In this case 1 is the id of the element.  An id can be a number of string. By default id field is `apiId` but you can specify your own custom id field using url param `apiId` when creating the service.
 
 >Update an element with POST method

`  http://localhost:8080/api/djulb/1`. 
Updates specified object. Doesn't check if JSON structure of old and new object are identical (You are on your own then :)  

>Delete entire service with DELETE method

 `  http://localhost:8080/api/djulb` 
 
>Delete entire service with DELETE method 

`  http://localhost:8080/api/djulb/1`  

## Configuring APIs
When creating a service using GET or POST methods pass add some of these url params
| url params | description |
|--|--|
| apiName | name of the API service |
| apiWait | wait time before responding to GET requests. Simulates server response time |
| apiCount | number of items |
| apiId | Name of the object identifier field. By default its apiId |

## Code examples

### PHP  Example using AJAX for HTTP request
```
      class Test {
        public $name;
        public $surname;
        public $age;
        public $address;
    
        function __construct($name, $surname, $age, $address){
          $this->name = $name;
          $this->surname = $surname;
          $this->age = $age;
          $this->address = $address;
        }
      }
    
      $url = 'http://localhost:8080/api/getdata';
      $tastArray = array('key1' => 'getName()', 'key2' => 'getName()');
      $testObj = new Test("getName()", "getSurname()", "getIntInRange(10,15)", "getStreet()") ;
      var_dump($testObj);
    
      echo json_encode($testObj);
    
      $options = array(
        'http' => array(
          'method'  => 'POST',
          'content' => json_encode( $testObj ),
          'header'=>  "Content-Type: application/json\r\n" .
                      "Accept: application/json\r\n"
          )
      );
    
      $context  = stream_context_create( $options );
      $result = file_get_contents( $url, false, $context );
    
    
      $response = json_decode( $result );
    
      var_dump($response);
      echo $response->name;
```
### React example using axios for HTTP request
```
      import { Component } from "react"
      import React from 'react';
      import axios from 'axios';
    
      class Users extends Component{
    
          componentDidMount(){
              let url = "http://localhost:8080/api/getdata";
              let user = {
                  name: 'getName()',
                  surname: 'getSurname()',
                  age: 'getIntBound(70)',
                  address: {
                      someStreetName: 'getStreet()',
                      someNumber: 'getIntInRange(10,50)',
                      city: 'getCity()',
                      description: 'getSentence()'
                  },
                  role: "pickRandom(admin,user)"
              }
    
              axios.post(url, user).then(response=>{
                  console.log(response.data);
              })
          }
    
          render(){
              return (
                  <div>Users</div>
              )
          }
      }
    
      export default Users;
```
### Java example using WebClient for HTTP request
>Requires dependency

```
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-webflux</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
      </dependencies>    
```

>Model data

```
      package com.example.jsp.configuration;
    
      public class Employee {
          private String name;
          private String surname;
          private Address address;
          
          ... getters and setters
      }
    
      package com.example.jsp.configuration;
    
      public class Address {
          private String streetName;
          private String houseNumber;
          
          ... getters and setters
      }
```
>JavaExample

```
      package com.example.jsp.configuration;
    
      import org.springframework.http.HttpHeaders;
      import org.springframework.http.MediaType;
      import org.springframework.web.reactive.function.client.WebClient;
    
      public class JavaExample {
          public static void main(String[] args) {
        // Data model
              Employee em = new Employee();
              Address ad = new Address();
              em.setName("getName()");
              em.setSurname("getSurname()");
              ad.setStreetName("getStreet()");
              ad.setHouseNumber("getIntInRange(1,5)");
              em.setAddress(ad);
    
        // Single mock object
        WebClient webClientSingle = WebClient.create("http://localhost:7070");
              Employee createdEmployee = webClientSingle.post()
                      .uri("/")
                      .accept(MediaType.APPLICATION_JSON)
                      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                      .bodyValue(em)
                      .retrieve()
                      .toEntity(Employee.class)
                      .block().getBody();
        System.out.println(createdEmployee);
        
        // List of mock objects
        WebClient webClientList = WebClient.create("http://localhost:7070/10");
              List<Employee> createdEmployees = webClientList.post()
                      .uri("/")
                      .accept(MediaType.APPLICATION_JSON)
                      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                      .bodyValue(em)
                      .retrieve()
                      .toEntityList(Employee.class)
                      .block().getBody();
              System.out.println(createdEmployees);
          }
      }
```

TODO:
- improve logging
- allow user to pick a language, currently english only
- assign datafactory to each api. Parse api request in a bulk instead one by one. Goal: So it can give back related data - if user asks for a world country, city or capital it will receive data that is related to each other insead of being random.

