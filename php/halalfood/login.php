<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
  $email = $_POST['email'];
  $password = $_POST['password'];

  echo "No";
  require_once 'connect.php';


  $sql = "SELECT * FROM user WHERE email = '$email'";

  echo $sql;

  $response = mysqli_connect($conn, $sql);
  $result = array();

  $result['login'] = array();


  if (mysqli_num_rows($response) === 1) {
    // code...

    $row = mysqli_fetch_assoc($response);


    if (password_verify($password, $row['password'])) {
      // code...
      $index['name'] = $row['name'];
      $index['email']  = $row['email'];

      array_push($result['login'], $index);

      $result['success']  = "1";
      $result['message']  = "Success";


      echo json_encode($result);
      echo "string";

      mysqli_close($conn);


    }else {
      $result['success']  = "0";
      $result['message']  = "Error";
      echo "string";


      echo json_encode($result);
      mysqli_close($conn);

    }
  }

  // code...
}
 ?>
