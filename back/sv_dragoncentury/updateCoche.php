<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

$id_coche = isset($_REQUEST['id_coche']) ? $_REQUEST['id_coche'] : '';
$num_charges = isset($_REQUEST['num_charges']) ? $_REQUEST['num_charges'] : '';
$num_change_battery = isset($_REQUEST['num_change_battery']) ? $_REQUEST['num_change_battery'] : '';
$condic_coche = isset($_REQUEST['condic_coche']) ? $_REQUEST['condic_coche'] : '';

$sqlUpdateCoche = "UPDATE coches SET num_charges=?, num_change_battery=?, condic_coche=? WHERE id_coche=?";
$stmt = $conexion->prepare($sqlUpdateCoche);

$stmt->bind_param("iisi", $num_charges, $num_change_battery, $condic_coche, $id_coche);

$stmt->execute();
$stmt->close();
$conexion->close();

?>