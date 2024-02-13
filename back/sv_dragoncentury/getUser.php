<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

$id_user = isset($_REQUEST['id_user']) ? $_REQUEST['id_user'] : null;

$sqlGetUser = "SELECT id_user, nomb_user, apell_user, rol_user FROM usuarios WHERE id_user = ?";

$stmt = $conexion->prepare($sqlGetUser);
$stmt->bind_param("i", $id_user);
$stmt->execute();

$resultado = $stmt->get_result();
if ($row = $resultado->fetch_assoc()) {
    echo json_encode($row, JSON_FORCE_OBJECT);     
}
mysqli_free_result($resultado);
$stmt->close();
$conexion->close();

?>
