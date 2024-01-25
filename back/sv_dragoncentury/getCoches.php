<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

$sqlGetCoches = "SELECT id_coche, img_coche, nomb_coche, color_coche, 
                num_charges, num_change_battery, total_vueltas, condic_coche
                 FROM coches";

$resp = $conexion->query($sqlGetCoches);

$result = array();

if ($resp->num_rows > 0) {
    while ($row = $resp->fetch_assoc()) {
        // Obtener el contenido binario de la imagen
        $imgData = $row['img_coche'];

        // Codificar la imagen en base64
        $imgBase64 = base64_encode($imgData);

        // Agregar la imagen codificada al arreglo de resultados
        $row['img_coche'] = $imgBase64;

        array_push($result, $row);
    }
} else {
    $result = "No existen coches";
}

echo json_encode($result);

$conexion->close();
?>
