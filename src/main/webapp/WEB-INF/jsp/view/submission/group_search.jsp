<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="filterModal" class="modal fade" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Filter</h5>
            </div>
            <div class="modal-body">
                <div class="container-fluid">
                    <%--@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"--%>
                    <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
                    <form:form modelAttribute="filterForm" method="post">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="row form-group">
                                    <form:label path="submissionIds" cssClass="col-form-label">Libraries:</form:label>
                                    <form:select path="submissionIds" cssClass="custom-select" multiple="multiple"
                                                 size="10">
                                        <c:forEach items="${filterOptions.submissions}" var="entry">
                                            <form:option value="${entry.key}"
                                                         selected="${filterForm.submissionIds.contains(entry.key) ? 'selected' : ''}">
                                                ${entry.value}
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="row form-group">
                                    <form:label path="species" cssClass="col-md-6 col-form-label">Species:</form:label>
                                    <form:select path="species" cssClass="col-md-6 form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.speciesList}"/>
                                    </form:select>
                                </div>
                                <div class="row form-group">
                                    <form:label path="source" cssClass="col-md-6 col-form-label">Source:</form:label>
                                    <form:select path="source" cssClass="col-md-6 form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.sourceList}"/>
                                    </form:select>
                                </div>
                                <div class="row form-group">
                                    <form:label path="disease" cssClass="col-md-6 col-form-label">Disease:</form:label>
                                    <form:select path="disease" cssClass="col-md-6 form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.diseaseList}"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="$('#filterForm').submit()">
                    Filter
                </button>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row row-content">
        <div class="col">
            <div class="btn-toolbar justify-content-end" role="toolbar">
                <div class="progress flex-grow-1 align-self-center mx-2">
                    <div id="progressBar" class="progress-bar" role="progressbar" aria-valuenow="0"
                         aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <button id="filterButton" type="button" class="btn btn-primary mr-2" data-toggle="modal"
                        data-target="#filterModal">Filter...
                </button>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Query</div>
                <div class="card-body small overflow-auto" style="height: 300px">
                    <div id="queryInfo"></div>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Plot</div>
                <%--                <div class="card-body small overflow-auto" style="height: 300px">--%>
                <div id="plot" style="height: 300px"></div>
                <%--                </div>--%>
            </div>
        </div>
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Match</div>
                <div class="card-body small overflow-auto" style="height: 300px">
                    <div id="matchInfo"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Group Search Results
                </div>
                <div class="card-body small">
                    <table id="match_table" class="display compact" style="width: 100%; clear:none;">
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Query</th>
                            <th title="Match spectra">Match</th>
                            <th title="Molecular weight">Molecular weight</th>
                            <th title="Number of studies" class="Count">Studies</th>
                            <th title="Minimum matching score between all spectra in a cluster">Score</th>
                            <th title="Difference between query and library neutral masses">Mass Error (mDa)</th>
                            <th title="Difference between query and library neutral masses">Mass Error (PPM)</th>
                            <th title="Difference between query and library retention times">Ret Time Error</th>
                            <th title="Average P-value of ANOVA tests">Average P-value</th>
                            <th title="Minimum P-value of ANOVA tests">Minimum P-value</th>
                            <th title="Maximum P-value of ANOVA tests">Maximum P-value</th>
                            <th title="Ontology level">Ontology Level</th>
                            <th title="Chromatography type">Type</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/SpeckTackle/st.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumInfo.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumPlot.js"/>"></script>
