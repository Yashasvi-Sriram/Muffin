<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Book Tickets</jsp:attribute>
    <jsp:body>
        <m:insessionmuffcommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionMuffId">${sessionScope.get(SessionKeys.MUFF).getId()}</jsp:attribute>
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

                    let isMovieValid = function (movieName) {
                        if (movieName === '') {
                            Materialize.toast('Movie Name is Empty', 2000);
                            return false;
                        }
                        return true;
                    };

                    let isRegionValid = function (city, state, country) {
                        if (city === '' || state === '' || country === '') {
                            Materialize.toast('Select a Region before search', 2000);
                            return false;
                        }
                        return true;
                    };

                    let dateString = function (localDateTime) {
                        let p = moment();
                        p.year(localDateTime.date.year).month(localDateTime.date.month - 1).date(localDateTime.date.day);
                        return p.format("ddd Do MMM YY");
                    };

                    let timeString = function (localDateTime) {
                        let p = moment();
                        p.hour(localDateTime.time.hour).minute(localDateTime.time.minute).second(localDateTime.time.second);
                        return p.format("HH:mm");
                    };

                    let MovieSearchResult = React.createClass({
                        render: function () {
                            return (
                                    <div className="collection-item"
                                         onClick={e => this.props.onItemClick(this.props.name)}>{this.props.name}</div>
                            );
                        }
                    });

                    let Region = React.createClass({
                        render: function () {
                            return (
                                    <div className="collection-item"
                                         onClick={e => this.props.onItemClick(this.props.city, this.props.state, this.props.country)}>
                                        {this.props.city}, {this.props.state}, {this.props.country}
                                    </div>
                            );
                        }
                    });

                    let ShowItem = React.createClass({
                        render: function () {
                            return (
                                    <div>
                                        <a href={"${pageContext.request.contextPath}/booking/create?showId=" + this.props.id}>
                                            {timeString(this.props.showtime.startTime)} - {timeString(this.props.showtime.endTime)}
                                        </a>
                                    </div>
                            );
                        }
                    });

                    let BuildingItem = React.createClass({
                        render: function () {
                            let showItems = this.props.showList.map(c => {
                                return <ShowItem key={c.id}
                                                 id={c.id}
                                                 movie={c.movie}
                                                 showtime={c.showtime}
                                />;
                            });
                            return (
                                    <div className="collection-item">
                                        <div><h5>{this.props.name} , {this.props.streetName}</h5></div>
                                        <div>{showItems}</div>
                                    </div>
                            );
                        }
                    });

                    let ShowApp = React.createClass({
                        getInitialState: function () {
                            return {
                                movieSearchResults: [],
                                offset: 0,
                                regions: [],
                                region_offset: 0,
                                shows: [],
                                date_offset: 0,
                            }
                        },
                        getDefaultProps: function () {
                            return {
                                contextPath: '',
                                region_limit: 5,
                                movie_limit: 5,
                            }
                        },
                        _movie_resetOffset: function () {
                            this.state.offset = 0;
                        },
                        _movie_incrementOffset: function (fetchedDataLength) {
                            // last batch
                            if (fetchedDataLength < this.props.movie_limit) {
                                this.state.offset -= this.props.movie_limit;
                                this.state.offset += fetchedDataLength;
                            }
                            // update movie_limit
                            this.state.offset += this.props.movie_limit;
                        },
                        _movie_decrementOffset: function () {
                            let floorExcess = this.state.offset % this.props.movie_limit;
                            this.state.offset -= floorExcess;
                            if (floorExcess === 0) {
                                let prevOffset = this.state.offset - 2 * this.props.movie_limit;
                                this.state.offset = prevOffset < 0 ? 0 : prevOffset;
                            } else {
                                let prevOffset = this.state.offset - this.props.movie_limit;
                                this.state.offset = prevOffset < 0 ? 0 : prevOffset;
                            }
                        },
                        movie_fetchNextBatch: function (pattern) {
                            let url = '${pageContext.request.contextPath}/show/activemoviesearch';
                            let self = this;
                            let currentTimeStamp = moment().format("YYYY-MM-DDTHH:mm");
                            $.ajax({
                                url: url,
                                type: 'GET',
                                data: {
                                    pattern: pattern,
                                    offset: self.state.offset,
                                    limit: self.props.movie_limit,
                                    currentTimeStamp: currentTimeStamp
                                },
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        self._movie_incrementOffset(data.length);
                                        // no movieSearchResults
                                        if (data.length === 0) {
                                            Materialize.toast('End of search!', 2000);
                                            return;
                                        }
                                        // add movieSearchResults
                                        self.setState(ps => {
                                            return {movieSearchResults: data};
                                        });
                                        $(self.refs.movieSearchResults).show();
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                        },
                        movie_fetchPreviousBatch: function (pattern) {
                            this._movie_decrementOffset();
                            if (this.state.offset === 0) {
                                Materialize.toast('Start of search!', 2000);
                            }
                            this.movie_fetchNextBatch(pattern);
                        },
                        movie_onRegexInputKeyDown: function (e) {
                            switch (e.keyCode || e.which) {
                                // Enter Key
                                case 13:
                                    let self = this;
                                    this._movie_resetOffset();
                                    self.setState({movieSearchResults: []});
                                    this.movie_fetchNextBatch(e.target.value);
                                    break;
                                // Escape key
                                case 27:
                                    $(this.refs.movieSearchResults).hide();
                                    break;
                                default:
                                    break;
                            }
                        },
                        selectMovie: function (text) {
                            this.refs.pattern.value = text;
                            $(this.refs.movieSearchResults).hide();
                            $(this.refs.dateSelectDiv).hide();

                        },
                        _region_resetOffset: function () {
                            this.state.region_offset = 0;
                        },
                        _region_incrementOffset: function (fetchedDataLength) {
                            // last batch
                            if (fetchedDataLength < this.props.region_limit) {
                                this.state.region_offset -= this.props.region_limit;
                                this.state.region_offset += fetchedDataLength;
                            }
                            // update limit
                            this.state.region_offset += this.props.region_limit;
                        },
                        _region_decrementOffset: function () {
                            let floorExcess = this.state.region_offset % this.props.region_limit;
                            this.state.region_offset -= floorExcess;
                            if (floorExcess === 0) {
                                let prevOffset = this.state.region_offset - 2 * this.props.region_limit;
                                this.state.region_offset = prevOffset < 0 ? 0 : prevOffset;
                            } else {
                                let prevOffset = this.state.region_offset - this.props.region_limit;
                                this.state.region_offset = prevOffset < 0 ? 0 : prevOffset;
                            }
                        },
                        region_fetchNextBatch: function (pattern) {
                            let url = '${pageContext.request.contextPath}/validregion/search';
                            let self = this;
                            $.ajax({
                                url: url,
                                type: 'GET',
                                data: {
                                    pattern: pattern,
                                    offset: self.state.region_offset,
                                    limit: self.props.region_limit
                                },
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        self._region_incrementOffset(data.length);
                                        // no results
                                        if (data.length === 0) {
                                            Materialize.toast('End of search!', 2000);
                                            return;
                                        }
                                        // add results
                                        self.setState(ps => {
                                            return {regions: data};
                                        });
                                        $(self.refs.regionResults).show();
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                        },
                        region_fetchPreviousBatch: function (pattern) {
                            this._region_decrementOffset();
                            if (this.state.region_offset === 0) {
                                Materialize.toast('Start of search!', 2000);
                            }
                            this.region_fetchNextBatch(pattern);
                        },
                        region_onRegexInputKeyDown: function (e) {
                            switch (e.keyCode || e.which) {
                                // Enter Key
                                case 13:
                                    let self = this;
                                    this._region_resetOffset();
                                    self.setState({regions: []});
                                    this.region_fetchNextBatch(e.target.value);
                                    break;
                                // Escape key
                                case 27:
                                    $(this.refs.regionResults).hide();
                                    break;
                                // Page Up key
                                case 33:
                                    this.region_fetchPreviousBatch(this.refs.region_pattern.value);
                                    break;
                                // Page Down key
                                case 34:
                                    this.region_fetchNextBatch(this.refs.region_pattern.value);
                                    break;
                                default:
                                    break;
                            }
                        },
                        selectRegion: function (city, state, country) {
                            this.refs.region_pattern.value = '';
                            $(this.refs.regionResults).hide();
                            $(this.refs.dateSelectDiv).hide();
                            this.refs.city.innerHTML = city;
                            this.refs.state.innerHTML = state;
                            this.refs.country.innerHTML = country;


                        },
                        getShows: function (movieName, city, state, country, date_offset) {
                            let url = '${pageContext.request.contextPath}/show/showlist';
                            let self = this;
                            let startTimeStamp = '';
                            if (date_offset === 0) {
                                startTimeStamp = moment().format("YYYY-MM-DDTHH:mm");
                            }
                            else {
                                startTimeStamp = moment().add(date_offset, 'days').startOf('day').format("YYYY-MM-DDTHH:mm");
                            }
                            let endTimeStamp = moment().add(date_offset, 'days').endOf('day').format("YYYY-MM-DDTHH:mm");
                            if (!isMovieValid(movieName)) {
                                return;
                            }
                            if (!isRegionValid(city, state, country)) {
                                return;
                            }
                            $.ajax({
                                url: url,
                                type: 'GET',
                                data: {
                                    movieName: movieName,
                                    city: city,
                                    state: state,
                                    country: country,
                                    startTimeStamp: startTimeStamp,
                                    endTimeStamp: endTimeStamp,
                                },
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        self.setState(ps => {
                                            return {shows: data, date_offset: date_offset};
                                        });
                                        $(self.refs.dateSelectDiv).show();
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                        },
                        getNextDateShows: function (movieName, city, state, country) {
                            let self = this;
                            self.getShows(movieName, city, state, country, self.state.date_offset + 1);
                        },
                        getPrevDateShows: function (movieName, city, state, country) {
                            let self = this;
                            if (self.state.date_offset === 0) {
                                Materialize.toast('You cannot go back', 2000);
                                return;
                            }
                            self.getShows(movieName, city, state, country, self.state.date_offset - 1);
                        },
                        render: function () {
                            let movieSearchResults = this.state.movieSearchResults.map(movie => {
                                return <MovieSearchResult
                                        key={movie.id}
                                        id={movie.id}
                                        name={movie.name}
                                        onItemClick={this.selectMovie}
                                />;
                            });
                            let regionResults = this.state.regions.map(region => {
                                return <Region
                                        key={region.id}
                                        id={region.id}
                                        city={region.city}
                                        state={region.state}
                                        country={region.country}
                                        onItemClick={this.selectRegion}

                                />;
                            });
                            let buildingResults = this.state.shows.map(building => {
                                return <BuildingItem
                                        key={building.key.id}
                                        id={building.key.id}
                                        name={building.key.name}
                                        streetName={building.key.streetName}
                                        showList={building.value}
                                />;
                            });


                            let dateToDisplay = moment().add(this.state.date_offset, 'days').format("ddd Do MMM YY");

                            return (
                                    <div>
                                        <div className="row" style={{margin: '0px'}}>
                                            <div className="col s6">
                                                <input onKeyDown={this.movie_onRegexInputKeyDown}
                                                       ref="pattern"
                                                       placeholder="Movie" type="text"/>
                                                <div className="collection with-header" ref="movieSearchResults">
                                                    <div className="collection-header"><span className="flow-text">Movies</span>
                                                        <span className="right">
                                                <button className="btn btn-flat"
                                                        onClick={e => this.movie_fetchPreviousBatch(this.refs.pattern.value)}><i
                                                        className="material-icons">keyboard_arrow_left</i></button>
                                                <button className="btn btn-flat"
                                                        onClick={e => this.movie_fetchNextBatch(this.refs.pattern.value)}><i
                                                        className="material-icons">keyboard_arrow_right</i></button>
                                                </span>
                                                    </div>
                                                    {movieSearchResults}
                                                </div>
                                            </div>
                                            <div className="col s6 flow-text">
                                                <pre>
                                                    <span ref="city"></span> <span ref="state"></span> <span
                                                        ref="country"></span>
                                                </pre>
                                            </div>
                                        </div>
                                        <div className="row" style={{margin: '0px'}}>
                                            <div className="col s6">
                                                <input onKeyDown={this.region_onRegexInputKeyDown}
                                                       ref="region_pattern"
                                                       placeholder="Region" type="text"/>
                                                <div className="collection with-header" ref="regionResults">
                                                    <div className="collection-header"><span className="flow-text">Regions</span>
                                                        <span className="right">
                                                <button className="btn btn-flat"
                                                        onClick={e => this.region_fetchPreviousBatch(this.refs.region_pattern.value)}><i
                                                        className="material-icons">keyboard_arrow_left</i></button>
                                                <button className="btn btn-flat"
                                                        onClick={e => this.region_fetchNextBatch(this.refs.region_pattern.value)}><i
                                                        className="material-icons">keyboard_arrow_right</i></button>
                                                </span>
                                                    </div>
                                                    {regionResults}
                                                </div>
                                            </div>
                                            <div className="col s4">
                                                <button className="btn btn-flat pink white-text"
                                                        style={{marginTop: '20px'}}
                                                        onClick={e => this.getShows(this.refs.pattern.value, this.refs.city.innerHTML, this.refs.state.innerHTML, this.refs.country.innerHTML, 0)}>
                                                    Search shows
                                                </button>
                                            </div>
                                        </div>
                                        <div ref="dateSelectDiv" className="collection">
                                            <div className="right">
                                                <span className="flow-text blue-text">{dateToDisplay}</span>
                                                <button className="btn btn-flat"
                                                        onClick={e => this.getPrevDateShows(this.refs.pattern.value, this.refs.city.innerHTML, this.refs.state.innerHTML, this.refs.country.innerHTML)}>
                                                    <i className="material-icons">keyboard_arrow_left</i></button>
                                                <button className="btn btn-flat"
                                                        onClick={e => this.getNextDateShows(this.refs.pattern.value, this.refs.city.innerHTML, this.refs.state.innerHTML, this.refs.country.innerHTML)}>
                                                    <i className="material-icons">keyboard_arrow_right</i></button>
                                            </div>
                                            <div>{buildingResults}</div>
                                        </div>
                                    </div>

                            );
                        },
                        componentDidMount: function () {
                            $(this.refs.movieSearchResults).hide();
                            $(this.refs.regionResults).hide();
                            $(this.refs.dateSelectDiv).hide();

                        },
                    });

                    ReactDOM.render(<ShowApp/>, document.getElementById('app'));
                </script>
                <div class="container">
                    <h2>Book tickets</h2>
                    <div id="app" style="min-height: 100vh"></div>
                </div>
            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>