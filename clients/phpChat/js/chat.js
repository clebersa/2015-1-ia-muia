$( document ).ready( function() {
	$("input[name=btnStartServer]").click( function( e ) {
		startServer();
	});
	
	$("input[name=btnStopServer]").click( function( e ) {
		stopServer();
	});
	
	$("input[name=btnRegisterClient]").click( function( e ) {
		registerClient(true);
	});
	
	$("input[name=btnUnregisterClient]").click( function( e ) {
		registerClient(false);
	});
	
	$("input[name=btnSubscribeIntoChannel]").click( function( e ) {
		var channelId = $("select[name=slctChannelSubscribeId]").val();
		subscribeClientIntoChannel(channelId, true);
	});
	
	$("input[name=btnUnsubscribeIntoChannel]").click( function( e ) {
		var channelId = $("select[name=slctChannelSubscribeId]").val();
		subscribeClientIntoChannel(channelId, false);
	});
	
	$("input[name=btnChangeChannel]").click( function( e ) {
		var channelId = $("select[name=slctUseChannels]").val();
		changeUseChannel(channelId);
	});
	
	$("input[name=btnSetClientAvailable]").click( function( e ) {
		setClientIsAvailable(true);
	});
	
	$("input[name=btnSetClientToUnavailable]").click( function( e ) {
		setClientIsAvailable(false);
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
		if(usingChannelId == null) {
			log("Erro: Você precisa selecionar um canal para enviar a mensagem");
			return;
		}
		
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
		
		sendMessage(base64Message, usingChannelId);
	});
	
	$("input[name=txtSendMessage]").keypress( function( e ) {
		if(e.keyCode === 13) {
			$("input[name=btnSend]").trigger("click");
			return false;
        }
	});
	
	$("select[name=slctChannelSubscribeId]").change(function() {
		var checked = $("select[name=slctChannelSubscribeId]").val();
		var btnSubscribeIntoChannel = $("input[name=btnSubscribeIntoChannel]");
		var btnUnsubscribeIntoChannel = $("input[name=btnUnsubscribeIntoChannel]");
		
		var isRegistered = $.inArray(checked, registeredChannels);
		if(isRegistered === -1) {
			btnUnsubscribeIntoChannel.prop("disabled", "true");
			btnSubscribeIntoChannel.removeProp("disabled");
		} else {
			btnSubscribeIntoChannel.prop("disabled", "true");
			btnUnsubscribeIntoChannel.removeProp("disabled");
		}
	});
	
	$("select[name=slctUseChannels]").change(function() {
		$("input[name=btnChangeChannel]").removeProp("disabled");
	});
	
	registeredChannels = []
	getAvailableChannels();
	
	usingChannelId = null;
	updateRegisteredChannelsList();
	
	preventNumberInputWrongKeys();
	
	setInterval(getAvailableChannels, 5000);
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

function registerClient(register) {
	inputData = {
		'action' : 'REGISTER_CLIENT',
		'register' : register
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			try {
				var status = msg['status'];
			} catch( e ) {
				log("Erro: Não foi possível contactar o servidor MUIA");
				return;
			}
			
			var btnRegisterClient = $("input[name=btnRegisterClient]");
			var btnUnregisterClient = $("input[name=btnUnregisterClient]");
			
			switch(status) {
				case 10:
					if(register === true) {
						btnRegisterClient.prop("disabled", "true");
						btnUnregisterClient.removeProp("disabled");
						log("Cliente registrado com sucesso!");
					} else {
						btnUnregisterClient.prop("disabled", "true");
						btnRegisterClient.removeProp("disabled");
						log("Cliente desregistrado com sucesso!");
					}
					break;
				case 11:
					if(register === true) {
						btnRegisterClient.prop("disabled", "true");
						btnUnregisterClient.removeProp("disabled");
						log("Erro: Um cliente com este mesmo identificador já está registrado no MUIA");
					}
					break;
				case 12:
					if(register === false) {
						btnUnregisterClient.prop("disabled", "true");
						btnRegisterClient.removeProp("disabled");
						log("Erro: Não existe nenhum cliente com este identificador registrado no MUIA");
					}
					break;
				case 13:
					btnUnregisterClient.prop("disabled", "true");
					btnRegisterClient.removeProp("disabled");
					log("Erro: Não foi possível completar a ação de registro");
					break;
			}
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
			var channelId = msg['channel-id'];
			var status = msg['status'];
			
			switch(status) {
				case 20:
					log("Canal '" + channelId + "' criado com sucesso");
					clearAddChannelFields();
					getAvailableChannels(channelId);
					break;
				case 21:
				case 2:
					log("Erro: Não foi possível criar o canal, verifique " +
						"se os dados do canal estão corretos e tente novamente");
					break;
			}
		}
	});
}

function clearAddChannelFields() {
	$("input[name=inpMaxSubscribers]").val("");
	$("input[name=inpMaxRetries]").val("");
	$("input[name=inpRetryInterval]").val("");
	$("input[name=inpSendTimeout]").val("");
	$("textarea[name=txtAChannelDescription]").val("");
}

