<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            input {
                border: none !important;
                box-shadow: none !important;
                outline: none !important;
                color: black !important;
                font-size: x-large !important;
                margin-left: 5% !important;
                width: 80% !important;
                margin-right: 5% !important;
                margin-bottom: 0 !important;
            }

            input:focus {
                border: none !important;
                box-shadow: none !important;
                outline: 1px solid black !important;
            }
        </style>
    </jsp:attribute>
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
                    return (
                            <tr title={this.props.name}>
                                <td className="flow-text">{truncate(this.props.name, 25)}</td>
                                <td className="flow-text">{truncate(this.props.streetName, 25)}</td>
                                <td className="flow-text">{truncate(this.props.city, 25)}</td>
                                <td className="flow-text">{truncate(this.props.state, 25)}</td>
                                <td className="flow-text">{truncate(this.props.country, 25)}</td>
                                <td>
                                    <a href="#"

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
        <div id="app" class="container"></div>
    </jsp:body>
</m:base>