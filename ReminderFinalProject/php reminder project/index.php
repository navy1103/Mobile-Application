<?php
    ini_set('display_errors', '1');
    error_reporting(E_ALL);

    $dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=navy1103';
    $username = 'navy1103';
    $password = 'MavOnum';

    try {
        $db = new PDO($dsn, $username, $password);    
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        //echo "Connected to $dbname at $host successfully.";
        
        $tag = isset($_GET['tag']) ? $_GET['tag'] : '';
        
        if($tag == 'login'){
            login();
        } else if ($tag == 'register'){
            register();            
        }        

        $db = null;
    
    } catch (PDOException $e) {
        die("Could not connect to the database :" . $e->getMessage());
    }

    function login(){
        //get inputs for registering information
        $username = isset($_GET['username']) ? $_GET['username'] : '';            
        $password = isset($_GET['password']) ? $_GET['password'] : '';    

        $sql = "SELECT username, password FROM Users ";
        $sql .= " WHERE username = '" .$username. "'";

        $q = $db->prepare($sql);
        $q->execute();
        $result = $q->fetch(PDO::FETCH_ASSOC);

        if ($result != false) {
            //on success, return the user id
            if (strcmp($password, $result['password']) == 0)
                echo '{"result": "success", "username": "' . $result['username'] . '"}';        
            else 
                echo '{"result": "fail", "error": "Incorrect password."}';
        } else {
            echo '{"result": "fail", "error": "Incorrect username."}';
        }        
    }

    function register(){
        //get inputs for registering information
        $username = isset($_GET['username']) ? $_GET['username'] : '';
        $email = isset($_GET['email']) ? $_GET['email'] : '';
        $password = isset($_GET['password']) ? $_GET['password'] : '';
        $checkUser = "SELECT username FROM Users WHERE username = '" .$username. "'";
        $q = $db->prepare($checkUser);
        $q->execute();
        $result = $q->fetch(PDO::FETCH_ASSOC);    
        
        $checkEmail = "SELECT username FROM Users WHERE username = '" .$email. "'";
        $w = $db->prepare($checkEmail);
        $w->execute();
        $result = $w->fetch(PDO::FETCH_ASSOC);
        
        if ($checkUser == true) {
            echo '{"result": "fail", "error": "Username already existed!"}';
        } else if ($checkEmail == true) {
            echo '{"result": "fail", "error": "Email already existed!"}';
        } else {
            if (validEmail($email) == true){
                $sql = "INSERT INTO Users VALUES ('$username', '$email', '$password')";             
                //attempts to add record
                if ($db->query($sql)) {
                    echo '{"result": "success"}';
                } 
            } else {
                echo '{"result": "fail", "error": "Email id invalid!"}';
            }
        }   
    }

    /**
    Validate an email address.
    Provide email address (raw input)
    Returns true if the email address has the email 
    address format and the domain exists.
    */
    function validEmail($email)
    {
       $isValid = true;
       $atIndex = strrpos($email, "@");
       if (is_bool($atIndex) && !$atIndex) {
          $isValid = false;
       } else {
          $domain = substr($email, $atIndex+1);
          $local = substr($email, 0, $atIndex);
          $localLen = strlen($local);
          $domainLen = strlen($domain);
          if ($localLen < 1 || $localLen > 64) {
             // local part length exceeded
             $isValid = false;
          } else if ($domainLen < 1 || $domainLen > 255) {
             // domain part length exceeded
             $isValid = false;
          } else if ($local[0] == '.' || $local[$localLen-1] == '.') {
             // local part starts or ends with '.'
             $isValid = false;
          } else if (preg_match('/\\.\\./', $local)) {
             // local part has two consecutive dots
             $isValid = false;
          } else if (!preg_match('/^[A-Za-z0-9\\-\\.]+$/', $domain)) {
             // character not valid in domain part
             $isValid = false;
          } else if (preg_match('/\\.\\./', $domain)) {
             // domain part has two consecutive dots
             $isValid = false;
          } else if (!preg_match('/^(\\\\.|[A-Za-z0-9!#%&`_=\\/$\'*+?^{}|~.-])+$/',
                     str_replace("\\\\","",$local))) {
             // character not valid in local part unless 
             // local part is quoted
             if (!preg_match('/^"(\\\\"|[^"])+"$/', str_replace("\\\\","",$local))) {
                $isValid = false;
             }
          } if ($isValid && !(checkdnsrr($domain,"MX") || checkdnsrr($domain,"A"))) {
             // domain not found in DNS
             $isValid = false;
          }
       }
       return $isValid;
    }
?>

