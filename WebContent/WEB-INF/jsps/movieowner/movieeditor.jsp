<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:body>
        <script type="text/javascript">
            $(document).ready(function () {
                $('#create-movie-form-submit-btn').click(function () {
                    $.ajax({
                        url: '${pageContext.request.contextPath}/movieowner/movieeditor/create',
                        type: 'GET',
                        data: $('#create-movie-form').serialize(),
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            } else {
                                $('#movie-table')
                                    .append('<tr><td>'
                                        + $('#create-movie-form').find('.name').val()
                                        + '</td><td>'
                                        + $('#create-movie-form').find('.durationInMinutes').val()
                                        + '</td><td>');
                                $('#create-movie-form').trigger('reset');
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                });
            });
        </script>
        <div class="container">
            Movies List
        </div>
        <table id="movie-table">
            <jstl:forEach items="${requestScope.movieList}" var="movieList">
                <tr id="${movieList.id}">
                    <td><jstl:out value="${movieList.name}"/></td>
                    <td><jstl:out value="${movieList.durationInMinutes}"/></td>
                </tr>
            </jstl:forEach>
        </table>
        <h4>Add new Movie</h4>
        <form id="create-movie-form">
            <div class="input-field">
                <i class="material-icons prefix">movie</i>
                <input id="icon_prefix"
                       class="name"
                       name="name"
                       type="text"
                       maxlength="50">
                <label for="icon_prefix">Movie Name</label>
            </div>
            <div class="input-field">
                <i class="material-icons prefix">timer</i>
                <input id="icon_telephone"
                       class="durationInMinutes valiadate"
                       name="durationInMinutes"
                       type="number" min="1" max="500">
                <label for="icon_telephone">Duration(in minutes)</label>
            </div>

        </form>

        <div>
            <button class="btn-flat black-text" id="create-movie-form-submit-btn"><i class="material-icons">send</i>
            </button>
        </div>
    </jsp:body>
</m:base>