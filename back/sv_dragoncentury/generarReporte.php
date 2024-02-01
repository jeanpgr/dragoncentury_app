<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

// Obtén el contenido JSON de la solicitud
$json_data = file_get_contents('php://input');

// Decodifica el JSON a un array asociativo
$data = json_decode($json_data, true);

$withGasto = isset($data['with_gasto']) ? $data['with_gasto'] : '';
$descrip_nov = isset($data['descrip_nov']) ? $data['descrip_nov'] : '';
$gasto_total = isset($data['gasto_total']) ? $data['gasto_total'] : '';

$id_user_per = isset($data['id_user_per']) ? $data['id_user_per'] : '';
$fecha = isset($data['fecha']) ? $data['fecha'] : '';
$total_vueltas = isset($data['total_vueltas']) ? $data['total_vueltas'] : '';
$total_venta = isset($data['total_venta']) ? $data['total_venta'] : '';

$detalle_reporte = isset($data['detalle_reporte']) ? $data['detalle_reporte'] : '';

if ($withGasto == "true") {
    $sqlMaxIdGasto = "SELECT MAX(id_nov_gasto) AS max_id_gasto FROM novedades_gastos";
    $resultMaxIdGasto = $conexion->query($sqlMaxIdGasto);
    $fila = $resultMaxIdGasto->fetch_assoc();
    $max_id_gasto = $fila['max_id_gasto'];
    $next_id_gasto = $max_id_gasto + 1;

    $sqlInsertGasto = "INSERT INTO novedades_gastos (id_nov_gasto, descrip_nov, gasto_total) VALUES (?, ?, ?)";
    $stmtIG = $conexion->prepare($sqlInsertGasto);
    $stmtIG->bind_param("isd", $next_id_gasto, $descrip_nov, $gasto_total);
    $stmtIG->execute();
    $stmtIG->close();

    $sqlMaxIdReport = "SELECT MAX(id_reporte) AS max_id_report FROM reporte_vueltas";
    $resultMaxIdReport = $conexion->query($sqlMaxIdReport);
    $row = $resultMaxIdReport->fetch_assoc();
    $max_id_report = $row['max_id_report'];
    $next_id_report = $max_id_report + 1;

    $sqlInsertReport = "INSERT INTO reporte_vueltas (id_reporte, id_nov_gasto_per, id_user_per, 
                        fecha, total_vueltas, total_venta) VALUES (?, ?, ?, ?, ?, ?)";
    $stmtIR = $conexion->prepare($sqlInsertReport);
    $stmtIR->bind_param("iiisid", $next_id_report, $next_id_gasto, $id_user_per, $fecha, $total_vueltas, $total_venta);
    $stmtIR->execute();
    $stmtIR->close();

    $sqlInsertDetalle = "INSERT INTO detalle_vuelta (id_reporte_per, id_coche_per, lectura_inicial, lectura_final, num_vueltas)
                            VALUES (?, ?, ?, ?, ?)";
    $stmtID = $conexion->prepare($sqlInsertDetalle);

    foreach ($detalle_reporte as $dr) {

        $id_coche_per = $dr['id_coche_per'];
        $lectura_inicial = $dr['lectura_inicial'];
        $lectura_final = $dr['lectura_final'];
        $num_vueltas = $dr['num_vueltas'];

        $stmtID->bind_param("iiiii", $next_id_report, $id_coche_per, $lectura_inicial, $lectura_final, $num_vueltas);

        $stmtID->execute();
    }

    $stmtID->close();

} else {

    $sqlMaxIdReport = "SELECT MAX(id_reporte) AS max_id_report FROM reporte_vueltas";
    $resultMaxIdReport = $conexion->query($sqlMaxIdReport);
    $row = $resultMaxIdReport->fetch_assoc();
    $max_id_report = $row['max_id_report'];
    $next_id_report = $max_id_report + 1;

    $id_nov_gasto_per = 1;

    $sqlInsertReport = "INSERT INTO reporte_vueltas (id_reporte, id_nov_gasto_per, id_user_per, 
                        fecha, total_vueltas, total_venta) VALUES (?, ?, ?, ?, ?, ?)";
    $stmtIR = $conexion->prepare($sqlInsertReport);
    $stmtIR->bind_param("iiisid", $next_id_report, $id_nov_gasto_per, $id_user_per, $fecha, $total_vueltas, $total_venta);
    $stmtIR->execute();
    $stmtIR->close();

    $sqlInsertDetalle = "INSERT INTO detalle_vuelta (id_reporte_per, id_coche_per, lectura_inicial, lectura_final, num_vueltas)
                            VALUES (?, ?, ?, ?, ?)";
    $stmtID = $conexion->prepare($sqlInsertDetalle);

    foreach ($detalle_reporte as $dr) {

        $id_coche_per = $dr['id_coche_per'];
        $lectura_inicial = $dr['lectura_inicial'];
        $lectura_final = $dr['lectura_final'];
        $num_vueltas = $dr['num_vueltas'];

        $stmtID->bind_param("iiiii", $next_id_report, $id_coche_per, $lectura_inicial, $lectura_final, $num_vueltas);

        $stmtID->execute();
    }

    $stmtID->close();   
}

$conexion->close();

?>