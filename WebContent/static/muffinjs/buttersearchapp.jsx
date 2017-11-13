let MuffButterSearchResult = React.createClass({
    getDefaultProps: function () {
        return {
            contextPath: '',
        }
    },
    render: function () {
        return (
            <a href={this.props.contextPath + '/muff/profile?muffId=' + this.props.id} className="collection-item">
                <div>{this.props.name}</div>
                <div className="pink-text">{this.props.handle}</div>
            </a>
        );
    }
});

let MovieButterSearchResult = React.createClass({
    getDefaultProps: function () {
        return {
            contextPath: '',
        }
    },
    render: function () {
        return (
            <a href="#" className="collection-item">
                <div>{this.props.name}</div>
            </a>
        );
    }
});

let ActorButterSearchResult = React.createClass({
    getDefaultProps: function () {
        return {
            contextPath: '',
        }
    },
    render: function () {
        return (
            <a href="#" className="collection-item">
                <div>{this.props.name}</div>
            </a>
        );
    }
});

window.ButterSearchApp = React.createClass({
    MODE: {
        MOVIE: 0,
        ACTOR: 1,
        MUFF: 2,
    },
    getInitialState: function () {
        return {
            mode: this.MODE.MOVIE,
            movies: [],
            muffs: [],
            actors: [],
            offset: 0,
        }
    },
    getDefaultProps: function () {
        return {
            limit: 3,
            contextPath: '',
            muffSearchUrl: '/muff/search',
            movieSearchUrl: '/movie/search',
            actorSearchUrl: '/actor/search',
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
        let self = this;
        let url = this.props.contextPath;
        switch (this.state.mode) {
            case this.MODE.MUFF:
                url += this.props.muffSearchUrl;
                break;
            case this.MODE.ACTOR:
                url += this.props.actorSearchUrl;
                break;
            case this.MODE.MOVIE:
                url += this.props.movieSearchUrl;
                break;
            default:
                break;
        }
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
                    switch (self.state.mode) {
                        case self.MODE.MUFF:
                            self.setState(ps => {
                                return {muffs: data};
                            });
                            break;
                        case self.MODE.ACTOR:
                            self.setState(ps => {
                                return {actors: data};
                            });
                            break;
                        case self.MODE.MOVIE:
                            self.setState(ps => {
                                return {movies: data};
                            });
                            break;
                        default:
                            break;
                    }
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
                switch (self.state.mode) {
                    case self.MODE.MUFF:
                        self.setState(ps => {
                            return {muffs: []};
                        });
                        break;
                    case self.MODE.ACTOR:
                        self.setState(ps => {
                            return {actors: []};
                        });
                        break;
                    case self.MODE.MOVIE:
                        self.setState(ps => {
                            return {movies: []};
                        });
                        break;
                    default:
                        break;
                }
                this.fetchNextBatch(e.target.value);
                break;
            // Escape key
            case 27:
                $(this.refs.results).hide();
                break;
            default:
                break;
        }
    },
    onDomainSelect: function (e) {
        this.setState({mode: Number(e.target.value), offset: 0});
        $(this.refs.pattern).focus();
    },
    render: function () {
        let results;
        switch (this.state.mode) {
            case this.MODE.MUFF:
                results = this.state.muffs.map(muff => {
                    return <MuffButterSearchResult
                        key={muff.id}
                        id={muff.id}
                        contextPath={this.state.contextPath}
                        name={muff.name}
                        handle={muff.handle}
                        level={muff.level}
                    />;
                });
                break;
            case this.MODE.ACTOR:
                results = this.state.actors.map(muff => {
                    return <ActorButterSearchResult
                        key={muff.id}
                        id={muff.id}
                        name={muff.name}
                        contextPath={this.state.contextPath}
                    />;
                });
                break;
            case this.MODE.MOVIE:
                results = this.state.movies.map(muff => {
                    return <MovieButterSearchResult
                        key={muff.id}
                        id={muff.id}
                        name={muff.name}
                        contextPath={this.state.contextPath}
                    />;
                });
                break;
            default:
                break;
        }
        return (
            <div>
                <div className="row">
                    <div className="col s4">
                        <input type="radio" value={this.MODE.MOVIE} id="movie-domain-select" name="domain"
                               defaultChecked="defaultChecked" onClick={this.onDomainSelect}/>
                        <label htmlFor="movie-domain-select">Movies</label>
                    </div>
                    <div className="col s4">
                        <input type="radio" value={this.MODE.MUFF} id="muff-domain-select" name="domain"
                               onClick={this.onDomainSelect}/>
                        <label htmlFor="muff-domain-select">Muffs</label>
                    </div>
                    <div className="col s4">
                        <input type="radio" value={this.MODE.ACTOR} id="actor-domain-select" name="domain"
                               onClick={this.onDomainSelect}/>
                        <label htmlFor="actor-domain-select">Actors</label>
                    </div>
                    <input onKeyDown={this.onRegexInputKeyDown}
                           ref="pattern"
                           placeholder="Search" type="text" className="col s12"/>
                    <div className="col s12" ref="results">
                        <div className="collection with-header">
                            <div className="collection-header"><span className="flow-text">Results</span>
                                <span className="right">
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_left</i></button>
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_right</i></button>
                            </span>
                            </div>
                            {results}
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
