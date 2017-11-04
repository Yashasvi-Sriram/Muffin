let SearchResult = React.createClass({
    render: function () {
        return (
            <tr>
                <td>{this.props.id}</td>
            </tr>
        );
    }
});

let SearchApp = React.createClass({
    getInitialState: function () {
        return {
            offset: 0,
            results: [],
        }
    },
    getDefaultProps: function () {
        return {
            url: '',
            limit: 10,
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
    fetchNextBatch: function (pattern, url) {
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
                        return {results: data};
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
    onPatternInputKeyDown: function (e) {
        switch (e.keyCode || e.which) {
            // Enter Key
            case 13:
                this._resetOffset();
                this.fetchNextBatch(e.target.value, this.props.url);
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
        let results = this.state.results.map(result => {
            return <SearchResult
                key={result.id}
                id={result.id}
            />;
        });
        return (
            <div>
                <input onKeyDown={this.onPatternInputKeyDown}
                       ref="pattern"
                       placeholder="Search" type="text"/>
                <div ref="results">
                    <button className="btn-floating"
                            onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                        className="material-icons">keyboard_arrow_left</i></button>
                    <button className="btn-floating"
                            onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                        className="material-icons">keyboard_arrow_right</i></button>
                    <table className="white">
                        <tbody>{results}</tbody>
                    </table>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        $(this.refs.results).hide();
    },
});