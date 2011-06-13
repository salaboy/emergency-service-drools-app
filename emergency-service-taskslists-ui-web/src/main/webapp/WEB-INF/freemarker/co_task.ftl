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
  	
			var url = "${rc.getContextPath()}/task/${user?substring(0,2)}/execute/${user}/${profile}/${id}/${name}/" + element.split("_")[1] + "/" + output;
			window.location = url;
		}
	</script>
		<link rel="stylesheet" type="text/css" href="${rc.getContextPath()}/static/css/screen.css" />
</head>
<body onLoad="init()">
<div class="head"> 
	<h1>Emergency Service</h1> 
    <h5>powered by plugtree</h5> 
</div> 
 
<h2> 
	${name}
</h2>
<div class="mydiv" id="statusid"/>
<#include "task/error.ftl">
<#include "task/details.ftl">
<br/>
<#include "task/co_form.ftl">
<#include "task/footer.ftl">
</body>
</html>
