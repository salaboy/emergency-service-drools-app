<h1>Task Form:</h1>
 <FORM action="#" method="post">
    <P/>
    <table>
    <#list taskOutput?keys as inputKey>
    <tr id='tr_${inputKey}' <#if children?? && children[inputKey]??> style="display:none"</#if>>
     	<th><h3><LABEL for="${inputKey}">${inputKey}: </LABEL></h3></th>
     	<#if taskOutput[inputKey]?is_sequence>
				<td><select multiple name="input_${inputKey}" id="input_${inputKey}" <#if taskInput['Status']!='IN_PROGRESS'> disabled=true</#if> onClick="handleOnClick()">
					<#list taskOutput[inputKey] as sequenceItem>
						<option>
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
    <br>
    <h1>Operations:</h1>
    <#list operations.getOperationsList() as operation>
    	<INPUT type="button" id="button_${operation}" value=${operation} onClick="buttonClicked('button_${operation}')">
    </#list>
    </P>
 </FORM>
</table>
