<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute
            name="title">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).name}  | Cinema Building Owner Home</jsp:attribute>
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

            a {
                font-size: 20px
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <script type="text/babel">
            let isBuildingValid = function (name, streetName, city, state, country, zip) {
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

            let Region = React.createClass({
                render: function () {
                    return (
                            <tr>
                                <td onClick={e => this.props.onItemClick(this.props.city, this.props.state, this.props.country)}>{this.props.city}({this.props.state},{this.props.country})</td>
                            </tr>
                    );
                }
            });

            let CreateCinemaBuilding = React.createClass({
                getInitialState: function () {
                    return {
                        regions: [],
                        offset: 0,
                    }
                },
                getDefaultProps: function () {
                    return {
                        limit: 3,
                        contextPath: '',
                        url: '',
                    }
                },
                _resetOffset: function () {
                    this.state.offset = 0;
                },
                _incrementOffset: function (fetchedDataLength) {
                    // last batch
                    if (fetchedDataLength < this.props.limit) {
                        this.state.offset -= this.props.limit;
                        this.state.offset += fetchedDataLength;
                    }
                    // update limit
                    this.state.offset += this.props.limit;
                },
                _decrementOffset: function () {
                    let floorExcess = this.state.offset % this.props.limit;
                    this.state.offset -= floorExcess;
                    if (floorExcess === 0) {
                        let prevOffset = this.state.offset - 2 * this.props.limit;
                        this.state.offset = prevOffset < 0 ? 0 : prevOffset;
                    } else {
                        let prevOffset = this.state.offset - this.props.limit;
                        this.state.offset = prevOffset < 0 ? 0 : prevOffset;
                    }
                },
                fetchNextBatch: function (pattern) {
                    let url = '${pageContext.request.contextPath}/validregion/search';
                    let self = this;
                    $.ajax({
                        url: url,
                        type: 'GET',
                        data: {pattern: pattern, offset: self.state.offset, limit: self.props.limit},
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                let data = json.data;
                                self._incrementOffset(data.length);
                                // no results
                                if (data.length === 0) {
                                    Materialize.toast('End of search!', 2000);
                                    return;
                                }
                                // add results
                                self.setState(ps => {
                                    return {regions: data};
                                });
                                $(self.refs.results).show();
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                fetchPreviousBatch: function (pattern) {
                    this._decrementOffset();
                    if (this.state.offset === 0) {
                        Materialize.toast('Start of search!', 2000);
                    }
                    this.fetchNextBatch(pattern);
                },
                onRegexInputKeyDown: function (e) {
                    switch (e.keyCode || e.which) {
                        // Enter Key
                        case 13:
                            let self = this;
                            this._resetOffset();
                            self.setState({regions: []});
                            this.fetchNextBatch(e.target.value);
                            break;
                        // Escape key
                        case 27:
                            $(this.refs.results).hide();
                            break;
                        // Page Up key
                        case 33:
                            this.fetchPreviousBatch(this.refs.pattern.value);
                            break;
                        // Page Down key
                        case 34:
                            this.fetchNextBatch(this.refs.pattern.value);
                            break;
                        default:
                            break;
                    }
                },
                selectRegion: function (city, state, country) {
                    this.refs.pattern.value = '';
                    this.refs.city.value = city;
                    this.refs.state.value = state;
                    this.refs.country.value = country;
                    $(this.refs.results).hide();

                },
                createNewBuilding: function () {
                    let self = this;
                    // validation
                    if (!isBuildingValid(this.refs.name.value, this.refs.streetName.value, this.refs.city.value, this.refs.state.value, this.refs.country.value, this.refs.zip.value)) {
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
                                $(self.refs.createBuildingForm).find('input').val('');
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                render: function () {
                    let results = this.state.regions.map(region => {
                        return <Region
                                key={region.id}
                                id={region.id}
                                city={region.city}
                                state={region.state}
                                country={region.country}
                                onItemClick={this.selectRegion}

                        />;
                    });
                    return (
                            <div className="create-building-form"
                                 ref="createBuildingForm">

                                <div className="row">
                                    <div className="col s6">
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
                                    <div className="col s2">
                                    </div>
                                    <div className="col s4">
                                        <input onKeyDown={this.onRegexInputKeyDown}
                                               ref="pattern"
                                               placeholder="Search For Valid Regions" type="text"/>
                                        <div ref="results">
                                            <div className="collection with-header">
                                                <div className="collection-header"><span
                                                        className="flow-text">Results</span>
                                                    <span className="right">
                                                    <button className="btn btn-flat"
                                                            onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                                            className="material-icons">keyboard_arrow_left</i></button>
                                                    <button className="btn btn-flat"
                                                            onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                                            className="material-icons">keyboard_arrow_right</i></button>
                                                    </span>
                                                </div>
                                                <table className="white highlight bordered">
                                                    <tbody>{results}</tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                    );
                },
                componentDidMount: function () {
                    $(this.refs.results).hide();
                },
            });


            ReactDOM.render(<CreateCinemaBuilding/>,
                document.getElementById('create-cinema-building'));
        </script>
        <a href="${pageContext.request.contextPath}/cinemabuildingowner/buildinglist">See your existing buildings</a>
        <h4>Add a new Building</h4>
        <div id="create-cinema-building"></div>
    </jsp:body>
</m:base>