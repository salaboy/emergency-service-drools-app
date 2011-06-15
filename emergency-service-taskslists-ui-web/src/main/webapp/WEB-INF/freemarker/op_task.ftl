<head>
	<title>Task</title>
<script type="text/javascript" src="${rc.getContextPath()}/static/js/jquery.min.js"></script>
<script type="text/javascript">
jQuery.expr[':'].regex = function(elem, index, match) {
    var matchParams = match[3].split(','),
        validLabels = /^(data|css):/,
        attr = {
            method: matchParams[0].match(validLabels) ? 
                        matchParams[0].split(':')[0] : 'attr',
            property: matchParams.shift().replace(validLabels,'')
        },
        regexFlags = 'ig',
        regex = new RegExp(matchParams.join('').replace(/^\s+|\s+$/g,''), regexFlags);
    return regex.test(jQuery(elem)[attr.method](attr.property));
}
</script>	
	
		<script type="text/javascript">
		var mymap = new Array();
		
		function init() {
			<#list operations.getOperationsList() as operation>
				document.getElementById('button_${operation}').disabled = true;
			</#list>
		<#list operations.getOperations(taskInput['Status']) as operation>
			document.getElementById('button_${operation}').disabled = false;
		</#list>
		}
		
		function buttonClicked(element)
		{
		var output = "";
		$(':regex(name,^input)').each(function() {
   			//...other codes...
   			output = this.name.split('_')[1] + "=" + this.value + "," + output; 
  		});
  		output = output + 'callId=${callId}'
			var url = "${rc.getContextPath()}/task/${user?substring(0,2)}/execute/${user}/${profile}/${id}/${name}/" + element.split("_")[1] + "/" + output;
			window.location = url;
		}
		
		function update() {
			var perspective = document.getElementById('perspective');
			var url = "${rc.getContextPath()}/task/${user?substring(0,2)}/${user}/"+perspective.value+"/${id}/${name}/";
			window.location = url;
		}
	</script>
	<link rel="stylesheet" type="text/css" href="${rc.getContextPath()}/static/css/screen.css" />	
</head>
<body onLoad="init()">
<div class="wrap"> 
 
<div class="head">
    <h5>powered by plugtree</h2>
</div>
<div class="img-profile">
<img src="${rc.getContextPath()}/static/image/${user}.png" />
<h2> 
	${name} - <span>${user}</span> - Perspective: <select id="perspective" onChange="update()"><option <#if profile == 'Default'> selected=true </#if>> Default</option><option <#if profile == 'Detailed'> selected=true </#if>>Detailed</option></select>
</h2>
<div class="mydiv" id="statusid"/>
<#include "task/error.ftl">
<#include "task/details.ftl">
<br/>
<#include "task/form.ftl">
<#include "task/footer.ftl"></body>
</html>
