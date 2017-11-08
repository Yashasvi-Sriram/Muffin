<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="extraHeaders">
        <link href="https://fonts.googleapis.com/css?family=Lobster" rel="stylesheet">
    </jsp:attribute>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name} | Home</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            #search-app td {
                padding: 10px;
                font-size: large;
                cursor: pointer;
            }

            #search-app input {
                font-size: large !important;
            }

            #infinite-feed-app .review textarea {
                border: none;
            }

            #infinite-feed-app .review {
                border-radius: 15px !important;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <script type="text/babel"
                src="${pageContext.request.contextPath}/static/muffinjs/buttersearchapp.jsx"></script>
        <script type="text/babel"
                src="${pageContext.request.contextPath}/static/muffinjs/givereviewapp.jsx"></script>
        <script type="text/babel"
                src="${pageContext.request.contextPath}/static/muffinjs/infinitefeedapp.jsx"></script>
        <script type="text/babel">
            ReactDOM.render(<ButterSearchApp limit={5}
                                             contextPath="${pageContext.request.contextPath}"
                                             muffSearchUrl="/muff/search"
                                             movieSearchUrl="/movie/search"
                                             actorSearchUrl="/actor/search"/>, document.getElementById('search-app'));
            ReactDOM.render(<GiveReviewApp contextPath="${pageContext.request.contextPath}"
                                           url="/review/create"/>, document.getElementById('give-review-app'));
            ReactDOM.render(<InfiniteFeedApp muffId={${sessionScope.get(SessionKeys.MUFF).getId()}}
                                             contextPath="${pageContext.request.contextPath}"
                                             reviewFetchUrl="/review/fetch/followers"/>, document.getElementById('infinite-feed-app'));
        </script>
        <script type="text/javascript">
            $(document).ready(function () {
                $('.button-collapse').sideNav({
                    menuWidth: '50vw',
                    edge: 'right',
                    closeOnClick: true,
                    draggable: true,
                });

                $('.modal').modal();
            });
        </script>
        <%--Give Review Modal--%>
        <div id="give-review-modal" class="modal modal-fixed-footer">
            <div class="modal-content">
                <div id="give-review-app"></div>
            </div>
            <div class="modal-footer">
                <button class="modal-action modal-close waves-effect btn-flat">
                    <i class="material-icons">close</i></button>
            </div>
        </div>
        <%--Give Review Modal Shortcut--%>
        <a href="#give-review-modal"
           title="Give Review"
           class="btn-floating btn-large waves-effect waves-light green modal-trigger"
           style="position:fixed;bottom:100px; right: 20px">
            <i class="material-icons">add</i>
        </a>

        <%--Butter Search Modal--%>
        <div id="butter-search-modal" class="modal modal-fixed-footer">
            <div class="modal-content">
                <div id="search-app"></div>
            </div>
            <div class="modal-footer">
                <a href="#" class="modal-action modal-close waves-effect btn-flat">
                    <i class="material-icons">close</i>
                </a>
            </div>
        </div>
        <%--Butter Search Modal Shortcut--%>
        <a href="#butter-search-modal"
           title="Butter Search"
           class="btn-floating btn-large waves-effect waves-light blue modal-trigger"
           style="position:fixed;bottom: 180px; right: 20px">
            <i class="material-icons">search</i>
        </a>

        <%--Nav bar--%>
        <ul id="nav-bar" class="side-nav">
            <li>
                <div class="user-view brown" style="margin: 0">
                    <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="Muffi(co)n">
                    <span class="white-text"
                          style="font-size: 50px; font-family: 'Lobster', cursive;">Movie Buff Inc.</span>
                </div>
            </li>
            <li>
                <div class="divider" style="margin: 0"></div>
            </li>
            <li><a class="modal-trigger"
                   href="#butter-search-modal"
                   title="Butter Search">
                <i class="material-icons">search</i>Butter Search</a>
            </li>
            <li>
                <a class="modal-trigger"
                   href="#give-review-modal"
                   title="Give Review">
                    <i class="material-icons">add</i>
                    Give Review
                </a>
            </li>
        </ul>
        <%--Nav bar trigger--%>
        <a href="#"
           data-activates="nav-bar"
           class="btn-floating btn-large waves-effect waves-light brown button-collapse pulse"
           style="position:fixed;bottom: 20px; right: 20px">
            <i class="material-icons">menu</i>
        </a>

        <%--Infinite Feed App--%>
        <div class="container">
            <div id="infinite-feed-app"></div>
        </div>
    </jsp:body>
</m:base>