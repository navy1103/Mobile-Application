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
  
            if (strlen($routine_note) < 2) {
                echo '{"result": "fail", "error": "Please enter valid data)."}';
            } else {    

                //build query
//                $sql = "INSERT INTO Routines (setDate, setTime, note, created_at)";
//                $sql .= " VALUES ('$routine_date', '$routine_time', '$routine_note', NOW())";
                
                $sql = "INSERT INTO Routines (setHour, setMin, note, created_at)";
                $sql .= " VALUES ('$routine_hour', '$routine_min', '$routine_note', NOW())";
                
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
