<?php
	error_reporting(E_ALL);
	include("config.php");
	
	if(isset($_POST['message'])) {
		$messageValue = $_POST['message'];
	} else {
		$messageValue = "";
	}
	
	$packet = array(
		"connection-header" => array(
			"app-type" => "client",
			"app-name" => $config['client']['name']
		),
		"message-packet" => array(
			"header-type" => "messaging",
			"header-data" => array(
				"channel" => "default",
				"source" => $config['client']['name']
			),
			"message-data" => array(
				"value" => $messageValue
			)
		)
	);
	
	$mensagem = json_encode($packet);
	$length = strlen($mensagem);
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
	socket_write($socket, $mensagem, $length);
    socket_close($socket);
?>
