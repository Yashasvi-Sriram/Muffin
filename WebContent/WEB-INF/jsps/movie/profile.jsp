<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name} | Movie Details</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            #infinite-feed-app {
                margin-bottom: 50vh;
            }

            #infinite-feed-app .review {
                background-color: whitesmoke;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <m:insessionmuffcommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute name="inSessionMuffId">${sessionScope.get(SessionKeys.MUFF).getId()}</jsp:attribute>
            <jsp:body>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/infinitefeedapp.jsx"></script>
                <script src="${pageContext.request.contextPath}/static/chartjs/Chart.min.js"></script>
                <script type="text/babel">
                    ReactDOM.render(<InfiniteFeedApp reviewFetchParam={${requestScope.profileMovie.getId()}}
                                                     contextPath="${pageContext.request.contextPath}"
                                                     reviewFetchUrl="/review/fetch/movie"/>, document.getElementById('infinite-feed-app'));
                </script>
                <%--Infinite Feed App--%>
                <div class="row" style="min-height: 100vh">
                    <div class="col s4">
                        <div class="collection with-header">
                            <div class="collection-header"><h4>${requestScope.profileMovie.getName()}</h4></div>
                            <div class="collection-item">
                                <jstl:forEach items="${requestScope.profileMovie.getGenres()}" var="genre">
                                    <span class="grey-text">${genre.getName()} </span>
                                </jstl:forEach>
                            </div>
                            <div class="collection-item">Duration
                                <div class="secondary-content">${requestScope.profileMovie.getDurationInMinutes()}
                                    min
                                </div>
                            </div>
                            <div class="collection-item">Average rating
                                <div class="secondary-content">${requestScope.averageRating} / 10</div>
                            </div>
                            <div class="collection-item">Number of ratings
                                <div class="secondary-content">${requestScope.reviewCount}</div>
                            </div>
                            <div class="collection-item">Rating distribution</div>
                            <canvas id="myChart" width="400" height="400"></canvas>
                            <script>
                                let ctx = document.getElementById("myChart").getContext('2d');
                                let myChart = new Chart(ctx, {
                                    type: 'bar',
                                    data: {
                                        labels: [
                                            <jstl:forEach items="${requestScope.ratingHistogram}" var="bin">
                                            ${bin.key},
                                            </jstl:forEach>
                                        ],
                                        datasets: [{
                                            label: 'Number of muffs',
                                            data: [
                                                <jstl:forEach items="${requestScope.ratingHistogram}" var="bin">
                                                ${bin.value},
                                                </jstl:forEach>
                                            ],
                                            backgroundColor: 'rgba(255, 99, 132, 0.7)',
                                            borderColor: 'rgba(255,99,132,1)',
                                            borderWidth: 1,
                                        }]
                                    },
                                    options: {
                                        scales: {
                                            yAxes: [{
                                                ticks: {
                                                    beginAtZero: true,
                                                    stepSize: 1,
                                                }
                                            }]
                                        }
                                    }
                                });
                            </script>
                        </div>
                    </div>
                    <div class="col s4">
                        <div id="infinite-feed-app"></div>
                    </div>
                </div>

            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>