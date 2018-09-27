<?php

$host='127.0.0.1';
$username='root';
$pwd='';
$db="halal_food";

$con=mysqli_connect($host,$username,$pwd,$db) or die('Unable to connect');

if(mysqli_connect_error($con))
{
    echo "Failed to Connect to Database ".mysqli_connect_error();
}

$result=mysqli_query($con,"SELECT * FROM restaurant");
$rows = array();
while($r = mysqli_fetch_assoc($result)) {
    $rows[] = $r;
}
 print(json_encode($rows));
mysqli_close($con);

?>
