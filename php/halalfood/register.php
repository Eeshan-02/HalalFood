<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

  $name = $_POST['name'];
  $mail = $_POST['email'];
  $pass = $_POST['password'];

  require_once 'connect.php';

  $sql = "INSERT INTO user(name, email, password) VALUES ('$name', '$mail', '$pass')";


  if (mysqli_query($conn, $sql)) {

    $result["success"] = "1";
    $result["message"] = "success";

    echo json_encode($result);


    // code...
  }
  else {
    // code...

    $result["success"] = "0";
    $result["message"] = "error";
    echo json_encode($result);

  }


  mysqli_close($conn);

}
 ?>
