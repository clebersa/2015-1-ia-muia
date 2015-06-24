$( document ).ready( function() {
	$("#startServer").click( function( e ) {
		startServer();
	});
	
	$("#stopServer").click( function( e ) {
		stopServer();
	});
	
	$("#btnSend").click( function( e ) {
		var base64Message = btoa($("#txtSendMessage").val());
		var add = $("#txtAChat").val();
		if(add != "") {
			add += "\n";
		}
		var concat = add + "Eu: " + $("#txtSendMessage").val();
		$("#txtAChat").val(concat);
		$('#txtAChat').scrollTop($('#txtAChat')[0].scrollHeight);
		
		$("#txtSendMessage").val("");
		$("#txtSendMessage").focus();
		
		sendMessage(base64Message);
	});
	
	$("#txtSendMessage").keypress( function( e ) {
		if(e.keyCode === 13) {
			$("#btnSend").trigger("click");
			return false;
        }
	});
	
	setInterval(getChatMessages, 1000);
});

function startServer() {
	$.ajax({
		url: 'server.php',
		type: 'POST',
		cache: false,
		success: function(msg) {}
	});
}

function stopServer() {
	$.ajax({
		url: 'serverShutdown.php',
		type: 'POST',
		cache: false,
		success: function(msg) {}
	});
}

function sendMessage(base64Message) {
	inputData = {
		'message' : base64Message
	}
	
	$.ajax({
		url: 'messageSender.php',
		type: 'POST',
		data: inputData,
		datatype: 'json',
		cache: false,
		success: function(msg) {}
	});
}

function getChatMessages() {
	$.ajax({
		url: 'messagesGetter.php',
		type: 'POST',
		datatype: 'json',
		cache: false,
		success: function(msg) {
			$.each(msg, function( index, value ) {
				try {
					var message = value['message-packet'];
					message = atob(message['message-data']['value']);
					
					var sender = value['message-packet']['header-data'];
					sender = sender['source'];
					
					var add = $("#txtAChat").val();
					if(add != "") {
						add += "\n";
					}
					var concat = add + sender + ": " + message;
					$("#txtAChat").val(concat);
					$('#txtAChat').scrollTop($('#txtAChat')[0].scrollHeight);
				} catch(e) {}
			});
		}
	});
}
