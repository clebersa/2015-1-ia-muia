$( document ).ready( function() {
	$("input[name=btnStartServer]").click( function( e ) {
		startServer();
	});
	
	$("input[name=btnStopServer]").click( function( e ) {
		stopServer();
	});
	
	$("input[name=btnRegisterClient]").click( function( e ) {
		registerClient();
	});
	
	$("input[name=btnSubscribeIntoChannel]").click( function( e ) {
		var channelId = $("input[name=inpChannelSubscribeId]").val();
		subscribeClientIntoChannel(channelId);
	});
	
	$("input[name=btnCreateChannel]").click( function( e ) {
		var maxSubscribers = $("input[name=inpMaxSubscribers]").val();
		var maxRetries = $("input[name=inpMaxRetries]").val();
		var retryInterval = $("input[name=inpRetryInterval]").val();
		var sendTimeout = $("input[name=inpSendTimeout]").val();
		var channelDescription = $("textarea[name=txtAChannelDescription]").val();
		
		createChannel(channelDescription, maxSubscribers, maxRetries, retryInterval, sendTimeout);
	});
	
	$("input[name=btnSend]").click( function( e ) {
		var base64Message = btoa($("input[name=txtSendMessage]").val());
		var add = $("textarea[name=txtAChat]").val();
		if(add != "") {
			add += "\n";
		}
		var concat = add + "Eu: " + $("input[name=txtSendMessage]").val();
		$("textarea[name=txtAChat]").val(concat);
		$('textarea[name=txtAChat]').scrollTop($('textarea[name=txtAChat]')[0].scrollHeight);
		
		$("input[name=txtSendMessage]").val("");
		$("input[name=txtSendMessage]").focus();
		
		sendMessage(base64Message);
	});
	
	$("input[name=txtSendMessage]").keypress( function( e ) {
		if(e.keyCode === 13) {
			$("input[name=btnSend]").trigger("click");
			return false;
        }
	});
	
	preventNumberInputWrongKeys();
	
	setInterval(getChatMessages, 1000);
});

function preventNumberInputWrongKeys() {
	$("input[type=number]").keydown(function (e) {
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
            (e.keyCode == 65 && ( e.ctrlKey === true || e.metaKey === true ) ) || 
            (e.keyCode >= 35 && e.keyCode <= 40)) {
                 return;
        }
        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
            e.preventDefault();
        }
    });
}

function createChannel(channelDescription, maxSubscribers, maxRetries, retryInterval, sendTimeout) {
	inputData = {
		'action' : 'CREATE_CHANNEL',
		'description' : channelDescription,
		'maxSubscribers' : maxSubscribers,
		'maxRetries' : maxRetries,
		'retyInterval' : retryInterval,
		'sendTimeout' : sendTimeout
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			console.log(msg);
		}
	});
}

function registerClient() {
	inputData = {
		'action' : 'REGISTER_CLIENT'
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			var status = msg['status'];
			var btnRegisterClient = $("input[name=btnRegisterClient]");
			
			switch(status) {
				case 10:
					btnRegisterClient.val("Cliente registrado com sucesso!");
					break;
				case 11:
					btnRegisterClient.val("Erro: Cliente já registrado");
					break;
			}
			
			btnRegisterClient.prop("disabled", "true");
		}
	});
}

function subscribeClientIntoChannel(channelId) {
	inputData = {
		'action' : 'SUBSCRIBE_CLIENT_INTO_CHANNEL',
		'channelId' : channelId
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {}
	});
}

function startServer() {
	var btnStartServer = $("input[name=btnStartServer]");
	var btnStopServer = $("input[name=btnStopServer]");
	
	btnStartServer.val("Servidor iniciado");
	btnStopServer.val("Finalizar servidor");
	btnStartServer.prop("disabled", "true");
	btnStopServer.removeProp("disabled");
	
	inputData = {
		'action' : 'START_SERVER'
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {}
	});
}

function stopServer() {
	inputData = {
		'action' : 'STOP_SERVER'
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			var btnStartServer = $("input[name=btnStartServer]");
			var btnStopServer = $("input[name=btnStopServer]");
			
			btnStartServer.val("Iniciar servidor");
			btnStopServer.val("Servidor finalizado");
			btnStartServer.removeProp("disabled");
			btnStopServer.prop("disabled", "true");
		}
	});
}

function sendMessage(base64Message, channelId) {
	inputData = {
		'action' : 'SEND_MESSAGE',
		'messageValue' : base64Message,
		'channelId' : channelId
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {}
	});
}

function getChatMessages() {
	inputData = {
		'action' : 'GET_MESSAGES_FROM_SERVER'
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			if(msg == null ) {
				return;
			}
			
			$.each(msg, function( index, value ) {
				try {
					var message = value['message-packet'];
					message = atob(message['message-data']['value']);
					
					var sender = value['message-packet']['header-data'];
					sender = sender['source'];
					
					var add = $("textarea[name=txtAChat]").val();
					if(add != "") {
						add += "\n";
					}
					var concat = add + sender + ": " + message;
					$("textarea[name=txtAChat]").val(concat);
					$('textarea[name=txtAChat]').scrollTop($('textarea[name=txtAChat]')[0].scrollHeight);
				} catch(e) {}
			});
		}
	});
}
