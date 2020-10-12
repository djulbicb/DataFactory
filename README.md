
# DataFactory  
  
DataFactory exposes [DataLibrary](https://github.com/djulbicb/DataLibrary) as an API service. This project allow users to create custom REST API service which provide random but realistic data, similar to lorem ipsum services.  
  
To configure it, send any JSON object to API service and it will return one or multiple mock JSON objects. These mock objects are structured and filled with data based on the sent object. (Check example below).

## Run it as docker
```
docker run -p 8080:8080 djulb/datafactory
```
  
## Examples  
Send any JSON object to API and it will fill it with random data based on method call. For example  
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
   "something": "This will remain same cause it is isnt a method call"
}
```  
And it will return this JSON object as response.
```  
{
    "name": "Odell",
    "test": {
        "testname": "Rebecca"
    },
    "surname": "Heap",
    "age": 49,
    "someFloat": 44.12,
    "number": 5,
    "list": [
        "Cras elit elit in gravida lectus.",
        "Utils Siemens ment processed limits."
    ],
    "something": "This will remain same cause it is isnt a method call"
}
```   

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
Requires dependency
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

Model data
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
JavaExample
```
package com.example.jsp.configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class JavaExample {
    public static void main(String[] args) {
        WebClient webClient = WebClient.create("http://localhost:7070");

        Employee em = new Employee();
        Address ad = new Address();
        em.setName("getName()");
        em.setSurname("getSurname()");
        ad.setStreetName("getStreet()");
        ad.setHouseNumber("getIntInRange(1,5)");
        em.setAddress(ad);

        Employee createdEmployee = webClient.post()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(em)
                .retrieve()
                .bodyToMono(Employee.class)
                .block();


        System.out.println(createdEmployee);
    }
}
```
