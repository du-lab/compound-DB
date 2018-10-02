<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="availableCategories" type="java.util.Map<org.dulab.adapcompounddb.models.SubmissionCategoryType, java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionCategories>>"--%>
<%--@elvariable id="availableTags" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionTag>"--%>
<%--@elvariable id="submissionDTO" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
<%--@elvariable id="submissionForm" type="org.dulab.adapcompounddb.site.controllers.SubmissionController.SubmissionForm"--%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="<c:url value="/resources/AdapCompoundDb/js/application.js"/>"></script>

<section>
    <div class="tabbed-pane">
        <c:set var="width" value="49" />
        <c:if test="${authenticated}">
            <c:set var="width" value="33" />
            <span class="active" data-tab="submission" style="width: ${width}%; border-radius: 10px 0 0 0; border-right: solid #ffffff">Submission Properties</span>
        </c:if>
        <span class="${!authenticated ? 'active' : ''}" data-tab="files" style="width: ${width}%; border-right: solid #ffffff">Files</span>
        <span data-tab="mass_spectra" style="width: ${width}%; border-radius: 0 10px 0 0; float: right;">Mass Spectra</span>
    </div>
    <div id="submission">
        <c:if test="${authenticated}">
            <jsp:include page="../../includes/submission_form.jsp">
                <jsp:param value="${submissionForm}" name="submissionForm"/>
            </jsp:include>
        </c:if>
    </div>
    <div id="files"class="${!authenticated ? '' : 'hide'}">
        <table id="file_table" class="display" style="width: 100%; clear:none;">
            <thead>
            <tr>
                <th>File</th>
                <th>Type</th>
                <th>Size</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${submission.files}" var="file" varStatus="loop">
                <tr>
                    <td><a href="${loop.index}/view/" target="_blank">${file.name}</a></td>
                    <td>${file.fileType.label}</td>
                    <td>${file.spectra.size()} spectra</td>
                    <td>
                        <a href="${loop.index}/view/" target="_blank">
                            <i class="material-icons" title="View">attach_file</i>
                        </a>
                        <a href="${loop.index}/download/" target="_blank">
                            <i class="material-icons" title="Download">save_alt</i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div id="mass_spectra" class="hide">
        <div align="center">
            <table id="spectrum_table" class="display" style="width: 100%; clear:none;">
                <thead>
                <tr>
                    <th></th>
                    <th>Name</th>
                    <th>Ret Time (min)</th>
                    <th>Precursor mass</th>
                    <th>Significance</th>
                    <th>Type</th>
                    <th></th>
                </tr>
    
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>
</section>s

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/tag-it-6ccd2de/js/tag-it.min.js"/>"></script>
<script>
    $(document).ready(function () {

        // Table with a list of spectra

        $('#spectrum_table').DataTable({
            serverSide: true,
            processing: true,
            ajax: {
                url: "${pageContext.request.contextPath}/spectrum/findSpectrumBySubmissionId.json?submissionId=${submission.id}",

                data: function (data) {
                    data.column = data.order[0].column;
                    data.sortDirection = data.order[0].dir;
                    data.search = data.search["value"];
                }
            },
            "columnDefs": [
                {
                    "targets": 0,
                    "orderable": false,
                    "searchable": false,
                    "render": function (data, type, row, meta) {
                        return meta.settings.oAjaxData.start + meta.row + 1;
                    }
                },
                {
                    "orderable": true,
                    "targets": 1,
                    "render": function (data, type, row, meta) {
                        content = '<a href="' + row.fileIndex +'/' + row.spectrumIndex + '/">' +
                            row.name +
                            '</a>' +
                            '<br/><small>' + row.fileName + '</small>';
                        return content;
                    }
                },
                {
                    "targets": 2,
                    "render": function (data, type, row, meta) {
                        var value = row.retentionTime;
                        if (value != null && !isNaN(value)) {
                            value = value.toFixed(3);
                        }
                        return value;
                    }
                },
                {
                    "targets": 3,
                    "render": function (data, type, row, meta) {
                        var value = row.precursor;
                        if (value != null && !isNaN(value)) {
                            value = value.toFixed(3);
                        }
                        return value;
                    }
                },
                {
                    "targets": 4,
                    "render": function (data, type, row, meta) {
                        var value = row.significance;
                        if (value != null && !isNaN(value)) {
                            value = value.toFixed(3);
                        }
                        return value;
                    }
                },
                {
                    "orderable": true,
                    "targets": 5,
                    "render": function (data, type, row, meta) {
                        content = '<img' +
                            ' src="${pageContext.request.contextPath}/' + row.chromatographyTypeIconPath + '"'
                            + ' alt="' + row.chromatographyTypeLabel + '"'
                            + ' title="' + row.chromatographyTypeLabel + '"'
                            + ' class="icon"/>';

                        return content;
                    }
                },
                {
                    "orderable": false,
                    "targets": 6,
                    "render": function (data, type, row, meta) {
                        content = '<a href="' + row.fileIndex +'/' + row.spectrumIndex + '/">' +
                            '<i class="material-icons" title="View spectrum">&#xE5D3;</i>' +
                            '</a>' +
                            '<a href="' + row.fileIndex +'/' + row.spectrumIndex + '/search/">' +
                            '<i class="material-icons" title="Search spectrum">&#xE8B6;</i>' +
                            '</a>';
                        if (JSON.parse("${submissionForm.authorized && edit}")) {
                            content += '<a href="spectrum/' + row.id + '/delete">' +
                                '<i class="material-icons" title="Delete spectrum">&#xE872;</i>' +
                                '</a>';
                        }
                        return content;
                    }
                }
            ]
        });

        /* table.on('order.dt search.dt', function () {
            table.column(0, {search: 'applied', order: 'applied'})
                .nodes()
                .each(function (cell, i) {
                    cell.innerHTML = i + 1;
                })
        }).draw(); */

        // Table with a list of files
        $('#file_table').DataTable({
            // bLengthChange: false,
            // info: false,
            // ordering: false,
            // paging: false,
            // searching: false
        });

        // Selector with autocomplete
        $('#tags').tagit({
            autocomplete: {
                source: ${dulab:stringsToJson(availableTags)}
            }
        });
    })
</script>