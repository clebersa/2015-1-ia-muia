<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>APP Cliente - MUIA</title>
		<script language="javascript" type="text/javascript" src="js/jquery-1.11.2.min.js"></script>
		<script language="javascript" type="text/javascript" src="js/jquery-migrate-1.2.1.min.js"></script>
		<script language="javascript" type="text/javascript" src="js/chat.js"></script>
	</head>
	
	<body>
		<table cellspacing="5px">
			<tr>
				<td>
					<input type="button" id="startServer" value="Iniciar servidor" style="width: 100%;">
				</td>
				<td>
					<input type="button" id="stopServer" value="Finalizar servidor" style="width: 100%;">
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<textarea id="txtAChat" style="width: -webkit-calc(100% - 6px); width: -moz-calc(100% - 6px); width: calc(100% - 6px); height: 200px;" readonly></textarea>
				</td>
			</tr>
			
			 
			<tr>
				<td>
					<input type="text" id="txtSendMessage" style="width: 100%;"/>
				</td>
				<td>
					<input type="button" id="btnSend" value="Enviar mensagem" style="width: 100%;">
				</td>
			</tr>
		</table>
	</body>
</html>
