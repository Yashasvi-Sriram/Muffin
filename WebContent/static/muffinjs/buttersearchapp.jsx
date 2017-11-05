let MuffSearchResult = React.createClass({
    render: function () {
        return (
            <tr>
                <td>{this.props.name}</td>
                <td className="pink-text">{this.props.handle}</td>
            </tr>
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
            mode: this.MODE.MUFF,
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
                url += '/muff/search';
                break;
            case this.MODE.ACTOR:
                url += '/actor/search';
                break;
            case this.MODE.MOVIE:
                url += '/movie/search';
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
    render: function () {
        let results = this.state.muffs.map(muff => {
            return <MuffSearchResult
                key={muff.id}
                id={muff.id}
                name={muff.name}
                handle={muff.handle}
                level={muff.level}
            />;
        });
        return (
            <div>
                <input onKeyDown={this.onRegexInputKeyDown}
                       ref="pattern"
                       placeholder="Search" type="text"/>
                <table className="white" ref="results">
                    <thead>
                    <tr>
                        <td>
                            <button className="btn"
                                    onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_left</i></button>
                        </td>
                        <td>
                            <button className="btn"
                                    onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_right</i></button>
                        </td>
                    </tr>
                    </thead>
                    <tbody>{results}</tbody>
                </table>
            </div>
        );
    },
    componentDidMount: function () {
        $(this.refs.results).hide();
    },
});
