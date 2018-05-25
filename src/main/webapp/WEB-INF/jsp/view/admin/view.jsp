<%--@elvariable id="statistics" type="java.util.Map<org.dulab.adapcompounddb.models.ChromatographyType, org.dulab.adapcompounddb.models.Statistics>"--%>
<%--@elvariable id="clusters" type="java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumCluster>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Number of Spectra in Library</h1>
    <div align="center">
        <table>
            <tr>
                <th></th>
                <th>Submitted</th>
                <th>Unmatched</th>
                <th>Consensus</th>
                <th>Matches</th>
            </tr>
            <c:forEach items="${statistics}" var="mapEntry">
                <tr>
                    <td>${mapEntry.key.label}</td>
                    <td>${mapEntry.value.numSubmittedSpectra}</td>
                    <td>${mapEntry.value.numUnmatchedSpectra}</td>
                    <td>${mapEntry.value.numConsensusSpectra}</td>
                    <td>${mapEntry.value.numSpectrumMatches}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Admin Tools</h1>
    <table>
        <tr>
            <td><a href="calculatescores/" class="button">Calculate Matching Scores...</a></td>
            <td>Calculates matching scores for all spectra in the library</td>
        </tr>
        <tr>
            <td><a href="cluster/" class="button">Cluster spectra...</a></td>
            <td>Cluster spectra into clusters</td>
        </tr>
    </table>
</section>

<section>
    <h1>Clusters</h1>
    <div align="center" style="overflow: auto; max-height: 400px;">
        <table>
            <tr>
                <th>Name</th>
                <th>Num Spectra</th>
                <th>Matching Score</th>
                <th>Chromatography</th>
                <th>View</th>
            </tr>
            <c:forEach items="${clusters}" var="cluster">
                <tr>
                    <td>${cluster}</td>
                    <td>${cluster.size}</td>
                    <td>${dulab:toIntegerScore(cluster.diameter)}</td>
                    <td>${cluster.chromatographyType.label}</td>
                    <td>
                        <!--more horiz-->
                        <a href="/cluster/${cluster.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />