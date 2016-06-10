<?PHP
ini_set('display_errors', '1');
error_reporting(E_ALL);

	    $dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=navy1103';
        $username = 'navy1103';
        $password = 'MavOnum';
       
    	try {
        	$db = new PDO($dsn, $username, $password);
            $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            //get inputs for registering information
            $username = isset($_GET['username']) ? $_GET['username'] : '';
            $email = isset($_GET['email']) ? $_GET['email'] : '';
            $password = isset($_GET['password']) ? $_GET['password'] : '';
  
            if (strlen($username) < 5 
                    || strlen($email) < 6 
                    || strlen($password) < 6 
                    ) {
                echo '{"result": "fail", "error": "Please enter valid data."}';
            } else {    

                //build query
                $sql = "INSERT INTO Users";
                $sql .= " VALUES ('$username', '$email', '$password')";
             
                //attempts to add record
                if ($db->query($sql)) {
                    //echo '{"result": "success"}';
                    echo '{"result": "success", "username": "'. $username. '"}'; 
                    $db = null;
                } 
            }   
        } catch(PDOException $e) {
            echo 'Error Number: ' . $e->getCode() . '<br>';
            echo '{"result": "fail", "error": "Unknown error (' . (((int)($e->getCode()) + 123) * 2) .')"}';
        }
?>
