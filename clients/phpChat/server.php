<?php
	error_reporting(E_ALL);
	include("config.php");
	
	$registrationPacket = array(
		"connection-header" => array(
			"app-type" => "client",
			"app-name" => $config['client']['name']
		),
		"message-packet" => array(
			"header-type" => "registration",
			"header-data" => array(
				"app-name" => $config['client']['name'],
				"app-address" => $config['client']['ip_address'],
				"app-port" => $config['client']['port'],
				"register" => true
			),
			"message-data" => array(
				"value" => ""
			)
		)
	);
	
	$channelSubscribePacket = array(
		"connection-header" => array(
			"app-type" => "client",
			"app-name" => $config['client']['name']
		),
		"message-packet" => array(
			"header-type" => "channel-subscribing",
			"header-data" => array(
				"channel" => "default",
				"client" => $config['client']['name'],
				"subscribe" => true
			),
			"message-data" => array(
				"value" => ""
			)
		)
	);
	
	$mensagem = json_encode($registrationPacket);
	$length = strlen($mensagem);
	
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
	socket_write($socket, $mensagem, $length);
	$response = socket_read($socket, 1024);
	socket_close($socket);
	
	$mensagem = json_encode($channelSubscribePacket);
	$length = strlen($mensagem);
	
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
	socket_write($socket, $mensagem, $length);
	$response = socket_read($socket, 1024);
	socket_close($socket);
	
	// Start server
	set_time_limit (0);
	
	$sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	socket_bind($sock, $config['client']['ip_address'], $config['client']['port']) or die('Could not bind to address');
	socket_listen($sock);
	
	$receivedMessages = array();
	
	while (true) {
		$client =  socket_accept($sock);
		$input =  socket_read($client, 1024000);
		
		switch($input) {
			case "SHUTDOWN":
				break 2;
			case "GET_MESSAGES":
				$writeMessages = json_encode($receivedMessages);
				$length = strlen($writeMessages);
				socket_write($client, $writeMessages, $length);
				$receivedMessages = array();
				break;
			default:
				$result = array("status" => 0);
				$writeMessages = json_encode($result);
				$length = strlen($writeMessages);
				socket_write($client, $writeMessages, $length);
				$receivedMessages[] = json_decode($input, true);
				break;
		}
		socket_close($client);
	}
	socket_close($sock);
?>
