<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

$withGasto = isset($_REQUEST['with_gasto']) ? $_REQUEST['with_gasto'] : '';
$descrip_nov = isset($_REQUEST['descrip_nov']) ? $_REQUEST['descrip_nov'] : '';
$gasto_total = isset($_REQUEST['gasto_total']) ? $_REQUEST['gasto_total'] : '';

$id_user_per = isset($_REQUEST['id_user_per']) ? $_REQUEST['id_user_per'] : '';
$fecha = isset($_REQUEST['fecha']) ? $_REQUEST['fecha'] : '';
$total_vueltas = isset($_REQUEST['total_vueltas']) ? $_REQUEST['total_vueltas'] : '';
$total_venta = isset($_REQUEST['total_venta']) ? $_REQUEST['total_venta'] : '';

$detalle_reporte = $_REQUEST['detalle_reporte'];

if ($withGasto == "true") {
    $sqlMaxIdGasto = "SELECT MAX(id_nov_gasto) AS max_id_gasto FROM novedades_gastos";
    $resultMaxIdGasto = $conexion->query($sqlMaxIdGasto);
    $fila = $resultMaxIdGasto->fetch_assoc();
    $max_id_gasto = $fila['max_id_gasto'];
    $next_id_gasto = $maximo_id_gasto + 1;

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

    $sqlInsertDetalle = "INSERT INTO detalle_vuelta (id_reporte_per, id_coche_per, lectura_inicial, lectura_final, num_vueltas)";
    $stmtID = $conexion->prepare($sqlInsertDetalle);

    foreach ($detalle_reporte as $dr) {
        $id_reporte_per = $dr['id_reporte_per'];
        $id_coche_per = $dr['id_coche_per'];
        $lectura_inicial = $dr['lectura_inicial'];
        $lectura_final = $dr['lectura_final'];
        $num_vueltas = $dr['num_vueltas'];

        $stmtID->bind_param("iiiii", $id_reporte_per, $id_coche_per, $lectura_inicial, $lectura_final, $num_vueltas);

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

    $sqlInsertDetalle = "INSERT INTO detalle_vuelta (id_reporte_per, id_coche_per, lectura_inicial, lectura_final, num_vueltas)";
    $stmtID = $conexion->prepare($sqlInsertDetalle);

    foreach ($detalle_reporte as $dr) {
        $id_reporte_per = $dr['id_reporte_per'];
        $id_coche_per = $dr['id_coche_per'];
        $lectura_inicial = $dr['lectura_inicial'];
        $lectura_final = $dr['lectura_final'];
        $num_vueltas = $dr['num_vueltas'];

        $stmtID->bind_param("iiiii", $id_reporte_per, $id_coche_per, $lectura_inicial, $lectura_final, $num_vueltas);

        $stmtID->execute();
    }

    $stmtID->close();   
}

$conexion->close();

?>