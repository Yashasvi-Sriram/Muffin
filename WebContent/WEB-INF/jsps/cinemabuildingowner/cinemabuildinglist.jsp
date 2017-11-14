<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).name}'s Cinemas</jsp:attribute>
    <jsp:body>
        <m:insessioncinemabuildingownercommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionCinemaBuildingOwnerId">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).getId()}</jsp:attribute>
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

                    /**
                     * @propFunctions: onDeleteClick
                     * */
                    let CinemaBuildingItem = React.createClass({

                        render: function () {
                            let url = "${pageContext.request.contextPath}/cinemabuildingowner/theatredetail?cinemaBuildingId=" + this.props.id;
                            return (
                                    <tr title={this.props.name}>
                                        <td>{truncate(this.props.name, 25)}</td>
                                        <td>{truncate(this.props.streetName, 25)}</td>
                                        <td>{truncate(this.props.city, 25)}</td>
                                        <td>{truncate(this.props.state, 25)}</td>
                                        <td>{truncate(this.props.country, 25)}</td>
                                        <td>
                                            <a href={url} type="submit" value={this.props.id}
                                               title="Theatres inside"
                                               className="btn-floating waves-effect waves-light orange">
                                                <i className="material-icons">info</i></a>
                                        </td>
                                        <td>
                                            <a href="#"
                                               onClick={(e) => {
                                                   this.props.onDeleteClick(this.props.id)
                                               }}
                                               title="Remove"
                                               className="btn-floating waves-effect waves-light red">
                                                <i className="material-icons">remove</i>
                                            </a>
                                        </td>
                                    </tr>
                            );
                        }
                    });

                    let CinemaBuildingList = React.createClass({
                        getInitialState: function () {

                            return {
                                cinemabuildings: [
                                    <jstl:forEach items="${requestScope.cinemaBuildingList}" var="cinemaBuilding">
                                    {
                                        id: ${cinemaBuilding.id},
                                        name: '${cinemaBuilding.name}',
                                        ownerId: '${cinemaBuilding.ownerId}',
                                        streetName: '${cinemaBuilding.streetName}',
                                        city: '${cinemaBuilding.city}',
                                        state: '${cinemaBuilding.state}',
                                        country: '${cinemaBuilding.country}',
                                        zip: '${cinemaBuilding.zip}',

                                    },
                                    </jstl:forEach>
                                ],
                            }
                        },

                        deleteBuilding: function (id) {
                            let self = this;
                            $.ajax({
                                url: '${pageContext.request.contextPath}/cinemabuilding/delete',
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
                                            prevState.cinemabuildings.forEach((cinemabuilding, i) => {
                                                if (cinemabuilding.id === id) {
                                                    delIndex = i;
                                                }
                                            });
                                            prevState.cinemabuildings.splice(delIndex, 1);
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
                                            <th>Name</th>
                                            <th>Street Name</th>
                                            <th>City</th>
                                            <th>State</th>
                                            <th>Country</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        {
                                            this.state.cinemabuildings.map(m => {
                                                return <CinemaBuildingItem key={m.id}
                                                                           id={m.id}
                                                                           name={m.name}
                                                                           city={m.city}
                                                                           state={m.state}
                                                                           country={m.country}
                                                                           zip={m.zip}
                                                                           streetName={m.streetName}
                                                                           ownerId={m.ownerId}
                                                                           onDeleteClick={this.deleteBuilding}
                                                />;
                                            })
                                        }
                                        </tbody>
                                    </table>
                            );
                        }
                    });

                    ReactDOM.render(<CinemaBuildingList/>, document.getElementById('app'));
                </script>

                <div class="container" style="min-height: 100vh">
                    <h1>${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).name}'s Cinemas</h1>
                    <div class="divider"></div>
                    <div id="app"></div>
                </div>
            </jsp:body>
        </m:insessioncinemabuildingownercommons>
    </jsp:body>
</m:base>