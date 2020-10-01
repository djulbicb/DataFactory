
# DataFactory  
  
DataFactory exposes [DataLibrary](https://github.com/djulbicb/DataLibrary) as an API service and web app with UI.  
<br>These 2 projects allow users to create custom API service which provide random but realistic data, similar to lorem ipsum services.  
  
Users can send any JSON object to the API service and it will return a single or an array of JSON objects filled with data based on the object you sent (Check example below). 
UI connects with database (just mysql for now) and inserts data into database     
  
## API  
Send any JSON object to API and it will fill it with random data based on specified method call. For example  
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

$url = 'http://localhost:8091/api/getdata';
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
        let url = "http://localhost:8091/api/getdata";
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
  
# Run DataFactory with UI
<img src="./ui.PNG">
Connects to sql database (for now just mysql) and allows users to populate db with data. 

Add environment vars to IDE
| ENV VAR | VALUE |
|--|--|
| STORAGE_PATH | C:/Users/user/IdeaProjects/storage/ |
| DB_CONNECTIONS | dbConnections.json  |
| PRESETS_FOLDER | presets/
  
### To do / test  
- unique constraint, value already exist  
- add to docker  

