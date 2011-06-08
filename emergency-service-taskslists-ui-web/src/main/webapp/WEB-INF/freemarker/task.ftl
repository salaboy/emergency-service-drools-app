<head>
	<title>Task</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>
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
  	
			var url = "/emergency-service-taskslists-ui-web/task/${user?substring(0,2)}/execute/${user}/${profile}/${id}/${name}/" + element.split("_")[1] + "/" + output;
			window.location = url;
		}
	</script>
	
</head>
<body onLoad="init()">
<h1> 
	<#assign idv="id"/>
	${name}
</h1>
<link rel="stylesheet" type="text/css" href="/emergency-service-taskslists-ui-web/resources/css/class.css" />

<div class="mydiv" id="statusid"/>
<#include "task/details.ftl">
<br/>
<#include "task/form.ftl">
<#include "task/footer.ftl">
</body>
</html>
