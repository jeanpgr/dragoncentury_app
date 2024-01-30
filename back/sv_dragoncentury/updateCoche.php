<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

$id_coche= $_REQUEST['id_coche'];
$num_charges = $_REQUEST['num_charges'];
$num_change_battery = $_REQUEST['num_change_battery'];
$condic_coche = $_REQUEST['condic_coche'];

$sqlUpdateCoche = "UPDATE coches SET num_charges='$num_charges', num_change_battery='$num_change_battery',
                                         condic_coche='$condic_coche'
                                     WHERE id_coche='$id_coche'";

$conexion->query($sqlUpdateCoche);

$conexion->close();

?>