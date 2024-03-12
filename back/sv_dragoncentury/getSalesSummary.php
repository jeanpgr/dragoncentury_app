<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Origin, X-Requested-with, Content-type, Accept, Authorization");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");

include 'conexion.php';

$date_dsd_rv = isset($_REQUEST['date_dsd_rv']) ? $_REQUEST['date_dsd_rv'] : null;
$date_hst_rv = isset($_REQUEST['date_hst_rv']) ? $_REQUEST['date_hst_rv'] : null;

if ($date_dsd_rv !== null && $date_hst_rv !== null) {
    
    $sqlDtRep = "SELECT rv.id_reporte, u.nomb_user, u.apell_user, rv.fecha, rv.total_vueltas, rv.total_cortesias, 
                        ng.gasto_total, rv.total_venta
                        FROM reporte_vueltas AS rv 
                        INNER JOIN novedades_gastos AS ng ON rv.id_nov_gasto_per = ng.id_nov_gasto
                        INNER JOIN usuarios AS u ON rv.id_user_per = u.id_user
                        WHERE rv.fecha BETWEEN ? AND ?
                        ORDER BY rv.id_reporte ASC";

    $sqlSumTotVen = "SELECT SUM(total_venta) AS sum_tot_ven
                        FROM reporte_vueltas
                        WHERE fecha BETWEEN '$date_dsd_rv' AND '$date_hst_rv'";

    $sqlSumTotGas = "SELECT SUM(ng.gasto_total) AS sum_tot_gas
                        FROM novedades_gastos AS ng
                        INNER JOIN reporte_vueltas AS rv ON rv.id_nov_gasto_per = ng.id_nov_gasto
                        WHERE rv.fecha BETWEEN '$date_dsd_rv' AND '$date_hst_rv'";

    $sqlSumTotCort = "SELECT SUM(total_cortesias) AS sum_tot_cor
                        FROM reporte_vueltas
                        WHERE fecha BETWEEN '$date_dsd_rv' AND '$date_hst_rv'";
    

    $stmtDtRep = $conexion->prepare($sqlDtRep);
    $resultSumTotVen = $conexion->query($sqlSumTotVen);
    $resultSumTotGas = $conexion->query($sqlSumTotGas);
    $resultSumTotCor = $conexion->query($sqlSumTotCort);

    $stmtDtRep->bind_param("ss", $date_dsd_rv, $date_hst_rv);

    $stmtDtRep->execute();

    $resultDtRep = $stmtDtRep->get_result();

// Obtener los datos de los reportes
$reportes = array();
while ($row = $resultDtRep->fetch_assoc()) {
    $reportes[] = $row;
}

// Obtener el total de ventas
$total_venta = $resultSumTotVen->fetch_assoc()['sum_tot_ven'];

// Obtener el total de gastos
$total_gasto = $resultSumTotGas->fetch_assoc()['sum_tot_gas'];

$total_cortesia = $resultSumTotCor->fetch_assoc()['sum_tot_cor'];

// Combinar los resultados en un solo arreglo asociativo
$resultado_final = array(
    "detail_sales" => $reportes,
    "sum_tot_ven" => $total_venta,
    "sum_tot_gas" => $total_gasto,
    "sum_tot_cor" => $total_cortesia
);

// Convertir el arreglo asociativo a JSON
$json_resultado = json_encode($resultado_final, JSON_PRETTY_PRINT);

echo $json_resultado;

$stmtDtRep->close();
$conexion->close();
} else {
echo json_encode(array('error' => 'Las fechas desde y hasta son requeridas'), JSON_PRETTY_PRINT);
}
