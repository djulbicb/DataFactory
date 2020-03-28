# DataFactory

DataFactory exposes [DataLibrary](https://github.com/djulbicb/DataLibrary) as an API service and web app with UI.
<br>These 2 projects allow users to create custom API service which provide random but realistic data, similar to lorem ipsum services.

API service receives any JSON object and fills it with data (Check example below).<br> 
UI connects with database (just mysql for now) and inserts data into database 

### To do / test
- unique constraint, value already exist
- add to docker

<img src="./ui.PNG">

## API
Send any JSON object to API and it will fill it with random data based on specified method call. For example
```
    {
        "name": "getNameMale()",
        "test": {
        	"testname": "getName()"
        },
        "surname": "getSurname()",
        "age": "getAgeAdult()",
        "country": "getDoubleInRange(10,50)",
        "number":"getIntInRange(1,5)",
        "list": ["loremIpsum()", "loremIpsum()"]
    }
```
and this will return 
```
    {
        "country":48.70817340284648,  
        "number":1,  
        "test":{
	    "testname":"Seymour"  
        },  
        "surname":"Felice",  
        "name":"Manual",  
    "list":[
        "Purus nam.",  
        "Ligula et vitae enim id nulla qui."  
    ],  
        "age":57  
    }
```
Order of properties is not kept at the moment. Here is a test example using PHP    
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
    
    
    ?>
```

