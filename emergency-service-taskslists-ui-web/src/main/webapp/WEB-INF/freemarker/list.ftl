<html>
<head>
	<title>List</title>
</head>
<body>
<h1> 
	Tasks list for user: ${user} and Profile: ${profile}
</h1>
<link rel="stylesheet" type="text/css" href="/emergency-service-taskslists-ui-web/resources/css/class.css" />
<#assign us = user?substring(0,2)/>
<table class="sample">
	<tr>
		<#list headers as headerValue>
			<td><b>${headerValue}</b></td>
		</#list>
	</tr>
	
	<#list data as row>
		<tr>
			<#assign i=0>
			<#list row as cell>
				<#if i == idIndex>
					<td><a href="/emergency-service-taskslists-ui-web/task/${us}/${user}/${profile}/${cell}/${taskNames[cell]}">${cell}</a></td>
				<#else>
					<td>${cell}</td>
				</#if>
				<#assign i=i+1>
			</#list>
		</tr>
	</#list>
</table>
<a href="../../new/">New List</a>
</body>
</html>
