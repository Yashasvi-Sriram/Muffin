<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).name}  | Cinema Building Owner Home</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            #search-app {
                position: absolute;
                z-index: 1000;
                top: 10px;
                left: 25vw;
                width: 50vw;
                height: 40px;
                background-color: white;
                border-radius: 2px;
            }

            #search-app td {
                padding: 10px;
                font-size: large;
                cursor: pointer;
            }

            #search-app input {
                border: none !important;
                outline: none;
                box-shadow: none;
                margin: 0 !important;
                font-size: large !important;
            }

            #search-app input:focus {
                border: none !important;
                outline: none;
                box-shadow: none !important;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <script type="text/babel">
            let isReviewValid = function (name, streetName, city, state, country, zip) {
                if (name === '') {
                    Materialize.toast('Cinema Building name is empty!', 3000);
                    return false;
                }
                if (name.length > 50) {
                    Materialize.toast('Cinema Building name must be less than 50 characters!', 3000);
                    return false;
                }
				if (streetName === '') {
                    Materialize.toast('Street Name is empty!', 3000);
                    return false;
                }
                if (streetName.length > 50) {
                    Materialize.toast('Street Name must be less than 50 characters!', 3000);
                    return false;
                }
				if (city === '') {
                    Materialize.toast('City field is empty!', 3000);
                    return false;
                }
				if (state === '') {
                    Materialize.toast('State field is empty!', 3000);
                    return false;
                }
				if (country === '') {
                    Materialize.toast('State field is empty!', 3000);
                    return false;
                }
				if (zip === '') {
                    Materialize.toast('Zip field is empty!', 3000);
                    return false;
                }

               

               
                return true;
            };

          


            let CreateCinemaBuilding = React.createClass({
                createNewBuilding: function () {
                    let self = this;
                    // validation
                    if (!isReviewValid(this.refs.name.value, this.refs.streetName.value, this.refs.city.value,this.refs.state.value,this.refs.country.value,this.refs.zip.value )) {
                        return;
                    }
                    // ajax call
                  

                    $.ajax({
                        url: '${pageContext.request.contextPath}/cinemabuilding/create',
                        type: 'POST',
                        data: {
                            name: this.refs.name.value,
                            streetName: this.refs.streetName.value,
							city: this.refs.city.value,
							state: this.refs.state.value,
							country: this.refs.country.value,
                            zip: this.refs.zip.value
                        },
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                Materialize.toast("Cinema Building Added Successfully", 2000);
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                render: function () {
                    return (
                            <div>
                                <input type="text" ref="name"
                                       name="name" placeholder="Cinema Building Name" defaultValue=""/>
								<input type="text" ref="streetName"
                                       name="streetName" placeholder="Street Name" defaultValue=""/>
								<input type="text" ref="city"
                                       name="city" placeholder="City" defaultValue=""/>
								<input type="text" ref="state"
                                       name="state" placeholder="State" defaultValue=""/>
								<input type="text" ref="country"
                                       name="country" placeholder="Country" defaultValue=""/>
								<input type="text" ref="zip"
                                       name="zip" placeholder="Zip" defaultValue=""/>
                                <button onClick={this.createNewBuilding}
                                        className="btn-floating waves-effect waves-light green">
                                    <i className="material-icons">add</i>
                                </button>
                            </div>
                    );
                }
            });

           
            ReactDOM.render(<CreateCinemaBuilding/>, document.getElementById('create-cinema-building'));
        </script>

        <div id="create-cinema-building"></div>
    </jsp:body>
</m:base>