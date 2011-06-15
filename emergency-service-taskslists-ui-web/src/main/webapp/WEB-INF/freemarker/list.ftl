<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"> 
	<title>List</title> 
    <link rel="stylesheet" type="text/css" href="${rc.getContextPath()}/static/css/screen.css" />
</head> 
<body> 
 
<div class="wrap"> 
 
<div class="head">
    <h5>powered by plugtree</h2>
</div>


<div class="img-profile">
<img src="${rc.getContextPath()}/static/image/${user}.png" />
 
 
<h2> 
	${name} - <span>${user}</span>  - Perspective: <select id="perspective" onChange="update()"><option <#if profile == 'Default'> selected=true </#if>> Default</option><option <#if profile == 'Detailed'> selected=true </#if>>Detailed</option></select>
</h2> 
</div>

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
					<td <#if i == Idstatus> class="special"</#if>
					>${cell}</td>
				</#if>
				<#assign i=i+1>
			</#list>
		</tr>
	</#list>
</table>
<a href="../../new/">New List</a>
</body>
</html>
