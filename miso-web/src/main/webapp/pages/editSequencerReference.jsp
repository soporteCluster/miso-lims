<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>

<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <form:form id="sequencer_reference_form" data-parsley-validate="" action="/miso/sequencer" method="POST" commandName="sequencerReference" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="sequencerReference"/>
      <h1>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          Edit
        </sec:authorize>
        Sequencer
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <button id="save" onclick="return Sequencer.validateSequencerReference();" class="fg-button ui-state-default ui-corner-all">Save</button>
        </sec:authorize>
      </h1>
      <div class="breadcrumbs">
        <ul>
          <li>
            <a href="<c:url value='/miso/'/>">Home</a>
          </li>
          <li>
            <a href='<c:url value="/miso/sequencers"/>'>Sequencers</a>
          </li>
        </ul>
      </div>
      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
        <div id="note_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notediv" class="note" style="display:none;">A Sequencer also tracks the data generated by the sequencer,
        via either a machine physically attached to a sequencer itself, or more commonly, a cluster or storage machine
        that holds the run directories.
      </div>
      
      <div class="bs-callout bs-callout-warning hidden">
        <h2>Oh snap!</h2>
        <p>This form seems to be invalid!</p>
      </div>
      
      <h2>Sequencer Information</h2>

      <table class="in">
        <tr>
          <td class="h">Sequencer ID:</td>
          <td><span id="instrumentId">
            <c:choose>
              <c:when test="${sequencerReference.id != 0}">${sequencerReference.id}</c:when>
              <c:otherwise><i>Unsaved</i></c:otherwise>
            </c:choose>
          </span></td>
        </tr>
        <tr>
          <td class="h">Platform:</td>
          <td><span id="platform">${sequencerReference.platform.nameAndModel}</span></td>
        </tr>
        <tr>
          <td class="h">Serial Number:</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input path="serialNumber" id="serialNumber" name="serialNumber" class="validateable"/><span id="serialNumberCounter" class="counter"></span>
              </c:when>
              <c:otherwise><span id="serialNumber">${sequencerReference.serialNumber}</span></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Name:*</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input path="name" id="name" name="name" class="validateable"/><span id="nameCounter" class="counter"></span>
              </c:when>
              <c:otherwise><span id="name">${sequencerReference.name}</span></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td>IP Address:*</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <input type="text" id="ipAddress" name="ipAddress" value="${trimmedIpAddress}" class="validateable"/>
                <input type="hidden" value="on" name="_ipAddress"/>
              </c:when>
              <c:otherwise><span id="ipAddress">${trimmedIpAddress}</span></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <c:if test="${preUpgradeSeqRef != null}">
          <tr>
            <td class="h">Upgraded From:</td>
            <td>
              <a href="<c:url value='/miso/sequencer/${preUpgradeSeqRef.id}'/>">${preUpgradeSeqRef.name}</a>
            </td>
          </tr>
        </c:if>
        <tr>
          <td class="h">Commissioned:</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input path="dateCommissioned" id="datecommissionedpicker" placeholder="YYYY-MM-DD" class="validateable"/>
                <script type="text/javascript">
                  Utils.ui.addDatePicker("datecommissionedpicker");
                </script>
              </c:when>
              <c:otherwise><span id="dateCommissioned">
                <fmt:formatDate value="${sequencerReference.dateCommissioned}" pattern="yyyy-MM-dd"/>
              </span></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td>Status:</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <label><input type="radio" name="status" value="production" onchange="Sequencer.ui.showStatusRows();" <c:if test="${sequencerReference.dateDecommissioned == null}">checked</c:if>/> Production</label>
                <label><input type="radio" name="status" value="retired" onchange="Sequencer.ui.showStatusRows();" <c:if test="${sequencerReference.dateDecommissioned != null && sequencerReference.upgradedSequencerReference == null}">checked</c:if>/> Retired</label>
                <label><input type="radio" name="status" value="upgraded" onchange="Sequencer.ui.showStatusRows();" <c:if test="${sequencerReference.dateDecommissioned != null && sequencerReference.upgradedSequencerReference != null}">checked</c:if>/> Upgraded</label>
              </c:when>
              <c:otherwise>
                <span name="status">
                <c:choose>
                  <c:when test="${sequencerReference.dateDecommissioned == null}">Production</c:when>
                  <c:when test="${sequencerReference.dateDecommissioned != null && sequencerReference.upgradedSequencerReference == null}">Retired</c:when>
                  <c:otherwise>Upgraded</c:otherwise>
                </c:choose>
                </span>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr id="decommissionedRow">
          <td class="h">Decommissioned:*</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input path="dateDecommissioned" id="datedecommissionedpicker" placeholder="YYYY-MM-DD" class="validateable"/>
                <script type="text/javascript">
                  Utils.ui.addDatePicker("datedecommissionedpicker");
                </script>
              </c:when>
              <c:otherwise>
                <fmt:formatDate value="${sequencerReference.dateDecommissioned}" pattern="yyyy-MM-dd"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr id="upgradedReferenceRow">
          <td class="h">Upgraded To:*</td>
          <td>
            <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <form:select id="upgradedSequencerReference" path="upgradedSequencerReference"  class="validateable" onchange="Sequencer.ui.updateUpgradedSequencerReferenceLink();">
                <form:option value="0">(choose)</form:option>
                <form:options items="${otherSequencerReferences}" itemLabel="name" itemValue="id"/>
              </form:select>
            </c:if>
            <span id="upgradedSequencerReferenceLink"></span>
          </td>
        </tr>
      </table>
      <c:choose>
        <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
          <script type="text/javascript">
            jQuery(document).ready(function() {
	          Sequencer.ui.showStatusRows();
	        });
          </script>
        </c:when>
        <c:otherwise>
          <script type="text/javascript">
            jQuery(document).ready(function() {
              Sequencer.ui.hideStatusRowsReadOnly(
                  ${sequencerReference.dateDecommissioned != null}, 
                  ${sequencerReference.upgradedSequencerReference != null}, 
                  ${sequencerReference.upgradedSequencerReference != null ? sequencerReference.upgradedSequencerReference.id : 0}, 
                  "<c:if test="${sequencerReference.upgradedSequencerReference != null}">${sequencerReference.upgradedSequencerReference.name}</c:if>"
              );
            });
          </script>
        </c:otherwise>
      </c:choose>
      <br/>
    </form:form>
    
    <script type="text/javascript">
      jQuery(document).ready(function () {
        // Attaches a Parsley form validator.
        Validate.attachParsley('#sequencer_reference_form');
      });
    </script>
    
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#records_arrowclick'), 'recordsdiv');">
      <c:choose>
        <c:when test="${fn:length(sequencerServiceRecords) == 1}">1 Service Record</c:when>
        <c:otherwise>${fn:length(sequencerServiceRecords)} Service Records</c:otherwise>
      </c:choose>
      <div id="records_arrowclick" class="toggleLeft"></div>
    </div>
    <h1>Service Records</h1>
    <span onclick="Sequencer.ui.addServiceRecord(${sequencerReference.dateDecommissioned != null}, ${sequencerReference.id})" 
          class="sddm fg-button ui-state-default ui-corner-all" id="addServiceRecord">Add Service Record</span>

    <div id="recordsdiv" style="display:none;">
      <div style="clear:both">
        <table class="list" id="records_table">
          <thead>
            <tr>
              <th>Service Date</th>
              <th>Title</th>
              <th>Serviced By</th>
              <th>Reference Number</th>
              <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <th class="fit">Delete</th>
              </c:if>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${sequencerServiceRecords}" var="record">
              <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                <td>${record.serviceDate}</td>
                <td><a href='<c:url value="/miso/sequencer/servicerecord/${record.id}"/>'>${record.title}</a></td>
                <td>${record.servicedByName}</td>
                <td>${record.referenceNumber}</td>
                <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <td class="misoicon" onclick="Sequencer.ui.deleteServiceRecord(${record.id}, Utils.page.pageReload);">
                    <span class="ui-icon ui-icon-trash"></span>
                  </td>
                </c:if>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#records_table').dataTable({
          "aaSorting": [
            [0, 'desc']
          ],
          "aoColumns": [
            { "sType": 'date' },
            { "sType": 'string' },
            { "sType": 'string' },
            { "sType": 'string' }
            <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              ,{ "bSortable": false }
            </c:if>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true
        });
      });
    </script>
    
    
    <br/>
    <a id="runs"></a>
    <div class="sectionDivider">Runs
    </div>
    <h1>Runs</h1>
    <div style="clear:both">
      <table id="run_table">
      </table>
    </div>
    <script type="text/javascript">
      ListUtils.createTable('run_table', ListTarget.run, null, { sequencer : ${sequencerReference.id} });
    </script>
    
  </div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#name').simplyCountable({
      counter: '#nameCounter',
      countType: 'characters',
      maxCount: ${maxLengths['name']},
      countDirection: 'down'
    });
    
    jQuery('#serialNumber').simplyCountable({
      counter: '#serialNumberCounter',
      countType: 'characters',
      maxCount: ${maxLengths['serialNumber']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
