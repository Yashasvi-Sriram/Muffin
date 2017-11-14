<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${requestScope.cinemaBuilding.getName()}, ${requestScope.cinemaBuilding.getStreetName()}, ${requestScope.cinemaBuilding.getCity()}</jsp:attribute>
    <jsp:body>
        <m:insessioncinemabuildingownercommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionMuffId">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).getId()}</jsp:attribute>
            <jsp:body>
                <script type="text/babel">
                    let truncate = function (string, maxLength) {
                        let label;
                        if (string.length > maxLength) {
                            label = string.substring(0, maxLength) + '...';
                        }
                        else {
                            label = string;
                        }
                        return label;
                    };

                    let isTheatreValid = function (screenNo) {

                        if (screenNo === ''
                            || isNaN(Number(screenNo))) {
                            Materialize.toast('Invalid Screen Number!', 3000);
                            return false;
                        }

                        return true;
                    };


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


                            return (
                                    <tr title={this.props.screenNo}>


                                        <td>{this.props.screenNo}</td>

                                        <td>
                                        </td>
                                        <td>
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

                    let TheatreEditor = React.createClass({
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
                        createTheatre: function () {
                            let self = this;
                            // validation
                            if (!isTheatreValid(this.refs.screenNo.value)) {
                                return;
                            }
                            // ajax call

                            $.ajax({
                                url: '${pageContext.request.contextPath}/theatre/create',
                                type: 'GET',
                                data: {cinemaBuildingId: this.state.cinemaBuildingId, screenNo: this.refs.screenNo.value},
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        self.setState((prevState, props) => {
                                            prevState.theatres.push(data);
                                            return prevState;
                                        });
                                        $(self.refs.createTheatreForm).find('input').val('');
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
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
                                        <tr className="create-theatre-form"
                                            ref="createTheatreForm">

                                            <td>
                                                <input type="number"
                                                       ref="screenNo"
                                                       name="screenNo"
                                                       placeholder="Screen Number"
                                                       defaultValue=""/>
                                            </td>


                                            <td>
                                            </td>
                                            <td>
                                                <button onClick={this.createTheatre}
                                                        className="btn-floating waves-effect waves-light green">
                                                    <i className="material-icons">add</i>
                                                </button>
                                            </td>
                                        </tr>
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

                    ReactDOM.render(<TheatreEditor/>, document.getElementById('app'));
                </script>
                <div class="container" style="min-height: 100vh">
                    <h1>${requestScope.cinemaBuilding.getName()}, ${requestScope.cinemaBuilding.getStreetName()}, ${requestScope.cinemaBuilding.getCity()}</h1>
                    <div id="app"></div>
                </div>
            </jsp:body>
        </m:insessioncinemabuildingownercommons>

    </jsp:body>
</m:base>