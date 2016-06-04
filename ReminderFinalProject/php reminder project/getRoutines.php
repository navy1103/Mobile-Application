<?PHP
ini_set('display_errors', '1');
error_reporting(E_ALL);
$command = $_GET['cmd'];

	// Connect to the Database
	$dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=navy1103';
    $username = 'navy1103';
    $password = 'MavOnum';

    try {
        $db = new PDO($dsn, $username, $password);
        $username = isset($_GET['username']) ? $_GET['username'] : '';
	       	
        if ($command == "routine") {
            $select_sql = 'SELECT * FROM Routines';
            $routine_query = $db->query($select_sql);
            $routines = $routine_query->fetchAll(PDO::FETCH_ASSOC);
            if ($routines) {	
	           echo json_encode($routines);
            }
        } else if ($command == "reminder") {
            $select_sql = "SELECT * FROM Reminders WHERE username = '" .$username. "'";
            $reminder_query = $db->query($select_sql);
            $reminder = $reminder_query->fetchAll(PDO::FETCH_ASSOC);
            if ($reminder) {	
	           echo json_encode($reminder);
            }
        }           
    } catch (PDOException $e) {
    	$error_message = $e->getMessage();
    	echo 'There was an error connecting to the database.';
		echo $error_message;
    	exit();
    }

?>
