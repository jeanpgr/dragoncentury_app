<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

$sqlMaxIdRep = "SELECT MAX(id_reporte) AS max_id_rep FROM reporte_vueltas";
$resultMaxIdRep = $conexion->query($sqlMaxIdRep);
$fila = $resultMaxIdRep->fetch_assoc();
$max_id_rep = $fila['max_id_rep'];
$min_id_rep = $max_id_rep - 4;

$sqlGetFiveReports = "SELECT rv.id_reporte, rv.id_user_per, rv.fecha, rv.total_vueltas, rv.total_cortesias,
                        rv.total_venta, ng.descrip_nov, ng.gasto_total, c.id_coche, c.nomb_coche, dv.lectura_inicial,
                        dv.lectura_final, dv.num_vueltas, u.nomb_user, u.apell_user 
                        FROM reporte_vueltas AS rv 
                        INNER JOIN novedades_gastos AS ng ON rv.id_nov_gasto_per = ng.id_nov_gasto 
                        INNER JOIN detalle_vuelta AS dv ON rv.id_reporte = dv.id_reporte_per 
                        INNER JOIN usuarios AS u ON rv.id_user_per = u.id_user 
                        INNER JOIN coches AS c ON dv.id_coche_per = c.id_coche
                        WHERE rv.id_reporte BETWEEN ? AND ?
                        ORDER BY rv.id_reporte DESC";

$stmt = $conexion->prepare($sqlGetFiveReports);
$stmt->bind_param("ii", $min_id_rep, $max_id_rep);
$stmt->execute();
$resultado = $stmt->get_result();
$reportes = array();

while ($fila = $resultado->fetch_assoc()) {
    $idReporte = $fila['id_reporte'];

    $coche = array(
        'id_coche' => $fila['id_coche'],
        'nomb_coche' => $fila['nomb_coche'],
        'lectura_inicial' => $fila['lectura_inicial'],
        'lectura_final' => $fila['lectura_final'],
        'num_vueltas' => $fila['num_vueltas']
    );

    if (!isset($reportes[$idReporte])) {
        $reportes[$idReporte] = array(
            'id_reporte' => $fila['id_reporte'],
            'id_user_per' => $fila['id_user_per'],
            'fecha' => $fila['fecha'],
            'total_vueltas' => $fila['total_vueltas'],
            'total_cortesias' => $fila['total_cortesias'],
            'total_venta' => $fila['total_venta'],
            'descrip_nov' => $fila['descrip_nov'],
            'gasto_total' => $fila['gasto_total'],
            'nombs_user' => $fila['nomb_user'] . " " . $fila['apell_user'],
            'coches' => array()
        );
    }

    $reportes[$idReporte]['coches'][] = $coche;
}

$json_resultado = json_encode(array_values($reportes), JSON_PRETTY_PRINT);

echo $json_resultado;

$stmt->close();
$conexion->close();

?>
