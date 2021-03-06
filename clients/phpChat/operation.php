<?php
	error_reporting(0);
	header('Content-Type: application/json');
	
	include_once("config.php");
	include_once("globalFunctions.php");
	
	if(isset($_POST['action'])) {
		$action = $_POST['action'];
	} else {
		$action = "ACTION_NOT_FOUND";
	}
	
	switch($action) {
		case "SET_CLIENT_AVAILABLE":
			if(isset($_POST['isAvailable'])) {
				$isAvailable = $_POST['isAvailable'];
			} else {
				$isAvailable = true;
			}
			$result = setClientAvailable($isAvailable);
			break;
		case "REGISTER_CLIENT":
			if(isset($_POST['register'])) {
				$register = $_POST['register'];
			} else {
				$register = true;
			}
			$result = registerClient($register);
			break;
		case "GET_AVAILABLE_CHANNELS":
			$result = getAvailableChannels();
			break;
		case "CREATE_CHANNEL":
			if(isset($_POST['description'])) {
				$description = $_POST['description'];
			} else {
				$description = "";
			}
			if(isset($_POST['maxSubscribers'])) {
				$maxSubscribers = $_POST['maxSubscribers'];
			} else {
				$maxSubscribers = 0;
			}
			if(isset($_POST['maxRetries'])) {
				$maxRetries = $_POST['maxRetries'];
			} else {
				$maxRetries = -1;
			}
			if(isset($_POST['retyInterval'])) {
				$retyInterval = $_POST['retyInterval'];
			} else {
				$retyInterval = 60000;
			}
			if(isset($_POST['sendTimeout'])) {
				$sendTimeout = $_POST['sendTimeout'];
			} else {
				$sendTimeout = 30000;
			}
			$result = createChannel($description, $maxSubscribers, $maxRetries, $retyInterval, $sendTimeout);
			break;
		case "SUBSCRIBE_CLIENT_INTO_CHANNEL":
			if(isset($_POST['channelId'])) {
				$channelId = $_POST['channelId'];
			} else {
				$channelId = "default";
			}
			if(isset($_POST['register'])) {
				$register = $_POST['register'];
			} else {
				$register = true;
			}
			$result = subscribeClientIntoChannel($register, $channelId);
			break;
		case "START_SERVER":
			startChatServer();
			break;
		case "STOP_SERVER":
			shutdownChatServer();
			break;
		case "GET_MESSAGES_FROM_SERVER":
			$result = getMessagesFromChatServer();
			break;
		case "SEND_MESSAGE":
			if(isset($_POST['messageValue'])) {
				$messageValue = $_POST['messageValue'];
			} else {
				$messageValue = "";
			}
			
			if(isset($_POST['channelId'])) {
				$channelId = $_POST['channelId'];
			} else {
				$channelId = "default";
			}
			$result = sendMessage($messageValue, $channelId);
			break;
		default:
			$result = array( "status" => 10, "action" => $action );
			break;
	}
	
	echo json_encode($result, JSON_PRETTY_PRINT);
?>
