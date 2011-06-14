<h2>Task Form:</h2>
 <FORM action="#" method="post">
    <P>
    <table>
    <#list taskOutput?keys as inputKey>
    <tr>
     	<th><h3><LABEL for="${inputKey}">${inputKey}: </LABEL></h3></th>
     	<#if taskOutput[inputKey]?is_sequence>
				<td><select name="input_${inputKey}" id="inputKey" <#if taskInput['Status']!='IN_PROGRESS'> disabled=true</#if>>
					<#list taskOutput[inputKey] as sequenceItem>
					<option
						<#if inputKey="Suggested Procedures" && sequenceItem=suggestedProcedure>
							selected=true
						</#if>
						 >
							${sequenceItem}
						</option>
					</#list>
				</select></td>
			<#else>
				<td><INPUT type="text" name="input_${inputKey}" id="input_${inputKey}" value="${taskOutput[inputKey]}" <#if taskInput['Status']!='IN_PROGRESS'> disabled=true</#if>/></td>
			</#if>
    <tr>
    </#list>
    </table>
    <#list operations.getOperationsList() as operation>
    	<INPUT type="button" id="button_${operation}" value=${operation} onClick="buttonClicked('button_${operation}')">
    </#list>
    </P>
 </FORM>
</table>