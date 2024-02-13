<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

if (isset($_REQUEST['nick_user']) && isset($_REQUEST['passw_user'])) {
	
include 'conexion.php';
$nick_user=$_REQUEST['nick_user'];
$passw_user=$_REQUEST['passw_user'];

$sentencia=$conexion->prepare("SELECT id_user, nomb_user, apell_user, rol_user FROM usuarios WHERE nick_user=? AND passw_user=?");
$sentencia->bind_param('ss',$nick_user,$passw_user);
$sentencia->execute();

$resultado = $sentencia->get_result();
if ($fila = $resultado->fetch_assoc()) {
    echo json_encode($fila,JSON_FORCE_OBJECT);     
}
mysqli_free_result($resultado);
$sentencia->close();
$conexion->close();
}
?>