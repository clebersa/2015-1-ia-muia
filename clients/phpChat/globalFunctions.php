<?php
	function createChannel($description, $maxSubscribers, $maxRetries, $retyInterval, $sendTimeout) {
		require("config.php");
		
		$packet = array(
			"connection-header" => array(
				"app-type" => "client",
				"app-name" => $config['client']['name']
			),
			"message-packet" => array(
				"header-type" => "channel-creating",
				"header-data" => array(
					"description" => $description,
					"max-subscribers" => $maxSubscribers,
					"max-retries" => $maxRetries,
					"retry-interval" => $retyInterval,
					"timeout" => $sendTimeout
				),
				"message-data" => array(
					"value" => ""
				)
			)
		);
		
		$message = json_encode($packet);
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
		socket_write($socket, $message, $length);
		
		$response = "";
		while( socket_recv($socket, $buffer, 1024, 0 ) ) {
			$response .= $buffer;
		}
		
		socket_close($socket);
		
		$response = json_decode($response, true);
		return $response;
	}
	
	function subscribeClientIntoChannel($subscribe = true, $channelId) {
		require("config.php");
		
		$packet = array(
			"connection-header" => array(
				"app-type" => "client",
				"app-name" => $config['client']['name']
			),
			"message-packet" => array(
				"header-type" => "channel-subscribing",
				"header-data" => array(
					"channel" => $channelId,
					"client" => $config['client']['name'],
					"subscribe" => $subscribe
				),
				"message-data" => array(
					"value" => ""
				)
			)
		);
		
		$message = json_encode($packet);
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
		socket_write($socket, $message, $length);
		
		$response = "";
		while( socket_recv($socket, $buffer, 1024, 0 ) ) {
			$response .= $buffer;
		}
		
		socket_close($socket);
		
		$response = json_decode($response, true);
		return $response;
	}
	
	function getAvailableChannels() {
		require("config.php");
		
		$packet = array(
			"connection-header" => array(
				"app-type" => "client",
				"app-name" => $config['client']['name']
			),
			"message-packet" => array(
				"header-type" => "channel-get-available-channels",
				"header-data" => array()
			)
		);
		
		$message = json_encode($packet);
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
		socket_write($socket, $message, $length);
		
		$response = "";
		while( socket_recv($socket, $buffer, 1024, 0 ) ) {
			$response .= $buffer;
		}
		
		socket_close($socket);
		
		$response = json_decode($response, true);
		return $response;
	}
	
	function setClientAvailable($isAvailable = true) {
		require("config.php");
		
		$packet = array(
			"connection-header" => array(
				"app-type" => "client",
				"app-name" => $config['client']['name']
			),
			"message-packet" => array(
				"header-type" => "messaging-availability",
				"header-data" => array(
					"client" => $config['client']['name'],
					"available" => $isAvailable,
				)
			)
		);
		
		$message = json_encode($packet);
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
		socket_write($socket, $message, $length);
		
		$response = "";
		while( socket_recv($socket, $buffer, 1024, 0 ) ) {
			$response .= $buffer;
		}
		
		socket_close($socket);
		
		$response = json_decode($response, true);
		return $response;
	}
	
	function registerClient($register = true) {
		require("config.php");
		
		$packet = array(
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
					"register" => $register
				),
				"message-data" => array(
					"value" => ""
				)
			)
		);
		
		$message = json_encode($packet);
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
		socket_write($socket, $message, $length);
		
		$response = "";
		while( socket_recv($socket, $buffer, 1024, 0 ) ) {
			$response .= $buffer;
		}
		
		socket_close($socket);
		
		$response = json_decode($response, true);
		return $response;
	}
	
	function startChatServer() {
		require("config.php");
		
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
	}
	
	function shutdownChatServer() {
		require("config.php");
		
		$message = "SHUTDOWN";
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['client']['ip_address'], $config['client']['port']);
		socket_write($socket, $message, $length);
		
		socket_close($socket);
	}
	
	function getMessagesFromChatServer() {
		require("config.php");
		
		$message = "GET_MESSAGES";
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['client']['ip_address'], $config['client']['port']);
		socket_write($socket, $message, $length);
		
		$response = "";
		while( socket_recv($socket, $buffer, 1024, 0 ) ) {
			$response .= $buffer;
		}
		
		socket_close($socket);
		
		$response = json_decode($response, true);
		return $response;
	}
	
	
	function sendMessage($messageValue = "", $channelId) {
		require("config.php");
		
		$packet = array(
			"connection-header" => array(
				"app-type" => "client",
				"app-name" => $config['client']['name']
			),
			"message-packet" => array(
				"header-type" => "messaging",
				"header-data" => array(
					"channel" => $channelId,
					"source" => $config['client']['name']
				),
				"message-data" => array(
					"value" => $messageValue
				)
			)
		);
		
		$message = json_encode($packet);
		$length = strlen($message);
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, $config['muia']['ip_address'], $config['muia']['port']);
		socket_write($socket, $message, $length);
		
		$response = "";
		while( socket_recv($socket, $buffer, 1024, 0 ) ) {
			$response .= $buffer;
		}
		
		socket_close($socket);
		
		$response = json_decode($response, true);
		return $response;
	}
?>
