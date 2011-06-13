<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"> 
	<title>List</title> 
    <link rel="stylesheet" type="text/css" href="${rc.getContextPath()}/static/css/screen.css" />
</head> 
<body> 
 
<div class="wrap"> 
 
<div class="head"> 
	<h1>Emergency Service</h1> 
    <h5>powered by plugtree</h2> 
</div> 
<h2> 
	Tasks list for user: <span>${user}</span> and Profile: <span>${profile}</span>
</h2>
<#assign us = user?substring(0,2)/>
<table>
	<tr>
		<#list headers as headerValue>
			<th><h3>${headerValue}</h4></th>
		</#list>
	</tr>
	
	<#list data as row>
		<tr>
			<#assign i=0>
			<#list row as cell>
				<#if i == idIndex>
					<td><a href="${rc.getContextPath()}/task/${us}/${user}/${profile}/${cell}/${taskNames[cell]}">${cell}</a></td>
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