<script>
    $(document).ready(function () {

        let table = $('#match_table').DataTable({
            // dom: 'lfrtip',
            serverSide: true,
            order: [[0, 'desc']],
            processing: true,
            responsive: true,
            scrollX: true,
            select: {style: 'single'},
            // scroller: true,
            rowId: 'position',
            ajax: {
                url: "${pageContext.request.contextPath}/file/group_search/data.json",
                data: function (data) {
                    data.column = data.order[0].column;
                    data.sortDirection = data.order[0].dir;
                    data.search = data.search["value"];
                },
                dataSrc: function (d) {
                    // Hide columns with no data
                    table.column(3).visible(d.data.map(row => row['molecularWeight']).join(''));
                    table.column(4).visible(d.data.map(row => row['size']).join(''));
                    // table.column(5).visible(d.data.map(row => row['score']).join(''));
                    // table.column(6).visible(d.data.map(row => row['massError']).join(''));
                    // table.column(7).visible(d.data.map(row => row['retTimeError']).join(''));
                    table.column(9).visible(d.data.map(row => row['aveSignificance']).join(''));
                    table.column(10).visible(d.data.map(row => row['minSignificance']).join(''));
                    table.column(11).visible(d.data.map(row => row['maxSignificance']).join(''));
                    return d.data;
                }
            },
            fnCreatedRow: function (row, data, dataIndex) {
                $(row).attr('data-position', data.position);
                $(row).attr('data-matchId', data.spectrumId);
                $(row).attr('data-queryHRef', data.queryHRef);
                $(row).attr('data-queryId', data.querySpectrumId);
                $(row).attr('data-queryFileIndex', data.queryFileIndex);
                $(row).attr('data-querySpectrumIndex', data.querySpectrumIndex);
            },
            "aoColumnDefs": [
                {
                    "targets": 0,
                    "bSortable": true,
                    "searchable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        // return meta.settings.oAjaxData.start + meta.row + 1;
                        return row.position;
                    }
                },
                {
                    "targets": 1,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        const href = (row.querySpectrumId !== 0)
                            ? `${pageContext.request.contextPath}/spectrum/\${row.querySpectrumId}/`
                            : `${pageContext.request.contextPath}/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/`;
                        return `<a href="\${href}">\${row.querySpectrumName}</a>`;
                    }
                },
                {
                    "targets": 2,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        let content = '';
                        if (row.name != null) {
                            content = `<a href="${pageContext.request.contextPath}\${row.href}">\${row.name}</a>`;
                        }
                        return content;
                    }
                },
                {
                    "targets": 3,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row) {
                        return (row.molecularWeight != null) ? row.molecularWeight.toFixed(3) : '';
                    }
                },
                {
                    "targets": 4,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return (row.size != null) ? row.size : '';
                    }
                },
                {
                    "targets": 5,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return (row.score != null) ? row.score.toFixed(3) * 1000 : '';
                    }
                },
                {
                    "targets": 6,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row) {
                        return (row.massError != null) ? (1000 * row.massError).toFixed(3) : '';
                    }
                },
                {
                    "targets": 7,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row) {
                        return (row.massErrorPPM != null) ? row.massErrorPPM.toFixed(3) : '';
                    }
                },
                {
                    "targets": 8,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row) {
                        return (row.retTimeError != null) ? row.retTimeError.toFixed(3) : '';
                    }
                },
                {
                    "targets": 9,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.aveSignificance != null) {
                            return row.aveSignificance.toFixed(3);
                        } else {
                            return '';
                        }
                    }
                },
                {
                    "targets": 10,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.minSignificance != null) {
                            return row.minSignificance.toFixed(3);
                        } else {
                            return '';
                        }
                    }
                },
                {
                    "targets": 11,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.maxSignificance != null) {
                            return row.maxSignificance.toFixed(3);
                        } else {
                            return '';
                        }
                    }
                }, {
                    "targets": 12,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row) {
                        return row.ontologyLevel;
                    }
                },
                {
                    "targets": 13,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        let content = '';
                        if (row.name != null) {
                            content = '<img' +
                                ' src="${pageContext.request.contextPath}/' + row.chromatographyTypePath + '"'
                                + ' alt="' + row.chromatographyTypeLabel + '"'
                                + ' title="' + row.chromatographyTypeLabel + '"'
                                + ' class="icon"/>';
                        }
                        return content;
                    }
                },
                {
                    "targets": 14,
                    "bSortable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        const href = (row.querySpectrumId !== 0)
                            ? `${pageContext.request.contextPath}/spectrum/\${row.querySpectrumId}/search/`
                            : `${pageContext.request.contextPath}/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/search/`;
                        return `<a href="\${href}"><i class="material-icons" title="Search spectrum">&#xE8B6;</i></a>`;
                    }
                },
                // {"className": "dt-center", "targets": "_all"}
            ],
        });

        table.on('select', function (e, dt, type, indexes) {

            let row = table.row(indexes).node();
            let position = $(row).attr('data-position');
            let queryHRef = $(row).attr('data-queryHRef');
            let queryId = $(row).attr('data-queryId');
            let queryFileIndex = $(row).attr('data-queryFileIndex');
            let querySpectrumIndex = $(row).attr('data-querySpectrumIndex');
            let matchId = $(row).attr('data-matchId');

            <%--let queryUrl = `${pageContext.request.contextPath}/file/\${queryFileIndex}/\${querySpectrumIndex}/search/`;--%>
            let queryUrl = `${pageContext.request.contextPath}\${queryHRef}search/`;
            let matchUrl = `${pageContext.request.contextPath}/spectrum/\${matchId}/search/`;

            $('#queryInfo').spectrumInfo(queryUrl + 'info.json');
            $('#matchInfo').spectrumInfo(matchUrl + 'info.json');
            $('#plot').spectrumPlot(position, queryUrl + 'positive/peaks.json', matchUrl + 'negative/peaks.json');
        });

        // refresh the datatable and progress bar every 1 second
        setInterval(function () {
            table.ajax.reload(null, false);
            $.getJSON(window.location.href + 'progress', function (x) {
                const width = x + '%';
                const progressBar = $('#progressBar')
                    .css('width', width)
                    .attr('aria-valuenow', x)
                    .html(width);
                if (0 < x && x < 100)
                    progressBar.addClass('progress-bar-striped progress-bar-animated');
                else {
                    progressBar.removeClass('progress-bar-striped progress-bar-animated');
                }
            });
        }, 1000);

        <c:if test="${pageContext.request.method == 'GET'}">$('#filterForm').submit();
        </c:if>
    });
</script>
