<?PHP
ini_set('display_errors', '1');
error_reporting(E_ALL);

	    // Connect to the Database
    $dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=navy1103';
    $username = 'navy1103';
    $password = 'MavOnum';
       
    	try {
        	$db = new PDO($dsn, $username, $password);
            $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            //get inputs for course information
             $routine_hour = isset($_GET['setHour']) ? $_GET['setHour'] : '';
            $routine_min = isset($_GET['setMin']) ? $_GET['setMin'] : '';
            $routine_note = isset($_GET['note']) ? $_GET['note'] : '';         
            $routine_id = isset($_GET['id']) ? $_GET['id'] : '';   
  
            if (strlen($routine_note) < 2) {
                echo '{"result": "fail", "error": "Please enter valid data)."}';
            } else {    

                //build query
                $sql = "UPDATE Routines";
                $sql .= " SET setHour = '$routine_hour', setMin = '$routine_min', note = '$routine_note', created_at = 'NOW()'";
                $sql .= " WHERE id = '$routine_id'";
                
                //attempts to add record
                if ($db->query($sql)) {
                    echo '{"result": "success"}';
                    $db = null;
                } 
            }   
        } catch(PDOException $e) {
                die("Could not connect to the database :" . $e->getMessage());
        }
?>