function subscribeClientIntoChannel(channelId, register) {
	inputData = {
		'action' : 'SUBSCRIBE_CLIENT_INTO_CHANNEL',
		'channelId' : channelId,
		'register' : register
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			try {
				var status = msg['status'];
			} catch( e ) {
				log("Erro: Não foi possível contactar o servidor MUIA");
				return;
			}
			
			switch(status) {
				case 30:
					if(register === true) {
						registeredChannels.push(channelId);
						log("Cliente inscrito com sucesso no canal '" + channelId + "'");
					} else {
						var rCListId = $.inArray(channelId, registeredChannels);
						registeredChannels.splice(rCListId, 1);
						log("Cliente desinscrito com sucesso do canal '" + channelId + "'");
					}
					break;
				case 31:
					if(register === true) {
						var rCListId = $.inArray(channelId, registeredChannels);
						if(rCListId === -1) {
							registeredChannels.push(channelId);
						}
						log("Erro: Este cliente já está inscrito no canal '" + channelId + "'");
					}
					break;
				case 32:
					if(register === false) {
						var rCListId = $.inArray(channelId, registeredChannels);
						registeredChannels.splice(rCListId, 1);
						log("Erro: Este cliente não está inscrito no canal '" + channelId + "'");
					}
					break;
				case 2:
					log("Não foi possível se inscrever/desinscrever no canal '" + channelId +
						"' pois este não existe ou o cliente não está registrado no MUIA");
					break;
			}
			updateRegisteredChannelsList();
			getAvailableChannels(channelId);
		}
	});
}

function updateRegisteredChannelsList() {
	var slctRegChannels = $("select[name=slctUseChannels]");
	slctRegChannels.find('option').remove().end();
	
	var registeredRemoved = true;
	$.each(registeredChannels, function( index, value ) {
		try {
			var option = $('<option/>').attr('value', value);
			option.text(value);
			if(usingChannelId == value) {
				option.attr('selected','true');
				option.attr('disabled','true');
				registeredRemoved = false;
			}
			slctRegChannels.append(option);
		} catch(e) {}
	});
	
	if(registeredRemoved === true) {
		usingChannelId = null;
	}
	
	var defaultOption = $('<option disabled>[Nenhum canal inscrito]</option>');
	if(usingChannelId == null) {
		defaultOption.attr('selected','true');
		$("input[name=btnChangeChannel]").prop("disabled", "true");
	}
	slctRegChannels.prepend(defaultOption);
}

function changeUseChannel(channelId) {
	if(channelId != null) {
		usingChannelId = channelId;
		$("input[name=btnChangeChannel]").prop("disabled", "true");
		updateRegisteredChannelsList();
	}
}

function getAvailableChannels(selected) {
	inputData = {
		'action' : 'GET_AVAILABLE_CHANNELS'
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			try {
				var channels = msg['channels'];
			} catch(e) { var channels = []; }
			updateChannelsList(channels, selected);
			if(selected != null) {
				$("select[name=slctChannelSubscribeId]").trigger("change");
			}
		}
	});
}

function updateChannelsList(channels, selected) {
	var channelsSelect = $("select[name=slctChannelSubscribeId]");
	var defaultOption = $('<option disabled>[Selecione um canal]</option>');
	if(selected == null) {
		selected = channelsSelect.val();
		if(selected == null) {
			defaultOption.attr('selected','true');
		}
	}
	channelsSelect.find('option').remove().end();
	channelsSelect.append(defaultOption);
	
	$.each(channels, function( index, value ) {
		try {
			var isRegistered = $.inArray(value['id'], registeredChannels);
			var option = $('<option/>').attr('value', value['id']);
			
			if(isRegistered !== -1) {
				option.text("[R] " + value['id']);
			} else {
				option.text(value['id']);
			}
			
			if(selected == value['id']) {
				option.attr('selected','true');
			}
			channelsSelect.append(option);
		} catch(e) {}
	});
}

function startServer() {
	var btnStartServer = $("input[name=btnStartServer]");
	var btnStopServer = $("input[name=btnStopServer]");
	
	btnStartServer.val("Servidor iniciado");
	btnStopServer.val("Finalizar servidor");
	btnStartServer.prop("disabled", "true");
	btnStopServer.removeProp("disabled");
	
	log("Servidor iniciado!");
	
	inputData = {
		'action' : 'START_SERVER'
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			btnStartServer.val("Iniciar servidor");
			btnStopServer.val("Servidor finalizado");
			btnStartServer.removeProp("disabled");
			btnStopServer.prop("disabled", "true");
			log("Servidor finalizado!");
		}
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
			log("Comando para finalização do servidor enviado!");
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
		success: function(msg) {
			log("Mensagem enviada pelo canal '" + channelId + "'");
		}
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
			if(msg == null) {
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

function setClientIsAvailable(isAvailable) {
	inputData = {
		'action' : 'SET_CLIENT_AVAILABLE',
		'isAvailable' : isAvailable
	}
	
	$.ajax({
		url: 'operation.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {
			try {
				var status = msg['status'];
			} catch( e ) {
				log("Erro: Não foi possível contactar o servidor MUIA");
				return;
			}
			
			var btnSetAvailable = $("input[name=btnSetClientAvailable]");
			var btnSetUnavailable = $("input[name=btnSetClientToUnavailable]");
			
			switch(status) {
				case 50:
					if(isAvailable === true) {
						btnSetAvailable.prop("disabled", "true");
						btnSetUnavailable.removeProp("disabled");
						log("O cliente agora está apto a receber as mensagens enviadas");
					} else {
						btnSetUnavailable.prop("disabled", "true");
						btnSetAvailable.removeProp("disabled");
						log("O cliente não está mais apto a receber as mensagens enviadas");
					}
					break;
				case 2:
					btnSetUnavailable.prop("disabled", "true");
					btnSetAvailable.removeProp("disabled");
					log("Não foi possível setar o cliente para disponível ou indisponível" +
						" pois este não existe ou o cliente não está registrado no MUIA");
					break;
			}
		}
	});
}

function log(message) {
	var logContent = $("textarea[name=txtALog]")
	var logText = logContent.val();
	var addBreakLine = "";
	if(logText != "") {
		addBreakLine = "\n";
	}
	
	var concat = addBreakLine + message;
	logContent.val(logText + concat);
	logContent.scrollTop(logContent[0].scrollHeight);
}
