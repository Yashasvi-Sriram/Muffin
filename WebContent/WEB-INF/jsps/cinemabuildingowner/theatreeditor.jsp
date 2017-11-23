<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute
            name="title">${requestScope.cinemaBuilding.getName()}, ${requestScope.cinemaBuilding.getStreetName()}, ${requestScope.cinemaBuilding.getCity()}</jsp:attribute>
    <jsp:body>
        <m:insessioncinemabuildingownercommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionCinemaBuildingOwnerId">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).getId()}</jsp:attribute>
            <jsp:body>
                <script type="text/babel">
                    /**
                     * @propFunctions: onDeleteClick
                     * */
                    let TheatreItem = React.createClass({
                        getInitialState: function () {

                            return {
                                inReadMode: true,
                            }
                        },
                        readModeRender: function () {
                            let url = "${pageContext.request.contextPath}/cinemabuildingowner/showeditor?theatreId=" + this.props.id;

                            return (
                                    <tr title={this.props.screenNo}>

                                        <td>{this.props.screenNo}</td>

                                        <td>
                                        </td>
                                        <td>
                                            <a href={url} type="submit" value={this.props.id}
                                               title="Shows present"
                                               className="btn-floating waves-effect waves-light blue">
                                                <i className="material-icons">local_movies</i></a>
                                        </td>
                                        <td title="Delete">
                                            <a href="#"
                                               onClick={(e) => {
                                                   this.props.onDeleteClick(this.props.id)
                                               }}
                                               className="btn-floating waves-effect waves-light red">
                                                <i className="material-icons">remove</i>
                                            </a>
                                        </td>

                                    </tr>
                            );
                        },

                        render: function () {
                            return this.readModeRender();
                        }
                    });

                    let TheatreList = React.createClass({
                        getInitialState: function () {

                            return {
                                cinemaBuildingId: ${requestScope.cinemaBuilding.getId()},
                                theatres: [
                                    <jstl:forEach items="${requestScope.theatreList}" var="theatre">
                                    {
                                        id: ${theatre.id},
                                        cinemaBuildingId: ${theatre.cinemaBuildingId},
                                        screenNo: ${theatre.screenNo},

                                    },
                                    </jstl:forEach>
                                ],
                            }
                        },
                        deleteTheatre: function (id) {
                            let self = this;
                            $.ajax({
                                url: '${pageContext.request.contextPath}/theatre/delete',
                                type: 'GET',
                                data: {id: id},
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        self.setState((prevState, props) => {
                                            let delIndex = -1;
                                            prevState.theatres.forEach((theatre, i) => {
                                                if (theatre.id === id) {
                                                    delIndex = i;
                                                }
                                            });
                                            prevState.theatres.splice(delIndex, 1);
                                            return prevState;
                                        });
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                        },
                        render: function () {
                            return (
                                    <table className="highlight centered striped">
                                        <thead>
                                        <tr>
                                            <th>Screen No</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {
                                            this.state.theatres.map(m => {
                                                return <TheatreItem key={m.id}
                                                                    id={m.id}
                                                                    screenNo={m.screenNo}
                                                                    onDeleteClick={this.deleteTheatre}
                                                />;
                                            })
                                        }
                                        </tbody>
                                    </table>
                            );
                        }
                    });

                    ReactDOM.render(<TheatreList/>, document.getElementById('app'));
                </script>
                <div class="container" style="min-height: 100vh">
                    <h1>${requestScope.cinemaBuilding.getName()}, ${requestScope.cinemaBuilding.getStreetName()}, ${requestScope.cinemaBuilding.getCity()}</h1>
                    <div id="app"></div>
                </div>
            </jsp:body>
        </m:insessioncinemabuildingownercommons>
    </jsp:body>
</m:base>