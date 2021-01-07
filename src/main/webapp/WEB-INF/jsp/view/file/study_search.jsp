<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>

<div>
    Study matching score is calculated based on the number of similar spectra between two studies.<br/>
    It ranges from 0 to 1000 with the higher score corresponding to higher similarity between studies.
</div>

<section>
<%-- TODO rename the header to "Matched Studies"   --%>
    <h1>Match Studies</h1>
    <div align="center">
        <table id="match_table" class="display responsive" style="width: 100%; clear:none;">
            <thead>
            <tr>
                <th></th>
<%-- TODO rename "Match submission Name" to "Name"               --%>
                <th title="Match submission Name">Match Submission Name</th>
                <th title="study external ID">Study ID</th>
                <th title="study tags">Study Tags</th>
                <th title="Match Score">Matching Score</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
<%-- TODO: Define variable math_submissions with @elvariable. See all_clusters.jsp for an example     --%>
            <c:forEach items="${match_submissions}" var="match_submission">
                <tr>
                    <td></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/submission/${match_submission.submissionId}/">${match_submission.submissionName}</a><br/>
                        <small>${dulab:abbreviate(match_submission.description, 80)}</small>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/submission/${match_submission.submissionId}/">${match_submission.externalId}</a><br/>
                    </td>
                    <td>
                        <c:forEach items="${match_submission.studyTag}" var="tag" varStatus="status">
                            <span id="${match_submission.submissionId}color${status.index}">${tag.toString()}&nbsp;</span>
                            <script>
                                var spanId = '${fn:length(match_submission.studyTag)}';
                                spanColor( ${match_submission.submissionId}, spanId );
                            </script>
                        </c:forEach>
                    </td>
                    <td><c:out value="${match_submission.score}"/></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/submission/${match_submission.submissionId}/"><i
                                class="material-icons" title="View">&#xE5D3;</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

    </div>

</section>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>

<script>

    $(document).ready(function () {
        var t = $('#match_table').DataTable({
            ordering:true,
            order: [[0,'asc'],[4, 'desc']],
            responsive: true,
            scrollX: true,
            scroller: true,
            columnDefs: [
                {
                    targets: 0,
                    sortable: false
                },
                {
                    targets: 1,
                    sortable: true
                },
                {
                    targets: 2,
                    sortable: true
                },
                {
                    targets: 3,
                    sortable: true
                },
                {
                    targets: 4,
                    sortable: true
                },
                {
                    targets: 5,
                    sortable: false
                }
            ],
        });
        t.on( 'order.dt search.dt', function () {
            t.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
                cell.innerHTML = i+1;
            } );
        } ).draw();
    });
</script>