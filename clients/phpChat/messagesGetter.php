<?php
	include("config.php");
	
	$mensagem = "GET_MESSAGES";
	$length = strlen($mensagem);
	
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	socket_connect($socket, $config['client']['ip_address'], $config['client']['port']);
	socket_write($socket, $mensagem, $length);
	
	$result = "";
	while( socket_recv($socket, $buffer, 1024, 0 ) ) {
		$result .= $buffer;
	}
	
	socket_close($socket);
	
	header('Content-Type: application/json');
	$result = json_decode($result, true);
	echo json_encode($result, JSON_PRETTY_PRINT);
?>
