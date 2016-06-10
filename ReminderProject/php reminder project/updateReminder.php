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
        $tag = isset($_GET['tag']) ? $_GET['tag'] : '';
        $id = isset($_GET['setID']) ? $_GET['setID'] : '';
        
        $date = isset($_GET['setDate']) ? $_GET['setDate'] : ''; 
        $hour = isset($_GET['setHour']) ? $_GET['setHour'] : '';
        $min = isset($_GET['setMin']) ? $_GET['setMin'] : '';
        
        $note = isset($_GET['note']) ? $_GET['note'] : '';
        $email = isset($_GET['email']) ? $_GET['email'] : '';
        $phone = isset($_GET['phone']) ? $_GET['phone'] : '';
        $location = isset($_GET['location']) ? $_GET['location'] : '';
            
        $username = isset($_GET['username']) ? $_GET['username'] : '';
  
        //build query
        $sql = "UPDATE Reminders";
        $sql .= " SET setDate = '$date', setHour = '$hour', setMin = '$min', note = '$note', email = '$email', phone = '$phone', location = '$location', created_at = 'NOW()'";
        $sql .= " WHERE id = '$id'";
                
            //attempts to update record
            if ($db->query($sql)) {
                echo '{"result": "success"}';
                $db = null;
            } 
          
            
        } catch(PDOException $e) {
                die("Could not connect to the database :" . $e->getMessage());
        }
?>
