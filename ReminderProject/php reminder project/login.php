<?php
    ini_set('display_errors', '1');
    error_reporting(E_ALL);

    $dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=navy1103';
    $username = 'navy1103';
    $password = 'MavOnum';

    try {
        $db = new PDO($dsn, $username, $password);    
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        
        $tag = isset($_GET['tag']) ? $_GET['tag'] : '';
        
        if($tag === 'login'){
            login($db);
        } else if ($tag === 'register'){
            register($db);            
        } else if ($tag === 'forget'){
            forget($db);
        } else if ($tag === 'profile'){
            getProfile($db);
        } 
        else if ($tag === 'changePass'){
            changePass($db);
        } else if ($tag === 'update'){
            updateProfile($db);
        }     

        $db = null;
    
    } catch (PDOException $e) {
        die("Could not connect to the database :" . $e->getMessage());
    }
    
    /**
    Get user profile
    */
    function getProfile($db){
        $username = isset($_GET['username']) ? $_GET['username'] : '';
        
        $sql = "SELECT firstName, lastName, email FROM Users ";
        $sql .= " WHERE username = '" .$username. "'";

        $q = $db->prepare($sql);
        $q->execute();
        $result = $q->fetch(PDO::FETCH_ASSOC);
            
        echo '{"result": "success", "first": "'.$result['firstName'].'", "last": "' . $result['lastName'] . '", "email": "' . $result['email'] . '"}'; 
    }

    /**
    Get the username and password from the URL and check with database
    */
    function login($db){
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
                echo '{"result": "success", "username": "'.$result['username'].'"}';        
            else 
                echo '{"result": "fail", "error": "Incorrect password."}';
        } else {
            echo '{"result": "fail", "error": "Incorrect username."}';
        }        
    }

    /**
    Register new account
    */
    function register($db){
        //get inputs for registering information
        $username = isset($_GET['username']) ? $_GET['username'] : '';
        $email = isset($_GET['email']) ? $_GET['email'] : '';
        $password = isset($_GET['password']) ? $_GET['password'] : '';
        
        $checkUser = "SELECT username FROM Users WHERE username = '" .$username. "'";
        $q = $db->prepare($checkUser);
        $q->execute();
        //$userRow = $q->rowCount();
        $userRow = $q->fetch(PDO::FETCH_ASSOC);    
        
        $checkEmail = "SELECT username FROM Users WHERE email = '" .$email. "'";
        $w = $db->prepare($checkEmail);
        $w->execute();
        //$emailRow = $q->rowCount();
        $emailRow = $w->fetch(PDO::FETCH_ASSOC);
        
        if ($userRow == true) {
            echo '{"result": "fail", "error": "Username already existed!"}';
        } else if ($emailRow == true) {
            echo '{"result": "fail", "error": "Email already existed!"}';
        } else {
            if (validEmail($email) == true){
                $sql = "INSERT INTO Users VALUES ('$username', '', '', '$email', '$password')";             
                //attempts to add record
                if ($db->query($sql)) {
                    echo '{"result": "success", "username": "'. $username. '"}';
                } 
            } else {
                echo '{"result": "fail", "error": "Email is invalid!"}';
            }
        }   
    }
    
    /**
    User forget password or id
    */
    function forget($db){
        $email = isset($_GET['email']) ? $_GET['email'] : '';
        
        $checkEmail = "SELECT username, email FROM Users WHERE email = '" .$email. "'";
        $w = $db->prepare($checkEmail);
        $w->execute();
        $result = $w->fetch(PDO::FETCH_ASSOC);
        
        if ($result == true) {
            //set up for new password and email
            $newpass = randomPassword();
            $to      = $result['email'];
            $subject = 'Password Recovery';
            $message = 'Hello '.$result['username'].',' 
                        . "\r\n" . 'Your password is sucessfully changed. Your new Password is '.$newpass.''
                        . "\r\n" . 'Login with your new password and you can change this password in the user profile.' 
                        . "\r\n" . 'Regards, Customer Service Team.';
        
            $headers = 'From: customer@reminder.com' . "\r\n" .
                    'Reply-To: customer@reminder.com' . "\r\n" .
                    'X-Mailer: PHP/' . phpversion();
            mail($to, $subject, $message, $headers);
             //build query
            $sql = "UPDATE Users SET password = '$newpass' WHERE email = '$email'";
            $db->query($sql);            
            
            echo '{"result": "success"}';            
        } else {
            echo '{"result": "fail", "error": "Email is not existed!"}';
        }
    }    

    /**
     * Random string which is sent by mail to reset password
     */ 
    function randomPassword() {
        $alphabet = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';
        $pass = array(); //remember to declare $pass as an array
        $alphaLength = strlen($alphabet) - 1; //put the length -1 in cache
        for ($i = 0; $i < 8; $i++) {
            $n = rand(0, $alphaLength);
            $pass[] = $alphabet[$n];
        }
        return implode($pass); //turn the array into a string
    }

//    /**
//    Update profile
//    */
    function updateProfile($db){
        $username = isset($_GET['username']) ? $_GET['username'] : '';
        $first = isset($_GET['first']) ? $_GET['first'] : '';
        $last = isset($_GET['last']) ? $_GET['last'] : '';
        $email = isset($_GET['email']) ? $_GET['email'] : '';
        
        if (validEmail($email) == true){
            $sql = "UPDATE Users SET firstName = '$first', lastName = '$last', email = '$email' WHERE username = '".$username."'";
            if($db->query($sql))            
                echo '{"result": "success"}';
            else
                echo '{"result": "fail", "error": "Can not update user profile."}';
        } else {
                echo '{"result": "fail", "error": "Email is invalid!"}';
        }
    }

    /**
    Change password
    */
    function changePass($db){
        $username = isset($_GET['username']) ? $_GET['username'] : '';            
        $oldPass = isset($_GET['oldPass']) ? $_GET['oldPass'] : '';
        $newPass = isset($_GET['newPass']) ? $_GET['newPass'] : '';

        $sql = "SELECT username, password FROM Users ";
        $sql .= " WHERE username = '" .$username. "'";

        $q = $db->prepare($sql);
        $q->execute();
        $result = $q->fetch(PDO::FETCH_ASSOC);

        //on success, return the user id
        if (strcmp($oldPass, $result['password']) == 0){
            $sql = "UPDATE Users SET password = '$newPass' WHERE username = '".$username."'";
            $db->query($sql);            
            echo '{"result": "success"}';
        } else {
            echo '{"result": "fail", "error": "Incorrect password."}';
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