<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:body>
        <script type="text/javascript">
            $(document).ready(function () {
                $('#formbutton').click(function () {
                    $.ajax({
                        url: '${pageContext.request.contextPath}/movieowner/addmovie',
                        type: 'POST',
                        dataType: 'json',
                        data: $('#addmovieform').serialize(),
                        success: function (data) {
                            if (data['status'] == 'success') {
                                $('#movieTable tbody').append("<tr><td>" + data.moviename + "</td><td>" + data.duration + "</td><td>");
                            }
                            $("#addmovieform").trigger("reset");
                        },
                        error: function (data) {
                        }
                    });
                });
            });
        </script>
        <div class="container">
            Movies List
        </div>
        <table id="movieTable">
            <jstl:forEach items="${requestScope.movieList}" var="movieList">
                <tr id="${movieList.id}">
                    <td><jstl:out value="${movieList.name}"/></td>
                    <td><jstl:out value="${movieList.durationInMinutes}"/></td>
                </tr>
            </jstl:forEach>
        </table>
        <h4>Add new Movie</h4>
        <form id="addmovieform">
            <div class="input-field">
                <i class="material-icons prefix">movie</i>
                <input id="icon_prefix" name="moviename"
                       value=""
                       type="text" maxlength="50">
                <label for="icon_prefix">Movie Name</label>
            </div>
            <div class="input-field">
                <i class="material-icons prefix">timer</i>
                <input id="icon_telephone" name="duration"
                       value=""
                       type="number" min="1" max="1000">
                <label for="icon_telephone">Duration(in minutes)</label>
            </div>

        </form>

        <div>
            <button class="btn-flat black-text" id="formbutton"><i class="material-icons">send</i>
            </button>
        </div>
    </jsp:body>
</m:base>