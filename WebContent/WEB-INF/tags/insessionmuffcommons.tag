<%@tag description="Basic template with javascript headers" pageEncoding="UTF-8" %>
<%@attribute name="contextPath" fragment="true" required="true" %>
<%@attribute name="inSessionMuffId" fragment="true" required="true" %>

<link href="https://fonts.googleapis.com/css?family=Lobster" rel="stylesheet">
<style>
    #butter-search-app input {
        font-size: large !important;
    }
</style>
<script type="text/babel"
        src="<jsp:invoke fragment="contextPath"/>/static/muffinjs/buttersearchapp.jsx"></script>
<script type="text/babel"
        src="<jsp:invoke fragment="contextPath"/>/static/muffinjs/givereviewapp.jsx"></script>
<script type="text/babel"
        src="<jsp:invoke fragment="contextPath"/>/static/muffinjs/seekentertainmentapp.jsx"></script>
<script type="text/babel">
    ReactDOM.render(<ButterSearchApp limit={5}
                                     contextPath="<jsp:invoke fragment="contextPath"/>"
                                     muffSearchUrl="/muff/search"
                                     movieSearchUrl="/movie/search"
                                     actorSearchUrl="/actor/search"/>, document.getElementById('butter-search-app'));
    ReactDOM.render(<GiveReviewApp contextPath="<jsp:invoke fragment="contextPath"/>"
                                   url="/review/give"/>, document.getElementById('give-review-app'));
    ReactDOM.render(<SeekEntertainmentApp
            contextPath="<jsp:invoke fragment="contextPath"/>"
            url='/seek/create'
            genreFetchUrl='/genre/fetch/all'/>, document.getElementById('seek-entertainment-app'));
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

        $('.parallax').parallax();
    });
</script>
<%--Home Shortcut--%>
<a href="<jsp:invoke fragment="contextPath"/>/muff/home"
   title="Home"
   class="btn-floating btn-large waves-effect waves-light red"
   style="position:fixed;bottom: 260px; right: 20px">
    <i class="material-icons">home</i>
</a>

<%--Butter Search Modal--%>
<div id="butter-search-modal" class="modal modal-fixed-footer">
    <div class="modal-content">
        <div id="butter-search-app"></div>
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

<%--Seek Entertainment Modal--%>
<div id="seek-entertainment-modal" class="modal modal-fixed-footer">
    <div class="modal-content">
        <div id="seek-entertainment-app"></div>
    </div>
    <div class="modal-footer">
        <button class="modal-action modal-close waves-effect btn-flat">
            <i class="material-icons">close</i></button>
    </div>
</div>
<%--Seek Entertainment Modal Shortcut--%>
<a href="#seek-entertainment-modal"
   title="Seek Entertainment"
   class="btn-floating btn-large waves-effect waves-light green modal-trigger"
   style="position:fixed;bottom:100px; right: 20px">
    <i class="material-icons">local_movies</i>
</a>

<%--Nav bar--%>
<ul id="nav-bar" class="side-nav">
    <li>
        <div class="user-view brown" style="margin: 0">
            <img src="<jsp:invoke fragment="contextPath"/>/static/images/logo.png" alt="Muffi(co)n">
            <a href="${pageContext.request.contextPath}/" class="white-text"
               style="font-size: 50px; font-family: 'Lobster', cursive;">
                M<span class="grey-text">ovie</span>
                <span class="grey-text">B</span>uff
                in<span class="grey-text">c.</span></a>
        </div>
    </li>
    <li>
        <div class="divider" style="margin: 0"></div>
    </li>
    <li>
        <a href="<jsp:invoke fragment="contextPath"/>/muff/home"
           title="Home">
            <i class="material-icons">home</i>
            Home
        </a>
    </li>
    <li>
        <a href="<jsp:invoke fragment="contextPath"/>/muff/profile?muffId=<jsp:invoke fragment="inSessionMuffId"/>"
           title="My Profile">
            <i class="material-icons">face</i>
            My Profile
        </a>
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
    <li>
        <a class="modal-trigger"
           href="#seek-entertainment-modal"
           title="Seek Entertainment">
            <i class="material-icons">local_movies</i>
            Seek Entertainment
        </a>
    </li>
    <li>
        <a href="<jsp:invoke fragment="contextPath"/>/muff/logout"
           title="Logout">
            <i class="material-icons">power_settings_new</i>
            Log out
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

<div class="parallax-container">
    <div class="parallax"><img src="${pageContext.request.contextPath}/static/images/fireways.jpg"
                               alt="Muffin"/></div>
</div>
<jsp:doBody/>