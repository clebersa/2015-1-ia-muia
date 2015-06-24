<?php
	include("config.php");
	
	$mensagem = "SHUTDOWN";
	$length = strlen($mensagem);
	
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	socket_connect($socket, $config['client']['ip_address'], $config['client']['port']);
	socket_write($socket, $mensagem, $length);
	
	socket_close($socket);
?>
