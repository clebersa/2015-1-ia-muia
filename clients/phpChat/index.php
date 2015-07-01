<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>APP Cliente - MUIA</title>
		<link href="css/style.css" rel="stylesheet" type="text/css"/>
		<script language="javascript" type="text/javascript" src="js/jquery-1.11.2.min.js"></script>
		<script language="javascript" type="text/javascript" src="js/jquery-migrate-1.2.1.min.js"></script>
		<script language="javascript" type="text/javascript" src="js/chat.js"></script>
	</head>
	
	<body>
		<div id="wrapper">
			<div class="float_adjustment">
				<div class="container float_left">
					<span class="title">MUIA CHAT</span>
					<div class="content">
						<table class="tbl_chat">
							<tr>
								<td>
									<select name="slctUseChannels"></select>
								</td>
								<td>
									<input type="button" name="btnChangeChannel" value="Mudar de canal">
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<textarea name="txtAChat" readonly></textarea>
								</td>
							</tr>
							 
							<tr>
								<td>
									<input type="text" name="txtSendMessage"/>
								</td>
								<td>
									<input type="button" name="btnSend" value="Enviar mensagem">
								</td>
							</tr>
						</table>
					</div>
				</div>
				
				<div class="float_right">
					<div class="container">
						<span class="title">Ações de Aplicação</span>
						<div class="content">
							<table class="tbl_config">
								<tr>
									<td>
										<input type="button" name="btnStartServer" value="Iniciar servidor">
									</td>
									<td>
										<input type="button" name="btnStopServer" value="Servidor finalizado" disabled>
									</td>
								</tr>
								<tr>
									<td>
										<input type="button" name="btnRegisterClient" value="Registrar no MUIA">
									</td>
									<td>
										<input type="button" name="btnUnregisterClient" value="Desregistrar do MUIA" disabled>
									</td>
								</tr>
							</table>
						</div>
					</div>
					
					<div class="container">
						<span class="title">Criar Canal</span>
						<div class="content">
							<table class="tbl_config">
								<tr>
									<td>
										<input type="number" name="inpMaxSubscribers" min="0" step="1" placeholder="Máximo de inscritos"/>
									</td>
									<td>
										<input type="number" name="inpMaxRetries" min="-1" step="1" placeholder="Tentativas de reenvio"/>
									</td>
								</tr>
								<tr>
									<td>
										<input type="number" name="inpRetryInterval" min="0" step="1000" placeholder="Intervalo de reenvio (ms)"/>
									</td>
									<td>
										<input type="number" name="inpSendTimeout" min="0" step="1000" placeholder="Timeout de envio (ms)"/>
									</td>
								</tr>
								<tr>
									<td colspan='2'>
										<textarea name="txtAChannelDescription" placeholder="Descrição do canal"></textarea>
									</td>
								</tr>
								<tr>
									<td colspan='2'>
										<input type="button" name="btnCreateChannel" value="Criar canal">
									</td>
								</tr>
							</table>
						</div>
					</div>
					
					<div class="container">
						<span class="title">Ações de Canal</span>
						<div class="content">
							<table class="tbl_config">
								<tr>
									<td colspan='2'>
										<select name="slctChannelSubscribeId"></select>
									</td>
								</tr>
								<tr>
									<td><input type="button" name="btnSubscribeIntoChannel" value="Inscrever no canal"></td>
									<td><input type="button" name="btnUnsubscribeIntoChannel" value="Desinscrever do canal"></td>
								</tr>
							</table>
						</div>
					</div>
				</div>
			</div>
			
			<div class="container log">
				<span class="title">Log</span>
				<div class="content">
					<textarea name="txtALog" readonly></textarea>
				</div>
			</div>
			
		</div>
		
	</body>
</html>
